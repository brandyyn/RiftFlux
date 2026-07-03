package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class EyeOfCthulhuMusicHandler {
    private static final ResourceLocation EYE_THEME = new ResourceLocation("riftflux", "eyeofcthulu");
    private static final ResourceLocation DESTROYER_THEME = new ResourceLocation("riftflux", "destroyer");
    private static final String[] MC_MUSIC_TICKER_FIELD_NAMES = {"mcMusicTicker", "field_147126_aw"};
    private static final String[] TICKER_CURRENT_SOUND_FIELD_NAMES = {"field_147678_c", "currentMusic"};
    private static final String[] TICKER_DELAY_FIELD_NAMES = {"field_147676_d", "timeUntilNextMusic"};
    private static final ResourceLocation EYE_GROWL = new ResourceLocation("mob.enderdragon.growl");
    private static final Map<Integer, EyeGrowlSound> ACTIVE_GROWLS = new HashMap<Integer, EyeGrowlSound>();
    private EyeThemeSound activeSound;
    private Field musicTickerField;
    private Field tickerCurrentSoundField;
    private Field tickerDelayField;
    private boolean reflectionInitialized;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (!ModConfig.enableTerraModule || (!ModConfig.eyeOfCthulhuMusicEnabled && !ModConfig.destroyerMusicEnabled)) {
            resetClientAudio(mc);
            return;
        }
        if (mc == null || mc.thePlayer == null || mc.theWorld == null || mc.getSoundHandler() == null) {
            resetClientAudio(mc);
            return;
        }
        pruneGrowls();

        ResourceLocation theme = getActiveBossTheme();
        if (theme == null) {
            if (activeSound != null || !ACTIVE_GROWLS.isEmpty()) {
                resetClientAudio(mc);
            } else {
                restoreBackgroundMusic(mc);
            }
            return;
        }
        suppressBackgroundMusic(mc);

        GuiScreen currentScreen = mc.currentScreen;
        if (currentScreen != null && currentScreen.doesGuiPauseGame()) {
            return;
        }

        boolean notPlaying = activeSound == null || !activeSound.isTheme(theme) || !isSoundPlaying(mc, activeSound);
        if (activeSound == null || activeSound.isDonePlaying() || notPlaying) {
            stopCurrent(mc);
            activeSound = new EyeThemeSound(theme);
            mc.getSoundHandler().playSound(activeSound);
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        resetClientAudio(Minecraft.getMinecraft());
    }

    public static void playGrowl(EntityEyeOfCthulhu eye, float volume, float pitch) {
        if (eye == null || eye.isDead || eye.worldObj == null || !eye.worldObj.isRemote) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.getSoundHandler() == null) {
            return;
        }

        Integer id = Integer.valueOf(eye.getEntityId());
        EyeGrowlSound current = ACTIVE_GROWLS.get(id);
        if (current != null) {
            current.setTarget(eye, volume, pitch);
            if (isSoundPlaying(mc, current) && !current.isDonePlaying()) {
                return;
            }
        }

        EyeGrowlSound growl = new EyeGrowlSound(eye, volume, pitch);
        ACTIVE_GROWLS.put(id, growl);
        mc.getSoundHandler().playSound(growl);
    }

    private static void stopAllGrowls(Minecraft mc) {
        if (ACTIVE_GROWLS.isEmpty()) {
            return;
        }
        for (EyeGrowlSound sound : ACTIVE_GROWLS.values()) {
            if (sound == null) {
                continue;
            }
            sound.stop();
            if (mc != null && mc.getSoundHandler() != null) {
                mc.getSoundHandler().stopSound(sound);
            }
        }
        ACTIVE_GROWLS.clear();
    }

    private static void pruneGrowls() {
        if (ACTIVE_GROWLS.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Integer, EyeGrowlSound>> iterator = ACTIVE_GROWLS.entrySet().iterator();
        while (iterator.hasNext()) {
            EyeGrowlSound sound = iterator.next().getValue();
            if (sound == null || sound.isDonePlaying() || sound.target == null || sound.target.isDead || sound.target.getHealth() <= 0.0F) {
                if (sound != null) {
                    sound.stop();
                }
                iterator.remove();
            }
        }
    }

    private void stopCurrent(Minecraft mc) {
        if (activeSound == null) {
            return;
        }
        activeSound.stop();
        if (mc != null && mc.getSoundHandler() != null) {
            mc.getSoundHandler().stopSound(activeSound);
        }
        activeSound = null;
    }

    private void resetClientAudio(Minecraft mc) {
        stopCurrent(mc);
        stopAllGrowls(mc);
        restoreBackgroundMusic(mc);
    }

    private void suppressBackgroundMusic(Minecraft mc) {
        Object ticker = getMusicTicker(mc);
        if (ticker == null || mc == null || mc.getSoundHandler() == null) {
            return;
        }

        ISound current = getTickerCurrentSound(ticker);
        if (current != null) {
            mc.getSoundHandler().stopSound(current);
            setTickerCurrentSound(ticker, null);
        }
        setTickerDelay(ticker, Integer.MAX_VALUE);
    }

    private void restoreBackgroundMusic(Minecraft mc) {
        Object ticker = getMusicTicker(mc);
        if (ticker == null) {
            return;
        }
        int delay = getTickerDelay(ticker);
        if (delay > 1200) {
            setTickerDelay(ticker, 40);
        }
    }

    private Object getMusicTicker(Minecraft mc) {
        if (mc == null) {
            return null;
        }
        ensureReflection(mc);
        if (musicTickerField == null) {
            return null;
        }
        try {
            return musicTickerField.get(mc);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    private ISound getTickerCurrentSound(Object ticker) {
        if (ticker == null || tickerCurrentSoundField == null) {
            return null;
        }
        try {
            Object value = tickerCurrentSoundField.get(ticker);
            return value instanceof ISound ? (ISound) value : null;
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    private void setTickerCurrentSound(Object ticker, ISound sound) {
        if (ticker == null || tickerCurrentSoundField == null) {
            return;
        }
        try {
            tickerCurrentSoundField.set(ticker, sound);
        } catch (IllegalAccessException ignored) {
        }
    }

    private int getTickerDelay(Object ticker) {
        if (ticker == null || tickerDelayField == null) {
            return -1;
        }
        try {
            return tickerDelayField.getInt(ticker);
        } catch (IllegalAccessException ignored) {
            return -1;
        }
    }

    private void setTickerDelay(Object ticker, int delay) {
        if (ticker == null || tickerDelayField == null) {
            return;
        }
        try {
            tickerDelayField.setInt(ticker, delay);
        } catch (IllegalAccessException ignored) {
        }
    }

    private void ensureReflection(Minecraft mc) {
        if (reflectionInitialized) {
            return;
        }
        reflectionInitialized = true;
        musicTickerField = findField(mc.getClass(), MC_MUSIC_TICKER_FIELD_NAMES);

        Object ticker = null;
        if (musicTickerField != null) {
            try {
                ticker = musicTickerField.get(mc);
            } catch (IllegalAccessException ignored) {
            }
        }
        if (ticker != null) {
            Class<?> tickerClass = ticker.getClass();
            tickerCurrentSoundField = findField(tickerClass, TICKER_CURRENT_SOUND_FIELD_NAMES);
            tickerDelayField = findField(tickerClass, TICKER_DELAY_FIELD_NAMES);
        }
    }

    private static Field findField(Class<?> owner, String[] names) {
        if (owner == null || names == null) {
            return null;
        }
        Class<?> cursor = owner;
        while (cursor != null) {
            for (String name : names) {
                try {
                    Field field = cursor.getDeclaredField(name);
                    field.setAccessible(true);
                    return field;
                } catch (NoSuchFieldException ignored) {
                }
            }
            cursor = cursor.getSuperclass();
        }
        return null;
    }

    private static boolean isSoundPlaying(Minecraft mc, ISound sound) {
        if (mc == null || mc.getSoundHandler() == null || sound == null) {
            return false;
        }
        try {
            return mc.getSoundHandler().isSoundPlaying(sound);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void setSoundPitch(MovingSound sound, float pitch) {
        if (sound == null) {
            return;
        }
        float clamped = Math.max(0.5F, Math.min(2.0F, pitch));
        Field field = findField(sound.getClass(), new String[]{"field_147663_c", "pitch"});
        if (field == null) {
            return;
        }
        try {
            field.setFloat(sound, clamped);
        } catch (IllegalAccessException ignored) {
        }
    }

    private static void setNoAttenuation(MovingSound sound) {
        if (sound == null) {
            return;
        }
        Field field = findField(sound.getClass(), new String[]{"field_147666_i", "attenuationType"});
        if (field == null) {
            return;
        }
        try {
            field.set(sound, ISound.AttenuationType.NONE);
        } catch (IllegalAccessException ignored) {
        }
    }

    private static ResourceLocation getActiveBossTheme() {
        if (BossStatus.statusBarTime <= 0) {
            return null;
        }
        String bossName = BossStatus.bossName;
        if (ModConfig.eyeOfCthulhuMusicEnabled && isEyeBossBarName(bossName)) {
            return EYE_THEME;
        }
        if (ModConfig.destroyerMusicEnabled && isDestroyerBossBarName(bossName)) {
            return DESTROYER_THEME;
        }
        return null;
    }
    private static boolean isEyeBossBarActive() {
        return BossStatus.statusBarTime > 0 && isEyeBossBarName(BossStatus.bossName);
    }

    private static boolean isEyeBossBarName(String name) {
        String current = normalizeBossName(name);
        if (current.isEmpty()) {
            return false;
        }
        if (current.contains("eye of cthulhu")) {
            return true;
        }

        String legacyName = normalizeBossName(StatCollector.translateToLocal("entity.EyeOfCthulhu.name"));
        if (!legacyName.isEmpty() && current.equals(legacyName)) {
            return true;
        }

        String moddedName = normalizeBossName(StatCollector.translateToLocal("entity.riftflux.EyeOfCthulhu.name"));
        return !moddedName.isEmpty() && current.equals(moddedName);
    }

    private static boolean isDestroyerBossBarName(String name) {
        String current = normalizeBossName(name);
        if (current.isEmpty()) {
            return false;
        }
        if (current.contains("destroyer")) {
            return true;
        }

        String legacyName = normalizeBossName(StatCollector.translateToLocal("entity.DestroyerHead.name"));
        if (!legacyName.isEmpty() && current.equals(legacyName)) {
            return true;
        }

        String moddedName = normalizeBossName(StatCollector.translateToLocal("entity.riftflux.DestroyerHead.name"));
        return !moddedName.isEmpty() && current.equals(moddedName);
    }

    private static String normalizeBossName(String text) {
        if (text == null) {
            return "";
        }
        return StringUtils.stripControlCodes(text).trim().toLowerCase(Locale.ROOT);
    }

    private static final class EyeThemeSound extends MovingSound {
        private final ResourceLocation theme;

        private EyeThemeSound(ResourceLocation theme) {
            super(theme);
            this.theme = theme;
            this.repeat = true;
            this.volume = 1.0F;
            setNoAttenuation(this);
            refreshPosition(Minecraft.getMinecraft() != null ? Minecraft.getMinecraft().thePlayer : null);
        }

        private boolean isTheme(ResourceLocation theme) {
            return this.theme != null && this.theme.equals(theme);
        }

        private void stop() {
            this.donePlaying = true;
        }

        @Override
        public void update() {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc != null ? mc.thePlayer : null;
            ResourceLocation activeTheme = getActiveBossTheme();
            if (player == null || activeTheme == null || !this.isTheme(activeTheme)) {
                this.donePlaying = true;
                return;
            }

            this.volume = 1.0F;
            refreshPosition(player);
        }

        private void refreshPosition(EntityPlayer player) {
            if (player != null) {
                this.xPosF = (float) player.posX;
                this.yPosF = (float) player.posY;
                this.zPosF = (float) player.posZ;
                return;
            }
            this.xPosF = 0.0F;
            this.yPosF = 0.0F;
            this.zPosF = 0.0F;
        }
    }

    private static final class EyeGrowlSound extends MovingSound {
        private EntityEyeOfCthulhu target;
        private int ageTicks;
        private int maxTicks;

        private EyeGrowlSound(EntityEyeOfCthulhu target, float volume, float pitch) {
            super(EYE_GROWL);
            this.repeat = false;
            setTarget(target, volume, pitch);
            this.maxTicks = 90;
        }

        private void setTarget(EntityEyeOfCthulhu target, float volume, float pitch) {
            this.target = target;
            this.volume = Math.max(0.0F, volume);
            setSoundPitch(this, pitch);
            refreshPosition();
        }

        private void stop() {
            this.donePlaying = true;
        }

        @Override
        public void update() {
            ageTicks++;
            if (this.target == null || this.target.isDead || this.target.getHealth() <= 0.0F || ageTicks > maxTicks) {
                this.donePlaying = true;
                if (this.target != null) {
                    ACTIVE_GROWLS.remove(Integer.valueOf(this.target.getEntityId()));
                }
                return;
            }
            refreshPosition();
        }

        private void refreshPosition() {
            if (this.target == null) {
                return;
            }
            this.xPosF = (float) this.target.posX;
            this.yPosF = (float) this.target.posY;
            this.zPosF = (float) this.target.posZ;
        }
    }
}

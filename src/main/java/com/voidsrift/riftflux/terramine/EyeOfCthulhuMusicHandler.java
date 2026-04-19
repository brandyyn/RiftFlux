package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class EyeOfCthulhuMusicHandler {
    private static final ResourceLocation EYE_THEME = new ResourceLocation("riftflux", "eyeofcthulu");
    private static final String[] MC_MUSIC_TICKER_FIELD_NAMES = {"mcMusicTicker", "field_147126_aw"};
    private static final String[] TICKER_CURRENT_SOUND_FIELD_NAMES = {"field_147678_c", "currentMusic"};
    private static final String[] TICKER_DELAY_FIELD_NAMES = {"field_147676_d", "timeUntilNextMusic"};
    private static final ResourceLocation EYE_GROWL = new ResourceLocation("mob.enderdragon.growl");
    private static final Map<Integer, EyeGrowlSound> ACTIVE_GROWLS = new HashMap<Integer, EyeGrowlSound>();
    private EyeThemeSound activeSound;
    private int trackedEntityId = Integer.MIN_VALUE;
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
        if (!ModConfig.enableTerraModule || !ModConfig.eyeOfCthulhuMusicEnabled) {
            stopCurrent(mc);
            stopAllGrowls(mc);
            restoreBackgroundMusic(mc);
            return;
        }
        if (mc == null || mc.thePlayer == null || mc.theWorld == null || mc.getSoundHandler() == null) {
            stopCurrent(mc);
            stopAllGrowls(mc);
            restoreBackgroundMusic(mc);
            return;
        }
        pruneGrowls();

        EntityEyeOfCthulhu eye = findBestEye(mc.theWorld, mc.thePlayer, mc);
        if (eye == null) {
            stopCurrent(mc);
            stopAllGrowls(mc);
            restoreBackgroundMusic(mc);
            return;
        }
        suppressBackgroundMusic(mc);

        GuiScreen currentScreen = mc.currentScreen;
        if (currentScreen != null && currentScreen.doesGuiPauseGame()) {
            if (activeSound != null) {
                activeSound.setTarget(eye);
            }
            return;
        }

        boolean notPlaying = activeSound == null || !isSoundPlaying(mc, activeSound);
        if (activeSound == null || activeSound.isDonePlaying() || trackedEntityId != eye.getEntityId() || notPlaying) {
            stopCurrent(mc);
            trackedEntityId = eye.getEntityId();
            activeSound = new EyeThemeSound(eye);
            mc.getSoundHandler().playSound(activeSound);
        } else {
            activeSound.setTarget(eye);
        }
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
        trackedEntityId = Integer.MIN_VALUE;
        if (activeSound == null) {
            return;
        }
        activeSound.stop();
        if (mc != null && mc.getSoundHandler() != null) {
            mc.getSoundHandler().stopSound(activeSound);
        }
        activeSound = null;
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

    private static EntityEyeOfCthulhu findBestEye(World world, EntityPlayer player, Minecraft mc) {
        if (world == null || player == null) {
            return null;
        }
        List loaded = world.loadedEntityList;
        if (loaded == null || loaded.isEmpty()) {
            return null;
        }

        EntityEyeOfCthulhu best = null;
        double bestDistanceSq = Double.MAX_VALUE;
        double maxDistance = getMusicRange(mc);
        double maxDistanceSq = maxDistance * maxDistance;

        for (Object obj : loaded) {
            if (!(obj instanceof EntityEyeOfCthulhu)) {
                continue;
            }
            EntityEyeOfCthulhu eye = (EntityEyeOfCthulhu) obj;
            if (eye.isDead || eye.getHealth() <= 0.0F) {
                continue;
            }
            double distanceSq = eye.getDistanceSqToEntity(player);
            if (distanceSq > maxDistanceSq) {
                continue;
            }
            if (distanceSq < bestDistanceSq) {
                bestDistanceSq = distanceSq;
                best = eye;
            }
        }
        return best;
    }

    private static double getMusicRange(Minecraft mc) {
        int chunks = mc != null && mc.gameSettings != null ? mc.gameSettings.renderDistanceChunks : 8;
        return Math.max(48.0D, chunks * 16.0D + 24.0D);
    }

    private static final class EyeThemeSound extends MovingSound {
        private EntityEyeOfCthulhu target;

        private EyeThemeSound(EntityEyeOfCthulhu target) {
            super(EYE_THEME);
            this.target = target;
            this.repeat = true;
            this.volume = 1.0F;
            setNoAttenuation(this);
            refreshPosition(Minecraft.getMinecraft() != null ? Minecraft.getMinecraft().thePlayer : null);
        }

        private void setTarget(EntityEyeOfCthulhu target) {
            this.target = target;
            refreshPosition(Minecraft.getMinecraft() != null ? Minecraft.getMinecraft().thePlayer : null);
        }

        private void stop() {
            this.donePlaying = true;
        }

        @Override
        public void update() {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc != null ? mc.thePlayer : null;
            if (player == null || this.target == null || this.target.isDead || this.target.getHealth() <= 0.0F) {
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
            if (this.target != null) {
                this.xPosF = (float) this.target.posX;
                this.yPosF = (float) this.target.posY;
                this.zPosF = (float) this.target.posZ;
            }
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

package com.voidsrift.riftflux.blessings;

import cpw.mods.fml.common.Loader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class BlessingLossNotifier {
    public static final int OFFLINE_BREAK_DELAY_TICKS = 20 * 60 * 5;

    private static final String TAG_PENDING = "BlessingLossPending";
    private static final String TAG_DELAY = "BlessingLossDelay";
    private static final String[] LOSS_SOUNDS = new String[]{
            "riftflux:craftstart",
            "riftflux:hhon",
            "riftflux:shock1",
            "riftflux:heartbeat"
    };
    private static boolean thaumcraftBurstResolved;
    private static Object thaumcraftProxy;
    private static Method thaumcraftBurstMethod;

    private BlessingLossNotifier() {
    }

    public static void queue(EntityPlayer player, int delayTicks) {
        NBTTagCompound tag = getPersisted(player, true);
        if (tag == null) {
            return;
        }

        int clampedDelay = Math.max(0, delayTicks);
        if (tag.getBoolean(TAG_PENDING)) {
            tag.setInteger(TAG_DELAY, Math.min(tag.getInteger(TAG_DELAY), clampedDelay));
            return;
        }

        tag.setBoolean(TAG_PENDING, true);
        tag.setInteger(TAG_DELAY, clampedDelay);
    }

    public static void clear(EntityPlayer player) {
        NBTTagCompound tag = getPersisted(player, false);
        if (tag == null) {
            return;
        }
        tag.removeTag(TAG_PENDING);
        tag.removeTag(TAG_DELAY);
    }

    public static void tick(EntityPlayer player) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }

        NBTTagCompound tag = getPersisted(player, false);
        if (tag == null || !tag.getBoolean(TAG_PENDING)) {
            return;
        }

        int delay = tag.getInteger(TAG_DELAY);
        if (delay > 0) {
            tag.setInteger(TAG_DELAY, delay - 1);
            return;
        }

        sendNow(player);
        clear(player);
    }

    public static void sendNow(EntityPlayer player) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }

        ChatComponentTranslation lineOne = new ChatComponentTranslation("blessing.riftflux.pillar_destroyed.line1");
        lineOne.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);
        player.addChatComponentMessage(lineOne);

        ChatComponentTranslation lineTwo = new ChatComponentTranslation("blessing.riftflux.pillar_destroyed.line2");
        lineTwo.getChatStyle().setColor(EnumChatFormatting.RED);
        player.addChatComponentMessage(lineTwo);

        for (String sound : LOSS_SOUNDS) {
            player.worldObj.playSoundAtEntity(player, sound, 1.0F, 1.0F);
        }

        playThaumcraftBurst(player);
    }

    private static NBTTagCompound getPersisted(EntityPlayer player, boolean create) {
        if (player == null) {
            return null;
        }

        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            if (!create) {
                return null;
            }
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    private static void playThaumcraftBurst(EntityPlayer player) {
        if (!Loader.isModLoaded("Thaumcraft")) {
            return;
        }

        resolveThaumcraftBurst();
        if (thaumcraftProxy == null || thaumcraftBurstMethod == null) {
            return;
        }

        try {
            thaumcraftBurstMethod.invoke(
                    thaumcraftProxy,
                    player.worldObj,
                    player.posX,
                    player.posY + (double) player.getEyeHeight(),
                    player.posZ,
                    0.5F
            );
        } catch (Exception ignored) {
        }
    }

    private static void resolveThaumcraftBurst() {
        if (thaumcraftBurstResolved) {
            return;
        }
        thaumcraftBurstResolved = true;

        try {
            Class<?> thaumcraftClass = Class.forName("thaumcraft.common.Thaumcraft");
            Field proxyField = thaumcraftClass.getField("proxy");
            Object proxy = proxyField.get(null);
            if (proxy == null) {
                return;
            }

            Method burst = proxy.getClass().getMethod(
                    "burst",
                    net.minecraft.world.World.class,
                    double.class,
                    double.class,
                    double.class,
                    float.class
            );
            thaumcraftProxy = proxy;
            thaumcraftBurstMethod = burst;
        } catch (Exception ignored) {
        }
    }
}

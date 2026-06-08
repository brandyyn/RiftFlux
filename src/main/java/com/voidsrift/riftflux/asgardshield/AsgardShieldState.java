package com.voidsrift.riftflux.asgardshield;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public final class AsgardShieldState {
    public static final String NBT_GUARD_GAUGE = "AsgardShieldGG";
    public static final String NBT_GUARD_BROKEN = "AsgardShieldGB";
    public static final String NBT_VANGUARD_COUNT = "RiftAsgardVanguardCount";
    public static final String NBT_VANGUARD_TICKS = "RiftAsgardVanguardTicks";
    public static final String NBT_LIVINGMETAL_AURA = "RiftAsgardLivingmetalAura";
    public static final String NBT_BIOMASS_AURA = "RiftAsgardBiomassAura";

    private AsgardShieldState() {
    }

    private static NBTTagCompound data(EntityPlayer player) {
        return player == null ? null : player.getEntityData();
    }

    public static int getGuardGauge(EntityPlayer player) {
        NBTTagCompound data = data(player);
        return data == null ? 0 : Math.max(0, data.getInteger(NBT_GUARD_GAUGE));
    }

    public static void setGuardGauge(EntityPlayer player, int value) {
        NBTTagCompound data = data(player);
        if (data == null) {
            return;
        }
        int clamped = Math.max(0, Math.min(200, value));
        if (!data.hasKey(NBT_GUARD_GAUGE) || data.getInteger(NBT_GUARD_GAUGE) != clamped) {
            data.setInteger(NBT_GUARD_GAUGE, clamped);
        }
    }

    public static boolean isGuardBroken(EntityPlayer player) {
        NBTTagCompound data = data(player);
        if (data == null) {
            return false;
        }
        if (data.hasKey(NBT_GUARD_BROKEN, 1)) {
            return data.getBoolean(NBT_GUARD_BROKEN);
        }
        // Legacy compatibility with old short-based value.
        return data.getInteger(NBT_GUARD_BROKEN) != 0;
    }

    public static void setGuardBroken(EntityPlayer player, boolean broken) {
        NBTTagCompound data = data(player);
        if (data == null) {
            return;
        }
        if (!data.hasKey(NBT_GUARD_BROKEN) || isGuardBroken(player) != broken) {
            data.setBoolean(NBT_GUARD_BROKEN, broken);
        }
    }

    public static int getVanguardCount(EntityPlayer player) {
        NBTTagCompound data = data(player);
        return data == null ? 0 : Math.max(0, Math.min(7, data.getInteger(NBT_VANGUARD_COUNT)));
    }

    public static void setVanguardCount(EntityPlayer player, int value) {
        NBTTagCompound data = data(player);
        if (data == null) {
            return;
        }
        int clamped = Math.max(0, Math.min(7, value));
        if (!data.hasKey(NBT_VANGUARD_COUNT) || data.getInteger(NBT_VANGUARD_COUNT) != clamped) {
            data.setInteger(NBT_VANGUARD_COUNT, clamped);
        }
    }

    public static int getVanguardTicks(EntityPlayer player) {
        NBTTagCompound data = data(player);
        return data == null ? 0 : Math.max(0, data.getInteger(NBT_VANGUARD_TICKS));
    }

    public static void setVanguardTicks(EntityPlayer player, int value) {
        NBTTagCompound data = data(player);
        if (data == null) {
            return;
        }
        int clamped = Math.max(0, value);
        if (!data.hasKey(NBT_VANGUARD_TICKS) || data.getInteger(NBT_VANGUARD_TICKS) != clamped) {
            data.setInteger(NBT_VANGUARD_TICKS, clamped);
        }
    }

    public static int getLivingmetalAura(EntityPlayer player) {
        NBTTagCompound data = data(player);
        return data == null ? 0 : Math.max(0, data.getInteger(NBT_LIVINGMETAL_AURA));
    }

    public static void setLivingmetalAura(EntityPlayer player, int value) {
        NBTTagCompound data = data(player);
        if (data == null) {
            return;
        }
        int clamped = Math.max(0, value);
        if (!data.hasKey(NBT_LIVINGMETAL_AURA) || data.getInteger(NBT_LIVINGMETAL_AURA) != clamped) {
            data.setInteger(NBT_LIVINGMETAL_AURA, clamped);
        }
    }

    public static int getBiomassAura(EntityPlayer player) {
        NBTTagCompound data = data(player);
        return data == null ? 0 : Math.max(0, data.getInteger(NBT_BIOMASS_AURA));
    }

    public static void setBiomassAura(EntityPlayer player, int value) {
        NBTTagCompound data = data(player);
        if (data == null) {
            return;
        }
        int clamped = Math.max(0, value);
        if (!data.hasKey(NBT_BIOMASS_AURA) || data.getInteger(NBT_BIOMASS_AURA) != clamped) {
            data.setInteger(NBT_BIOMASS_AURA, clamped);
        }
    }
}

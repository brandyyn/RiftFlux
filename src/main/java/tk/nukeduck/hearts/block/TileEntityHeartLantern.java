/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 */
package tk.nukeduck.hearts.block;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import tk.nukeduck.hearts.HeartCrystal;
import tk.nukeduck.hearts.HeartsConfig;

public class TileEntityHeartLantern
extends TileEntity {
    private static final int AURA_DURATION_TICKS = 60;
    private static final int AURA_REFRESH_INTERVAL_TICKS = 40;
    private static final int AURA_MIN_REMAINING_TICKS = 30;
    private static final int CHARGE_TICK_INTERVAL = 4;
    private static final float CHARGE_TICK_CHANCE = 0.2f;
    private static final float CHARGE_INCREMENT = 0.05f;
    float chargeLevel;
    public static final String CHARGE_KEY = "ChargeLevel";

    @Override
    public void updateEntity() {
        this.chargeLevel = Math.max(0.0f, Math.min(1.0f, this.chargeLevel));
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }

        long tick = this.worldObj.getTotalWorldTime() + this.getTickOffset();
        boolean changed = false;
        if (tick % (long)CHARGE_TICK_INTERVAL == 0L && this.chargeLevel < 1.0f && HeartCrystal.random.nextFloat() < CHARGE_TICK_CHANCE) {
            float nextCharge = Math.min(1.0f, this.chargeLevel + CHARGE_INCREMENT);
            if (nextCharge != this.chargeLevel) {
                this.chargeLevel = nextCharge;
                changed = true;
            }
        }

        if (tick % (long)AURA_REFRESH_INTERVAL_TICKS == 0L) {
            this.refreshLanternAura();
        }

        if (changed) {
            this.markDirty();
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.chargeLevel = compound.getFloat(CHARGE_KEY);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setFloat(CHARGE_KEY, this.chargeLevel);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound data = new NBTTagCompound();
        this.writeToNBT(data);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, data);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        this.readFromNBT(packet.func_148857_g());
    }

    private void refreshLanternAura() {
        HeartsConfig config = HeartCrystal.config;
        if (config == null || !config.isLanternAuraEnabled()) {
            return;
        }

        List<HeartsConfig.LanternAuraEffect> effects = config.getLanternAuraEffects();
        if (effects == null || effects.isEmpty()) {
            return;
        }

        float radius = config.getLanternAuraRadius();
        if (radius <= 0.0f) {
            return;
        }

        double centerX = (double)this.xCoord + 0.5;
        double centerY = (double)this.yCoord + 0.5;
        double centerZ = (double)this.zCoord + 0.5;
        double radiusSq = (double)radius * (double)radius;
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(centerX - (double)radius, centerY - (double)radius, centerZ - (double)radius, centerX + (double)radius, centerY + (double)radius, centerZ + (double)radius);
        List<EntityPlayer> players = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, bounds);

        for (EntityPlayer player : players) {
            if (player == null || !player.isEntityAlive()) {
                continue;
            }
            double dx = player.posX - centerX;
            double dy = player.posY + (double)(player.height * 0.5f) - centerY;
            double dz = player.posZ - centerZ;
            if (dx * dx + dy * dy + dz * dz > radiusSq) {
                continue;
            }
            this.applyLanternEffects(player, effects);
        }
    }

    private void applyLanternEffects(EntityPlayer player, List<HeartsConfig.LanternAuraEffect> effects) {
        for (HeartsConfig.LanternAuraEffect effectDef : effects) {
            if (effectDef == null) {
                continue;
            }
            int potionId = effectDef.getPotionId();
            if (potionId < 0 || potionId >= Potion.potionTypes.length) {
                continue;
            }
            Potion potion = Potion.potionTypes[potionId];
            if (potion == null) {
                continue;
            }
            PotionEffect active = player.getActivePotionEffect(potion);
            if (active != null && active.getAmplifier() == effectDef.getAmplifier() && active.getDuration() > AURA_MIN_REMAINING_TICKS) {
                continue;
            }
            player.addPotionEffect(new PotionEffect(potionId, AURA_DURATION_TICKS, effectDef.getAmplifier(), true));
        }
    }

    private long getTickOffset() {
        return ((long)this.xCoord * 31L + (long)this.yCoord * 17L + (long)this.zCoord * 13L) & 255L;
    }
}

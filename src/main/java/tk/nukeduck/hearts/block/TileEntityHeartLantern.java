/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 */
package tk.nukeduck.hearts.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import tk.nukeduck.hearts.HeartCrystal;
import tk.nukeduck.hearts.HeartsConfig;

public class TileEntityHeartLantern
extends TileEntity {
    private static final int DEFAULT_AURA_DURATION_TICKS = 80;
    private static final int MIN_AURA_DURATION_TICKS = 20;
    private static final int MIN_AURA_REFRESH_INTERVAL_TICKS = 10;
    private static final int MAX_AURA_REFRESH_INTERVAL_TICKS = 40;
    private static final int CHARGE_TICK_INTERVAL = 4;
    private static final float CHARGE_TICK_CHANCE = 0.2f;
    private static final float CHARGE_INCREMENT = 0.05f;
    private static final String AURA_EFFECTS_KEY = "AuraEffects";
    private static final String AURA_EFFECT_POTION_ID_KEY = "PotionId";
    private static final String AURA_EFFECT_AMPLIFIER_KEY = "Amplifier";
    float chargeLevel;
    public static final String CHARGE_KEY = "ChargeLevel";
    private final List<HeartsConfig.LanternAuraEffect> infusedAuraEffects = new ArrayList<HeartsConfig.LanternAuraEffect>();

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

        int auraRefreshInterval = this.getAuraRefreshIntervalTicks();
        if (tick % (long)auraRefreshInterval == 0L) {
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
        this.infusedAuraEffects.clear();
        NBTTagList effectList = compound.getTagList(AURA_EFFECTS_KEY, 10);
        for (int i = 0; i < effectList.tagCount(); ++i) {
            NBTTagCompound effectTag = effectList.getCompoundTagAt(i);
            int potionId = effectTag.getInteger(AURA_EFFECT_POTION_ID_KEY);
            int amplifier = Math.max(0, effectTag.getInteger(AURA_EFFECT_AMPLIFIER_KEY));
            if (potionId < 0 || potionId >= Potion.potionTypes.length || Potion.potionTypes[potionId] == null) {
                continue;
            }
            this.mergeInfusedAuraEffect(new HeartsConfig.LanternAuraEffect(potionId, amplifier));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setFloat(CHARGE_KEY, this.chargeLevel);
        NBTTagList effectList = new NBTTagList();
        for (HeartsConfig.LanternAuraEffect effect : this.infusedAuraEffects) {
            if (effect == null) {
                continue;
            }
            NBTTagCompound effectTag = new NBTTagCompound();
            effectTag.setInteger(AURA_EFFECT_POTION_ID_KEY, effect.getPotionId());
            effectTag.setInteger(AURA_EFFECT_AMPLIFIER_KEY, effect.getAmplifier());
            effectList.appendTag(effectTag);
        }
        compound.setTag(AURA_EFFECTS_KEY, effectList);
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

        List<HeartsConfig.LanternAuraEffect> effects = this.getAuraEffects();
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
        int auraDurationTicks = this.getAuraDurationTicks();
        int minRemainingTicks = this.getAuraMinRemainingTicks(auraDurationTicks);
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
            if (active != null && active.getAmplifier() == effectDef.getAmplifier() && active.getDuration() > minRemainingTicks) {
                continue;
            }
            player.addPotionEffect(new PotionEffect(potionId, auraDurationTicks, effectDef.getAmplifier(), true));
        }
    }

    public boolean canAcceptPotionEffects(ItemStack stack) {
        return !this.extractPotionEffects(stack).isEmpty();
    }

    public boolean addPotionEffects(ItemStack stack) {
        List<HeartsConfig.LanternAuraEffect> effects = this.extractPotionEffects(stack);
        if (effects.isEmpty()) {
            return false;
        }
        boolean changed = false;
        for (HeartsConfig.LanternAuraEffect effect : effects) {
            changed |= this.mergeInfusedAuraEffect(effect);
        }
        if (!changed) {
            return false;
        }
        this.markDirty();
        if (this.worldObj != null) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
        return true;
    }

    private int getAuraDurationTicks() {
        HeartsConfig config = HeartCrystal.config;
        if (config == null) {
            return DEFAULT_AURA_DURATION_TICKS;
        }
        return Math.max(MIN_AURA_DURATION_TICKS, config.getLanternAuraDurationSeconds() * 20);
    }

    private int getAuraRefreshIntervalTicks() {
        int auraDurationTicks = this.getAuraDurationTicks();
        return Math.max(MIN_AURA_REFRESH_INTERVAL_TICKS, Math.min(MAX_AURA_REFRESH_INTERVAL_TICKS, auraDurationTicks / 2));
    }

    private int getAuraMinRemainingTicks(int auraDurationTicks) {
        return Math.min(auraDurationTicks - 1, this.getAuraRefreshIntervalTicks());
    }

    private long getTickOffset() {
        return ((long)this.xCoord * 31L + (long)this.yCoord * 17L + (long)this.zCoord * 13L) & 255L;
    }

    protected List<HeartsConfig.LanternAuraEffect> getBaseAuraEffects() {
        HeartsConfig config = HeartCrystal.config;
        if (config == null) {
            return Collections.emptyList();
        }
        List<HeartsConfig.LanternAuraEffect> effects = config.getLanternAuraEffects();
        return effects != null ? effects : Collections.<HeartsConfig.LanternAuraEffect>emptyList();
    }

    private List<HeartsConfig.LanternAuraEffect> getAuraEffects() {
        LinkedHashMap<Integer, HeartsConfig.LanternAuraEffect> merged = new LinkedHashMap<Integer, HeartsConfig.LanternAuraEffect>();
        this.mergeAuraEffects(merged, this.getBaseAuraEffects());
        this.mergeAuraEffects(merged, this.infusedAuraEffects);
        if (merged.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<HeartsConfig.LanternAuraEffect>(merged.values());
    }

    private void mergeAuraEffects(Map<Integer, HeartsConfig.LanternAuraEffect> merged, List<HeartsConfig.LanternAuraEffect> effects) {
        if (effects == null || effects.isEmpty()) {
            return;
        }
        for (HeartsConfig.LanternAuraEffect effect : effects) {
            if (effect == null) {
                continue;
            }
            HeartsConfig.LanternAuraEffect existing = merged.get(effect.getPotionId());
            if (existing != null && existing.getAmplifier() >= effect.getAmplifier()) {
                continue;
            }
            merged.put(effect.getPotionId(), effect);
        }
    }

    private List<HeartsConfig.LanternAuraEffect> extractPotionEffects(ItemStack stack) {
        if (stack == null || stack.getItem() != Items.potionitem) {
            return Collections.emptyList();
        }
        ItemPotion potionItem = (ItemPotion)Items.potionitem;
        List potionEffects = potionItem.getEffects(stack);
        if (potionEffects == null || potionEffects.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<HeartsConfig.LanternAuraEffect> extracted = new ArrayList<HeartsConfig.LanternAuraEffect>();
        for (Object effectObject : potionEffects) {
            if (!(effectObject instanceof PotionEffect)) {
                continue;
            }
            PotionEffect effect = (PotionEffect)effectObject;
            int potionId = effect.getPotionID();
            if (potionId < 0 || potionId >= Potion.potionTypes.length) {
                continue;
            }
            Potion potion = Potion.potionTypes[potionId];
            if (potion == null || potion.isInstant()) {
                continue;
            }
            extracted.add(new HeartsConfig.LanternAuraEffect(potionId, Math.max(0, effect.getAmplifier())));
        }
        if (extracted.isEmpty()) {
            return Collections.emptyList();
        }
        return extracted;
    }

    private boolean mergeInfusedAuraEffect(HeartsConfig.LanternAuraEffect effect) {
        if (effect == null) {
            return false;
        }
        int potionId = effect.getPotionId();
        for (int i = 0; i < this.infusedAuraEffects.size(); ++i) {
            HeartsConfig.LanternAuraEffect existing = this.infusedAuraEffects.get(i);
            if (existing == null || existing.getPotionId() != potionId) {
                continue;
            }
            if (existing.getAmplifier() >= effect.getAmplifier()) {
                return false;
            }
            this.infusedAuraEffects.set(i, effect);
            return true;
        }
        this.infusedAuraEffects.add(effect);
        return true;
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.stats.StatBase
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IExtendedEntityProperties
 */
package net.nmccoy.legendgear;

import com.voidsrift.riftflux.net.sync.IPlayerSyncData;
import com.voidsrift.riftflux.net.sync.PlayerSyncHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.nmccoy.legendgear.LegendGear2;

public class PlayerStarstatsExtension
implements IExtendedEntityProperties, IPlayerSyncData {
    public int starChargePoints = 0;
    public int starCooldownTimer = 0;
    public int starsCollected = 0;
    public int emeraldDropsRemaining = LegendGear2.maxEmeraldDropsBanked;
    public int lastKillX = 0;
    public int lastKillZ = 0;
    public int lastSkyX = 0;
    public int lastSkyZ = 0;
    public int lastSkyWorld;
    public int grassEmeraldSupply;
    public int phoenixAffinity;
    public float castingProgress;
    public float rechargeDelay;
    public long lastStarwellDrink;
    public static final String EXT_PROP_NAME = "PlayerStarstats";
    private final EntityPlayer player;
    public static final int FINE = 0;
    public static final int FATIGUED = 1;
    public static final int EXHAUSTED = 2;
    private float fractionalMana = 0.0f;
    public float glideCharge = 0.0f;
    public int skylensTagCharge = 0;
    public float lastGlideCharge = 0.0f;
    public int starwellCharge = 0;
    private int manaWhole;
    private float glideValue;

    public int fatigueLevel() {
        float magicFatigue = this.getMana();
        if (magicFatigue > 20.0f) {
            return 2;
        }
        if (magicFatigue > this.breathingRoom() - 1.0f) {
            return 1;
        }
        return 0;
    }

    public float recuperationRate() {
        if (this.fatigueLevel() == 2) {
            return 0.25f;
        }
        if (this.fatigueLevel() == 1) {
            return 0.5f;
        }
        return 1.0f;
    }

    public float breathingRoom() {
        return 20 - this.player.getTotalArmorValue();
    }

    public boolean expendMana(ItemStack stack, float amount) {
        float penalty;
        if (this.fatigueLevel() == 2 && amount > 0.25f) {
            this.player.attackEntityFrom(new DamageSource("exhaustion").setDamageIsAbsolute().setDamageBypassesArmor(), amount);
        }
        boolean overspent = false;
        if (!this.player.capabilities.isCreativeMode) {
            overspent = this.adjustManaFatigue(amount);
        }
        float delay = 0.0f;
        if (overspent) {
            delay = LegendGear2.fatiguedRechargeDelay;
            if (this.fatigueLevel() == 2 && this.player.getTotalArmorValue() > 0) {
                delay = LegendGear2.exhaustedRechargeDelay;
            }
        } else {
            delay = LegendGear2.manaRechargeDelay;
        }
        int fortitude = 0;
        if (stack != null) {
            fortitude = EnchantmentHelper.getEnchantmentLevel((int)LegendGear2.enchSpellArmoredID, (ItemStack)stack);
        }
        if ((delay -= (penalty = delay - LegendGear2.manaRechargeDelay) * (float)fortitude / 3.0f) >= this.rechargeDelay) {
            this.rechargeDelay = delay;
        }
        return overspent;
    }

    public static final void register(EntityPlayer regplayer) {
        regplayer.registerExtendedProperties(EXT_PROP_NAME, (IExtendedEntityProperties)new PlayerStarstatsExtension(regplayer));
    }

    public static final PlayerStarstatsExtension get(EntityPlayer player) {
        return (PlayerStarstatsExtension)player.getExtendedProperties(EXT_PROP_NAME);
    }

    public PlayerStarstatsExtension(EntityPlayer pla) {
        this.player = pla;
        this.grassEmeraldSupply = LegendGear2.maxEmeraldGrassDropsBanked;
        this.phoenixAffinity = 0;
        this.rechargeDelay = 0.0f;
    }

    public boolean adjustManaFatigue(float amount) {
        float magicFatigue = this.getMana();
        if ((magicFatigue += amount) <= 0.0f) {
            magicFatigue = 0.0f;
        }
        if (amount < 0.0f) {
            if (this.fatigueLevel() == 0 && this.rechargeDelay > LegendGear2.manaRechargeDelay) {
                this.rechargeDelay = LegendGear2.manaRechargeDelay;
            }
            if (this.fatigueLevel() == 1 && this.rechargeDelay > LegendGear2.fatiguedRechargeDelay) {
                this.rechargeDelay = LegendGear2.fatiguedRechargeDelay;
            }
        }
        if (!this.player.worldObj.isRemote) {
            this.setMana(magicFatigue);
        }
        boolean overspent = false;
        PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(this.player);
        if (magicFatigue > pse.breathingRoom()) {
            overspent = true;
        }
        return overspent;
    }

    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("starChargePoints", this.starChargePoints);
        data.setInteger("starCooldownTimer", this.starCooldownTimer);
        data.setInteger("starsCollected", this.starsCollected);
        data.setInteger("emeraldDropsRemaining", this.emeraldDropsRemaining);
        data.setInteger("grassEmeraldSupply", this.grassEmeraldSupply);
        data.setInteger("phoenixAffinity", this.phoenixAffinity);
        data.setInteger("lastKillX", this.lastKillX);
        data.setInteger("lastKillZ", this.lastKillZ);
        data.setInteger("lastSkyX", this.lastSkyX);
        data.setInteger("lastSkyZ", this.lastSkyZ);
        data.setInteger("lastSkyWorld", this.lastSkyWorld);
        data.setFloat("magicFatigue", this.getMana());
        data.setFloat("rechargeDelay", this.rechargeDelay);
        data.setFloat("glideEnergy", this.getGlide());
        data.setLong("lastStarwellDrink", this.lastStarwellDrink);
        data.setInteger("starwellCharge", this.starwellCharge);
        compound.setTag(EXT_PROP_NAME, (NBTBase)data);
    }

    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound data = compound.getCompoundTag(EXT_PROP_NAME);
        this.starChargePoints = data.getInteger("starChargePoints");
        this.starCooldownTimer = data.getInteger("starCooldownTimer");
        this.starsCollected = data.getInteger("starsCollected");
        this.emeraldDropsRemaining = data.getInteger("emeraldDropsRemaining");
        this.grassEmeraldSupply = data.getInteger("grassEmeraldSupply");
        this.phoenixAffinity = data.getInteger("phoenixAffinity");
        this.lastKillX = data.getInteger("lastKillX");
        this.lastKillZ = data.getInteger("lastKillZ");
        this.lastSkyX = data.getInteger("lastSkyX");
        this.lastSkyZ = data.getInteger("lastSkyZ");
        this.lastSkyWorld = data.getInteger("lastSkyWorld");
        this.applyLocalMana(data.getFloat("magicFatigue"));
        this.rechargeDelay = data.getFloat("rechargeDelay");
        this.glideValue = data.getFloat("glideEnergy");
        this.lastGlideCharge = this.glideValue;
        this.lastStarwellDrink = data.getLong("lastStarwellDrink");
        this.starwellCharge = data.getInteger("starwellCharge");
    }

    public void init(Entity entity, World world) {
    }

    public long starwellDrinkTime() {
        return this.player.worldObj.getTotalWorldTime() - this.lastStarwellDrink;
    }

    public void resetStarwellDrinkTime() {
        this.lastStarwellDrink = this.player.worldObj.getTotalWorldTime();
    }

    public float getGlide() {
        return this.glideValue;
    }

    public void setGlide(float glide) {
        float previous = this.glideValue;
        if (Float.compare(previous, glide) == 0) {
            return;
        }
        this.lastGlideCharge = previous;
        if (this.player.worldObj.isRemote) {
            this.glideValue = glide;
            return;
        }
        this.glideValue = glide;
        PlayerSyncHelper.sync(this.player, this);
        if (this.getGlide() == 0.0f) {
            this.lastGlideCharge = 0.0f;
        } else {
            this.player.addStat((StatBase)LegendGear2.achievementFlight, 1);
        }
    }

    public float getMana() {
        return this.fractionalMana + (float)this.manaWhole;
    }

    public static float availableMana(EntityPlayer player) {
        return 20.0f - PlayerStarstatsExtension.get(player).getMana();
    }

    public void setMana(float amount) {
        int intAmount = (int)Math.floor(amount);
        float fracAmount = amount - (float)intAmount;
        if (this.manaWhole == intAmount && Float.compare(this.fractionalMana, fracAmount) == 0) {
            return;
        }
        this.manaWhole = intAmount;
        this.fractionalMana = fracAmount;
        if (this.player.worldObj != null && !this.player.worldObj.isRemote) {
            PlayerSyncHelper.sync(this.player, this);
        }
    }

    private void applyLocalMana(float amount) {
        int intAmount = (int)Math.floor(amount);
        this.manaWhole = intAmount;
        this.fractionalMana = amount - (float)intAmount;
    }

    @Override
    public String rf$getSyncKey() {
        return EXT_PROP_NAME;
    }

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setInteger("ManaWhole", this.manaWhole);
        tag.setFloat("ManaFraction", this.fractionalMana);
        tag.setFloat("Glide", this.glideValue);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        this.manaWhole = tag.getInteger("ManaWhole");
        this.fractionalMana = tag.getFloat("ManaFraction");
        this.glideValue = tag.getFloat("Glide");
    }
}

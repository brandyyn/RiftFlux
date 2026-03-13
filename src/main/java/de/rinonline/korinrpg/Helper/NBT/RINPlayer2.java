/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IExtendedEntityProperties
 */
package de.rinonline.korinrpg.Helper.NBT;

import de.rinonline.korinrpg.ConfigurationMoD2;
import de.rinonline.korinrpg.EnchRegistry;
import de.rinonline.korinrpg.Helper.Network.SuperPacketDispatcher;
import de.rinonline.korinrpg.Helper.Network.SyncNewPlayerPropsMessage;
import com.voidsrift.riftflux.avatar.glider.GliderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class RINPlayer2
implements IExtendedEntityProperties {
    public static final String RINS_PROP_NAME = "RINSprinting";
    private final EntityPlayer player;
    private int timer;
    private int log;
    private int renderstage = 0;
    private float rendertime = 0.0f;
    private double sprintime;
    private double retime;
    private double MaxSprintingtime = 0.0;
    private double Overchargetime = 0.0;
    private double StaminaRegen = 0.0;
    private boolean isOvercharged;

    public double getStaminaRegen() {
        return this.StaminaRegen;
    }

    public double getOverchargetime() {
        return this.Overchargetime;
    }

    public RINPlayer2(EntityPlayer player) {
        this.player = player;
    }

    public static final void register(EntityPlayer player) {
        player.registerExtendedProperties(RINS_PROP_NAME, (IExtendedEntityProperties)new RINPlayer2(player));
    }

    public static final RINPlayer2 get(EntityPlayer player) {
        return (RINPlayer2)player.getExtendedProperties(RINS_PROP_NAME);
    }

    public void copy(RINPlayer2 props) {
        if (props == null) {
            return;
        }
        this.setSprintime(props.getSprintime());
        this.setOvercharged(props.isOvercharged());
        this.setRetime(props.getRetime());
        this.MaxSprintingtime = props.MaxSprintingtime;
        this.Overchargetime = props.Overchargetime;
        this.StaminaRegen = props.StaminaRegen;
        this.rendertime = props.rendertime;
    }

    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound properties = new NBTTagCompound();
        properties.setDouble("sprintime", this.sprintime);
        properties.setDouble("retime", this.retime);
        properties.setBoolean("isOvercharged", this.isOvercharged);
        properties.setDouble("Overchargetime", this.Overchargetime);
        properties.setDouble("MaxSprintingtime", this.MaxSprintingtime);
        properties.setDouble("StaminaRegen", this.StaminaRegen);
        compound.setTag(RINS_PROP_NAME, (NBTBase)properties);
    }

    public void loadNBTData(NBTTagCompound compound) {
        if (compound == null) {
            return;
        }
        NBTTagCompound properties = (NBTTagCompound)compound.getTag(RINS_PROP_NAME);
        if (properties == null) {
            return;
        }
        this.sprintime = properties.getDouble("sprintime");
        this.retime = properties.getDouble("retime");
        this.isOvercharged = properties.getBoolean("isOvercharged");
        this.Overchargetime = properties.getDouble("Overchargetime");
        this.MaxSprintingtime = properties.getDouble("MaxSprintingtime");
        this.StaminaRegen = properties.getDouble("StaminaRegen");
        if (this.rendertime < 0.0f) {
            this.rendertime = 0.0f;
        }
    }

    public void init(Entity entity, World world) {
        this.sprinttime(0);
        this.setOvercharged(false);
    }

    private void sprinttime(int i) {
    }

    public void onUpdate() {
        if (!this.player.capabilities.isCreativeMode && this.updateTimer()) {
            if (!this.player.worldObj.isRemote) {
                if (this.log == 1) {
                    this.log = 2;
                }
                if (!this.isOvercharged()) {
                    this.MaxSprintingtime = ConfigurationMoD2.MaxSprintime;
                    this.Overchargetime = ConfigurationMoD2.OverchargeSprintime;
                    this.StaminaRegen = ConfigurationMoD2.rechargeSprintime;
                    if (this.player.getEquipmentInSlot(1) != null && this.player.getEquipmentInSlot(1).isItemEnchanted()) {
                        this.MaxSprintingtime += (double)EnchantmentHelper.getEnchantmentLevel((int)EnchRegistry.MaxStamina.effectId, (ItemStack)this.player.getEquipmentInSlot(1)) * ConfigurationMoD2.EnchantmentMaxStamina;
                        this.Overchargetime -= (double)EnchantmentHelper.getEnchantmentLevel((int)EnchRegistry.Overloadreduction.effectId, (ItemStack)this.player.getEquipmentInSlot(1)) * ConfigurationMoD2.EnchantmentOverloadreduction;
                        this.StaminaRegen += (double)EnchantmentHelper.getEnchantmentLevel((int)EnchRegistry.StaminaRegen.effectId, (ItemStack)this.player.getEquipmentInSlot(1)) * ConfigurationMoD2.EnchantmentStaminaRegen;
                    }
                    if (ConfigurationMoD2.EnablePotionEffects) {
                        int u;
                        String[] arrOfStr;
                        String str = ConfigurationMoD2.PotionEffectregenerationspeedID;
                        for (String a : arrOfStr = str.split(",")) {
                            u = Integer.parseInt(a);
                            if (!this.player.isPotionActive(u)) continue;
                            this.StaminaRegen += this.StaminaRegen * ConfigurationMoD2.PotionEffectregenerationspeed;
                        }
                        str = ConfigurationMoD2.PotionEffectreduceregenID;
                        for (String a : arrOfStr = str.split(",")) {
                            u = Integer.parseInt(a);
                            if (!this.player.isPotionActive(u)) continue;
                            this.StaminaRegen -= this.StaminaRegen * ConfigurationMoD2.PotionEffectreduceregen;
                        }
                        str = ConfigurationMoD2.PotionEffectreducemaxtimeID;
                        for (String a : arrOfStr = str.split(",")) {
                            u = Integer.parseInt(a);
                            if (!this.player.isPotionActive(u)) continue;
                            this.MaxSprintingtime -= this.MaxSprintingtime * ConfigurationMoD2.PotionEffectreducemaxtime;
                        }
                        str = ConfigurationMoD2.PotionEffectexpandmaxtimeID;
                        for (String a : arrOfStr = str.split(",")) {
                            u = Integer.parseInt(a);
                            if (!this.player.isPotionActive(u)) continue;
                            this.MaxSprintingtime += this.MaxSprintingtime * ConfigurationMoD2.PotionEffectexpandmaxtime;
                        }
                    }
                    boolean gliding = false;
                    boolean hovering = false;
                    String gliderName = this.player.getDisplayName();
                    if (gliderName != null
                            && GliderState.isPlayerGliding(gliderName)
                            && !this.player.onGround
                            && !this.player.isInWater()) {
                        gliding = true;
                        hovering = GliderState.isPlayerHovering(gliderName);
                    }

                    if (this.player.isSprinting()) {
                        if (this.sprintime >= this.MaxSprintingtime) {
                            this.sprintime = this.MaxSprintingtime;
                            this.player.setSprinting(false);
                            this.isOvercharged = true;
                            this.retime = this.Overchargetime;
                        } else {
                            this.sprintime += 0.1;
                        }
                    } else if (gliding && hovering) {
                        if (this.sprintime >= this.MaxSprintingtime) {
                            this.sprintime = this.MaxSprintingtime;
                            this.player.setSprinting(false);
                            this.isOvercharged = true;
                            this.retime = this.Overchargetime;
                        } else {
                            this.sprintime += 0.1;
                        }
                    } else if (!gliding) {
                        this.recharge();
                    }
                } else {
                    if (this.retime == 0.0) {
                        this.isOvercharged = false;
                        this.retime = 0.0;
                        this.sprintime = 0.0;
                    } else {
                        this.player.setSprinting(false);
                    }
                    boolean gliding = false;
                    String gliderName = this.player.getDisplayName();
                    if (gliderName != null
                            && GliderState.isPlayerGliding(gliderName)
                            && !this.player.onGround
                            && !this.player.isInWater()) {
                        gliding = true;
                    }
                    if (!gliding) {
                        this.recharge();
                    }
                }
                SuperPacketDispatcher.sendTo(new SyncNewPlayerPropsMessage(this.player), (EntityPlayerMP)this.player);
            } else {
                if (this.isOvercharged()) {
                    KeyBinding.setKeyBindState((int)Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode(), (boolean)false);
                }
                if (this.player.isSprinting() || this.sprintime != 0.0) {
                    float maxAlpha = (float)(ConfigurationMoD2.transparency / 100.0);
                    if (this.rendertime <= maxAlpha) {
                        this.rendertime += 0.1f;
                        if (this.rendertime > maxAlpha) {
                            this.rendertime = maxAlpha;
                        }
                    }
                } else if (this.retime == 0.0 && this.sprintime == 0.0 && this.rendertime >= 0.0f) {
                    this.rendertime -= 0.05f;
                    if (this.rendertime < 0.0f) {
                        this.rendertime = 0.0f;
                    }
                }
            }
        }
    }

    public boolean isOvercharged() {
        return this.isOvercharged;
    }

    private void recharge() {
        this.retime -= 0.1;
        this.sprintime -= 0.1;
        if (this.sprintime < 0.0) {
            this.sprintime = 0.0;
        }
        if (this.retime < 0.0) {
            this.retime = 0.0;
        }
        SuperPacketDispatcher.sendTo(new SyncNewPlayerPropsMessage(this.player), (EntityPlayerMP)this.player);
    }

    private boolean updateTimer() {
        ++this.timer;
        if (this.timer >= 2) {
            this.timer = 0;
            return true;
        }
        return false;
    }

    public double getSprintime() {
        return this.sprintime;
    }

    public void setSprintime(double sprintime) {
        this.sprintime = sprintime;
    }

    public double getRetime() {
        return this.retime;
    }

    public void setRetime(double retime) {
        this.retime = retime;
    }

    public void setOvercharged(boolean isOvercharged) {
        this.isOvercharged = isOvercharged;
    }

    public float getRendertime() {
        return this.rendertime;
    }

    public void onAttack() {
        if (this.player.isSprinting()) {
            this.log = 1;
        }
    }

    public double getMaxSprintingtime() {
        return this.MaxSprintingtime;
    }

    public void subtracttamina(int amount) {
        this.sprintime += (double)(amount / 10);
        if (this.sprintime >= this.MaxSprintingtime) {
            this.sprintime = this.MaxSprintingtime;
            this.player.setSprinting(false);
            this.retime = this.Overchargetime;
            this.isOvercharged = true;
            SuperPacketDispatcher.sendTo(new SyncNewPlayerPropsMessage(this.player), (EntityPlayerMP)this.player);
        }
    }

    public void restoreStamina(double amount) {
        if (amount <= 0.0) {
            return;
        }
        this.sprintime = Math.max(0.0, this.sprintime - amount);
        if (this.retime > 0.0) {
            this.retime = Math.max(0.0, this.retime - amount);
        }
        if (this.retime == 0.0 && this.sprintime == 0.0) {
            this.isOvercharged = false;
        }
        if (this.player instanceof EntityPlayerMP) {
            SuperPacketDispatcher.sendTo(new SyncNewPlayerPropsMessage(this.player), (EntityPlayerMP)this.player);
        }
    }

    public void refillStamina() {
        this.sprintime = 0.0;
        this.retime = 0.0;
        this.Overchargetime = 0.0;
        this.isOvercharged = false;
        SuperPacketDispatcher.sendTo(new SyncNewPlayerPropsMessage(this.player), (EntityPlayerMP)this.player);
    }
}

/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IExtendedEntityProperties
 */
package zelda;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import zelda.Config;

public class ExtendedPlayerProperties
implements IExtendedEntityProperties {
    public static final String EXT_PROP_NAME = "ExtendedPlayer";
    private final EntityPlayer player;
    private double hearts;
    private boolean fresh;

    public ExtendedPlayerProperties(EntityPlayer player) {
        this.player = player;
        this.hearts = Config.STARTING_HEARTS;
        this.fresh = true;
    }

    public static final void register(EntityPlayer player) {
        player.registerExtendedProperties(EXT_PROP_NAME, (IExtendedEntityProperties)new ExtendedPlayerProperties(player));
    }

    public static final ExtendedPlayerProperties get(EntityPlayer player) {
        return (ExtendedPlayerProperties)player.getExtendedProperties(EXT_PROP_NAME);
    }

    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound props = new NBTTagCompound();
        props.setDouble("Hearts", this.hearts);
        props.setBoolean("Fresh", this.fresh);
        compound.setTag(EXT_PROP_NAME, (NBTBase)props);
    }

    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound props = (NBTTagCompound)compound.getTag(EXT_PROP_NAME);
        this.hearts = props.getDouble("Hearts");
        this.setBaseHeartsMax();
        this.fresh = props.getBoolean("Fresh");
    }

    public void init(Entity entity, World world) {
    }

    public void addHeart() {
        double amount = 4.0;
        try {
            amount = this.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getModifier(this.player.getPersistentID()).getAmount() + 4.0;
        }
        catch (Exception e) {
            // empty catch block
        }
        AttributeModifier moreHealth = new AttributeModifier(this.player.getPersistentID(), "HealthBoost", amount, 0);
        IAttributeInstance attributeInstance = this.player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        attributeInstance.removeModifier(moreHealth);
        attributeInstance.applyModifier(moreHealth);
        this.hearts += 1.0;
        this.player.setHealth((float)(this.hearts * 4.0));
    }

    public void setBaseHearts(double hearts) {
        double amount = hearts * 4.0;
        IAttributeInstance attributeInstance = this.player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        attributeInstance.setBaseValue(amount);
        if (this.fresh) {
            this.fresh = false;
        }
    }

    public void setBaseHeartsMax() {
        this.setBaseHearts(this.hearts);
        this.player.setHealth((float)(this.hearts * 4.0));
    }

    public double getMaxHearts() {
        return this.hearts;
    }

    public boolean isFresh() {
        return this.fresh;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IExtendedEntityProperties
 */
package inurosen.healaltar.common.entity;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.sync.IPlayerSyncData;
import com.voidsrift.riftflux.net.sync.PlayerSyncHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer
implements IExtendedEntityProperties, IPlayerSyncData {
    public static final String EXT_PROP_NAME = "HealingAltar";
    private final EntityPlayer player;
    private float soulHearts;

    public ExtendedPlayer(EntityPlayer player) {
        this.player = player;
    }

    public static final void register(EntityPlayer player) {
        player.registerExtendedProperties(EXT_PROP_NAME, (IExtendedEntityProperties)new ExtendedPlayer(player));
    }

    public static final ExtendedPlayer get(EntityPlayer player) {
        return (ExtendedPlayer)player.getExtendedProperties(EXT_PROP_NAME);
    }

    public final void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound properties = new NBTTagCompound();
        properties.setFloat("soulHearts", this.soulHearts);
        compound.setTag(EXT_PROP_NAME, (NBTBase)properties);
    }

    public final void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound properties = (NBTTagCompound)compound.getTag(EXT_PROP_NAME);
        if (properties == null) {
            this.soulHearts = 0.0f;
            return;
        }
        this.soulHearts = Math.max(0.0f, Math.min(properties.getFloat("soulHearts"), this.getMaxSoulHearts()));
    }

    public void init(Entity entity, World world) {
    }

    public final float getSoulHearts() {
        float current = this.soulHearts;
        float clamped = Math.max(0.0f, Math.min(current, this.getMaxSoulHearts()));
        if (current != clamped) {
            this.soulHearts = clamped;
        }
        return clamped;
    }

    public final void setSoulHearts(float amount) {
        float clamped = Math.max(0.0f, Math.min(amount, this.getMaxSoulHearts()));
        if (this.soulHearts == clamped) {
            return;
        }
        this.soulHearts = clamped;
        if (this.player != null && this.player.worldObj != null && !this.player.worldObj.isRemote) {
            PlayerSyncHelper.sync(this.player, this);
        }
    }

    private float getMaxSoulHearts() {
        if (this.player == null || this.player.getEntityAttribute(SharedMonsterAttributes.maxHealth) == null) {
            return 20.0f;
        }
        double max = this.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
        if (Double.isNaN(max) || max <= 0.0) {
            return 20.0f;
        }
        return (float)max;
    }

    @Override
    public String rf$getSyncKey() {
        return EXT_PROP_NAME;
    }

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setFloat("SoulHearts", this.soulHearts);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        this.soulHearts = Math.max(0.0f, Math.min(tag.getFloat("SoulHearts"), this.getMaxSoulHearts()));
    }
}

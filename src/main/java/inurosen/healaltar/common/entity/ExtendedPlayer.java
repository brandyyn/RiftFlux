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
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer
implements IExtendedEntityProperties {
    public static final String EXT_PROP_NAME = "HealingAltar";
    private final EntityPlayer player;
    public static final int DEFAULT_HEARTS_WATCHER = 22;

    public ExtendedPlayer(EntityPlayer player) {
        this.player = player;
        this.player.getDataWatcher().addObject(ExtendedPlayer.getHeartsWatcherId(), (Object)Float.valueOf(0.0f));
    }

    public static final void register(EntityPlayer player) {
        player.registerExtendedProperties(EXT_PROP_NAME, (IExtendedEntityProperties)new ExtendedPlayer(player));
    }

    public static final ExtendedPlayer get(EntityPlayer player) {
        return (ExtendedPlayer)player.getExtendedProperties(EXT_PROP_NAME);
    }

    public final void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound properties = new NBTTagCompound();
        properties.setFloat("soulHearts", this.player.getDataWatcher().getWatchableObjectFloat(ExtendedPlayer.getHeartsWatcherId()));
        compound.setTag(EXT_PROP_NAME, (NBTBase)properties);
    }

    public final void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound properties = (NBTTagCompound)compound.getTag(EXT_PROP_NAME);
        if (properties == null) {
            this.setSoulHearts(0.0f);
            return;
        }
        this.setSoulHearts(properties.getFloat("soulHearts"));
    }

    public void init(Entity entity, World world) {
    }

    public final float getSoulHearts() {
        float current = this.player.getDataWatcher().getWatchableObjectFloat(ExtendedPlayer.getHeartsWatcherId());
        float clamped = Math.max(0.0f, Math.min(current, this.getMaxSoulHearts()));
        if (current != clamped) {
            this.player.getDataWatcher().updateObject(ExtendedPlayer.getHeartsWatcherId(), (Object)Float.valueOf(clamped));
        }
        return clamped;
    }

    public final void setSoulHearts(float amount) {
        float clamped = Math.max(0.0f, Math.min(amount, this.getMaxSoulHearts()));
        this.player.getDataWatcher().updateObject(ExtendedPlayer.getHeartsWatcherId(), (Object)Float.valueOf(clamped));
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

    private static int getHeartsWatcherId() {
        int id = ModConfig.healAltarSoulHeartsDatawatcherId;
        if (ModConfig.isValidPlayerDatawatcherId(id)) {
            return id;
        }
        return DEFAULT_HEARTS_WATCHER;
    }
}

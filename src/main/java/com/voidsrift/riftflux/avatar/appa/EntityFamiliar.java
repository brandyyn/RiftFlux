package com.voidsrift.riftflux.avatar.appa;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import java.util.Random;

import com.voidsrift.riftflux.ModConfig;

public abstract class EntityFamiliar extends EntityCreature implements EntityChatListener {
    public String owner = "";
    public int mood = 0;
    private int moodrandom = 0;
    private long moodrandomtime = 0L;

    public EntityFamiliar(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(19, "");
    }

    public void setOwner(EntityPlayer owner) {
        if (owner == null) {
            return;
        }
        if (EntityFamiliar.getFamiliarByOwner(owner) == null) {
            this.owner = owner.getDisplayName();
            this.sendNameUpdate();
            this.sendHealthUpdate();
        } else {
            owner.addChatComponentMessage((IChatComponent) new ChatComponentText("You already have a familiar, dumbass..."));
        }
    }

    public void setName(String name) {
        this.dataWatcher.updateObject(19, name);
        this.sendNameUpdate();
    }

    public String getName() {
        return this.dataWatcher.getWatchableObjectString(19);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (this.owner != null) {
            this.sendDeathUpdate();
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (!(this.worldObj.isRemote || this.riddenByEntity != null && this.riddenByEntity != player)) {
            if (player.getHeldItem() != null && player.getHeldItem().getItem() == this.getTameItem()) {
                --player.getHeldItem().stackSize;
                Random r = this.getRNG();
                for (int var3 = 0; var3 < 7; ++var3) {
                    double vx = r.nextGaussian() * 202.0;
                    double vy = r.nextGaussian() * 202.0;
                    double vz = r.nextGaussian() * 202.0;
                    this.worldObj.spawnParticle("heart",
                            this.posX + (double) (r.nextFloat() * this.width * 2.0f) - (double) this.width,
                            this.posY + 0.5 + (double) (r.nextFloat() * this.height),
                            this.posZ + (double) (r.nextFloat() * this.width * 2.0f) - (double) this.width,
                            vx, vy, vz);
                }
                this.setOwner(player);
            } else {
                boolean hasOwner = this.owner != null && !this.owner.isEmpty();
                if (ModConfig.appaRequireTameToRide && !hasOwner) {
                    return true;
                }
                if (ModConfig.appaRestrictRideToOwner && hasOwner) {
                    String playerName = player.getDisplayName();
                    if (playerName == null || !playerName.equals(this.owner)) {
                        return true;
                    }
                }
                player.mountEntity(this);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (System.currentTimeMillis() - this.moodrandomtime > 1000L) {
            this.moodrandomtime = System.currentTimeMillis();
            this.moodrandom = this.rand.nextInt(40) - 20;
        }
        this.updateMood();
    }

    public void updateMood() {
        if ((int) this.getHealth() < 20) {
            this.mood = 3;
            return;
        }
        int moodval = (int) this.getHealth();
        if ((moodval += this.moodrandom) < 50) {
            this.mood = 2;
            return;
        }
        if (moodval < 100) {
            this.mood = 0;
            return;
        }
        if (moodval < 2000) {
            this.mood = 1;
        }
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    public void sendNameUpdate() {
        // No-op: data watcher already syncs name to clients.
    }

    public void sendDeathUpdate() {
        // No-op: UI hooks from the original mod are not ported.
    }

    public void sendHealthUpdate() {
        // No-op.
    }

    public void sendMoodUpdate() {
        // No-op.
    }

    public abstract Item getTameItem();

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setString("Name", this.dataWatcher.getWatchableObjectString(19));
        tag.setString("Owner", this.owner);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.getString("Name") != null) {
            this.dataWatcher.updateObject(19, tag.getString("Name"));
        }
        this.owner = tag.getString("Owner");
    }

    public static EntityFamiliar getFamiliarByOwner(EntityPlayer owner) {
        if (owner == null || owner.worldObj == null) {
            return null;
        }
        String ownerStr = owner.getDisplayName();
        for (Object o : owner.worldObj.getLoadedEntityList()) {
            if (!(o instanceof EntityFamiliar)) {
                continue;
            }
            EntityFamiliar e = (EntityFamiliar) o;
            if (!ownerStr.equals(e.owner)) {
                continue;
            }
            return e;
        }
        return null;
    }

    public static String getMoodName(int mood) {
        switch (mood) {
            case 0:
                return "Neutral";
            case 1:
                return "Happy";
            case 2:
                return "Unhappy";
            case 3:
                return "Scared";
            default:
                return "";
        }
    }

    public static int getMoodColor(int mood) {
        switch (mood) {
            case 0:
                return 0x00FFFF;
            case 1:
                return 0x00FF00;
            case 2:
                return 0xFF6600;
            case 3:
                return 0xFF0000;
            default:
                return 0xFFFFFF;
        }
    }
}

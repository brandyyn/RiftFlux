package com.voidsrift.riftflux.chester;

import com.voidsrift.riftflux.riftflux;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EntityChester extends EntityTameable {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_SHADOW = 2;

    private static final int WATCHER_TYPE = 20;
    private static final int WATCHER_OPEN = 24;

    private ChesterInventory inventory;

    public EntityChester(World world) {
        super(world);
        setSize(0.5F, 1.0F);
        tasks.addTask(1, new EntityAIFollowOwner(this, 1.0D, 4.0F, 2.0F));
        tasks.addTask(7, new EntityAIWander((EntityCreature) this, 1.0D));
        setupInventory();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(WATCHER_TYPE, 0);
        dataWatcher.addObject(WATCHER_OPEN, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.4D);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public int getTalkInterval() {
        return 1;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public EntityAgeable createChild(EntityAgeable mate) {
        return null;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (!isOwner(player)) {
            return false;
        }

        setOpen(true);
        if (!worldObj.isRemote) {
            worldObj.playSoundEffect(posX, posY, posZ, "chester:chesteropen", 1.0F, 1.0F);
            player.openGui(
                    riftflux.instance,
                    ChesterContent.GUI_ID,
                    worldObj,
                    getEntityId(),
                    0,
                    0
            );
        }
        return true;
    }

    @Override
    protected String getLivingSound() {
        return "chester:chesterbreathe";
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack != null && stack.getItem() == ChesterContent.eyeBone;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!(source.getEntity() instanceof EntityPlayer) || !isOwner((EntityPlayer) source.getEntity())) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (inventory == null || worldObj.isRemote) {
            return;
        }
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack != null) {
                entityDropItem(stack, 0.0F);
                inventory.setInventorySlotContents(slot, null);
            }
        }
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightning) {
        if (!worldObj.isRemote && getChesterType() != TYPE_SHADOW) {
            dataWatcher.updateObject(WATCHER_TYPE, TYPE_SHADOW);
        }
        extinguish();
    }

    public ChesterInventory getInventory() {
        return inventory;
    }

    public int getInventorySize() {
        return 27;
    }

    public int getInventoryRows() {
        return 3;
    }

    public int getChesterType() {
        return dataWatcher.getWatchableObjectInt(WATCHER_TYPE);
    }

    @Override
    public String getCommandSenderName() {
        if (hasCustomNameTag()) {
            return getCustomNameTag();
        }
        return StatCollector.translateToLocal(
                getChesterType() == TYPE_SHADOW
                        ? "entity.riftflux.ShadowChester.name"
                        : "entity.riftflux.Chester.name"
        );
    }

    public boolean isOpen() {
        return dataWatcher.getWatchableObjectByte(WATCHER_OPEN) != 0;
    }

    public void setOpen(boolean open) {
        dataWatcher.updateObject(WATCHER_OPEN, (byte) (open ? 1 : 0));
    }

    public boolean canPlayerUseInventory(EntityPlayer player) {
        return isEntityAlive()
                && isOwner(player)
                && getDistanceSqToEntity(player) <= 64.0D;
    }

    private boolean isOwner(EntityPlayer player) {
        return isTamed() && player != null && getOwner() == player;
    }

    private void setupInventory() {
        ChesterInventory oldInventory = inventory;
        inventory = new ChesterInventory("container.chester", getInventorySize());
        if (oldInventory != null) {
            int copySlots = Math.min(oldInventory.getSizeInventory(), inventory.getSizeInventory());
            for (int slot = 0; slot < copySlots; slot++) {
                ItemStack stack = oldInventory.getStackInSlot(slot);
                if (stack != null) {
                    inventory.setInventorySlotContents(slot, stack.copy());
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("Type", getChesterType());
        NBTTagList items = new NBTTagList();
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) {
                continue;
            }
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte) slot);
            stack.writeToNBT(itemTag);
            items.appendTag(itemTag);
        }
        tag.setTag("Items", items);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        dataWatcher.updateObject(WATCHER_TYPE, tag.getInteger("Type"));
        setupInventory();

        NBTTagList items = tag.getTagList("Items", 10);
        for (int index = 0; index < items.tagCount(); index++) {
            NBTTagCompound itemTag = items.getCompoundTagAt(index);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot < inventory.getSizeInventory()) {
                inventory.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(itemTag));
            }
        }
    }
}

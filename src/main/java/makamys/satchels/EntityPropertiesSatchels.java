package makamys.satchels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.google.common.primitives.Ints;

import codechicken.lib.inventory.InventorySimple;
import codechicken.lib.inventory.InventoryUtils;
import makamys.satchels.inventory.ContainerSatchels;
import makamys.satchels.inventory.InventoryAggregate;
import makamys.satchels.inventory.InventorySimpleNotifying;
import makamys.satchels.item.ItemPouch;
import makamys.satchels.item.ItemSatchel;
import makamys.satchels.compat.BaublesCompat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class EntityPropertiesSatchels implements IExtendedEntityProperties {
    private static final String CARRIER_CONTENTS_TAG = "RiftFluxStoredItems";
    
    public static final int SATCHEL_MAX_SLOTS = 9;
    public static final int POUCH_MAX_SLOTS = 8;
    public static final int POUCH_INITIAL_SLOTS = 3;
    
    private static final int SLOT_SATCHEL = 0;
    private static final int SLOT_LEFT_POUCH = 1;
    private static final int SLOT_RIGHT_POUCH = 2;
    private boolean chesterContentsDirty = true;
    
    public static final Predicate<ItemStack> satchelsSlotPredicate =
            (stack) -> ConfigSatchels.backpackHelper == null || ConfigSatchels.backpackHelper.isAllowed(stack);
    
    public InventorySimpleNotifying equipment = new InventorySimpleNotifying(3, (stack) -> updateInventories(stack)) {
        public boolean isItemValidForSlot(int i, ItemStack itemstack) {
            return  (i == SLOT_SATCHEL && itemstack.getItem() instanceof ItemSatchel) ||
                    ((i == SLOT_LEFT_POUCH || i == SLOT_RIGHT_POUCH) && itemstack.getItem() instanceof ItemPouch);
        };
    };
    
    public InventorySimpleNotifying satchel =
            new InventorySimpleNotifying(SATCHEL_MAX_SLOTS, null, this::onSatchelContentsDirty);
    public InventorySimpleNotifying leftPouch =
            new InventorySimpleNotifying(POUCH_MAX_SLOTS, null, this::onLeftPouchContentsDirty);
    public InventorySimpleNotifying rightPouch =
            new InventorySimpleNotifying(POUCH_MAX_SLOTS, null, this::onRightPouchContentsDirty);
    public InventoryAggregate aggregate = new InventoryAggregate(satchel, leftPouch, rightPouch);
    
    public EntityPlayer player;

    private ItemStack lastSatchelStack;
    private ItemStack lastLeftPouchStack;
    private ItemStack lastRightPouchStack;
    private boolean satchelContentsBound;
    private boolean leftPouchContentsBound;
    private boolean rightPouchContentsBound;
    private boolean synchronizingCarrierContents;
    
    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound satchelsTag = new NBTTagCompound();
        
        if(!SatchelsUtils.isInventoryEmpty(satchel)) {
            satchelsTag.setTag("Satchel", InventoryUtils.writeItemStacksToTag(satchel.items));
        }
        if(!SatchelsUtils.isInventoryEmpty(leftPouch)) {
            satchelsTag.setTag("LeftPouch", InventoryUtils.writeItemStacksToTag(leftPouch.items));
        }
        if(!SatchelsUtils.isInventoryEmpty(rightPouch)) {
            satchelsTag.setTag("RightPouch", InventoryUtils.writeItemStacksToTag(rightPouch.items));
        }
        
        if(!satchelsTag.func_150296_c().isEmpty()) {
            compound.setTag("Satchels", satchelsTag);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if(compound.hasKey("Satchels")) {
            NBTTagCompound satchelsTag = compound.getCompoundTag("Satchels");
            if(satchelsTag.hasKey("Equipment")) {
                InventorySimple legacy = new InventorySimple(3, "container.satchelsLegacy");
                InventoryUtils.readItemStacksFromTag(legacy.items, satchelsTag.getTagList("Equipment", 10));
                migrateLegacyEquipment(legacy);
            }
            if(satchelsTag.hasKey("Satchel")) {
                SatchelsUtils.clearInventory(satchel);
                InventoryUtils.readItemStacksFromTag(satchel.items, satchelsTag.getTagList("Satchel", 10));
            }
            if(satchelsTag.hasKey("LeftPouch")) {
                SatchelsUtils.clearInventory(leftPouch);
                InventoryUtils.readItemStacksFromTag(leftPouch.items, satchelsTag.getTagList("LeftPouch", 10));
            }
            if(satchelsTag.hasKey("RightPouch")) {
                SatchelsUtils.clearInventory(rightPouch);
                InventoryUtils.readItemStacksFromTag(rightPouch.items, satchelsTag.getTagList("RightPouch", 10));
            }
            updateInventories(null);
        }
    }
    
    public void dropItems() {
        // Baubles handles dropping equipped items.
    }
    
    public void dropItems(IInventory inv) {
        for(int i = 0; i < inv.getSizeInventory(); i++) {
            dropStack(inv, i);
        }
    }
    
    public ItemStack equip(ItemStack stack) {
        if(player != null && stack != null) {
            String[] types = stack.getItem() instanceof ItemSatchel
                    ? BaublesCompat.getTypes(BaublesCompat.ITEM_SATCHEL, BaublesCompat.TYPE_SATCHEL)
                    : (stack.getItem() instanceof ItemPouch
                    ? BaublesCompat.getTypes(BaublesCompat.ITEM_POUCH, BaublesCompat.TYPE_POUCH)
                    : null);
            if(types != null && !player.worldObj.isRemote) {
                BaublesCompat.equipToFirstEmpty(player, stack, types);
            }
        }
        return stack;
    }

    @Override
    public void init(Entity entity, World world) {
        player = (EntityPlayer)entity;
    }
    
    public void updateInventories(ItemStack stack) {
        markChesterContentsDirty();
        if(stack != null) {
            player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "satchels:item.armor.equip_leather", 1f, 1f);
        }
        
        refreshEquipmentCache();
        ContainerSatchels container = ((ContainerSatchels)player.inventoryContainer);
        container.redoSlots();
    }
    
    private void dropStack(IInventory inv, int i) {
        if(inv.getStackInSlot(i) != null) {
            player.func_146097_a(inv.getStackInSlot(i), true, false);
            inv.setInventorySlotContents(i, null);
        }
    }
    
    public int getLeftPouchSlotCount() {
        return ItemPouch.getSlotCount(getLeftPouchStack());
    }
    
    public int getRightPouchSlotCount() {
        return ItemPouch.getSlotCount(getRightPouchStack());
    }
    
    public boolean hasSatchel() {
        return getSatchelSlotCount() > 0;
    }
    
    public boolean hasLeftPouch() {
        return getLeftPouchSlotCount() > 0;
    }
    
    public boolean hasRightPouch() {
        return getRightPouchSlotCount() > 0;
    }
    
    public int getSatchelSlotCount() {
        return getSatchelStack() != null ? SATCHEL_MAX_SLOTS : 0;
    }

    public ItemStack getSatchelStack() {
        return BaublesCompat.getBaubleStack(
                player,
                SatchelsItems.satchel,
                0,
                BaublesCompat.getTypes(BaublesCompat.ITEM_SATCHEL, BaublesCompat.TYPE_SATCHEL)
        );
    }

    public ItemStack getLeftPouchStack() {
        return BaublesCompat.getBaubleStack(
                player,
                SatchelsItems.pouch,
                0,
                BaublesCompat.getTypes(BaublesCompat.ITEM_POUCH, BaublesCompat.TYPE_POUCH)
        );
    }

    public ItemStack getRightPouchStack() {
        return BaublesCompat.getBaubleStack(
                player,
                SatchelsItems.pouch,
                1,
                BaublesCompat.getTypes(BaublesCompat.ITEM_POUCH, BaublesCompat.TYPE_POUCH)
        );
    }
    
    public static EntityPropertiesSatchels fromPlayer(EntityPlayer player) {
        return (EntityPropertiesSatchels)player.getExtendedProperties("satchels");
    }

    public boolean refreshEquipmentCache() {
        ItemStack satchelStack = getSatchelStack();
        ItemStack leftPouchStack = getLeftPouchStack();
        ItemStack rightPouchStack = getRightPouchStack();
        boolean satchelChanged = satchelStack != lastSatchelStack;
        boolean leftPouchChanged = leftPouchStack != lastLeftPouchStack;
        boolean rightPouchChanged = rightPouchStack != lastRightPouchStack;
        if (!satchelChanged && !leftPouchChanged && !rightPouchChanged) {
            return false;
        }

        synchronizingCarrierContents = true;
        try {
            if (satchelChanged) saveCarrierContents(lastSatchelStack, satchel);
            if (leftPouchChanged) saveCarrierContents(lastLeftPouchStack, leftPouch);
            if (rightPouchChanged) saveCarrierContents(lastRightPouchStack, rightPouch);

            if (satchelChanged) {
                satchelContentsBound = bindCarrierContents(satchelStack, lastSatchelStack, satchel, satchelContentsBound);
            }
            if (leftPouchChanged) {
                leftPouchContentsBound = bindCarrierContents(leftPouchStack, lastLeftPouchStack, leftPouch, leftPouchContentsBound);
            }
            if (rightPouchChanged) {
                rightPouchContentsBound = bindCarrierContents(rightPouchStack, lastRightPouchStack, rightPouch, rightPouchContentsBound);
            }

            lastSatchelStack = satchelStack;
            lastLeftPouchStack = leftPouchStack;
            lastRightPouchStack = rightPouchStack;
        } finally {
            synchronizingCarrierContents = false;
        }
        markChesterContentsDirty();
        return true;
    }

    private boolean bindCarrierContents(ItemStack currentStack, ItemStack previousStack,
            InventorySimpleNotifying inventory, boolean contentsBound) {
        if (currentStack == null) {
            if (previousStack != null) {
                SatchelsUtils.clearInventory(inventory);
                return true;
            }
            return contentsBound;
        }

        if (currentStack.hasTagCompound() && currentStack.getTagCompound().hasKey(CARRIER_CONTENTS_TAG, 9)) {
            SatchelsUtils.clearInventory(inventory);
            InventoryUtils.readItemStacksFromTag(inventory.items,
                    currentStack.getTagCompound().getTagList(CARRIER_CONTENTS_TAG, 10));
            return true;
        }

        if (!contentsBound && !SatchelsUtils.isInventoryEmpty(inventory)) {
            saveCarrierContents(currentStack, inventory);
            return true;
        }

        SatchelsUtils.clearInventory(inventory);
        return true;
    }

    private void saveCarrierContents(ItemStack carrier, InventorySimpleNotifying inventory) {
        if (carrier == null) {
            return;
        }
        if (SatchelsUtils.isInventoryEmpty(inventory)) {
            if (carrier.hasTagCompound()) {
                carrier.getTagCompound().removeTag(CARRIER_CONTENTS_TAG);
            }
            return;
        }
        carrier.setTagInfo(CARRIER_CONTENTS_TAG, InventoryUtils.writeItemStacksToTag(inventory.items));
    }

    private void onSatchelContentsDirty() {
        markChesterContentsDirty();
        if (!synchronizingCarrierContents) {
            saveCarrierContents(getSatchelStack(), satchel);
        }
    }

    private void onLeftPouchContentsDirty() {
        markChesterContentsDirty();
        if (!synchronizingCarrierContents) {
            saveCarrierContents(getLeftPouchStack(), leftPouch);
        }
    }

    private void onRightPouchContentsDirty() {
        markChesterContentsDirty();
        if (!synchronizingCarrierContents) {
            saveCarrierContents(getRightPouchStack(), rightPouch);
        }
    }

    public List<ItemStack> extractStoredContents(ItemStack carrier) {
        List<ItemStack> extracted = new ArrayList<ItemStack>();
        if (carrier == null) {
            return extracted;
        }

        InventorySimpleNotifying inventory = null;
        if (carrier == getSatchelStack()) {
            inventory = satchel;
        } else if (carrier == getLeftPouchStack()) {
            inventory = leftPouch;
        } else if (carrier == getRightPouchStack()) {
            inventory = rightPouch;
        }

        synchronizingCarrierContents = true;
        try {
            if (inventory != null) {
                for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
                    ItemStack stack = inventory.getStackInSlot(slot);
                    if (stack != null) {
                        extracted.add(stack);
                        inventory.setInventorySlotContents(slot, null);
                    }
                }
            }
            if (extracted.isEmpty() && carrier.hasTagCompound()
                    && carrier.getTagCompound().hasKey(CARRIER_CONTENTS_TAG, 9)) {
                net.minecraft.nbt.NBTTagList stored = carrier.getTagCompound().getTagList(CARRIER_CONTENTS_TAG, 10);
                for (int index = 0; index < stored.tagCount(); ++index) {
                    ItemStack stack = ItemStack.loadItemStackFromNBT(stored.getCompoundTagAt(index));
                    if (stack != null) {
                        extracted.add(stack);
                    }
                }
            }
            if (carrier.hasTagCompound()) {
                carrier.getTagCompound().removeTag(CARRIER_CONTENTS_TAG);
            }
        } finally {
            synchronizingCarrierContents = false;
        }
        markChesterContentsDirty();
        return extracted;
    }
    
    public boolean preAddItemStackToInventory(final ItemStack stack) {
        return addItemStackToInventory(stack, 0);
    }
    
    public boolean postAddItemStackToInventory(final ItemStack stack) {
        return addItemStackToInventory(stack, 1);
    }
    
    private boolean addItemStackToInventory(final ItemStack stack, int pass) {
        if(stack == null || stack.stackSize == 0 || stack.getItem() == null) return false;
        
        int originalSize = stack.stackSize;
        int[] slots = getEnabledSlots();
        if(pass == 0) {
            mergeIntoExistingStacks(slots, stack);
        } else {
            insertIntoEmptySlots(slots, stack);
        }
        return stack.stackSize != originalSize;
    }
    
    public int[] getEnabledSlots() {
        List<Integer> slots = new ArrayList<>();
        if(hasSatchel()) {
            for(int i = 0; i < satchel.getSizeInventory(); i++) {
                slots.add(aggregate.toGlobalIdx(satchel, i));
            }
        }
        if(hasLeftPouch()) {
            for(int i = 0; i < getLeftPouchSlotCount(); i++) {
                slots.add(aggregate.toGlobalIdx(leftPouch, i));
            }
        }
        if(hasRightPouch()) {
            for(int i = 0; i < getRightPouchSlotCount(); i++) {
                slots.add(aggregate.toGlobalIdx(rightPouch, i));
            }
        }
        return Ints.toArray(slots);
    }

    public void markChesterContentsDirty() {
        chesterContentsDirty = true;
    }

    public boolean consumeChesterContentsDirty() {
        boolean dirty = chesterContentsDirty;
        chesterContentsDirty = false;
        return dirty;
    }

    private void mergeIntoExistingStacks(int[] slots, ItemStack stack) {
        if(!satchelsSlotPredicate.test(stack)) {
            return;
        }
        for(int slot : slots) {
            if(stack.stackSize <= 0) {
                return;
            }
            ItemStack existing = aggregate.getStackInSlot(slot);
            if(existing == null || !canStacksMerge(existing, stack)) {
                continue;
            }
            int limit = Math.min(existing.getMaxStackSize(), aggregate.getInventoryStackLimit());
            int space = limit - existing.stackSize;
            if(space <= 0) {
                continue;
            }
            int toMove = Math.min(space, stack.stackSize);
            existing.stackSize += toMove;
            stack.stackSize -= toMove;
            aggregate.markDirty();
        }
    }

    private void insertIntoEmptySlots(int[] slots, ItemStack stack) {
        if(!satchelsSlotPredicate.test(stack)) {
            return;
        }
        for(int slot : slots) {
            if(stack.stackSize <= 0) {
                return;
            }
            ItemStack existing = aggregate.getStackInSlot(slot);
            if(existing != null) {
                continue;
            }
            int limit = Math.min(stack.getMaxStackSize(), aggregate.getInventoryStackLimit());
            int toMove = Math.min(limit, stack.stackSize);
            ItemStack placed = stack.copy();
            placed.stackSize = toMove;
            aggregate.setInventorySlotContents(slot, placed);
            stack.stackSize -= toMove;
            aggregate.markDirty();
        }
    }

    private boolean canStacksMerge(ItemStack existing, ItemStack stack) {
        if(existing == null || stack == null) return false;
        if(!existing.isStackable()) return false;
        if(existing.stackSize >= existing.getMaxStackSize()) return false;
        if(existing.getItem() != stack.getItem()) return false;
        if(existing.getItemDamage() != stack.getItemDamage()) return false;
        return ItemStack.areItemStackTagsEqual(existing, stack);
    }

    private void migrateLegacyEquipment(InventorySimple legacy) {
        if(legacy == null) return;
        if(player == null) return;
        ItemStack satchelStack = legacy.getStackInSlot(SLOT_SATCHEL);
        if(satchelStack != null) {
            if(!BaublesCompat.equipToFirstEmpty(
                    player,
                    satchelStack,
                    BaublesCompat.getTypes(BaublesCompat.ITEM_SATCHEL, BaublesCompat.TYPE_SATCHEL))) {
                player.inventory.addItemStackToInventory(satchelStack);
            }
        }
        ItemStack leftPouchStack = legacy.getStackInSlot(SLOT_LEFT_POUCH);
        if(leftPouchStack != null) {
            if(!BaublesCompat.equipToFirstEmpty(
                    player,
                    leftPouchStack,
                    BaublesCompat.getTypes(BaublesCompat.ITEM_POUCH, BaublesCompat.TYPE_POUCH))) {
                player.inventory.addItemStackToInventory(leftPouchStack);
            }
        }
        ItemStack rightPouchStack = legacy.getStackInSlot(SLOT_RIGHT_POUCH);
        if(rightPouchStack != null) {
            if(!BaublesCompat.equipToFirstEmpty(
                    player,
                    rightPouchStack,
                    BaublesCompat.getTypes(BaublesCompat.ITEM_POUCH, BaublesCompat.TYPE_POUCH))) {
                player.inventory.addItemStackToInventory(rightPouchStack);
            }
        }
    }

}

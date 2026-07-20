package makamys.satchels.inventory;

import java.util.function.Consumer;

import codechicken.lib.inventory.InventorySimple;
import net.minecraft.item.ItemStack;

public class InventorySimpleNotifying extends InventorySimple {

    public Consumer<ItemStack> callback;
    public Runnable dirtyCallback;
    
    public InventorySimpleNotifying(int size, Consumer<ItemStack> callback) {
        this(size, callback, null);
    }

    public InventorySimpleNotifying(int size, Consumer<ItemStack> callback, Runnable dirtyCallback) {
        super(size);
        this.callback = callback;
        this.dirtyCallback = dirtyCallback;
    }
    
    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        boolean changed = false;
        if(!ItemStack.areItemStacksEqual(getStackInSlot(slot), stack)) {
            changed = true;
        }
        super.setInventorySlotContents(slot, stack);
        if(changed && callback != null) {
            callback.accept(stack);
        }
    };

    @Override
    public void markDirty() {
        super.markDirty();
        if (dirtyCallback != null) {
            dirtyCallback.run();
        }
    }
}

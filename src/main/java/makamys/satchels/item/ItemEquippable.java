package makamys.satchels.item;

import java.util.List;

import makamys.satchels.compat.BaublesCompat;
import makamys.satchels.gui.TooltippedItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class ItemEquippable extends Item implements TooltippedItem {

    @Override
    public void getTooltips(List<String> linesNormal, List<String> linesDetails, ItemTooltipEvent event) {
        // No extra hints; equip via Baubles slots.
    }
    
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(!world.isRemote && stack != null) {
            String type = stack.getItem() instanceof ItemSatchel ? BaublesCompat.TYPE_SATCHEL :
                    (stack.getItem() instanceof ItemPouch ? BaublesCompat.TYPE_POUCH : null);
            if(type != null && BaublesCompat.equipToFirstEmpty(player, stack, type)) {
                return stack;
            }
        }
        return stack;
    }

}

package com.voidsrift.riftflux.painting;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class PaintingTooltipHandler {

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (event == null || event.toolTip == null) {
            return;
        }

        ItemStack stack = event.itemStack;
        if (stack == null || stack.getItem() != Items.painting) {
            return;
        }

        EntityPlayer player = event.entityPlayer;
        String selectedName = PaintingSelectionData.getSelectedMotive(player);
        EnumChatFormatting nameColor;
        if (selectedName == null || selectedName.trim().isEmpty()) {
            selectedName = "Random";
            nameColor = EnumChatFormatting.YELLOW;
        } else {
            nameColor = EnumChatFormatting.AQUA;
        }

        event.toolTip.add(
                EnumChatFormatting.GRAY + "Selected Painting: "
                        + nameColor + selectedName
        );
        event.toolTip.add(EnumChatFormatting.DARK_GRAY + "Crouch + Right-click to select paintings");
    }
}

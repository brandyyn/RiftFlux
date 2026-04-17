package com.voidsrift.riftflux.pumpkinpastures;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class ItemColoredDisplayName extends Item {
    private final EnumChatFormatting color;

    public ItemColoredDisplayName(EnumChatFormatting color) {
        this.color = color == null ? EnumChatFormatting.WHITE : color;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return color + super.getItemStackDisplayName(stack);
    }
}

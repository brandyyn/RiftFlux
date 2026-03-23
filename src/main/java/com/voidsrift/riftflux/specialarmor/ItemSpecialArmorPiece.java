package com.voidsrift.riftflux.specialarmor;

import com.voidsrift.riftflux.Constants;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemSpecialArmorPiece extends ItemArmor {
    private final String armorTexture;
    private final String tooltipKey;

    public ItemSpecialArmorPiece(ArmorMaterial material, int renderIndex, int armorType, String unlocalizedName, String textureName, String armorTexture, String tooltipKey) {
        super(material, renderIndex, armorType);
        this.armorTexture = armorTexture;
        this.tooltipKey = tooltipKey;
        this.setUnlocalizedName(unlocalizedName);
        this.setTextureName(Constants.MODID + ":" + textureName);
        this.setCreativeTab(CreativeTabs.tabCombat);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return Constants.MODID + ":textures/armor/" + this.armorTexture;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        if (this.tooltipKey != null && !this.tooltipKey.isEmpty()) {
            list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal(this.tooltipKey));
        }
    }
}

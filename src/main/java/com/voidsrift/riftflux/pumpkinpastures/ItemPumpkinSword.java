package com.voidsrift.riftflux.pumpkinpastures;

import com.google.common.collect.Multimap;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.asgardshield.AsgardShieldLogic;
import com.voidsrift.riftflux.asgardshield.AsgardShieldState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemPumpkinSword extends ItemSword {
    public ItemPumpkinSword(ToolMaterial material) {
        super(material);
        setUnlocalizedName("pumpkin_sword");
        setTextureName("riftflux:pumpkinpastures/pumpkin_sword");
        setCreativeTab(CreativeTabs.tabCombat);
        setMaxDamage(Math.max(0, ModConfig.pumpkinPasturesEnderflameSwordDurability));
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        boolean result = super.hitEntity(stack, target, attacker);
        if (target != null) {
            target.setFire(8);
        }
        return result;
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap multimap = super.getItemAttributeModifiers();
        String attackName = SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName();
        multimap.removeAll(attackName);
        multimap.put(
                attackName,
                new AttributeModifier(field_111210_e, "Weapon modifier", ModConfig.pumpkinPasturesEnderflameSwordDamage, 0)
        );
        return multimap;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player == null || AsgardShieldState.isGuardBroken(player)) {
            return stack;
        }
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return EnumChatFormatting.GOLD + super.getItemStackDisplayName(stack);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
        list.add(EnumChatFormatting.GRAY + "Special Perk: " + AsgardShieldLogic.getPerkName(3));
        list.add(EnumChatFormatting.GRAY + "Weakness: " + AsgardShieldLogic.getPerkWeakness(3));
    }
}

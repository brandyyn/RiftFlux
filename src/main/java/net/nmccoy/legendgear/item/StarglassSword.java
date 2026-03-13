/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.item.ItemTool
 *  net.minecraft.potion.Potion
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 */
package net.nmccoy.legendgear.item;

import com.google.common.collect.Multimap;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.nmccoy.legendgear.CustomAttributes;
import net.nmccoy.legendgear.LegendGear2;

public class StarglassSword
extends ItemSword {
    public static final UUID starglassUUID = UUID.fromString("249c6e90-17a2-11e4-8c21-0800200c9a66");
    public static final AttributeModifier starglassCriticalDamage = new AttributeModifier(starglassUUID, "Starglass Critical Modifier", 13.0, 0);

    public StarglassSword() {
        super(LegendGear2.starglassMaterial);
        this.setUnlocalizedName("starglassSword");
        this.setCreativeTab(LegendGear2.legendgearTab);
        this.setTextureName("legendgear:starglassSwordAnim");
    }

    public Multimap getItemAttributeModifiers() {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put((Object)CustomAttributes.criticalBonusDamage.getAttributeUnlocalizedName(), (Object)starglassCriticalDamage);
        return multimap;
    }

    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        list.add(EnumChatFormatting.RED + "Breaks on critical hit");
    }

    public void addRecipes() {
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)this), (Object[])new Object[]{"I", "I", "S", Character.valueOf('S'), Items.stick, Character.valueOf('I'), LegendGear2.starglassIngot});
    }

    @SubscribeEvent
    public void handleAttack(LivingHurtEvent lhe) {
        Entity source = lhe.source.getEntity();
        if (source instanceof EntityPlayer && !lhe.entity.worldObj.isRemote) {
            EntityPlayer player = (EntityPlayer)source;
            ItemStack weapon = player.getHeldItem();
            double critBonus = player.getAttributeMap().getAttributeInstance(CustomAttributes.criticalBonusDamage).getAttributeValue();
            if (critBonus > 0.0) {
                boolean flag;
                boolean bl = flag = player.fallDistance > 0.0f && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null;
                if (flag) {
                    lhe.ammount = (float)((double)lhe.ammount + critBonus);
                }
                System.out.println(weapon.getItem());
                boolean isStarglass = false;
                if (weapon.getItem() instanceof ItemTool && ((ItemTool)weapon.getItem()).getToolMaterialName().equals(LegendGear2.starglassMaterial.name())) {
                    isStarglass = true;
                }
                if (weapon.getItem() instanceof ItemSword && ((ItemSword)weapon.getItem()).getToolMaterialName().equals(LegendGear2.starglassMaterial.name())) {
                    isStarglass = true;
                }
                if (isStarglass && flag) {
                    if (!player.capabilities.isCreativeMode) {
                        player.getHeldItem().damageItem(9999, (EntityLivingBase)player);
                    }
                    player.worldObj.playSoundAtEntity((Entity)player, "legendgear:eviscerate", 0.8f, 1.3f);
                    player.worldObj.playSoundAtEntity((Entity)player, "dig.glass", 0.8f, 0.75f);
                }
            }
        }
    }
}

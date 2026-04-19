/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.World
 */
package zairus.worldexplorer.archery.items;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import zairus.worldexplorer.archery.entity.EntityBoomerang;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.items.WEItem;

public class Boomerang
extends WEItem {
    @SideOnly(value=Side.CLIENT)
    protected IIcon iconGunpowder;
    @SideOnly(value=Side.CLIENT)
    protected IIcon iconSlime;
    @SideOnly(value=Side.CLIENT)
    protected IIcon iconGlowstone;
    @SideOnly(value=Side.CLIENT)
    protected IIcon iconRedstone;
    @SideOnly(value=Side.CLIENT)
    protected IIcon iconEnder;

    public Boomerang() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("boomerang");
        this.setTextureName("worldexplorer:boomerang");
        this.setCreativeTab(WorldExplorer.tabWorldExplorer);
        this.setFull3D();
        this.setMaxDamage(Math.max(0, ModConfig.riftExplorerBoomerangDurability));
    }

    @Override
    public void addImprovements() {
        this.itemImprovements.add(new WEItem.Improvement(Items.gunpowder, WEItem.ImprovementType.IMPACT, 0.05f));
        this.itemImprovements.add(new WEItem.Improvement(Items.slime_ball, WEItem.ImprovementType.ADHERENCE, 1.0f));
        this.itemImprovements.add(new WEItem.Improvement(Items.glowstone_dust, WEItem.ImprovementType.POWER, 0.05f));
        this.itemImprovements.add(new WEItem.Improvement(Item.getItemFromBlock((Block)Blocks.glowstone), WEItem.ImprovementType.POWER, 0.45f));
        this.itemImprovements.add(new WEItem.Improvement(Items.redstone, WEItem.ImprovementType.ENERGETIC, 0.25f));
        this.itemImprovements.add(new WEItem.Improvement(Item.getItemFromBlock((Block)Blocks.redstone_block), WEItem.ImprovementType.ENERGETIC, 2.25f));
        this.itemImprovements.add(new WEItem.Improvement(Items.ender_pearl, WEItem.ImprovementType.ENDER, 0.05f));
        this.itemImprovements.add(new WEItem.Improvement(Items.ender_eye, WEItem.ImprovementType.ENDER, 0.15f));
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        stack.damageItem(1, (EntityLivingBase)player);
        EntityBoomerang entityBoomerang = new EntityBoomerang(world, (EntityLivingBase)player, 1.0f);
        float redstoneModifier = 0.0f;
        float capacityModifier = 0.0f;
        float powerModifier = 0.0f;
        float punchModifier = 0.0f;
        if (stack.getTagCompound() != null) {
            if (stack.getTagCompound().hasKey(WEItem.ImprovementType.ADHERENCE.getKey())) {
                capacityModifier = stack.getTagCompound().getFloat(WEItem.ImprovementType.ADHERENCE.getKey());
            }
            if (stack.getTagCompound().hasKey(WEItem.ImprovementType.ENERGETIC.getKey())) {
                redstoneModifier = stack.getTagCompound().getFloat(WEItem.ImprovementType.ENERGETIC.getKey());
            }
            if (stack.getTagCompound().hasKey(WEItem.ImprovementType.POWER.getKey())) {
                powerModifier = stack.getTagCompound().getFloat(WEItem.ImprovementType.POWER.getKey());
            }
            if (stack.getTagCompound().hasKey(WEItem.ImprovementType.IMPACT.getKey())) {
                punchModifier = stack.getTagCompound().getFloat(WEItem.ImprovementType.IMPACT.getKey());
            }
        }
        entityBoomerang.setReach(16.0 + (double)redstoneModifier);
        entityBoomerang.setInventoryCapacity(1 + (int)capacityModifier);
        entityBoomerang.setDamage(entityBoomerang.getDamage() + (double)powerModifier);
        entityBoomerang.setKnockbackStrength(Math.round(punchModifier));
        if (EnchantmentHelper.getEnchantmentLevel((int)Enchantment.flame.effectId, (ItemStack)stack) > 0) {
            entityBoomerang.setFire(100);
        }
        if (!world.isRemote) {
            world.playSoundAtEntity((Entity)player, "random.bow", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + 0.5f);
        }
        entityBoomerang.setThrownBoomerang(player.inventory.getStackInSlot(player.inventory.currentItem).copy());
        if (player.capabilities.isCreativeMode) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        } else {
            --stack.stackSize;
        }
        if (!world.isRemote) {
            world.spawnEntityInWorld((Entity)entityBoomerang);
        }
        return stack;
    }

    public int getItemEnchantability() {
        return 1;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            if (tag.hasKey("Gunpowder Modifier")) {
                list.add("Punch modifier +" + tag.getFloat("Gunpowder Modifier"));
            }
            if (tag.hasKey("Slime Modifier")) {
                list.add("Capacity modifier +" + tag.getFloat("Slime Modifier"));
            }
            if (tag.hasKey("Glowstone Modifier")) {
                list.add("Power modifier +" + tag.getFloat("Glowstone Modifier"));
            }
            if (tag.hasKey("Redstone Modifier")) {
                list.add("Reach modifier +" + tag.getFloat("Redstone Modifier"));
            }
            if (tag.hasKey("Ender Modifier")) {
                list.add("Special modifier +" + tag.getFloat("Ender Modifier"));
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconregister) {
        this.itemIcon = iconregister.registerIcon(this.getIconString());
        this.iconGunpowder = iconregister.registerIcon(this.getIconString() + "_gunpowder");
        this.iconSlime = iconregister.registerIcon(this.getIconString() + "_slime");
        this.iconGlowstone = iconregister.registerIcon(this.getIconString() + "_glowstone");
        this.iconRedstone = iconregister.registerIcon(this.getIconString() + "_redstone");
        this.iconEnder = iconregister.registerIcon(this.getIconString() + "_ender");
    }

    public IIcon getModifierIconLayer(String layer) {
        IIcon iconLayer = null;
        switch (layer) {
            case "redstone": {
                iconLayer = this.iconRedstone;
                break;
            }
            case "glowstone": {
                iconLayer = this.iconGlowstone;
                break;
            }
            case "gunpowder": {
                iconLayer = this.iconGunpowder;
                break;
            }
            case "slime": {
                iconLayer = this.iconSlime;
                break;
            }
            case "ender": {
                iconLayer = this.iconEnder;
            }
        }
        return iconLayer;
    }
}

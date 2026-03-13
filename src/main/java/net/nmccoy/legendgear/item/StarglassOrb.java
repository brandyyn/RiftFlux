/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.entity.EntityThrownOrb;
import net.nmccoy.legendgear.item.ItemNucleus;
import net.nmccoy.legendgear.item.LGItem;

public class StarglassOrb
extends LGItem {
    private static final String prefix = "legendgear:item.orb.";
    private IIcon[] icons;

    public void addInformation(ItemStack stack, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        super.addInformation(stack, p_77624_2_, list, p_77624_4_);
        int damage = stack.getItemDamage();
        if (damage >= OrbTypes.values().length) {
            list.add(EnumChatFormatting.RED + "Something has gone horribly wrong.");
        } else {
            OrbTypes type = OrbTypes.values()[damage];
            if (type.craftIngredient) {
                list.add("Usable in crafting");
            }
            if (type.canBeThrown) {
                list.add("Throwable");
            }
        }
    }

    public int types() {
        return OrbTypes.values().length;
    }

    public boolean hasEffect(ItemStack par1ItemStack) {
        if (par1ItemStack.getItemDamage() == OrbTypes.twinkle.ordinal()) {
            return true;
        }
        if (par1ItemStack.getItemDamage() == OrbTypes.fire.ordinal()) {
            return true;
        }
        if (par1ItemStack.getItemDamage() == OrbTypes.ice.ordinal()) {
            return true;
        }
        return par1ItemStack.getItemDamage() == OrbTypes.zap.ordinal();
    }

    public StarglassOrb() {
        this.setUnlocalizedName("emptyOrb");
        this.tabs.add(CreativeTabs.tabMaterials);
        this.setTextureName("legendgear:emptyOrb");
        this.setMaxStackSize(16);
    }

    public IIcon getIconFromDamage(int damage) {
        return this.icons[damage];
    }

    public void registerIcons(IIconRegister register) {
        this.icons = new IIcon[OrbTypes.values().length];
        for (int i = 0; i < this.icons.length; ++i) {
            String name = "legendgear:" + OrbTypes.values()[i].toString() + "Orb";
            this.icons[i] = register.registerIcon(name);
        }
    }

    public void addRecipes() {
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, OrbTypes.water.ordinal()), (Object[])new Object[]{new ItemStack((Item)this, 1, 0), LegendGear2.dimensionalCatalyst, Items.water_bucket});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, OrbTypes.lava.ordinal()), (Object[])new Object[]{new ItemStack((Item)this, 1, 0), LegendGear2.dimensionalCatalyst, Items.lava_bucket});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, OrbTypes.blast.ordinal()), (Object[])new Object[]{new ItemStack((Item)this, 1, 0), LegendGear2.dimensionalCatalyst, Items.gunpowder, Items.flint});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, OrbTypes.twinkle.ordinal()), (Object[])new Object[]{new ItemStack((Item)this, 1, 0), LegendGear2.dimensionalCatalyst, new ItemStack((Item)LegendGear2.starDust, 1, 4)});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, OrbTypes.ice.ordinal()), (Object[])new Object[]{new ItemStack((Item)this, 1, 0), LegendGear2.dimensionalCatalyst, LegendGear2.dimensionalCatalyst, new ItemStack((Item)LegendGear2.starDust, 1, 4), new ItemStack((Item)LegendGear2.elementNucleus, 1, ItemNucleus.NucleusType.ICE.ordinal())});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, OrbTypes.fire.ordinal()), (Object[])new Object[]{new ItemStack((Item)this, 1, 0), LegendGear2.dimensionalCatalyst, LegendGear2.dimensionalCatalyst, new ItemStack((Item)LegendGear2.starDust, 1, 4), new ItemStack((Item)LegendGear2.elementNucleus, 1, ItemNucleus.NucleusType.FIRE.ordinal())});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, OrbTypes.zap.ordinal()), (Object[])new Object[]{new ItemStack((Item)this, 1, 0), LegendGear2.dimensionalCatalyst, LegendGear2.dimensionalCatalyst, new ItemStack((Item)LegendGear2.starDust, 1, 4), new ItemStack((Item)LegendGear2.elementNucleus, 1, ItemNucleus.NucleusType.LIGHTNING.ordinal())});
    }

    public boolean getHasSubtypes() {
        return true;
    }

    public String getUnlocalizedName(ItemStack stack) {
        return "item.orb." + OrbTypes.values()[this.getDamage(stack)].toString();
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        for (int i = 0; i < OrbTypes.values().length; ++i) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    public ItemStack onItemRightClick(ItemStack stack, World p_77659_2_, EntityPlayer p_77659_3_) {
        if (OrbTypes.values()[this.getDamage(stack)].canBeThrown) {
            if (!p_77659_3_.capabilities.isCreativeMode) {
                --stack.stackSize;
            }
            p_77659_2_.playSoundAtEntity((Entity)p_77659_3_, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
            if (!p_77659_2_.isRemote) {
                EntityThrownOrb orb = new EntityThrownOrb(p_77659_2_, (EntityLivingBase)p_77659_3_);
                orb.orbIndex = this.getDamage(stack);
                p_77659_2_.spawnEntityInWorld((Entity)orb);
            }
        }
        return stack;
    }

    public static enum OrbTypes {
        empty(false, true),
        water(true),
        lava(true),
        blast(true),
        twinkle(true, true),
        fire(true, true),
        ice(true, true),
        zap(true, true);

        private final boolean canBeThrown;
        private final boolean craftIngredient;
        private String additionalInfo;

        private OrbTypes() {
            this.canBeThrown = false;
            this.craftIngredient = false;
        }

        private OrbTypes(boolean throwable) {
            this.canBeThrown = throwable;
            this.craftIngredient = false;
        }

        private OrbTypes(boolean throwable, boolean ingredient) {
            this.canBeThrown = throwable;
            this.craftIngredient = ingredient;
        }
    }
}


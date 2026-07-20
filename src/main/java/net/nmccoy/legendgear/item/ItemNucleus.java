/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 */
package net.nmccoy.legendgear.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.item.LGItem;

public class ItemNucleus
extends LGItem {
    public static final int GEM_OFFSET = 1000;
    private IIcon baseIcon;
    private IIcon coreIcon;
    private IIcon centerIcon;
    private IIcon gemBase;
    private IIcon gemFill;
    private IIcon gemCover;

    public ItemNucleus() {
        this.setUnlocalizedName("nucleus");
        this.tabs.add(CreativeTabs.tabMaterials);
        this.setHasSubtypes(true);
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        int i;
        for (i = 0; i < NucleusType.values().length; ++i) {
            list.add(new ItemStack(item, 1, i));
        }
        for (i = 0; i < NucleusType.values().length; ++i) {
            list.add(new ItemStack(item, 1, i + 1000));
        }
    }

    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    public String getUnlocalizedName(ItemStack stack) {
        boolean gem;
        NucleusType type = NucleusType.convert(stack.getItemDamage());
        boolean bl = gem = stack.getItemDamage() >= 1000;
        if (gem) {
            return "item.gem" + type.toString();
        }
        return "item.nucleus" + type.toString();
    }

    public int getRenderPasses(int metadata) {
        boolean gem;
        boolean bl = gem = metadata >= 1000;
        if (gem) {
            return 3;
        }
        return 2;
    }

    public int getColorFromItemStack(ItemStack stack, int pass) {
        NucleusType type = NucleusType.convert(stack.getItemDamage());
        if (pass == 1) {
            return type.color2;
        }
        if (pass == 0) {
            return type.color1;
        }
        return 0xFFFFFF;
    }

    public IIcon getIcon(ItemStack stack, int pass) {
        boolean gem;
        boolean bl = gem = stack.getItemDamage() >= 1000;
        if (gem) {
            if (pass == 0) {
                return this.gemBase;
            }
            if (pass == 1) {
                return this.gemFill;
            }
            if (pass == 2) {
                return this.gemCover;
            }
        }
        if (pass == 0) {
            return this.baseIcon;
        }
        if (pass == 1) {
            return this.coreIcon;
        }
        return this.centerIcon;
    }

    public void addRecipes() {
        for (NucleusType type : NucleusType.values()) {
            if (type == NucleusType.LIGHTNING) {
                GameRegistry.addShapelessRecipe(
                        (ItemStack)new ItemStack((Item)this, 1, type.ordinal()),
                        (Object[])new Object[]{
                                LegendGear2.abstractionGel,
                                type.ingredient,
                                new ItemStack((Item)this, 1, NucleusType.SKY.ordinal())
                        }
                );
            } else {
                GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, type.ordinal()), (Object[])new Object[]{LegendGear2.abstractionGel, type.ingredient});
            }
            GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, type.ordinal() + 1000), (Object[])new Object[]{Items.diamond, new ItemStack((Item)this, 1, type.ordinal())});
        }
    }

    public static ItemStack gem(NucleusType type) {
        return new ItemStack((Item)LegendGear2.elementNucleus, 1, NucleusType.meta(type, true));
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister ireg) {
        this.baseIcon = ireg.registerIcon("legendgear:nucleusBase");
        this.coreIcon = ireg.registerIcon("legendgear:nucleusCore");
        this.centerIcon = ireg.registerIcon("legendgear:nucleusCenter");
        this.gemBase = ireg.registerIcon("legendgear:gemSolidBase");
        this.gemFill = ireg.registerIcon("legendgear:gemTranspFill");
        this.gemCover = ireg.registerIcon("legendgear:gemOverlay");
    }

    public static enum NucleusType {
        FIRE(0xFF6600, 0xFFDD00, Items.lava_bucket),
        ICE(0x88BBFF, 0xCCEEFF, Blocks.ice),
        LIGHTNING(0x22FFEE, 0xFFFFB4, LegendGear2.fulgurite),
        CUT(0xEEEEEE, 0xBBBBCC, Items.shears),
        SKY(26367, 0x22DDFF, new ItemStack((Item)LegendGear2.azurite, 1, 1)),
        SUN(0xFFBB00, 0xFFFFAA, new ItemStack((Block)Blocks.double_plant, 1, 0)),
        NAVIGATE(0x888888, 0xFF5555, Items.compass),
        DARK(17, 0x330066, Blocks.obsidian),
        STAR(0xFF77FF, 0xFFFFDD, new ItemStack((Item)LegendGear2.starDust, 1, 1)),
        HEALTH(0xAA0000, 0xFF4444, Items.apple),
        WEAPON(0xD8D8D8, 9004839, Items.iron_sword),
        WEALTH(6550682, 45112, Items.emerald);

        public int color1;
        public int color2;
        public Object ingredient;

        private NucleusType(int color1, int color2, Object ingredient) {
            this.color1 = color1;
            this.color2 = color2;
            this.ingredient = ingredient;
        }

        public static NucleusType convert(int index) {
            if ((index %= 1000) >= NucleusType.values().length) {
                index = 0;
            }
            return NucleusType.values()[index];
        }

        public static int meta(NucleusType type, boolean gem) {
            int out = type.ordinal();
            if (gem) {
                out += 1000;
            }
            return out;
        }
    }
}

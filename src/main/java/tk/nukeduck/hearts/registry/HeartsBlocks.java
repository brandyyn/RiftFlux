/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.creativetab.CreativeTabs
 */
package tk.nukeduck.hearts.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import tk.nukeduck.hearts.HeartCrystal;
import tk.nukeduck.hearts.block.BlockHeartCrystal;
import tk.nukeduck.hearts.block.BlockLantern;
import tk.nukeduck.hearts.block.ItemBlock16;
import tk.nukeduck.hearts.item.ItemHeartCrystal;
import tk.nukeduck.hearts.item.ItemHeartLantern;

public class HeartsBlocks {
    public static BlockHeartCrystal crystal;
    public static BlockLantern lantern;

    public static final void init() {
        crystal = new BlockHeartCrystal();
        int miningLevel = 2;
        if (HeartCrystal.config != null) {
            miningLevel = Math.max(0, HeartCrystal.config.getMiningLevel());
        }
        crystal.setBlockName("heartCrystal").setHardness(1.5f).setResistance(0.5f).setHarvestLevel("pickaxe", miningLevel);
        crystal.setCreativeTab(CreativeTabs.tabMisc);
        GameRegistry.registerBlock((Block)crystal, ItemHeartCrystal.class, (String)"heart_crystal");
        lantern = new BlockLantern(Material.glass);
        lantern.setBlockName("heartLantern").setHardness(0.2f).setResistance(0.2f).setStepSound(Block.soundTypeGlass).setHarvestLevel("pickaxe", 0);
        lantern.setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock((Block)lantern, ItemHeartLantern.class, (String)"heart_lantern");
    }
}

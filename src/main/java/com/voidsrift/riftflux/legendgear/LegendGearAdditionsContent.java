package com.voidsrift.riftflux.legendgear;

import com.voidsrift.riftflux.util.LegacyRegistryAliasHelper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.MinecraftForge;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.item.StarglassSword;

import java.util.Locale;

public final class LegendGearAdditionsContent {
    private static final String[] LEGENDGEAR_NAMESPACES = {"legendgear", "legendgear2", "legendgearreturns"};
    private static boolean preInited;
    private static boolean initialized;
    private static boolean activated;

    public static Block starrySand;
    public static Block redStarrySand;
    public static Block lightningStruckRedSand;
    public static StarglassSword starglassSword;

    private LegendGearAdditionsContent() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        if (LegendGear2.starDust == null) {
            return;
        }

        registerBlocks();
        registerItems();
        addRecipes();

        MinecraftForge.EVENT_BUS.register(new LegendGearAdditionsEvents());
        MinecraftForge.EVENT_BUS.register(starglassSword);
        activated = true;
    }

    public static void init(FMLInitializationEvent event) {
        if (!activated || initialized) {
            return;
        }
        initialized = true;
    }

    public static Item resolveLegacyItemAlias(String fullName) {
        if (fullName == null) {
            return null;
        }
        String key = aliasKey(fullName);
        if ("phoenixfeather".equals(key)) {
            return LegendGear2.phoenixFeather;
        }
        if ("starglasssword".equals(key)) {
            return starglassSword;
        }
        Block block = resolveLegacyBlockAlias(fullName);
        return block == null ? null : Item.getItemFromBlock(block);
    }

    public static Block resolveLegacyBlockAlias(String fullName) {
        if (fullName == null) {
            return null;
        }
        String key = aliasKey(fullName);
        if ("starrysand".equals(key)) {
            return starrySand;
        }
        if ("redstarrysand".equals(key)) {
            return redStarrySand;
        }
        if ("lightningstruckredsand".equals(key)) {
            return lightningStruckRedSand;
        }
        return null;
    }

    public static void registerLegacyAliases() {
        if (!activated) {
            return;
        }
        for (String namespace : LEGENDGEAR_NAMESPACES) {
            registerItemAlias(LegendGear2.phoenixFeather, namespace + ":phoenix_feather");
            registerItemAlias(starglassSword, namespace + ":starglass_sword");
            registerBlockAlias(starrySand, namespace + ":starry_sand");
            registerBlockAlias(redStarrySand, namespace + ":red_starry_sand");
            registerBlockAlias(lightningStruckRedSand, namespace + ":lightning_struck_red_sand");
        }
    }

    private static void registerBlocks() {
        starrySand = registerBlock(new LegendGearFallingBlock("starry_sand", "starry_sand", 0.5F, 0.4F), "starry_sand");
        redStarrySand = registerBlock(new LegendGearFallingBlock("red_starry_sand", "red_starry_sand", 0.5F, 0.4F), "red_starry_sand");
        lightningStruckRedSand = registerBlock(new LightningStruckRedSandBlock(), "lightning_struck_red_sand");
    }

    private static void registerItems() {
        starglassSword = new StarglassSword();
        starglassSword.setUnlocalizedName("starglass_sword");
        GameRegistry.registerItem(starglassSword, "starglass_sword");
    }

    private static Block registerBlock(Block block, String name) {
        GameRegistry.registerBlock(block, name);
        return block;
    }

    private static void addRecipes() {
        ItemStack chargedDust = new ItemStack(LegendGear2.starDust, 1, 3);
        GameRegistry.addShapelessRecipe(new ItemStack(starrySand), new ItemStack(LegendGear2.starSandBlock));
        GameRegistry.addShapelessRecipe(new ItemStack(redStarrySand), new ItemStack(Blocks.sand, 1, 1), chargedDust);
        starglassSword.addRecipes();
    }

    private static String aliasKey(String fullName) {
        int split = fullName.indexOf(':');
        String path = split >= 0 ? fullName.substring(split + 1) : fullName;
        return path.toLowerCase(Locale.ROOT).replace("_", "").replace("-", "").replace(".", "");
    }

    private static void registerItemAlias(Item item, String... aliases) {
        if (item == null || aliases == null) {
            return;
        }
        for (String alias : aliases) {
            LegacyRegistryAliasHelper.registerItemAliases(item, alias, alias.toLowerCase(Locale.ROOT));
        }
    }

    private static void registerBlockAlias(Block block, String... aliases) {
        if (block == null || aliases == null) {
            return;
        }
        for (String alias : aliases) {
            LegacyRegistryAliasHelper.registerBlockAliases(block, alias, alias.toLowerCase(Locale.ROOT));
            Item item = Item.getItemFromBlock(block);
            if (item != null) {
                LegacyRegistryAliasHelper.registerItemAliases(item, alias, alias.toLowerCase(Locale.ROOT));
            }
        }
    }

    private static class LegendGearFallingBlock extends BlockFalling {
        LegendGearFallingBlock(String name, String texture, float hardness, float light) {
            super(Material.sand);
            setBlockName(name);
            setBlockTextureName("legendgear:" + texture);
            setHardness(hardness);
            setLightLevel(light);
            setStepSound(Block.soundTypeSand);
            setCreativeTab(LegendGear2.legendgearTab);
        }
    }

    private static final class LightningStruckRedSandBlock extends LegendGearFallingBlock {
        @SideOnly(Side.CLIENT)
        private IIcon topIcon;
        @SideOnly(Side.CLIENT)
        private IIcon sideIcon;

        private LightningStruckRedSandBlock() {
            super("lightning_struck_red_sand", "lightning_struck_red_sand", 0.5F, 0.0F);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void registerBlockIcons(IIconRegister register) {
            this.topIcon = register.registerIcon("legendgear:lightning_struck_red_sand");
            this.sideIcon = Blocks.sand.getIcon(2, 1);
            this.blockIcon = this.sideIcon != null ? this.sideIcon : this.topIcon;
        }

        @Override
        public IIcon getIcon(int side, int meta) {
            if (side == 1 && this.topIcon != null) {
                return this.topIcon;
            }
            if (this.sideIcon != null) {
                return this.sideIcon;
            }
            return Blocks.sand.getIcon(side, 1);
        }
    }
}

package zelda;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import zelda.blocks.ZBlocks;
import zelda.items.ZItems;
import zelda.proxy.ClientProxy;
import zelda.proxy.CommonProxy;

public class Core {
    public static final String MOD_ID = "zelda";
    public static final String NAME = "Zelda";
    public static final String VERSION = "0.2";
    public static CommonProxy proxy;
    public static final CreativeTabs tab = new CreativeTabZelda();

    public static void preInit(FMLPreInitializationEvent e) {
        Config.syncFromModConfig();
        if (!Config.HEARTS_ENABLED) {
            return;
        }
        ZItems.init();
        ZBlocks.init();
    }

    public static void init(FMLInitializationEvent e) {
        if (!Config.HEARTS_ENABLED) {
            return;
        }
        proxy = createProxy();
        proxy.registerClientStuff();
        FMLCommonHandler.instance().bus().register(new TickHandler());
        MinecraftForge.EVENT_BUS.register(new ZEventHandler());
        CraftingManager.getInstance().addRecipe(
                new ItemStack(ZItems.heartContainer),
                "xx",
                "xx",
                Character.valueOf('x'),
                ZItems.heartPiece
        );
        if (Config.HEARTPIECE_RARITY > 0) {
            ChestGenHooks.getInfo("dungeonChest").addItem(
                    new WeightedRandomChestContent(new ItemStack(ZItems.heartPiece), 1, 2, Config.HEARTPIECE_RARITY)
            );
        }
    }

    public static void postInit(FMLPostInitializationEvent e) {
    }

    private static CommonProxy createProxy() {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            return new ClientProxy();
        }
        return new CommonProxy();
    }
}

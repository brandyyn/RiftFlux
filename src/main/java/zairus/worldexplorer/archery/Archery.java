package zairus.worldexplorer.archery;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.riftexplorer.RecipeRiftExplorerDartInfusion;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import zairus.worldexplorer.archery.client.renderer.ArcheryRenderManager;
import zairus.worldexplorer.archery.entity.ArcheryEntityManager;
import zairus.worldexplorer.archery.entity.monster.ArcheryMonsterManager;
import zairus.worldexplorer.archery.items.SlingshotAmmoHelper;
import zairus.worldexplorer.archery.items.SpecialArrow;
import zairus.worldexplorer.archery.items.WEArcheryItems;
import zairus.worldexplorer.core.IWEAddonEntityManager;
import zairus.worldexplorer.core.IWEAddonMod;
import zairus.worldexplorer.core.IWEAddonMonsterManager;
import zairus.worldexplorer.core.IWEAddonRenderManager;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.items.WorldExplorerItems;
import zairus.worldexplorer.equipment.items.WEEquipmentItems;

public class Archery implements IWEAddonMod {
    public static Archery instance;

    public void preInit(FMLPreInitializationEvent event) {
        WorldExplorer.registerWEAddonMod(this);
        WEArcheryItems.init();
    }

    public void init(FMLInitializationEvent event) {
        WEArcheryItems.register();
        this.addRecipes();
    }

    private void addRecipes() {
        GameRegistry.addShapedRecipe(new ItemStack(WEArcheryItems.slingshot), "tst", " t ", 't', Items.stick, 's', Items.string);

        if (ModConfig.riftExplorerEnablePebbleRecipes) {
            GameRegistry.addShapelessRecipe(new ItemStack(WEArcheryItems.pebble, 4), Blocks.cobblestone);
            GameRegistry.addShapelessRecipe(new ItemStack(Blocks.cobblestone), WEArcheryItems.pebble, WEArcheryItems.pebble, WEArcheryItems.pebble, WEArcheryItems.pebble);
        }

        GameRegistry.addShapedRecipe(new ItemStack(WEArcheryItems.boomerang), "ppl", "slp", " sp", 'p', Blocks.planks, 'l', Items.leather, 's', Items.string);
        if (SpecialArrow.isArrowTypeEnabled(1)) {
            GameRegistry.addShapedRecipe(new ItemStack(WEArcheryItems.specialarrow, 1, 1), "  p", " s ", "f  ", 'p', WEArcheryItems.pebble, 's', new ItemStack(WEArcheryItems.specialarrow, 1, 0), 'f', Items.feather);
        }
        if (SpecialArrow.isArrowTypeEnabled(2)) {
            GameRegistry.addShapedRecipe(new ItemStack(WEArcheryItems.specialarrow, 1, 2), "  p", " s ", "f  ", 'p', Items.flint, 's', new ItemStack(WEArcheryItems.specialarrow, 1, 0), 'f', Items.feather);
        }
        if (SpecialArrow.isArrowTypeEnabled(3)) {
            GameRegistry.addShapedRecipe(new ItemStack(WEArcheryItems.specialarrow, 1, 3), "  p", " s ", "f  ", 'p', Items.iron_ingot, 's', new ItemStack(WEArcheryItems.specialarrow, 1, 0), 'f', Items.feather);
        }
        if (SpecialArrow.isArrowTypeEnabled(4)) {
            GameRegistry.addShapedRecipe(new ItemStack(WEArcheryItems.specialarrow, 1, 4), "  p", " s ", "f  ", 'p', Items.diamond, 's', new ItemStack(WEArcheryItems.specialarrow, 1, 0), 'f', Items.feather);
        }
        if (SpecialArrow.isArrowTypeEnabled(5)) {
            GameRegistry.addShapedRecipe(new ItemStack(WEArcheryItems.specialarrow, 1, 5), "  p", " s ", "f  ", 'p', Blocks.obsidian, 's', new ItemStack(WEArcheryItems.specialarrow, 1, 0), 'f', Items.feather);
        }
        GameRegistry.addShapedRecipe(new ItemStack(WEArcheryItems.blowpipe), " h ", "scs", "scs", 'h', new ItemStack(WEArcheryItems.specialarrow, 1, 0), 's', Items.string, 'c', Items.reeds);
        GameRegistry.addShapedRecipe(new ItemStack(WEArcheryItems.dart, 6, 0), " n ", " h ", " f ", 'n', WorldExplorerItems.needle, 'h', new ItemStack(WEArcheryItems.specialarrow, 1, 0), 'f', Items.feather);
        GameRegistry.addRecipe(new RecipeRiftExplorerDartInfusion());
        GameRegistry.addShapedRecipe(new ItemStack(WEArcheryItems.longbow_handle), "stt", "tl ", "stt", 's', Items.string, 't', Items.stick, 'l', Items.leather);
        GameRegistry.addShapedRecipe(new ItemStack(WEArcheryItems.longbow_string), " ss", " ss", " ss", 's', Items.string);
        GameRegistry.addShapedRecipe(new ItemStack(WEArcheryItems.longbow), "hs", 'h', WEArcheryItems.longbow_handle, 's', WEArcheryItems.longbow_string);

        // Crossbow and Wooden Handle are intentionally removed in RiftFlux.

        if (SpecialArrow.isArrowTypeEnabled(0)) {
            if (SlingshotAmmoHelper.isCobblestoneAllowed()) {
                GameRegistry.addShapelessRecipe(new ItemStack(WEArcheryItems.specialarrow, 2, 0), Blocks.cobblestone, Items.stick);
            } else {
                GameRegistry.addShapelessRecipe(new ItemStack(WEArcheryItems.specialarrow, 2, 0), Items.stick, Items.stick);
            }
        }
    }

    @Override
    public IWEAddonEntityManager getEntityManager() {
        return new ArcheryEntityManager();
    }

    @Override
    public IWEAddonMonsterManager getMonsterManager() {
        return new ArcheryMonsterManager();
    }

    @Override
    public IWEAddonRenderManager getRenderManager() {
        return new ArcheryRenderManager();
    }

    public static void onBowFOV(ItemStack stack, EntityPlayer player, int count, net.minecraft.item.Item itemInUse) {
        float f = 1.0f;
        if (player.capabilities.isFlying) {
            f *= 1.1f;
        }
        float speedOnGround = ((Float) ObfuscationReflectionHelper.getPrivateValue(EntityPlayer.class, player, new String[]{"speedOnGround", "speedOnGround"})).floatValue();
        int i = player.getItemInUseDuration();
        float f1 = (float) i / 20.0f;
        f1 = f1 > 1.0f ? 1.0f : (f1 *= f1);
        float fovModifierHand = ((Float) ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, new String[]{"fovModifierHand", "fovModifierHand"})).floatValue();
        fovModifierHand += ((f *= (player.capabilities.getWalkSpeed() * 1.0f / speedOnGround + 1.0f) / 2.0f) - fovModifierHand) * 0.5f;
        if (fovModifierHand > 1.5f) {
            fovModifierHand = 1.5f;
        }
        if (fovModifierHand < 0.1f) {
            fovModifierHand = 0.1f;
        }
        ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, Float.valueOf(fovModifierHand), new String[]{"fovModifierHand", "fovModifierHand"});
    }
}

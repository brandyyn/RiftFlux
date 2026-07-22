package com.voidsrift.riftflux.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.IRecipeFilter;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.NEIClientConfig;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.FMLLog;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import com.voidsrift.riftflux.placeablegunpowder.PlaceableGunpowderContent;
import com.voidsrift.riftflux.glowstonedust.GlowstoneDustContent;
import com.voidsrift.riftflux.gravestone.GravestoneTypeGrouping;
import com.voidsrift.riftflux.placeditem.PlacedItemContent;
import com.voidsrift.riftflux.terramine.TerrariaContent;
import com.voidsrift.riftflux.vortex.block.ModBlocks;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.legacy.LegendGear;
import gravestone.core.GSBlock;
import de.sanandrew.mods.claysoldiers.util.RegistryBlocks;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Optional.Interface(iface = "codechicken.nei.api.IConfigureNEI", modid = "NotEnoughItems")
public class NEIRiftFluxConfig implements IConfigureNEI {

    private static void addClaySoldiersStandardRecipeFilter() {
        API.addRecipeFilter(new IRecipeFilter.IRecipeFilterProvider() {
            @Override
            public IRecipeFilter getRecipeFilter() {
                return new IRecipeFilter() {
                    @Override
                    public boolean matches(IRecipeHandler handler, int recipeIndex) {
                        if (handler == null) {
                            return true;
                        }
                        String handlerClass = handler.getClass().getName();
                        if (!"codechicken.nei.recipe.ShapedRecipeHandler".equals(handlerClass)
                                && !"codechicken.nei.recipe.ShapelessRecipeHandler".equals(handlerClass)) {
                            return true;
                        }

                        PositionedStack out = handler.getResultStack(recipeIndex);
                        if (out == null || out.item == null) {
                            return true;
                        }
                        if (isClaySoldiersOutput(out.item)) {
                            return false;
                        }

                        // The shear-blade recombination recipe is the only Clay Soldiers recipe
                        // whose output is a vanilla item.
                        return out.item.getItem() != Items.shears
                                || !containsIngredient(handler.getIngredientStacks(recipeIndex), RegistryItems.shearBlade);
                    }
                };
            }
        });
    }

    private static boolean isClaySoldiersOutput(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        return item == RegistryItems.dollSoldier
                || item == RegistryItems.dollBrick
                || item == RegistryItems.disruptor
                || item == RegistryItems.disruptorHardened
                || item == RegistryItems.shearBlade
                || item == RegistryItems.statDisplay
                || item == RegistryItems.dollHorseMount
                || item == RegistryItems.dollTurtleMount
                || item == RegistryItems.dollBunnyMount
                || item == RegistryItems.dollGeckoMount
                || item == Item.getItemFromBlock(RegistryBlocks.clayNexus);
    }

    private static boolean containsIngredient(List<PositionedStack> ingredients, Item item) {
        if (ingredients == null || item == null) {
            return false;
        }
        for (PositionedStack positioned : ingredients) {
            if (positioned == null || positioned.items == null) {
                continue;
            }
            for (ItemStack stack : positioned.items) {
                if (stack != null && stack.getItem() == item) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public void loadConfig() {
        if (com.voidsrift.riftflux.ModConfig.enableClaySoldiersModule) {
            try {
                ClaySoldiersRecipeHandler handler = new ClaySoldiersRecipeHandler();
                API.registerRecipeHandler(handler);
                API.registerUsageHandler(handler);
                NEIClientConfig.handlerOrdering.put(handler.getHandlerId(), -100);
                NEIClientConfig.handlerOrdering.put("codechicken.nei.recipe.InformationHandler", 100);
                addClaySoldiersStandardRecipeFilter();
                FMLLog.info("[RiftFlux] NEI plugin loaded: Clay Soldiers handler registered.");
            } catch (Throwable t) {
                FMLLog.severe("[RiftFlux] Failed to register Clay Soldiers NEI handler: %s", t);
            }
        }
        if (PlacedItemContent.placedItemBlock != null) {
            try {
                API.hideItem(new ItemStack(PlacedItemContent.placedItemBlock));
            } catch (Throwable t) {
                FMLLog.severe("[RiftFlux] Failed to hide placed item block in NEI: %s", t);
            }
        }
        if (PlaceableGunpowderContent.gunpowderBlock != null) {
            try {
                API.hideItem(new ItemStack(PlaceableGunpowderContent.gunpowderBlock));
            } catch (Throwable t) {
                FMLLog.severe("[RiftFlux] Failed to hide placeable gunpowder block in NEI: %s", t);
            }
        }
        if (GlowstoneDustContent.glowstoneDustBlock != null) {
            try {
                API.hideItem(new ItemStack(GlowstoneDustContent.glowstoneDustBlock));
            } catch (Throwable t) {
                FMLLog.severe("[RiftFlux] Failed to hide placeable glowstone dust block in NEI: %s", t);
            }
        }
        // Hide hidden Glow Carpet block-id variants; the base item places these randomly.
        for (int i = 1; i < ModBlocks.glowCarpets.length; ++i) {
            if (ModBlocks.glowCarpets[i] != null) {
                try {
                    API.hideItem(new ItemStack(ModBlocks.glowCarpets[i]));
                } catch (Throwable t) {
                    FMLLog.severe("[RiftFlux] Failed to hide Glow Carpet variant %d in NEI: %s", i, t);
                }
            }
        }
        if (TerrariaContent.iceRodBlock != null) {
            try {
                API.hideItem(new ItemStack(TerrariaContent.iceRodBlock));
            } catch (Throwable t) {
                FMLLog.severe("[RiftFlux] Failed to hide temporary magic ice block in NEI: %s", t);
            }
        }
        if (LegendGear2.starPieceBlock != null) {
            try {
                API.hideItem(new ItemStack(LegendGear2.starPieceBlock));
            } catch (Throwable t) {
                FMLLog.severe("[RiftFlux] Failed to hide placed star piece block in NEI: %s", t);
            }
        }
        if (LegendGear2.infusedStarPieceBlock != null) {
            try {
                API.hideItem(new ItemStack(LegendGear2.infusedStarPieceBlock));
            } catch (Throwable t) {
                FMLLog.severe("[RiftFlux] Failed to hide infused placed star piece block in NEI: %s", t);
            }
        }
        if (LegendGear.blockPedestalTech != null) {
            try {
                API.hideItem(new ItemStack(LegendGear.blockPedestalTech));
            } catch (Throwable t) {
                FMLLog.severe("[RiftFlux] Failed to hide inserted sword pedestal block in NEI: %s", t);
            }
        }
        if (com.voidsrift.riftflux.ModConfig.enableGravestoneModule) {
            hideRedundantGravestoneVariants();
        }
    }

    private static void hideRedundantGravestoneVariants() {
        if (GSBlock.invisibleWall != null) {
            API.hideItem(new ItemStack(GSBlock.invisibleWall));
        }
        showOnePerGravestoneFamily(GSBlock.graveStone);
        showOnePerGravestoneFamily(GSBlock.memorial);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void showOnePerGravestoneFamily(net.minecraft.block.Block block) {
        if (block == null) {
            return;
        }
        Item item = Item.getItemFromBlock(block);
        List variants = new ArrayList();
        block.getSubBlocks(item, block.getCreativeTabToDisplayOn(), variants);
        Set<String> visibleFamilies = new HashSet<String>();
        List<ItemStack> visibleVariants = new ArrayList<ItemStack>();
        for (Object value : variants) {
            if (!(value instanceof ItemStack)) {
                continue;
            }
            ItemStack stack = (ItemStack)value;
            String family = GravestoneTypeGrouping.getFamily(stack);
            if (family != null && visibleFamilies.add(family)) {
                visibleVariants.add(stack);
            }
        }
        API.setItemListEntries(item, visibleVariants);
    }

    @Override
    public String getName() {
        return "RiftFlux NEI Plugin";
    }

    @Override
    public String getVersion() {
        return com.voidsrift.riftflux.Constants.VERSION;
    }
}

package com.voidsrift.riftflux.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.IRecipeFilter;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.NEIClientConfig;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.FMLLog;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

@Optional.Interface(iface = "codechicken.nei.api.IConfigureNEI", modid = "NotEnoughItems")
public class NEIRiftFluxConfig implements IConfigureNEI {

    private static void addClaySoldiersDuplicateRecipeFilter() {
        API.addRecipeFilter(new IRecipeFilter.IRecipeFilterProvider() {
            @Override
            public IRecipeFilter getRecipeFilter() {
                return new IRecipeFilter() {
                    private final Item claymanDoll = resolveClaymanDollItem();

                    @Override
                    public boolean matches(IRecipeHandler handler, int recipeIndex) {
                        if (handler == null || !"codechicken.nei.recipe.ShapelessRecipeHandler".equals(handler.getClass().getName())) {
                            return true;
                        }
                        if (claymanDoll == null) {
                            return true;
                        }

                        // Match output: 4x clayman doll
                        PositionedStack out = handler.getResultStack(recipeIndex);
                        if (out == null || out.item == null) {
                            return true;
                        }
                        ItemStack outStack = out.item;
                        if (outStack.getItem() != claymanDoll || outStack.stackSize != 4) {
                            return true;
                        }

                        // Match inputs: exactly 2 ingredients containing one soul sand and one clay block.
                        List<PositionedStack> in = handler.getIngredientStacks(recipeIndex);
                        if (in == null || in.size() != 2) {
                            return true;
                        }

                        boolean hasSoulSand = false;
                        boolean hasClay = false;
                        for (PositionedStack ps : in) {
                            if (ps == null || ps.items == null || ps.items.length == 0) {
                                continue;
                            }
                            for (ItemStack s : ps.items) {
                                if (s == null) continue;
                                if (s.getItem() == Item.getItemFromBlock(Blocks.soul_sand)) {
                                    hasSoulSand = true;
                                } else if (s.getItem() == Item.getItemFromBlock(Blocks.clay)) {
                                    hasClay = true;
                                }
                            }
                        }

                        return !(hasSoulSand && hasClay);
                    }
                };
            }
        });
    }

    private static Item resolveClaymanDollItem() {
        try {
            Item it = cpw.mods.fml.common.registry.GameRegistry.findItem("claysoldiers", "clayman_doll");
            if (it != null) return it;
        } catch (Throwable ignored) {}

        try {
            Class<?> c = Class.forName("de.sanandrew.mods.claysoldiers.util.RegistryItems");
            return (Item) c.getField("dollSoldier").get(null);
        } catch (Throwable ignored) {}
        return null;
    }

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public void loadConfig() {
        if (Loader.isModLoaded("claysoldiers")) {
            try {
                ClaySoldiersRecipeHandler handler = new ClaySoldiersRecipeHandler();
                API.registerRecipeHandler(handler);
                API.registerUsageHandler(handler);
                NEIClientConfig.handlerOrdering.put(handler.getHandlerId(), -100);
                NEIClientConfig.handlerOrdering.put("codechicken.nei.recipe.InformationHandler", 100);
                addClaySoldiersDuplicateRecipeFilter();
                FMLLog.info("[RiftFlux] NEI plugin loaded: Clay Soldiers handler registered.");
            } catch (Throwable t) {
                FMLLog.severe("[RiftFlux] Failed to register Clay Soldiers NEI handler: %s", t);
            }
        }
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

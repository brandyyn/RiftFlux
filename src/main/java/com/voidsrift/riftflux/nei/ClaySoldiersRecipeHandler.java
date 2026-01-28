package com.voidsrift.riftflux.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * NEI handler for Clay Soldiers
 *
 * Clay Soldiers contains several dynamic recipes (team/material dependent) which often don't show up in NEI.
 * This handler generates "display recipes" for NEI and uses reflection to avoid a hard dependency on Clay Soldiers.
 */
public class ClaySoldiersRecipeHandler extends TemplateRecipeHandler {

    private static final int SLOT_X = 25;
    private static final int SLOT_Y = 6;
    private static final int SLOT_SIZE = 18;

    private List<CachedRecipe> cachedAll;

    @Override
    public String getRecipeName() {
        return "Clay Soldiers";
    }

    @Override
    public String getOverlayIdentifier() {
        return "crafting";
    }

    @Override
    public String getGuiTexture() {
        return "textures/gui/container/crafting_table.png";
    }

    @Override
    public int recipiesPerPage() {
        return 2;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
    FMLLog.info("[RiftFlux] ClaySoldiersRecipeHandler.loadCraftingRecipes(outputId=%s)", outputId);

    if (outputId == null) {
        return;
    }

    if ("item".equals(outputId)) {
        if (results != null && results.length > 0 && results[0] instanceof ItemStack) {
            this.loadCraftingRecipes((ItemStack) results[0]);
        }
        return;
    }

    if (!"crafting".equals(outputId)) {
        return;
    }

    List<CachedRecipe> all = buildAllRecipes();
    FMLLog.info("[RiftFlux] Built %d Clay Soldiers NEI recipes.", all.size());
    this.arecipes.addAll(all);
}


    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (result == null) {
            return;
        }
        // For Clay Soldiers dolls, the "team" is stored in NBT and the stack can also carry
        // other upgrade NBT. We match by team name (not full NBT) so pressing R on a specific
        // colored doll shows the correct recipe even if it has extra tags.
        String wantTeam = getTeamNameForStack(result);
        Item dollSoldier = registryItem("dollSoldier");

        for (CachedRecipe r : buildAllRecipes()) {
            PositionedStack psOut = r.getResult();
            ItemStack out = psOut != null ? psOut.item : null;
            if (out == null) continue;

            // soldier dolls are dyed/team-colored via NBT, not item damage.
            if (dollSoldier != null && out.getItem() == dollSoldier && result.getItem() == dollSoldier) {
                String outTeam = getTeamNameForStack(out);
                if (wantTeam != null && outTeam != null && wantTeam.equals(outTeam)) {
                    this.arecipes.add(r);
                }
                continue;
            }

            // For mounts and other outputs, require an exact match including NBT so pressing R on
            // a specific variant doesn't show all variants.
            if (areStacksEquivalentStrict(out, result)) {
                this.arecipes.add(r);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (ingredient == null) {
            return;
        }
        for (CachedRecipe r : buildAllRecipes()) {
            for (PositionedStack ps : r.getIngredients()) {
                if (ps != null && ps.items != null) {
                    for (ItemStack is : ps.items) {
                        if (is != null && areStacksEquivalentLoose(is, ingredient)) {
                            this.arecipes.add(r);
                            break;
                        }
                    }
                }
            }
        }
    }

@Override
public void loadUsageRecipes(String inputId, Object... ingredients) {
    if (inputId == null) return;

    if ("item".equals(inputId)) {
        if (ingredients != null && ingredients.length > 0 && ingredients[0] instanceof ItemStack) {
            this.loadUsageRecipes((ItemStack) ingredients[0]);
        }
    }
}


    private static boolean areStacksEquivalentLoose(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        int da = a.getItemDamage();
        int db = b.getItemDamage();
        return da == db || da == OreDictionary.WILDCARD_VALUE || db == OreDictionary.WILDCARD_VALUE;
    }

    private static boolean areStacksEquivalentStrict(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        if (a.getItemDamage() != b.getItemDamage()) return false;
        if (a.hasTagCompound() != b.hasTagCompound()) return false;
        if (!a.hasTagCompound()) return true;
        return net.minecraft.item.ItemStack.areItemStackTagsEqual(a, b);
    }

    private List<CachedRecipe> buildAllRecipes() {
        if (this.cachedAll != null) {
            return this.cachedAll;
        }
        if (!isClaySoldiersPresent()) {
            this.cachedAll = Collections.emptyList();
            return this.cachedAll;
        }

        List<CachedRecipe> out = new ArrayList<CachedRecipe>();

        add(out, buildShapeless(
                setTeamForItem("clay", stack(registryItem("dollSoldier"), 4, 0)),
                stack(Blocks.clay, 1, 0),
                stack(Blocks.soul_sand, 1, 0)
        ));

        add(out, buildShapeless(stack(registryItem("shearBlade"), 2, 0),
                stack(Items.shears, 1, 0)));

        add(out, buildShapeless(stack(Items.shears, 1, 0),
                stack(registryItem("shearBlade"), 1, OreDictionary.WILDCARD_VALUE),
                stack(registryItem("shearBlade"), 1, OreDictionary.WILDCARD_VALUE)));

        add(out, buildShaped(stack(registryItem("disruptor"), 1, 0),
                new ItemStack[]{
                        stack(Blocks.soul_sand, 1, 0), stack(Items.stick, 1, 0), stack(Blocks.soul_sand, 1, 0),
                        stack(Blocks.soul_sand, 1, 0), stack(Items.redstone, 1, 0), stack(Blocks.soul_sand, 1, 0),
                        null, null, null
                }));

        add(out, buildShaped(stack(registryItem("disruptorHardened"), 1, 0),
                new ItemStack[]{
                        stack(Blocks.hardened_clay, 1, OreDictionary.WILDCARD_VALUE), stack(Items.stick, 1, 0), stack(Blocks.hardened_clay, 1, OreDictionary.WILDCARD_VALUE),
                        stack(Blocks.hardened_clay, 1, OreDictionary.WILDCARD_VALUE), stack(Items.redstone, 1, 0), stack(Blocks.hardened_clay, 1, OreDictionary.WILDCARD_VALUE),
                        null, null, null
                }));

        add(out, buildShaped(stack(registryItem("statDisplay"), 1, 0),
                new ItemStack[]{
                        stack(Blocks.soul_sand, 1, 0), stack(Blocks.glass, 1, 0), stack(Blocks.soul_sand, 1, 0),
                        stack(Blocks.soul_sand, 1, 0), stack(Items.redstone, 1, 0), stack(Blocks.soul_sand, 1, 0),
                        null, null, null
                }));

        add(out, buildShaped(stack(registryItem("statDisplay"), 1, 0),
                new ItemStack[]{
                        stack(Blocks.soul_sand, 1, 0), stack(Blocks.stained_glass, 1, OreDictionary.WILDCARD_VALUE), stack(Blocks.soul_sand, 1, 0),
                        stack(Blocks.soul_sand, 1, 0), stack(Items.redstone, 1, 0), stack(Blocks.soul_sand, 1, 0),
                        null, null, null
                }));

        add(out, buildShaped(stack(registryBlock("clayNexus"), 1, 0),
                new ItemStack[]{
                        stack(Items.clay_ball, 1, 0), stack(Items.diamond, 1, 0), stack(Items.clay_ball, 1, 0),
                        stack(Blocks.clay, 1, 0), stack(Blocks.obsidian, 1, 0), stack(Blocks.clay, 1, 0),
                        stack(Blocks.obsidian, 1, 0), stack(Blocks.obsidian, 1, 0), stack(Blocks.obsidian, 1, 0)
                }));

        addSoldierDyeRecipes(out);
        addBunnyRecipes(out);
        addGeckoRecipes(out);
        addHorseRecipes(out);
        addTurtleRecipes(out);

        this.cachedAll = out;
        FMLLog.info("[RiftFlux] Built %d Clay Soldiers NEI recipes.", out.size());
        return this.cachedAll;
    }

    private void addSoldierDyeRecipes(List<CachedRecipe> out) {
        Item dollSoldier = registryItem("dollSoldier");
        if (dollSoldier == null) return;

        ItemStack baseDoll = stack(dollSoldier, 1, OreDictionary.WILDCARD_VALUE);

        for (ItemStack mat : expandDollMaterials()) {
            String teamName = teamNameForMaterial(mat);
            if (teamName == null) continue;

            ItemStack result = setTeamForItem(teamName, stack(dollSoldier, 1, 0));
            if (result == null) continue;

            add(out, buildShapeless(result, baseDoll, mat));
        }
    }

    private void addBunnyRecipes(List<CachedRecipe> out) {
    Item bunny = registryItem("dollBunnyMount");
    if (bunny == null) return;

    Object recipe = newInstance("de.sanandrew.mods.claysoldiers.crafting.RecipeBunnies");
    if (recipe == null) return;

    for (int meta = 0; meta < 16; meta++) {
        ItemStack wool = stack(Blocks.wool, 1, meta);

        ItemStack[] grid = new ItemStack[]{
                wool, stack(Blocks.soul_sand, 1, 0), wool,
                null, null, null,
                null, null, null
        };
        if (!simulateMatches(recipe, grid)) continue;

        ItemStack res = simulateCraftingResult(recipe, grid);
        if (res == null || res.getItem() == null) continue;
        add(out, buildShaped(res, grid));
    }
}

    private void addGeckoRecipes(List<CachedRecipe> out) {
    Item gecko = registryItem("dollGeckoMount");
    if (gecko == null) return;

    Object recipe = newInstance("de.sanandrew.mods.claysoldiers.crafting.RecipeGeckos");
    if (recipe == null) return;

    for (int metaA = 0; metaA <= 5; metaA++) {
        for (int metaB = 0; metaB <= 5; metaB++) {
            ItemStack sapA = stack(Blocks.sapling, 1, metaA);
            ItemStack sapB = stack(Blocks.sapling, 1, metaB);

            ItemStack[] grid = new ItemStack[]{
                    sapA, null, null,
                    stack(Blocks.soul_sand, 1, 0), null, null,
                    sapB, null, null
            };
            if (!simulateMatches(recipe, grid)) continue;

            ItemStack res = simulateCraftingResult(recipe, grid);
            if (res == null || res.getItem() == null) continue;
            add(out, buildShaped(res, grid));
        }
    }
}

    private void addHorseRecipes(List<CachedRecipe> out) {
    Item horse = registryItem("dollHorseMount");
    if (horse == null) return;

    Object recipe = newInstance("de.sanandrew.mods.claysoldiers.crafting.RecipeHorses");
    if (recipe == null) return;

    List<Object> types = getEnumValues("de.sanandrew.mods.claysoldiers.util.mount.EnumHorseType", "VALUES");
    if (types.isEmpty()) return;

    for (Object type : types) {
        ItemStack typeItem = getEnumTypeItemStack(type);
        if (typeItem == null) continue;

        ItemStack[] gridNormal = new ItemStack[]{
                typeItem, stack(Blocks.soul_sand, 1, 0), typeItem,
                typeItem, null, typeItem,
                null, null, null
        };
        if (simulateMatches(recipe, gridNormal)) {
            ItemStack res = simulateCraftingResult(recipe, gridNormal);
            if (res != null && res.getItem() != null) {
                add(out, buildShaped(res, gridNormal));
            }
        }

        ItemStack[] gridPegasus = new ItemStack[]{
                null, stack(Items.feather, 1, 0), null,
                typeItem, stack(Blocks.soul_sand, 1, 0), typeItem,
                typeItem, null, typeItem
        };
        if (simulateMatches(recipe, gridPegasus)) {
            ItemStack res = simulateCraftingResult(recipe, gridPegasus);
            if (res != null && res.getItem() != null) {
                add(out, buildShaped(res, gridPegasus));
            }
        }
    }
}

    private void addTurtleRecipes(List<CachedRecipe> out) {
    Item turtle = registryItem("dollTurtleMount");
    if (turtle == null) return;

    Object recipe = newInstance("de.sanandrew.mods.claysoldiers.crafting.RecipeTurtles");
    if (recipe == null) return;

    List<Object> types = getEnumValues("de.sanandrew.mods.claysoldiers.util.mount.EnumTurtleType", "VALUES");
    if (types.isEmpty()) return;

    for (Object type : types) {
        ItemStack typeItem = getEnumTypeItemStack(type);
        if (typeItem == null) continue;

        // Pattern (2-row): [empty, type, type] / [soul, soul, type]
        ItemStack[] grid = new ItemStack[]{
                null, typeItem, typeItem,
                stack(Blocks.soul_sand, 1, 0), stack(Blocks.soul_sand, 1, 0), typeItem,
                null, null, null
        };
        if (!simulateMatches(recipe, grid)) continue;

        ItemStack res = simulateCraftingResult(recipe, grid);
        if (res == null || res.getItem() == null) continue;
        add(out, buildShaped(res, grid));
    }
}

    private void add(List<CachedRecipe> list, CachedRecipe r) {
        if (r != null && r.getResult() != null && r.getResult().item != null) {
            list.add(r);
        }
    }

    // -----------------------------
// Recipe simulation helpers (no hard dependency on Clay Soldiers types)
// -----------------------------

private static net.minecraft.inventory.InventoryCrafting makeInv(ItemStack[] grid9) {
    net.minecraft.inventory.InventoryCrafting inv = new net.minecraft.inventory.InventoryCrafting(new DummyContainer(), 3, 3);
    for (int i = 0; i < 9 && i < grid9.length; i++) {
        inv.setInventorySlotContents(i, grid9[i]);
    }
    return inv;
}

private static ItemStack simulateCraftingResult(Object recipe, ItemStack[] grid9) {
    if (recipe == null) return null;
    try {
        Method mGet = recipe.getClass().getMethod("func_77572_b", net.minecraft.inventory.InventoryCrafting.class);
        return (ItemStack) mGet.invoke(recipe, makeInv(grid9));
    } catch (Throwable ignored) {
    }
    try {
        // Deobf name in dev
        Method mGet = recipe.getClass().getMethod("getCraftingResult", net.minecraft.inventory.InventoryCrafting.class);
        return (ItemStack) mGet.invoke(recipe, makeInv(grid9));
    } catch (Throwable ignored) {
    }
    return null;
}

private static boolean simulateMatches(Object recipe, ItemStack[] grid9) {
    if (recipe == null) return false;
    try {
        Method m = recipe.getClass().getMethod("func_77569_a", net.minecraft.inventory.InventoryCrafting.class, net.minecraft.world.World.class);
        return (Boolean) m.invoke(recipe, makeInv(grid9), null);
    } catch (Throwable ignored) {
    }
    try {
        Method m = recipe.getClass().getMethod("matches", net.minecraft.inventory.InventoryCrafting.class, net.minecraft.world.World.class);
        return (Boolean) m.invoke(recipe, makeInv(grid9), null);
    } catch (Throwable ignored) {
    }
    return false;
}

private static class DummyContainer extends net.minecraft.inventory.Container {
    @Override
    public boolean canInteractWith(net.minecraft.entity.player.EntityPlayer player) {
        return false;
    }
}


private static Object newInstance(String className) {
    try {
        Class<?> c = Class.forName(className);
        return c.newInstance();
    } catch (Throwable ignored) {
        return null;
    }
}

// -----------------------------
    // Reflection helpers
    // -----------------------------

    private static boolean isClaySoldiersPresent() {
        try {
            Class.forName("de.sanandrew.mods.claysoldiers.util.RegistryItems");
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private static Item registryItem(String fieldName) {
        try {
            Class<?> c = Class.forName("de.sanandrew.mods.claysoldiers.util.RegistryItems");
            Field f = c.getField(fieldName);
            Object v = f.get(null);
            return (v instanceof Item) ? (Item) v : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static net.minecraft.block.Block registryBlock(String fieldName) {
        try {
            Class<?> c = Class.forName("de.sanandrew.mods.claysoldiers.util.RegistryBlocks");
            Field f = c.getField(fieldName);
            Object v = f.get(null);
            return (v instanceof net.minecraft.block.Block) ? (net.minecraft.block.Block) v : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static ItemStack stack(Object itemOrBlock, int size, int meta) {
        if (itemOrBlock == null) return null;
        if (itemOrBlock instanceof Item) {
            return new ItemStack((Item) itemOrBlock, size, meta);
        }
        if (itemOrBlock instanceof net.minecraft.block.Block) {
            return new ItemStack((net.minecraft.block.Block) itemOrBlock, size, meta);
        }
        return null;
    }

    private static ItemStack setTeamForItem(String team, ItemStack stack) {
        if (stack == null || team == null) return stack;
        try {
            Class<?> c = Class.forName("de.sanandrew.mods.claysoldiers.item.ItemClayManDoll");
            Method m = c.getMethod("setTeamForItem", String.class, ItemStack.class);
            Object r = m.invoke(null, team, stack);
            return (r instanceof ItemStack) ? (ItemStack) r : stack;
        } catch (Throwable t) {
            return stack;
        }
    }

    private static String teamNameForMaterial(ItemStack material) {
        if (material == null) return null;
        try {
            Class<?> cTeam = Class.forName("de.sanandrew.mods.claysoldiers.util.soldier.ClaymanTeam");
            Method getTeam = cTeam.getMethod("getTeam", ItemStack.class);
            Object team = getTeam.invoke(null, material);
            if (team == null) return null;
            Method getTeamName = team.getClass().getMethod("getTeamName");
            Object name = getTeamName.invoke(team);
            return name != null ? name.toString() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static String getTeamNameForStack(ItemStack stack) {
        if (stack == null) {
            return null;
        }

        // Dolls store team in NBT as a simple string in most Clay Soldiers builds.
        // Use this first so upgraded dolls (extra NBT) still resolve to the correct team.
        try {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("team", 8)) {
                String t = stack.getTagCompound().getString("team");
                if (t != null && !t.isEmpty()) {
                    return t;
                }
            }
        } catch (Throwable ignored) {
        }

        // Fallback to Clay Soldiers' own team resolver (works for materials + some doll stacks).
        return teamNameForMaterial(stack);
    }

    private static int getBunnyWoolMeta(ItemStack wool) {
        try {
            Class<?> c = Class.forName("de.sanandrew.mods.claysoldiers.util.mount.EnumBunnyType");
            Method m = c.getMethod("getTypeFromItem", ItemStack.class);
            Object type = m.invoke(null, wool);
            if (type == null) return -1;
            Field f = type.getClass().getField("woolMeta");
            return f.getInt(type);
        } catch (Throwable t) {
            return -1;
        }
    }

    private static int getGeckoTypeOrdinal(ItemStack sapOne, ItemStack sapTwo) {
        try {
            Class<?> c = Class.forName("de.sanandrew.mods.claysoldiers.util.mount.EnumGeckoType");
            Method m = c.getMethod("getTypeFromItem", ItemStack.class, ItemStack.class);
            Object type = m.invoke(null, sapOne, sapTwo);
            if (type == null) return -1;
            if (type instanceof Enum) {
                return ((Enum<?>) type).ordinal();
            }
            Method ord = type.getClass().getMethod("ordinal");
            Object o = ord.invoke(type);
            return (o instanceof Integer) ? ((Integer) o).intValue() : -1;
        } catch (Throwable t) {
            return -1;
        }
    }

    private static List<Object> getEnumValues(String enumClassName, String valuesFieldName) {
        try {
            Class<?> c = Class.forName(enumClassName);
            Field f = c.getField(valuesFieldName);
            Object arr = f.get(null);
            if (arr instanceof Object[]) {
                Object[] a = (Object[]) arr;
                List<Object> list = new ArrayList<Object>(a.length);
                Collections.addAll(list, a);
                return list;
            }
        } catch (Throwable ignored) {
        }
        return Collections.emptyList();
    }

    private static ItemStack getEnumTypeItemStack(Object enumConst) {
        if (enumConst == null) return null;
        try {
            Field f = enumConst.getClass().getField("item");
            Object v = f.get(enumConst);
            if (v instanceof ItemStack) {
                return (ItemStack) v;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static ItemStack setHorseType(ItemStack stack, Object enumHorseType, boolean pegasus) {
        if (stack == null || enumHorseType == null) return stack;
        try {
            Class<?> c = Class.forName("de.sanandrew.mods.claysoldiers.item.ItemHorseDoll");
            // signature: setType(ItemStack, EnumHorseType, boolean)
            Method m = c.getMethod("setType", ItemStack.class, enumHorseType.getClass(), boolean.class);
            m.invoke(null, stack, enumHorseType, Boolean.valueOf(pegasus));
            return stack;
        } catch (Throwable t) {
            return null;
        }
    }

    private static ItemStack setTurtleType(ItemStack stack, Object enumTurtleType) {
        if (stack == null || enumTurtleType == null) return stack;
        try {
            Class<?> c = Class.forName("de.sanandrew.mods.claysoldiers.item.ItemTurtleDoll");
            Method m = c.getMethod("setType", ItemStack.class, enumTurtleType.getClass());
            m.invoke(null, stack, enumTurtleType);
            return stack;
        } catch (Throwable t) {
            return null;
        }
    }

    private static List<ItemStack> expandDollMaterials() {
    // Prefer the real material list from Clay Soldiers' RegistryRecipes.recipeSoldiers (if available),
    // so we only show recipes that actually exist in the installed Clay Soldiers build.
    List<ItemStack> fromRecipe = getRecipeSoldierMaterials();
    if (!fromRecipe.isEmpty()) {
        return expandWildcardMaterials(fromRecipe);
    }
    // Fallback (very old builds): dyes only (0..15)
    List<ItemStack> mats = new ArrayList<ItemStack>();
    for (int i = 0; i < 16; i++) {
        mats.add(stack(Items.dye, 1, i));
    }
    return mats;
}

@SuppressWarnings("unchecked")
private static List<ItemStack> getRecipeSoldierMaterials() {
    try {
        Class<?> cReg = Class.forName("de.sanandrew.mods.claysoldiers.util.RegistryRecipes");
        Field fRecipe = cReg.getField("recipeSoldiers");
        Object recipe = fRecipe.get(null);
        if (recipe == null) return Collections.emptyList();

        Field fMats = recipe.getClass().getDeclaredField("p_dollMaterials");
        fMats.setAccessible(true);
        Object v = fMats.get(recipe);
        if (v instanceof List) {
            return (List<ItemStack>) v;
        }
    } catch (Throwable ignored) {
    }
    return Collections.emptyList();
}

private static List<ItemStack> expandWildcardMaterials(List<ItemStack> raw) {
    List<ItemStack> out = new ArrayList<ItemStack>();
    for (ItemStack s : raw) {
        if (s == null) continue;
        int meta = s.getItemDamage();
        int wc = OreDictionary.WILDCARD_VALUE;

        // Many Clay Soldiers builds use WILDCARD_VALUE / Short.MAX_VALUE to mean "any color".
        if (meta == wc || meta == Short.MAX_VALUE) {
            Item it = s.getItem();
            if (it == Items.dye) {
                for (int i = 0; i < 16; i++) out.add(stack(Items.dye, 1, i));
                continue;
            }
            // Common "colorable" inputs across builds (only expanded if the recipe actually listed them).
            if (it == Item.getItemFromBlock(Blocks.wool)) {
                for (int i = 0; i < 16; i++) out.add(stack(Blocks.wool, 1, i));
                continue;
            }
            if (it == Item.getItemFromBlock(Blocks.stained_hardened_clay)) {
                for (int i = 0; i < 16; i++) out.add(stack(Blocks.stained_hardened_clay, 1, i));
                continue;
            }
            if (it == Item.getItemFromBlock(Blocks.stained_glass)) {
                for (int i = 0; i < 16; i++) out.add(stack(Blocks.stained_glass, 1, i));
                continue;
            }
            if (it == Item.getItemFromBlock(Blocks.hardened_clay)) {
                out.add(stack(Blocks.hardened_clay, 1, 0));
                continue;
            }

            // Unknown wildcard item - keep wildcard (better than inventing variants).
            out.add(s);
        } else {
            out.add(s);
        }
    }
    return out;
}

    // -----------------------------
    // Recipe builders
    // -----------------------------

    private CachedRecipe buildShaped(ItemStack output, ItemStack[] grid9) {
        if (output == null || grid9 == null || grid9.length != 9) {
            return null;
        }
        List<PositionedStack> in = new ArrayList<PositionedStack>();
        for (int i = 0; i < 9; i++) {
            ItemStack is = grid9[i];
            if (is != null) {
                int col = i % 3;
                int row = i / 3;
                in.add(new PositionedStack(is, SLOT_X + col * SLOT_SIZE, SLOT_Y + row * SLOT_SIZE));
            }
        }
        return new SimpleCachedRecipe(in, new PositionedStack(output, 119, 24));
    }

    private CachedRecipe buildShapeless(ItemStack output, ItemStack... inputs) {
        if (output == null || inputs == null) return null;
        List<PositionedStack> in = new ArrayList<PositionedStack>();
        int idx = 0;
        for (ItemStack is : inputs) {
            if (is == null) continue;
            int col = idx % 3;
            int row = idx / 3;
            in.add(new PositionedStack(is, SLOT_X + col * SLOT_SIZE, SLOT_Y + row * SLOT_SIZE));
            idx++;
        }
        return new SimpleCachedRecipe(in, new PositionedStack(output, 119, 24));
    }

    private class SimpleCachedRecipe extends CachedRecipe {
        private final List<PositionedStack> inputs;
        private final PositionedStack output;

        private SimpleCachedRecipe(List<PositionedStack> inputs, PositionedStack output) {
            this.inputs = inputs;
            this.output = output;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return inputs;
        }

        @Override
        public PositionedStack getResult() {
            return output;
        }
    }
}

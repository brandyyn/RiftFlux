/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  cpw.mods.fml.common.eventhandler.EventBus
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraftforge.oredict.RecipeSorter
 *  net.minecraftforge.oredict.RecipeSorter$Category
 */
package de.sanandrew.core.manpack.util.helpers;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.registry.GameRegistry;
import de.sanandrew.core.manpack.util.ReflectionNames;
import de.sanandrew.core.manpack.util.SAPReflectionHelper;
import de.sanandrew.core.manpack.util.helpers.AppHelper;
import de.sanandrew.core.manpack.util.helpers.ItemUtils;
import de.sanandrew.core.manpack.util.javatuples.Quartet;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

public final class SAPUtils {
    public static final Random RNG = new Random();
    public static final EventBus EVENT_BUS = new EventBus();
    private static final Pattern UUID_PTRN = Pattern.compile("[a-f0-9]{8}\\-[a-f0-9]{4}\\-4[a-f0-9]{3}\\-[89ab][a-f0-9]{3}\\-[a-f0-9]{12}", 2);

    public static int getAverage(int val1, int val2) {
        return Math.round((float)(val1 + val2) / 2.0f);
    }

    public static Block[] getToolBlocks(ItemTool tool) {
        Set<Block> blockSet = (Set<Block>)SAPReflectionHelper.getCachedFieldValue(ItemTool.class, tool, ReflectionNames.FIELD_150914_C.mcpName, ReflectionNames.FIELD_150914_C.srgName);
        return blockSet.toArray(new Block[blockSet.size()]);
    }

    public static <T> Class<T> getGenericType(Object obj, int pos) {
        Type[] paramTypes;
        Type genSuperCls = obj.getClass().getGenericSuperclass();
        if (genSuperCls instanceof ParameterizedType && (paramTypes = ((ParameterizedType)genSuperCls).getActualTypeArguments()).length > 0) {
            return (Class)SAPUtils.getCasted(paramTypes[0]);
        }
        return null;
    }

    public static void registerBlocks(Block ... blocks) {
        for (Block block : blocks) {
            String blockName = block.getUnlocalizedName();
            blockName = blockName.substring(blockName.lastIndexOf(58) + 1);
            GameRegistry.registerBlock((Block)block, (String)blockName.toLowerCase());
        }
    }

    public static void registerBlockWithItem(Block block, Class<? extends ItemBlock> itemClass) {
        String blockName = block.getUnlocalizedName();
        blockName = blockName.substring(blockName.lastIndexOf(58) + 1);
        GameRegistry.registerBlock((Block)block, itemClass, (String)blockName.toLowerCase());
    }

    public static void registerItems(Item ... items) {
        for (Item item : items) {
            String itemName = item.getUnlocalizedName();
            itemName = itemName.substring(itemName.lastIndexOf(58) + 1);
            GameRegistry.registerItem((Item)item, (String)itemName.toLowerCase());
        }
    }

    public static RGBAValues getRgbaFromColorInt(int rgba) {
        return new RGBAValues(rgba >> 16 & 0xFF, rgba >> 8 & 0xFF, rgba & 0xFF, rgba >> 24 & 0xFF);
    }

    public static void restartApp() {
        AppHelper.restartApp();
    }

    public static void shutdownApp() {
        AppHelper.shutdownApp();
    }

    public static boolean isIndexInRange(Object[] array, int index) {
        return index >= 0 && index < array.length;
    }

    public static void registerSortedRecipe(IRecipe recipe, String name, RecipeSorter.Category category, String dependencies) {
        RecipeSorter.register((String)name, recipe.getClass(), (RecipeSorter.Category)category, (String)dependencies);
        CraftingManager.getInstance().getRecipeList().add(recipe);
    }

    public static String translate(String key) {
        return StatCollector.translateToLocal(key);
    }

    public static <T> T getCasted(Object obj) {
        return (T)obj;
    }

    public static String translatePostFormat(String key, Object ... data) {
        return String.format(SAPUtils.translate(key), data);
    }

    public static String translatePreFormat(String key, Object ... data) {
        return SAPUtils.translate(String.format(key, data));
    }

    public static boolean isStringUuid(String uuid) {
        return UUID_PTRN.matcher(uuid).matches();
    }

    public static boolean isPlayerNameOrUuidEqual(EntityPlayer e, String ... namesUuids) {
        if (e == null) {
            return false;
        }
        GameProfile profile = e.getGameProfile();
        if (profile == null) {
            return false;
        }
        String name = profile.getName();
        UUID id = profile.getId();
        if (id == null || name == null) {
            return false;
        }
        for (String val : namesUuids) {
            if ((!SAPUtils.isStringUuid(val) || !id.equals(UUID.fromString(val))) && !name.equals(val)) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    public static ItemStack decrStackSize(ItemStack stack) {
        return ItemUtils.decrStackSize(stack, 1);
    }

    @Deprecated
    public static ItemStack decrStackSize(ItemStack stack, int amount) {
        return ItemUtils.decrStackSize(stack, amount);
    }

    @Deprecated
    public static ItemStack decrInvStackSize(ItemStack stack, int amount) {
        return ItemUtils.decrStackSize(stack, amount);
    }

    @Deprecated
    public static boolean areStacksEqualWithWCV(ItemStack stack1, ItemStack stack2) {
        return ItemUtils.areStacksEqual(stack1, stack2, false);
    }

    @Deprecated
    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2, boolean checkNbt) {
        return ItemUtils.areStacksEqual(stack1, stack2, checkNbt);
    }

    @Deprecated
    public static ItemStack[] getGoodItemStacks(ItemStack stack) {
        return ItemUtils.getGoodItemStacks(stack);
    }

    @Deprecated
    public static int getInBetweenVal(int val1, int val2) {
        return SAPUtils.getAverage(val1, val2);
    }

    @Deprecated
    public static ItemStack getSilkBlock(Block block, int meta) {
        return (ItemStack)SAPReflectionHelper.invokeCachedMethod(Block.class, block, "createStackedBlock", "func_71880_c_", new Class[]{Integer.TYPE}, new Object[]{meta});
    }

    @Deprecated
    public static void dropBlockAsItem(Block block, World world, int x, int y, int z, ItemStack stack) {
        SAPUtils.dropBlockAsItem(world, x, y, z, stack);
    }

    @Deprecated
    public static void dropBlockAsItem(World world, int x, int y, int z, ItemStack stack) {
        EntityItem item = new EntityItem(world, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, stack);
        world.spawnEntityInWorld(item);
    }

    @Deprecated
    public static void dropBlockXP(Block block, World world, int x, int y, int z, int meta, int fortune) {
        block.dropXpOnBlockBreak(world, x, y, z, block.getExpDrop(world, meta, fortune));
    }

    @Deprecated
    public static boolean isItemInStackArray(ItemStack base, ItemStack ... stackArray) {
        return ItemUtils.isItemStackInArray(base, false, stackArray);
    }

    @Deprecated
    public static boolean isItemInStackArray(ItemStack base, List<ItemStack> stackArray) {
        return ItemUtils.isItemStackInArray(base, false, stackArray.toArray(new ItemStack[stackArray.size()]));
    }

    @Deprecated
    public static boolean isItemInStackArray(ItemStack base, boolean checkSize, ItemStack ... stackArray) {
        return ItemUtils.isItemStackInArray(base, checkSize, stackArray);
    }

    @Deprecated
    public static boolean isItemInStackArray(ItemStack base, boolean checkSize, List<ItemStack> stackArray) {
        return ItemUtils.isItemStackInArray(base, checkSize, stackArray.toArray(new ItemStack[stackArray.size()]));
    }

    @Deprecated
    public static boolean isToolEffective(Block[] effectives, Block block) {
        for (Block currBlock : effectives) {
            if (block != currBlock) continue;
            return true;
        }
        return false;
    }

    public static class RGBAValues {
        private final Quartet<Integer, Integer, Integer, Integer> value;

        public RGBAValues(int r, int g, int b, int a) {
            this.value = Quartet.with(r, g, b, a);
        }

        public RGBAValues(int color) {
            this(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF);
        }

        public int getRed() {
            return this.value.getValue0();
        }

        public int getGreen() {
            return this.value.getValue1();
        }

        public int getBlue() {
            return this.value.getValue2();
        }

        public int getAlpha() {
            return this.value.getValue3();
        }

        public float[] getColorFloatArray() {
            return new float[]{(float)this.getRed() / 255.0f, (float)this.getGreen() / 255.0f, (float)this.getBlue() / 255.0f, (float)this.getAlpha() / 255.0f};
        }

        public int getColorInt() {
            return (this.value.getValue3() & 0xFF) << 24 | (this.value.getValue0() & 0xFF) << 16 | (this.value.getValue1() & 0xFF) << 8 | this.value.getValue2() & 0xFF;
        }
    }
}

/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.mount;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.javatuples.Pair;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public enum EnumGeckoType {
    OAK_OAK(0x919191, 0, 0, 5151801, 8347961, "textures/entity/mount/gecko/spots_oak.png", "textures/entity/mount/gecko/body_oak.png"),
    OAK_PINE(0x919191, 0, 1, 5151801, 3022090, "textures/entity/mount/gecko/spots_oak.png", "textures/entity/mount/gecko/body_pine.png"),
    OAK_BIRCH(0x919191, 0, 2, 5151801, 13624250, "textures/entity/mount/gecko/spots_oak.png", "textures/entity/mount/gecko/body_birch.png"),
    OAK_JUNGLE(0x919191, 0, 3, 5151801, 1580805, "textures/entity/mount/gecko/spots_oak.png", "textures/entity/mount/gecko/body_jungle.png"),
    OAK_ACACIA(0x919191, 0, 4, 5151801, 8676370, "textures/entity/mount/gecko/spots_oak.png", "textures/entity/mount/gecko/body_acacia.png"),
    OAK_DARKOAK(0x919191, 0, 5, 5151801, 4467986, "textures/entity/mount/gecko/spots_oak.png", "textures/entity/mount/gecko/body_darkoak.png"),
    PINE_OAK(0x919191, 1, 0, 3758649, 8347961, "textures/entity/mount/gecko/spots_pine.png", "textures/entity/mount/gecko/body_oak.png"),
    PINE_PINE(0x919191, 1, 1, 3758649, 3022090, "textures/entity/mount/gecko/spots_pine.png", "textures/entity/mount/gecko/body_pine.png"),
    PINE_BIRCH(0x919191, 1, 2, 3758649, 13624250, "textures/entity/mount/gecko/spots_pine.png", "textures/entity/mount/gecko/body_birch.png"),
    PINE_JUNGLE(0x919191, 1, 3, 3758649, 1580805, "textures/entity/mount/gecko/spots_pine.png", "textures/entity/mount/gecko/body_jungle.png"),
    PINE_ACACIA(0x919191, 1, 4, 3758649, 8676370, "textures/entity/mount/gecko/spots_pine.png", "textures/entity/mount/gecko/body_acacia.png"),
    PINE_DARKOAK(0x919191, 1, 5, 3758649, 4467986, "textures/entity/mount/gecko/spots_pine.png", "textures/entity/mount/gecko/body_darkoak.png"),
    BIRCH_OAK(0x919191, 2, 0, 9487993, 8347961, "textures/entity/mount/gecko/spots_birch.png", "textures/entity/mount/gecko/body_oak.png"),
    BIRCH_PINE(0x919191, 2, 1, 9487993, 3022090, "textures/entity/mount/gecko/spots_birch.png", "textures/entity/mount/gecko/body_pine.png"),
    BIRCH_BIRCH(0x919191, 2, 2, 9487993, 13624250, "textures/entity/mount/gecko/spots_birch.png", "textures/entity/mount/gecko/body_birch.png"),
    BIRCH_JUNGLE(0x919191, 2, 3, 9487993, 1580805, "textures/entity/mount/gecko/spots_birch.png", "textures/entity/mount/gecko/body_jungle.png"),
    BIRCH_ACACIA(0x919191, 2, 4, 9487993, 8676370, "textures/entity/mount/gecko/spots_birch.png", "textures/entity/mount/gecko/body_acacia.png"),
    BIRCH_DARKOAK(0x919191, 2, 5, 9487993, 4467986, "textures/entity/mount/gecko/spots_birch.png", "textures/entity/mount/gecko/body_darkoak.png"),
    JUNGLE_OAK(0x919191, 3, 0, 3637280, 8347961, "textures/entity/mount/gecko/spots_jungle.png", "textures/entity/mount/gecko/body_oak.png"),
    JUNGLE_PINE(0x919191, 3, 1, 3637280, 3022090, "textures/entity/mount/gecko/spots_jungle.png", "textures/entity/mount/gecko/body_pine.png"),
    JUNGLE_BIRCH(0x919191, 3, 2, 3637280, 13624250, "textures/entity/mount/gecko/spots_jungle.png", "textures/entity/mount/gecko/body_birch.png"),
    JUNGLE_JUNGLE(0x919191, 3, 3, 3637280, 1580805, "textures/entity/mount/gecko/spots_jungle.png", "textures/entity/mount/gecko/body_jungle.png"),
    JUNGLE_ACACIA(0x919191, 3, 4, 3637280, 8676370, "textures/entity/mount/gecko/spots_jungle.png", "textures/entity/mount/gecko/body_acacia.png"),
    JUNGLE_DARKOAK(0x919191, 3, 5, 3637280, 4467986, "textures/entity/mount/gecko/spots_jungle.png", "textures/entity/mount/gecko/body_darkoak.png"),
    ACACIA_OAK(0x919191, 4, 0, 7506203, 8347961, "textures/entity/mount/gecko/spots_acacia.png", "textures/entity/mount/gecko/body_oak.png"),
    ACACIA_PINE(0x919191, 4, 1, 7506203, 3022090, "textures/entity/mount/gecko/spots_acacia.png", "textures/entity/mount/gecko/body_pine.png"),
    ACACIA_BIRCH(0x919191, 4, 2, 7506203, 13624250, "textures/entity/mount/gecko/spots_acacia.png", "textures/entity/mount/gecko/body_birch.png"),
    ACACIA_JUNGLE(0x919191, 4, 3, 7506203, 1580805, "textures/entity/mount/gecko/spots_acacia.png", "textures/entity/mount/gecko/body_jungle.png"),
    ACACIA_ACACIA(0x919191, 4, 4, 7506203, 8676370, "textures/entity/mount/gecko/spots_acacia.png", "textures/entity/mount/gecko/body_acacia.png"),
    ACACIA_DARKOAK(0x919191, 4, 5, 7506203, 4467986, "textures/entity/mount/gecko/spots_acacia.png", "textures/entity/mount/gecko/body_darkoak.png"),
    DARKOAK_OAK(0x919191, 5, 0, 4560435, 8347961, "textures/entity/mount/gecko/spots_darkoak.png", "textures/entity/mount/gecko/body_oak.png"),
    DARKOAK_PINE(0x919191, 5, 1, 4560435, 3022090, "textures/entity/mount/gecko/spots_darkoak.png", "textures/entity/mount/gecko/body_pine.png"),
    DARKOAK_BIRCH(0x919191, 5, 2, 4560435, 13624250, "textures/entity/mount/gecko/spots_darkoak.png", "textures/entity/mount/gecko/body_birch.png"),
    DARKOAK_JUNGLE(0x919191, 5, 3, 4560435, 1580805, "textures/entity/mount/gecko/spots_darkoak.png", "textures/entity/mount/gecko/body_jungle.png"),
    DARKOAK_ACACIA(0x919191, 5, 4, 4560435, 8676370, "textures/entity/mount/gecko/spots_darkoak.png", "textures/entity/mount/gecko/body_acacia.png"),
    DARKOAK_DARKOAK(0x919191, 5, 5, 4560435, 4467986, "textures/entity/mount/gecko/spots_darkoak.png", "textures/entity/mount/gecko/body_darkoak.png");

    public static final EnumGeckoType[] VALUES;
    public final int typeColor;
    public final Pair<Integer, Integer> saplingTypes;
    public final Pair<Integer, Integer> colors;
    public final ResourceLocation[] textures;

    private EnumGeckoType(int typeColor, int saplingOne, int saplingTwo, int itemColorLimbs, int itemColorBody, String textureSpots, String textureBody) {
        this.typeColor = typeColor;
        this.saplingTypes = Pair.with(saplingOne, saplingTwo);
        this.colors = Pair.with(itemColorLimbs, itemColorBody);
        this.textures = new ResourceLocation[]{new ResourceLocation("claysoldiers", textureSpots), new ResourceLocation("claysoldiers", textureBody)};
    }

    public static EnumGeckoType getTypeFromItem(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null) {
            return null;
        }
        ItemStack sapling1 = new ItemStack(Blocks.sapling);
        ItemStack sapling2 = new ItemStack(Blocks.sapling);
        for (EnumGeckoType type : VALUES) {
            sapling1.setItemDamage(type.saplingTypes.getValue0());
            sapling2.setItemDamage(type.saplingTypes.getValue1());
            if (!SAPUtils.areStacksEqualWithWCV(sapling1, stack1) || !SAPUtils.areStacksEqualWithWCV(sapling2, stack2)) continue;
            return type;
        }
        return null;
    }

    static {
        VALUES = EnumGeckoType.values();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.mount;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.javatuples.Pair;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public enum EnumHorseType {
    DIRT(35.0f, 0.6f, 10244864, new ItemStack(Blocks.dirt), "claysoldiers:doll_horse", 10244864, new ResourceLocation("claysoldiers", "textures/entity/mount/horses/dirt1.png"), new ResourceLocation("claysoldiers", "textures/entity/mount/horses/dirt2.png"), new ResourceLocation("claysoldiers", "textures/entity/mount/horses/dirt3.png"), new ResourceLocation("claysoldiers", "textures/entity/mount/horses/dirt4.png")),
    SAND(30.0f, 0.7f, 16383872, new ItemStack(Blocks.sand), "claysoldiers:doll_horse", 16383872, new ResourceLocation("claysoldiers", "textures/entity/mount/horses/sand.png")),
    GRAVEL(45.0f, 0.4f, 13744826, new ItemStack(Blocks.gravel), "claysoldiers:doll_horse", 13744826, new ResourceLocation("claysoldiers", "textures/entity/mount/horses/gravel1.png"), new ResourceLocation("claysoldiers", "textures/entity/mount/horses/gravel2.png")),
    SNOW(40.0f, 0.5f, 0xFFFFFF, new ItemStack(Blocks.snow), "claysoldiers:doll_horse", 0xFFFFFF, new ResourceLocation("claysoldiers", "textures/entity/mount/horses/snow.png")),
    GRASS(20.0f, 0.9f, 2800154, new ItemStack(Blocks.tallgrass, 1, Short.MAX_VALUE), "claysoldiers:doll_horse", 2800154, new ResourceLocation("claysoldiers", "textures/entity/mount/horses/grass1.png"), new ResourceLocation("claysoldiers", "textures/entity/mount/horses/grass2.png")),
    LAPIS(35.0f, 0.9f, 4468930, new ItemStack(Items.dye, 1, 4), "claysoldiers:doll_horse", 4468930, new ResourceLocation("claysoldiers", "textures/entity/mount/horses/lapis.png")),
    CLAY(35.0f, 0.6f, 0xA3A3A3, new ItemStack(Blocks.clay), "claysoldiers:doll_horse", 0xA3A3A3, new ResourceLocation("claysoldiers", "textures/entity/mount/horses/clay.png")),
    CARROT(35.0f, 0.9f, 15771648, new ItemStack(Items.carrot), "claysoldiers:doll_horse", 15771648, new ResourceLocation("claysoldiers", "textures/entity/mount/horses/carrot1.png"), new ResourceLocation("claysoldiers", "textures/entity/mount/horses/carrot2.png")),
    SOULSAND(35.0f, 0.8f, 6041856, new ItemStack(Blocks.soul_sand), "claysoldiers:doll_horse", 6041856, new ResourceLocation("claysoldiers", "textures/entity/mount/horses/soulsand.png")),
    CAKE(30.0f, 1.1f, 0xFFFFFF, new ItemStack(Items.cake), "claysoldiers:doll_horse_cake", 0xFFFFFF, new ResourceLocation("claysoldiers", "textures/entity/mount/horses/cake.png")),
    NIGHTMARE(50.0f, 1.2f, 0, null, new ResourceLocation("claysoldiers", "textures/entity/mount/horses/spec_nightmare1.png"), new ResourceLocation("claysoldiers", "textures/entity/mount/horses/spec_nightmare2.png"));

    public static final EnumHorseType[] VALUES;
    public final float health;
    public final float moveSpeed;
    public final ResourceLocation[] textures;
    public final Pair<String, Integer> itemData;
    public final int typeColor;
    public final ItemStack item;

    private EnumHorseType(float health, float speed, int typeColor, ItemStack materialItem, ResourceLocation ... textures) {
        this.health = health;
        this.moveSpeed = speed;
        this.textures = textures;
        this.itemData = null;
        this.typeColor = typeColor;
        this.item = materialItem;
    }

    private EnumHorseType(float health, float speed, int typeColor, ItemStack materialItem, String itemTexture, int itemColor, ResourceLocation ... textures) {
        this.health = health;
        this.moveSpeed = speed;
        this.textures = textures;
        this.itemData = Pair.with(itemTexture, itemColor);
        this.typeColor = typeColor;
        this.item = materialItem;
    }

    public static EnumHorseType getTypeFromItem(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (stack.getItem() == Item.getItemFromBlock(Blocks.tallgrass) && stack.getItemDamage() == 0) {
            return null;
        }
        for (EnumHorseType type : VALUES) {
            if (type.item == null) {
                return null;
            }
            if (!SAPUtils.areStacksEqualWithWCV(type.item, stack)) continue;
            return type;
        }
        return null;
    }

    static {
        VALUES = EnumHorseType.values();
    }
}


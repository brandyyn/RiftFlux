/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.mount;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public enum EnumTurtleType {
    COBBLE(40.0f, 0.4f, 0x919191, new ItemStack(Blocks.cobblestone), "claysoldiers:doll_turtle_shell", 5059109, 0x919191, new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/cobble.png"), new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/cobble2.png")),
    MOSSY(45.0f, 0.3f, 8823684, new ItemStack(Blocks.mossy_cobblestone), "claysoldiers:doll_turtle_shell", 5059109, 8823684, new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/mossy.png")),
    NETHERRACK(35.0f, 0.4f, 0xCC3131, new ItemStack(Blocks.netherrack), "claysoldiers:doll_turtle_shell", 5059109, 0xCC3131, new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/netherrack.png")),
    MELON(25.0f, 0.6f, 908049, new ItemStack(Blocks.melon_block), "claysoldiers:doll_turtle_shell", 15555162, 908049, new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/melon.png"), new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/melon2.png")),
    SANDSTONE(30.0f, 0.4f, 16774571, new ItemStack(Blocks.sandstone, 1, Short.MAX_VALUE), "claysoldiers:doll_turtle_shell", 5059109, 16774571, new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/sandstone.png"), new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/sandstone2.png"), new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/sandstone3.png")),
    ENDSTONE(45.0f, 0.4f, 14407074, new ItemStack(Blocks.end_stone), "claysoldiers:doll_turtle_shell", 5059109, 14407074, new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/endstone.png")),
    PUMPKIN(25.0f, 0.6f, 15908111, new ItemStack(Blocks.pumpkin), "claysoldiers:doll_turtle_shell", 5059109, 15908111, new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/pumpkin.png"), new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/pumpkin.png")),
    LAPIS(35.0f, 0.6f, 2558665, new ItemStack(Items.dye, 1, 4), "claysoldiers:doll_turtle_shell", 5059109, 2558665, new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/lapis.png")),
    CAKE(30.0f, 0.6f, 0xFFA1A1, new ItemStack(Items.cake), "claysoldiers:doll_turtle_cakeshell", 0xA60000, 0xFFFFFF, new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/cake.png")),
    KAWAKO(50.0f, 0.5f, 13056, null, new ResourceLocation("claysoldiers", "textures/entity/mount/turtle/spec_kawako.png"));

    public static final EnumTurtleType[] VALUES;
    public final float health;
    public final float moveSpeed;
    public final ResourceLocation[] textures;
    public final Triplet<String, Integer, Integer> itemData;
    public final int typeColor;
    public final ItemStack item;

    private EnumTurtleType(float health, float speed, int typeColor, ItemStack materialItem, ResourceLocation ... textures) {
        this.health = health;
        this.moveSpeed = speed;
        this.textures = textures;
        this.itemData = null;
        this.typeColor = typeColor;
        this.item = materialItem;
    }

    private EnumTurtleType(float health, float speed, int typeColor, ItemStack materialItem, String shellTexture, int itemColorBody, int itemColorShell, ResourceLocation ... textures) {
        this.health = health;
        this.moveSpeed = speed;
        this.textures = textures;
        this.itemData = Triplet.with(shellTexture, itemColorBody, itemColorShell);
        this.typeColor = typeColor;
        this.item = materialItem;
    }

    public static EnumTurtleType getTypeFromItem(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        for (EnumTurtleType type : VALUES) {
            if (type.item == null) {
                return null;
            }
            if (!SAPUtils.areStacksEqualWithWCV(type.item, stack)) continue;
            return type;
        }
        return null;
    }

    static {
        VALUES = EnumTurtleType.values();
    }
}


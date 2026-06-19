package com.voidsrift.riftflux.mixin.late.geostrata;

import Reika.DragonAPI.Instantiable.MetadataItemBlock;
import Reika.GeoStrata.Blocks.BlockDecoGen;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.geostrata.client.DecoGenItemRenderer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = MetadataItemBlock.class, remap = false)
public abstract class MixinMetadataItemBlock_DecoGenItems extends ItemBlockWithMetadata {
    @Unique
    private static final int riftflux$ORIENTATION_MASK = 14;
    @Unique
    private static final int riftflux$ORIENTATION_NORTH = 2;
    @Unique
    private static final int riftflux$ORIENTATION_SOUTH = 4;
    @Unique
    private static final int riftflux$ORIENTATION_WEST = 6;
    @Unique
    private static final int riftflux$ORIENTATION_DOWN = 8;
    @Unique
    private static final int riftflux$ORIENTATION_EAST = 10;

    protected MixinMetadataItemBlock_DecoGenItems(Block block) {
        super(block, block);
    }

    public int getSpriteNumber() {
        return this.riftflux$isDecoGenItem() ? 0 : super.getSpriteNumber();
    }

    public int func_94901_k() {
        return this.riftflux$isDecoGenItem() ? 0 : super.getSpriteNumber();
    }

    public IIcon getIconFromDamage(int damage) {
        IIcon icon = this.riftflux$getDecoGenItemIcon(damage);
        return icon != null ? icon : super.getIconFromDamage(damage);
    }

    public IIcon func_77617_a(int damage) {
        IIcon icon = this.riftflux$getDecoGenItemIcon(damage);
        return icon != null ? icon : super.getIconFromDamage(damage);
    }

    public IIcon getIconIndex(ItemStack stack) {
        IIcon icon = this.riftflux$getDecoGenItemIcon(stack.getItemDamage());
        return icon != null ? icon : super.getIconIndex(stack);
    }

    public IIcon func_77650_f(ItemStack stack) {
        IIcon icon = this.riftflux$getDecoGenItemIcon(stack.getItemDamage());
        return icon != null ? icon : super.getIconIndex(stack);
    }

    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        IIcon icon = this.riftflux$getDecoGenItemIcon(damage);
        return icon != null ? icon : super.getIconFromDamageForRenderPass(damage, pass);
    }

    public IIcon func_77618_c(int damage, int pass) {
        IIcon icon = this.riftflux$getDecoGenItemIcon(damage);
        return icon != null ? icon : super.getIconFromDamageForRenderPass(damage, pass);
    }

    public IIcon getIcon(ItemStack stack, int pass) {
        IIcon icon = this.riftflux$getDecoGenItemIcon(stack.getItemDamage());
        return icon != null ? icon : super.getIcon(stack, pass);
    }

    public IIcon getIcon(ItemStack stack, int pass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        IIcon icon = this.riftflux$getDecoGenItemIcon(stack.getItemDamage());
        return icon != null ? icon : super.getIcon(stack, pass, player, usingItem, useRemaining);
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                                int side, float hitX, float hitY, float hitZ, int metadata) {
        int placedMeta = metadata;
        if (this.riftflux$isCrystalSpike(metadata)) {
            if (ModConfig.enableGeoStrataCeilingCrystalSpikes && side == 0) {
                placedMeta = riftflux$ORIENTATION_DOWN;
            } else if (ModConfig.enableGeoStrataWallCrystalSpikes) {
                int orientation = riftflux$getWallOrientation(side);
                if (orientation != 0) {
                    placedMeta = orientation;
                }
            }
        }
        return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, placedMeta);
    }

    @Unique
    private boolean riftflux$isDecoGenItem() {
        return this.field_150939_a instanceof BlockDecoGen;
    }

    @Unique
    private boolean riftflux$isCrystalSpike(int meta) {
        return this.riftflux$isDecoGenItem() && riftflux$getBaseMeta(meta) == 0;
    }

    @Unique
    private IIcon riftflux$getDecoGenItemIcon(int damage) {
        if (!this.riftflux$isDecoGenItem()) {
            return null;
        }
        return DecoGenItemRenderer.getDecoGenIcon(damage);
    }

    @Unique
    private static int riftflux$getBaseMeta(int meta) {
        return meta & ~riftflux$ORIENTATION_MASK;
    }

    @Unique
    private static int riftflux$getWallOrientation(int side) {
        if (side == 2) {
            return riftflux$ORIENTATION_NORTH;
        }
        if (side == 3) {
            return riftflux$ORIENTATION_SOUTH;
        }
        if (side == 4) {
            return riftflux$ORIENTATION_WEST;
        }
        if (side == 5) {
            return riftflux$ORIENTATION_EAST;
        }
        return 0;
    }
}

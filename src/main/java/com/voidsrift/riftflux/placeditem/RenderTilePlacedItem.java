package com.voidsrift.riftflux.placeditem;

import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

public class RenderTilePlacedItem extends TileEntitySpecialRenderer {
    private static final boolean ITEM_PHYSIC = Loader.isModLoaded("itemphysic");

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TilePlacedItem)) {
            return;
        }
        TilePlacedItem placed = (TilePlacedItem) tile;
        if (placed.getStack() == null) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glTranslated(x + 0.5D, y + 0.5D, z + 0.5D);
            int meta = tile.getBlockMetadata();
            switch (meta) {
                case 1:
                    GL11.glRotated(180.0D, 1.0D, 0.0D, 0.0D);
                    GL11.glRotated(180.0D, 0.0D, 1.0D, 0.0D);
                    break;
                case 2:
                    GL11.glRotated(90.0D, 1.0D, 0.0D, 0.0D);
                    break;
                case 3:
                    GL11.glRotated(90.0D, 1.0D, 0.0D, 0.0D);
                    GL11.glRotated(180.0D, 0.0D, 0.0D, 1.0D);
                    break;
                case 4:
                    GL11.glRotated(90.0D, 1.0D, 0.0D, 0.0D);
                    GL11.glRotated(-90.0D, 0.0D, 0.0D, 1.0D);
                    break;
                case 5:
                    GL11.glRotated(90.0D, 1.0D, 0.0D, 0.0D);
                    GL11.glRotated(90.0D, 0.0D, 0.0D, 1.0D);
                    break;
                default:
                    break;
            }
            GL11.glTranslated(0.0D, -0.5D, 0.0D);
            GL11.glRotated(-placed.rotation, 0.0D, 1.0D, 0.0D);
            renderItem(placed, partialTicks);
        } finally {
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private void renderItem(TilePlacedItem tile, float partialTicks) {
        EntityItem entity = tile.getOrCreateRenderEntity();
        if (entity == null) {
            return;
        }
        ItemStack stack = entity.getEntityItem();
        renderStack(tile.getWorldObj(), stack, entity, partialTicks);
    }

    private static int getRenderTypeFromStack(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return 0;
        }
        if (stack.getItem() instanceof ItemBlock && !PlacedItemRenderRules.isBlockLikePlacedItem(stack)) {
            return 0;
        }
        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.ENTITY);
        if (renderer != null) {
            boolean helper = renderer.shouldUseRenderHelper(
                    IItemRenderer.ItemRenderType.ENTITY,
                    stack,
                    IItemRenderer.ItemRendererHelper.BLOCK_3D
            );
            if (helper) {
                return 1;
            }
            if (stack.getItem() instanceof ItemBlock) {
                Block block = Block.getBlockFromItem(stack.getItem());
                if (block != null && RenderBlocks.renderItemIn3d(block.getRenderType())) {
                    return 1;
                }
            }
            return 0;
        }
        if (stack.getItemSpriteNumber() == 0 && stack.getItem() instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(stack.getItem());
            if (block != null && RenderBlocks.renderItemIn3d(block.getRenderType())) {
                return 1;
            }
        }
        return 0;
    }

    private void renderStack(World world, ItemStack stack, EntityItem entity, float partialTicks) {
        ItemStack entityStack = entity.getEntityItem();
        if (entityStack == null) {
            return;
        }

        GL11.glPushMatrix();
        int previousStackSize = entityStack.stackSize;
        boolean previousInFrame = RenderItem.renderInFrame;
        VanillaToolRenderContext.enterPlacedItem();
        try {
            entityStack.stackSize = 1;
            entity.age = 0;
            entity.hoverStart = 0.0F;
            entity.rotationYaw = 0.0F;
            entity.rotationPitch = 0.0F;
            entity.onGround = true;

            int renderType = getRenderTypeFromStack(stack);
            boolean onGround = true;

            if (renderType == 1) {
                GL11.glScaled(0.5D, 0.5D, 0.5D);
                GL11.glTranslated(0.0D, 0.5D, 0.0D);
                GL11.glRotated(-90.0D, 0.0D, 1.0D, 0.0D);
                GL11.glScaled(4.0D, 4.0D, 4.0D);
                if (onGround) {
                    GL11.glRotated(90.0D, 0.0D, 1.0D, 0.0D);
                    if (ITEM_PHYSIC) {
                        GL11.glTranslated(0.0D, -0.09D, 0.0D);
                    }
                    GL11.glTranslated(0.0D, -0.05D, 0.0D);
                    GL11.glScaled(0.8D, 0.8D, 0.8D);
                }
            } else {
                GL11.glRotated(-90.0D, 1.0D, 0.0D, 0.0D);
                GL11.glTranslated(0.0D, -0.25D, 0.04D);
                if (RenderManager.instance.options.fancyGraphics) {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    if (onGround) {
                        GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);
                        if (ITEM_PHYSIC) {
                            GL11.glTranslatef(0.0F, -0.09F, 0.0F);
                        }
                    } else if (ITEM_PHYSIC) {
                        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                    }
                } else if (!onGround) {
                    GL11.glRotatef(180.0F - RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
                }
                if (onGround) {
                    GL11.glTranslatef(0.0F, 0.05F, 0.0F);
                    GL11.glScaled(1.95D, 1.95D, 1.95D);
                } else {
                    GL11.glScaled(2.0D, 2.0D, 2.0D);
                }
            }

            RenderItem.renderInFrame = onGround;
            if (!ITEM_PHYSIC) {
                GL11.glTranslated(0.0D, -0.1D, 0.0D);
            }
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        } finally {
            VanillaToolRenderContext.exitPlacedItem();
            entityStack.stackSize = previousStackSize;
            RenderItem.renderInFrame = previousInFrame;
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
        }
    }

    
}

package com.voidsrift.riftflux.placeditem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import net.minecraft.block.Block;

public class RenderTilePlacedItem extends TileEntitySpecialRenderer {

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
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glTranslated(x, y, z);
        renderItem(placed);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void renderItem(TilePlacedItem tile) {
        ItemStack stack = tile.getStack();
        ItemStack renderStack = stack;
        if (stack != null && stack.stackSize > 1) {
            renderStack = stack.copy();
            renderStack.stackSize = 1;
        }
        World world = Minecraft.getMinecraft().theWorld;
        EntityItem entity = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, renderStack);
        int meta = world.getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord);

        entity.hoverStart = 0.0F;
        boolean isBlock = stack.getItem() instanceof ItemBlock;

        if (!isBlock) {
            renderFlatItem(tile, stack, meta);
            return;
        }

        GL11.glTranslatef(0.5F, 0.25F, 0.5F);
        GL11.glScalef(1.5F, 1.5F, 1.5F);
        metaAdjustBlock(meta);
        GL11.glRotatef(tile.rotation, 0.0F, 1.0F, 0.0F);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;
        GL11.glPopAttrib();
    }

    private void renderFlatItem(TilePlacedItem tile, ItemStack stack, int meta) {
        IIcon icon = stack.getItem().getIcon(stack, 0);
        if (icon == null) {
            return;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(
                stack.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture
        );
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        switch (meta) {
            case 0:
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case 1:
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case 2:
                break;
            case 3:
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case 4:
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case 5:
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                break;
            default:
                break;
        }
        GL11.glTranslatef(0.0F, 0.0F, 0.5F);
        GL11.glRotatef(tile.rotation, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-0.5F, -0.5F, 0.0F);

        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        int brightness = tile.getWorldObj().getLightBrightnessForSkyBlocks(
                tile.xCoord, tile.yCoord, tile.zCoord, 0
        );
        int brightnessLow = brightness % 65536;
        int brightnessHigh = brightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(
                OpenGlHelper.lightmapTexUnit,
                brightnessLow / 1.0F,
                brightnessHigh / 1.0F
        );
        float ao = Minecraft.getMinecraft().gameSettings.ambientOcclusion > 0
                ? getAoFactor(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, meta)
                : 1.0F;
        Tessellator.instance.setColorOpaque_F(ao, ao, ao);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        ItemRenderer.renderItemIn2D(
                Tessellator.instance,
                (float) maxU,
                (float) minV,
                (float) minU,
                (float) maxV,
                icon.getIconWidth(),
                icon.getIconHeight(),
                1.0F / 16.0F
        );
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopAttrib();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    private float getAoFactor(World world, int x, int y, int z, int meta) {
        float a1;
        float a2;
        float a3;
        float a4;
        switch (meta) {
            case 0:
            case 1:
                a1 = getAo(world, x + 1, y, z);
                a2 = getAo(world, x - 1, y, z);
                a3 = getAo(world, x, y, z + 1);
                a4 = getAo(world, x, y, z - 1);
                break;
            case 2:
            case 3:
                a1 = getAo(world, x + 1, y, z);
                a2 = getAo(world, x - 1, y, z);
                a3 = getAo(world, x, y + 1, z);
                a4 = getAo(world, x, y - 1, z);
                break;
            case 4:
            case 5:
            default:
                a1 = getAo(world, x, y + 1, z);
                a2 = getAo(world, x, y - 1, z);
                a3 = getAo(world, x, y, z + 1);
                a4 = getAo(world, x, y, z - 1);
                break;
        }
        return (a1 + a2 + a3 + a4) * 0.25F;
    }

    private float getAo(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block == null ? 1.0F : block.getAmbientOcclusionLightValue();
    }

    private void metaAdjustBlock(int meta) {
        switch (meta) {
            case 0:
                GL11.glTranslatef(0.0F, 0.51F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                break;
            case 1:
                GL11.glTranslatef(0.0F, -0.17F, 0.0F);
                break;
            case 2:
                GL11.glTranslatef(-0.0F, 0.17F, 0.34F);
                GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
                break;
            case 3:
                GL11.glTranslatef(0.0F, 0.17F, -0.34F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case 4:
                GL11.glTranslatef(0.34F, 0.17F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case 5:
                GL11.glTranslatef(-0.34F, 0.17F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);
                break;
            default:
                break;
        }
    }
}

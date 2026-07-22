package com.voidsrift.riftflux.placeditem;

import Reika.DragonAPI.Interfaces.Item.IndexedItemSprites;
import Reika.DragonAPI.Interfaces.Item.MultisheetItem;
import Reika.DragonAPI.Auxiliary.ReikaSpriteSheets;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public final class DragonApiPlacedItemRenderer {
    private DragonApiPlacedItemRenderer() {
    }

    public static boolean render(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof IndexedItemSprites)) {
            return false;
        }

        IndexedItemSprites item = (IndexedItemSprites) stack.getItem();
        int sprite = item.getItemSpriteIndex(stack);
        int row = sprite / 16;
        int column = sprite % 16;
        float minU = column / 16.0F;
        float maxU = (column + 1) / 16.0F;
        float minV = row / 16.0F;
        float maxV = (row + 1) / 16.0F;

        String texture = item instanceof MultisheetItem
                ? ((MultisheetItem) item).getSpritesheet(stack)
                : item.getTexture(stack);
        ReikaTextureHelper.bindTexture(item.getTextureReferenceClass(), texture);
        GL11.glPushMatrix();
        try {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (VanillaToolRenderContext.isPlacedItem()) {
                renderPlacedItem(stack, minU, maxU, minV, maxV);
            } else {
                renderGravestoneItem(stack, minU, maxU, minV, maxV);
            }
        } finally {
            GL11.glPopMatrix();
        }
        return true;
    }

    private static void renderPlacedItem(ItemStack stack, float minU, float maxU, float minV, float maxV) {
        // Match RenderItem's renderInFrame path exactly. Forge has already applied its
        // custom-renderer 0.5 scale here, so 1.025641 produces vanilla's net 0.5128205.
        GL11.glScalef(1.025641F, 1.025641F, 1.025641F);
        GL11.glTranslatef(0.0F, -0.05F, 0.0F);
        if (Minecraft.getMinecraft().gameSettings.fancyGraphics) {
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.25F, 0.0421875F);
            renderExtrudedSprite(minU, maxU, minV, maxV);
            ReikaSpriteSheets.renderEffect(IItemRenderer.ItemRenderType.ENTITY, stack);
        } else {
            renderFlatSprite(minU, maxU, minV, maxV);
        }
    }

    private static void renderGravestoneItem(ItemStack stack, float minU, float maxU, float minV, float maxV) {
        // Fallen Warrior renderer supplies its own fixed sword transform; do not apply
        // RenderItem's in-frame rotation or centering a second time.
        GL11.glTranslatef(-0.5F, -0.25F, 0.0F);
        renderExtrudedSprite(minU, maxU, minV, maxV);
        ReikaSpriteSheets.renderEffect(IItemRenderer.ItemRenderType.ENTITY, stack);
    }

    private static void renderExtrudedSprite(float minU, float maxU, float minV, float maxV) {
        ItemRenderer.renderItemIn2D(
                Tessellator.instance,
                maxU,
                minV,
                minU,
                maxV,
                16,
                16,
                0.0625F
        );
    }

    private static void renderFlatSprite(float minU, float maxU, float minV, float maxV) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV(-0.5D, -0.25D, 0.0D, minU, maxV);
        tessellator.addVertexWithUV(0.5D, -0.25D, 0.0D, maxU, maxV);
        tessellator.addVertexWithUV(0.5D, 0.75D, 0.0D, maxU, minV);
        tessellator.addVertexWithUV(-0.5D, 0.75D, 0.0D, minU, minV);
        tessellator.draw();
    }
}

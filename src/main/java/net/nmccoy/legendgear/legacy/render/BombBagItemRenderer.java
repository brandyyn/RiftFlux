package net.nmccoy.legendgear.legacy.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BombBagItemRenderer implements IItemRenderer {
    private static final RenderItem RENDER_ITEM = new RenderItem();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (stack == null || stack.getItem() == null) {
            return;
        }

        Item item = stack.getItem();
        int count = Math.max(0, Math.min(999, stack.getItemDamage()));
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_CURRENT_BIT);
        try {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            this.renderIcon(item.getIconFromDamageForRenderPass(count, 0), 0, 0, item.getColorFromItemStack(stack, 0));
            if (count >= 100) {
                this.renderIcon(item.getIconFromDamageForRenderPass(count, 3), -10, 0, item.getColorFromItemStack(stack, 3));
            }
            if (count >= 10) {
                this.renderIcon(item.getIconFromDamageForRenderPass(count, 2), -5, 0, item.getColorFromItemStack(stack, 2));
            }
            this.renderIcon(item.getIconFromDamageForRenderPass(count, 1), 0, 0, item.getColorFromItemStack(stack, 1));
        } finally {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopAttrib();
        }
    }

    private void renderIcon(IIcon icon, int x, int y, int color) {
        if (icon == null) {
            return;
        }
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, 1.0F);
        RENDER_ITEM.renderIcon(x, y, icon, 16, 16);
    }
}

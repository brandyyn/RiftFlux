/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.item.ItemStack
 *  org.lwjgl.opengl.GL11
 */
package zairus.worldexplorer.core.helpers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderHelper {
    public static void drawItemStack(ItemStack stack, int x, int y, String text) {
        Minecraft mc = Minecraft.getMinecraft();
        RenderItem r = RenderItem.getInstance();
        float zLev = r.zLevel;
        r.zLevel = 200.0f;
        FontRenderer font = null;
        if (stack != null) {
            font = stack.getItem().getFontRenderer(stack);
        }
        if (font == null) {
            font = mc.fontRenderer;
        }
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)32.0f);
        r.renderItemIntoGUI(font, mc.getTextureManager(), stack, x, y, false);
        r.zLevel = zLev;
        GL11.glDisable((int)2896);
    }
}


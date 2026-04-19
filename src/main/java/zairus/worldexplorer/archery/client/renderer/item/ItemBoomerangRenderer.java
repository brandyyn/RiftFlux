/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  org.lwjgl.opengl.GL11
 */
package zairus.worldexplorer.archery.client.renderer.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import zairus.worldexplorer.archery.items.Boomerang;
import zairus.worldexplorer.core.ClientProxy;
import zairus.worldexplorer.core.helpers.ColorHelper;

@SideOnly(value=Side.CLIENT)
public class ItemBoomerangRenderer
implements IItemRenderer {
    private static final ResourceLocation ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final Tessellator tessellator = Tessellator.instance;
    private static Minecraft mc = null;
    private static final RenderItem renderItem = new RenderItem();

    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return type.equals((Object)IItemRenderer.ItemRenderType.EQUIPPED) || type.equals((Object)IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) || type.equals((Object)IItemRenderer.ItemRenderType.INVENTORY) || type.equals((Object)IItemRenderer.ItemRenderType.ENTITY);
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return helper.equals((Object)IItemRenderer.ItemRendererHelper.ENTITY_BOBBING) || helper.equals((Object)IItemRenderer.ItemRendererHelper.ENTITY_ROTATION);
    }

    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object ... data) {
        if (mc == null) {
            mc = ClientProxy.mc;
        }
        Item item = stack.getItem();
        if (type.equals((Object)IItemRenderer.ItemRenderType.INVENTORY)) {
            GL11.glDisable((int)2896);
            int color = item.getColorFromItemStack(stack, 0);
            IIcon icon = stack.getItem().getIcon(stack, 0);
            if (icon != null) {
                this.renderIcon(color, icon);
            }
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Gunpowder Modifier")) {
                icon = ((Boomerang)stack.getItem()).getModifierIconLayer("gunpowder");
                color = item.getColorFromItemStack(stack, 1);
                if (icon != null) {
                    this.renderIcon(color, icon);
                }
            }
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Slime Modifier")) {
                icon = ((Boomerang)stack.getItem()).getModifierIconLayer("slime");
                color = item.getColorFromItemStack(stack, 1);
                if (icon != null) {
                    this.renderIcon(color, icon);
                }
            }
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Glowstone Modifier")) {
                icon = ((Boomerang)stack.getItem()).getModifierIconLayer("glowstone");
                color = item.getColorFromItemStack(stack, 1);
                if (icon != null) {
                    this.renderIcon(color, icon);
                }
            }
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Redstone Modifier")) {
                icon = ((Boomerang)stack.getItem()).getModifierIconLayer("redstone");
                color = item.getColorFromItemStack(stack, 1);
                if (icon != null) {
                    this.renderIcon(color, icon);
                }
            }
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Ender Modifier")) {
                icon = ((Boomerang)stack.getItem()).getModifierIconLayer("ender");
                color = item.getColorFromItemStack(stack, 1);
                if (icon != null) {
                    this.renderIcon(color, icon);
                }
            }
            if (stack.hasEffect(0)) {
                GL11.glPushMatrix();
                GL11.glDepthFunc((int)516);
                GL11.glDisable((int)2896);
                GL11.glDepthMask((boolean)false);
                GL11.glEnable((int)3042);
                GL11.glBlendFunc((int)774, (int)774);
                GL11.glColor4f((float)0.5f, (float)0.25f, (float)0.8f, (float)1.0f);
                ItemBoomerangRenderer.mc.renderEngine.bindTexture(ITEM_GLINT);
                this.renderGlint(-2, -2, 20, 20);
                GL11.glDisable((int)3042);
                GL11.glDepthMask((boolean)true);
                GL11.glEnable((int)2896);
                GL11.glDepthFunc((int)515);
                GL11.glPopMatrix();
            }
            GL11.glEnable((int)2896);
        } else if (type.equals((Object)IItemRenderer.ItemRenderType.ENTITY)) {
            GL11.glTranslatef((float)-0.5f, (float)-0.25f, (float)0.04f);
            GL11.glPushMatrix();
            for (int pass = 0; pass < 2; ++pass) {
                IIcon icon = item.getIconFromDamage(0);
                if (icon == null) continue;
                int color = item.getColorFromItemStack(stack, pass);
                ColorHelper.glSetColor(color);
                if (pass == 0) {
                    this.drawItem(icon, 0.08f);
                    continue;
                }
                GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-0.01f);
                this.drawItem(icon, 0.06f);
            }
            GL11.glPopMatrix();
        } else if (type.equals((Object)IItemRenderer.ItemRenderType.EQUIPPED) || type.equals((Object)IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON)) {
            GL11.glPushMatrix();
            int iconOffset = 0;
            int size = 1;
            float scale = 1.0f;
            float px = 1.0f / (float)(16 * size);
            float scaleOffsetX = 1.0f;
            float scaleOffsetY = 1.0f;
            boolean thirdPerson = !type.equals((Object)IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON);
            boolean ifp = false;
            GL11.glPushMatrix();
            IIcon icon = ((Boomerang)stack.getItem()).getIcon(stack, 0);
            this.drawItem(icon, 0.09375f);
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Gunpowder Modifier") && (icon = ((Boomerang)stack.getItem()).getModifierIconLayer("gunpowder")) != null) {
                this.drawItem(icon, 0.09375f);
            }
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Slime Modifier") && (icon = ((Boomerang)stack.getItem()).getModifierIconLayer("slime")) != null) {
                this.drawItem(icon, 0.09375f);
            }
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Glowstone Modifier") && (icon = ((Boomerang)stack.getItem()).getModifierIconLayer("glowstone")) != null) {
                this.drawItem(icon, 0.09375f);
            }
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Redstone Modifier") && (icon = ((Boomerang)stack.getItem()).getModifierIconLayer("redstone")) != null) {
                this.drawItem(icon, 0.09375f);
            }
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Ender Modifier") && (icon = ((Boomerang)stack.getItem()).getModifierIconLayer("ender")) != null) {
                this.drawItem(icon, 0.09375f);
            }
            GL11.glPopMatrix();
            GL11.glTranslatef((float)scaleOffsetX, (float)scaleOffsetY, (float)0.0f);
            GL11.glScalef((float)scale, (float)scale, (float)1.0f);
            GL11.glTranslatef((float)(-scaleOffsetX), (float)(-scaleOffsetY), (float)0.0f);
            GL11.glPushMatrix();
            if (iconOffset > 0) {
                if (thirdPerson && ifp) {
                    GL11.glRotatef((float)5.0f, (float)1.0f, (float)-1.0f, (float)0.0f);
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-0.03f);
                } else {
                    GL11.glRotatef((float)-5.0f, (float)1.0f, (float)-1.0f, (float)0.0f);
                    GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.03f);
                }
                float offset = (float)(-(iconOffset - 3)) * px;
                GL11.glTranslatef((float)offset, (float)offset, (float)0.0f);
            }
            GL11.glMatrixMode((int)5888);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
    }

    private void renderIcon(int color, IIcon icon) {
        ColorHelper.glSetColor(color, 1.0f);
        GL11.glDisable((int)2896);
        GL11.glEnable((int)3008);
        renderItem.renderIcon(0, 0, icon, 16, 16);
        GL11.glDisable((int)3008);
        GL11.glEnable((int)2896);
    }

    private void drawItem(IIcon icon, float thickness) {
        float xStart = icon.getMinU();
        float xEnd = icon.getMaxU();
        float yStart = icon.getMinV();
        float yEnd = icon.getMaxV();
        int height = icon.getIconHeight();
        int width = icon.getIconWidth();
        ItemRenderer.renderItemIn2D((Tessellator)tessellator, (float)xEnd, (float)yStart, (float)xStart, (float)yEnd, (int)width, (int)height, (float)thickness);
    }

    private void renderGlint(int par2, int par3, int par4, int par5) {
        for (int j1 = 0; j1 < 2; ++j1) {
            if (j1 == 0) {
                GL11.glBlendFunc((int)768, (int)1);
            }
            if (j1 == 1) {
                GL11.glBlendFunc((int)768, (int)1);
            }
            float f = 0.0039063f;
            float f1 = 0.0039063f;
            float f2 = (float)(Minecraft.getGLMaximumTextureSize() % (3000 + j1 * 1873)) / (3000.0f + (float)(j1 * 1873)) * 256.0f;
            float f3 = 0.0f;
            Tessellator tessellator = Tessellator.instance;
            float f4 = 4.0f;
            if (j1 == 1) {
                f4 = -1.0f;
            }
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + par5), -50.0, (double)((f2 + (float)par5 * f4) * f), (double)((f3 + (float)par5) * f1));
            tessellator.addVertexWithUV((double)(par2 + par4), (double)(par3 + par5), -50.0, (double)((f2 + (float)par4 + (float)par5 * f4) * f), (double)((f3 + (float)par5) * f1));
            tessellator.addVertexWithUV((double)(par2 + par4), (double)(par3 + 0), -50.0, (double)((f2 + (float)par4) * f), (double)((f3 + 0.0f) * f1));
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), -50.0, (double)((f2 + 0.0f) * f), (double)((f3 + 0.0f) * f1));
            tessellator.draw();
        }
    }
}


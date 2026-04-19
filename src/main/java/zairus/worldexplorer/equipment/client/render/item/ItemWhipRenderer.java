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
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  org.lwjgl.opengl.GL11
 */
package zairus.worldexplorer.equipment.client.render.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import zairus.worldexplorer.core.ClientProxy;
import zairus.worldexplorer.core.helpers.ColorHelper;
import zairus.worldexplorer.equipment.client.model.ModelWhip;
import zairus.worldexplorer.equipment.items.Whip;

@SideOnly(value=Side.CLIENT)
public class ItemWhipRenderer
implements IItemRenderer {
    private static final ResourceLocation whipTextures = new ResourceLocation("worldexplorer", "textures/model/whip_textures.png");
    private ModelWhip whipModel = new ModelWhip();
    private ModelWhip whipBody = new ModelWhip(ModelWhip.WhipPart.partBody);
    private Tessellator tessellator = Tessellator.instance;
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
        IIcon icon = stack.getItem().getIconFromDamage(0);
        int color = stack.getItem().getColorFromItemStack(stack, 0);
        switch (type) {
            case EQUIPPED: 
            case EQUIPPED_FIRST_PERSON: 
            case ENTITY: {
                float scaleOffsetX = -0.52f;
                float scaleOffsetY = 0.35f;
                float scaleOffsetZ = 0.0f;
                if (!((Whip)stack.getItem()).unleashed) {
                    GL11.glPushMatrix();
                    icon = ((Whip)stack.getItem()).getIconFromUseTick();
                    GL11.glTranslatef((float)scaleOffsetX, (float)scaleOffsetY, (float)scaleOffsetZ);
                    this.drawItem(icon, 0.09375f);
                    GL11.glTranslatef((float)(-scaleOffsetX), (float)(-scaleOffsetY), (float)(-scaleOffsetZ));
                    GL11.glPopMatrix();
                }
                GL11.glPushMatrix();
                ItemWhipRenderer.mc.renderEngine.bindTexture(whipTextures);
                scaleOffsetX = 0.77f;
                scaleOffsetY = 0.08f;
                scaleOffsetZ = -0.105f;
                GL11.glTranslatef((float)scaleOffsetX, (float)scaleOffsetY, (float)scaleOffsetZ);
                GL11.glRotatef((float)45.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                this.whipModel.render((Entity)data[1], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glRotatef((float)-45.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)(-scaleOffsetX), (float)(-scaleOffsetY), (float)(-scaleOffsetZ));
                GL11.glPopMatrix();
                if (!((Whip)stack.getItem()).unleashed) break;
                GL11.glPushMatrix();
                Whip usedWhip = (Whip)stack.getItem();
                ItemWhipRenderer.mc.renderEngine.bindTexture(whipTextures);
                scaleOffsetX = 0.4f;
                scaleOffsetY = 0.5f;
                scaleOffsetZ = -0.08f;
                GL11.glTranslatef((float)scaleOffsetX, (float)scaleOffsetY, (float)scaleOffsetZ);
                float rPitch = usedWhip.getCurPitch() - usedWhip.getThrowPitch();
                float rYaw = usedWhip.getCurYaw() - usedWhip.getThrowYaw();
                if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
                    float swing_perc = (float)usedWhip.getShooter().getItemInUseDuration() / 2.0f;
                    if (swing_perc > 1.0f) {
                        swing_perc = 1.0f;
                    }
                    GL11.glRotatef((float)(rYaw + 5.0f), (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glRotatef((float)(rPitch - 50.0f + 35.0f * (1.0f - usedWhip.getShooter().getSwingProgress(1.0f))), (float)0.0f, (float)0.0f, (float)1.0f);
                } else {
                    float swing_perc = (float)usedWhip.getShooter().getItemInUseDuration() / 5.0f;
                    if (swing_perc > 1.0f) {
                        swing_perc = 1.0f;
                    }
                    GL11.glRotatef((float)(-rYaw - 9.0f - 5.0f * (1.0f - usedWhip.getPercentaje())), (float)1.0f, (float)0.0f, (float)0.0f);
                    GL11.glRotatef((float)(rPitch - 47.0f + 120.0f * (1.0f - usedWhip.getShooter().getSwingProgress(1.0f))), (float)0.0f, (float)0.0f, (float)1.0f);
                }
                GL11.glScaled((double)1.0, (double)((Whip)stack.getItem()).getWhipTipDistance(), (double)1.0);
                this.whipBody.render((Entity)data[1], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glPopMatrix();
                break;
            }
            case INVENTORY: {
                icon = stack.getItem().getIconFromDamage(0);
                ColorHelper.glSetColor(color, 1.0f);
                GL11.glDisable((int)2896);
                GL11.glEnable((int)3008);
                renderItem.renderIcon(0, 0, icon, 16, 16);
                GL11.glDisable((int)3008);
                GL11.glEnable((int)2896);
                break;
            }
        }
    }

    private void drawItem(IIcon icon, float thickness) {
        float xStart = icon.getMinU();
        float xEnd = icon.getMaxU();
        float yStart = icon.getMinV();
        float yEnd = icon.getMaxV();
        int height = icon.getIconHeight();
        int width = icon.getIconWidth();
        ItemRenderer.renderItemIn2D((Tessellator)this.tessellator, (float)xEnd, (float)yStart, (float)xStart, (float)yEnd, (int)width, (int)height, (float)thickness);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.mods.claysoldiers.client.render.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.client.model.tileentity.ModelClayNexus;
import de.sanandrew.mods.claysoldiers.client.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class ItemRendererClayNexus
implements IItemRenderer {
    private ModelClayNexus modelClayNexus = new ModelClayNexus();

    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return true;
    }

    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object ... data) {
        switch (type) {
            case ENTITY: {
                this.renderNexus(0.0f, -0.45f, 0.0f);
                break;
            }
            case EQUIPPED: {
                this.renderNexus(0.5f, 0.4f, 0.5f);
                break;
            }
            case INVENTORY: {
                this.renderNexus(1.0f, 0.26f, 1.0f);
                break;
            }
            default: {
                this.renderNexus(0.0f, 0.4f, 0.5f);
            }
        }
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return true;
    }

    private void renderNexus(float x, float y, float z) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)(y + 1.57f + 0.125f), (float)z);
        GL11.glRotatef((float)180.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glRotatef((float)90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glScalef((float)1.1875f, (float)1.1875f, (float)1.1875f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.NEXUS_TEXTURE);
        this.modelClayNexus.renderTileEntity();
        Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.NEXUS_GLOWING);
        GL11.glEnable((int)3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        this.modelClayNexus.renderTileEntityGlowmap();
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.mods.claysoldiers.client.render.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.client.helpers.ItemRenderHelper;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.client.model.tileentity.ModelClayNexus;
import de.sanandrew.mods.claysoldiers.client.util.Textures;
import de.sanandrew.mods.claysoldiers.item.ItemClayManDoll;
import de.sanandrew.mods.claysoldiers.tileentity.TileEntityClayNexus;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.ASoldierUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.IThrowableUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgrades;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderClayNexus
extends TileEntitySpecialRenderer {
    public ModelClayNexus nexusModel = new ModelClayNexus();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partTicks) {
        ItemStack heldItem;
        TileEntityClayNexus nexus = (TileEntityClayNexus)tileEntity;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)x + 0.5f), (float)((float)y + 1.5f), (float)((float)z + 0.5f));
        GL11.glRotatef((float)180.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        this.bindTexture(Textures.NEXUS_TEXTURE);
        this.nexusModel.renderTileEntity();
        this.renderGlowmap(nexus);
        if (nexus.getStackInSlot(0) != null) {
            RenderClayNexus.renderSoldierItem(nexus, nexus.getStackInSlot(0), partTicks);
        }
        if (nexus.getStackInSlot(1) != null) {
            RenderClayNexus.renderThrowableItem(nexus, nexus.getStackInSlot(1), partTicks);
        }
        if ((heldItem = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem()) != null && heldItem.getItem() == RegistryItems.statDisplay) {
            RenderClayNexus.renderHealth(nexus);
        }
        GL11.glPopMatrix();
    }

    private void renderGlowmap(TileEntityClayNexus nexus) {
        float[] colors = new float[]{1.0f, 1.0f, 1.0f};
        if (nexus.getStackInSlot(0) != null) {
            SAPUtils.RGBAValues rgba = SAPUtils.getRgbaFromColorInt(ItemClayManDoll.getTeam(nexus.getStackInSlot(0)).getIconColor());
            colors[0] = (float)rgba.getRed() / 255.0f;
            colors[1] = (float)rgba.getGreen() / 255.0f;
            colors[2] = (float)rgba.getBlue() / 255.0f;
        }
        GL11.glEnable((int)3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable((int)3008);
        float prevBrightX = OpenGlHelper.lastBrightnessX;
        float prevBrightY = OpenGlHelper.lastBrightnessY;
        if (nexus.isActive) {
            int brightness = 240;
            int brightX = brightness % 65536;
            int brightY = brightness / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
        }
        this.bindTexture(Textures.NEXUS_GLOWING);
        GL11.glColor3f((float)colors[0], (float)colors[1], (float)colors[2]);
        this.nexusModel.renderTileEntityGlowmap();
        GL11.glColor3f((float)1.0f, (float)1.0f, (float)1.0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightX, prevBrightY);
        GL11.glEnable((int)3008);
        GL11.glDisable((int)3042);
    }

    private static void renderSoldierItem(TileEntityClayNexus nexus, ItemStack stack, float partTicks) {
        float[] colors = new float[]{1.0f, 1.0f, 1.0f};
        float itmAngle = nexus.prevSpinAngle + (nexus.spinAngle - nexus.prevSpinAngle) * partTicks - 45.0f;
        SAPUtils.RGBAValues rgba = SAPUtils.getRgbaFromColorInt(ItemClayManDoll.getTeam(stack).getIconColor());
        colors[0] = (float)rgba.getRed() / 255.0f;
        colors[1] = (float)rgba.getGreen() / 255.0f;
        colors[2] = (float)rgba.getBlue() / 255.0f;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)0.0f, (float)1.225f, (float)0.0f);
        GL11.glRotatef((float)180.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glScalef((float)0.25f, (float)0.25f, (float)0.25f);
        GL11.glRotatef((float)itmAngle, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glTranslatef((float)-0.5f, (float)0.0f, (float)0.0f);
        GL11.glColor3f((float)colors[0], (float)colors[1], (float)colors[2]);
        ItemRenderHelper.renderItemIn3D(stack);
        GL11.glColor3f((float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopMatrix();
    }

    private static void renderThrowableItem(TileEntityClayNexus nexus, ItemStack stack, float partTicks) {
        IThrowableUpgrade throwableUpg;
        float itmAngle = nexus.prevSpinAngle + (nexus.spinAngle - nexus.prevSpinAngle) * partTicks - 45.0f;
        ASoldierUpgrade upg = SoldierUpgrades.getUpgrade(stack);
        IThrowableUpgrade iThrowableUpgrade = throwableUpg = upg instanceof IThrowableUpgrade ? (IThrowableUpgrade)((Object)upg) : null;
        if (throwableUpg == null) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float)0.0f, (float)0.875f, (float)0.0f);
        GL11.glRotatef((float)180.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glScalef((float)0.25f, (float)0.25f, (float)0.25f);
        GL11.glRotatef((float)(-itmAngle), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glTranslatef((float)-0.5f, (float)0.0f, (float)0.0f);
        GL11.glColor3f((float)1.0f, (float)1.0f, (float)1.0f);
        throwableUpg.renderNexusThrowable(nexus, partTicks);
        GL11.glColor3f((float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopMatrix();
    }

    private static void renderHealth(TileEntityClayNexus nexus) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)0.0f, (float)0.5f, (float)0.0f);
        GL11.glRotatef((float)RenderManager.instance.playerViewY, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)RenderManager.instance.playerViewX, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2896);
        float healthPerc = Math.min(1.0f, 1.0f - nexus.getHealth() / nexus.getMaxHealth());
        float prevBrightX = OpenGlHelper.lastBrightnessX;
        float prevBrightY = OpenGlHelper.lastBrightnessY;
        int brightness = 240;
        int brightX = brightness % 65536;
        int brightY = brightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(0.0f, 0.0f, 0.0f, 1.0f);
        tessellator.addVertex(-0.5, -0.05, 0.0);
        tessellator.addVertex(-0.5 + (double)healthPerc, -0.05, 0.0);
        tessellator.addVertex(-0.5 + (double)healthPerc, 0.05, 0.0);
        tessellator.addVertex(-0.5, 0.05, 0.0);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1.0f, 0.0f, 0.0f, 1.0f);
        tessellator.addVertex(-0.5 + (double)healthPerc, -0.05, 0.0);
        tessellator.addVertex(0.5, -0.05, 0.0);
        tessellator.addVertex(0.5, 0.05, 0.0);
        tessellator.addVertex(-0.5 + (double)healthPerc, 0.05, 0.0);
        tessellator.draw();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightX, prevBrightY);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)3553);
        GL11.glPopMatrix();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.mods.claysoldiers.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import de.sanandrew.mods.claysoldiers.client.event.SoldierRenderEvent;
import de.sanandrew.mods.claysoldiers.client.render.entity.RenderClayMan;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgrades;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class SoldierBodyRenderHandler {
    private Random p_thunderboldRNG = new Random();
    private ItemStack p_feather = new ItemStack(Items.feather);
    private ItemStack p_glass = new ItemStack(Blocks.glass);
    private ItemStack p_glassStained = new ItemStack(Blocks.stained_glass);

    @SubscribeEvent
    public void onSoldierRender(SoldierRenderEvent event) {
        if (event.stage == SoldierRenderEvent.EnumRenderStage.PRE || event.stage == SoldierRenderEvent.EnumRenderStage.POST) {
            if (event.clayMan.hasUpgrade("egg")) {
                SoldierBodyRenderHandler.renderStealthEffect(event.stage);
            }
            if (event.clayMan.hasUpgrade("glowstone")) {
                SoldierBodyRenderHandler.renderGlowstoneEffect(event.stage);
            }
            if (event.clayMan.hasEffect("thunder") && event.stage == SoldierRenderEvent.EnumRenderStage.PRE) {
                this.renderThunderbolt(event.clayMan, event.renderX, event.renderY, event.renderZ);
            }
        }
        if (event.stage == SoldierRenderEvent.EnumRenderStage.EQUIPPED) {
            if (event.clayMan.hasUpgrade("feather") && !event.clayMan.onGround && event.clayMan.motionY < -0.1 && event.clayMan.fallDistance >= 1.3f) {
                this.renderFeather(event.clayMan, event.clayManRender);
            }
            if (event.clayMan.hasUpgrade("glass")) {
                this.renderGlass(event.clayMan, event.clayManRender);
            }
        }
    }

    @SubscribeEvent
    public void onSoldierLivingRender(SoldierRenderEvent.RenderLivingEvent event) {
        if (event.clayMan.hasUpgrade("iron_ingot")) {
            GL11.glScalef((float)1.19f, (float)1.19f, (float)1.19f);
        }
        if (event.clayMan.hasUpgrade("enderpearl")) {
            GL11.glColor3f((float)0.5f, (float)0.5f, (float)0.5f);
        }
    }

    private static void renderStealthEffect(SoldierRenderEvent.EnumRenderStage stage) {
        if (stage == SoldierRenderEvent.EnumRenderStage.PRE) {
            GL11.glEnable((int)3042);
            GL11.glDisable((int)3008);
            GL11.glBlendFunc((int)1, (int)1);
        } else {
            GL11.glDisable((int)3042);
        }
    }

    private static void renderGlowstoneEffect(SoldierRenderEvent.EnumRenderStage stage) {
        if (stage == SoldierRenderEvent.EnumRenderStage.PRE) {
            int brightness = 240;
            int brightX = brightness % 65536;
            int brightY = brightness / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)brightX / 1.0f, (float)brightY / 1.0f);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }

    private void renderThunderbolt(EntityClayMan clayMan, double targetX, double targetY, double targetZ) {
        NBTTagCompound effectNbt = clayMan.getEffect("thunder").getNbtTag();
        if (effectNbt.getShort("ticksRemaining") < 17) {
            return;
        }
        double originX = effectNbt.getDouble("originX") - clayMan.posX + targetX;
        double originY = effectNbt.getDouble("originY") - clayMan.posY + targetY;
        double originZ = effectNbt.getDouble("originZ") - clayMan.posZ + targetZ;
        ArrayList<Triplet<Double, Double, Double>> randCoords = new ArrayList<Triplet<Double, Double, Double>>();
        randCoords.add(Triplet.with(0.0, 0.0, 0.0));
        this.p_thunderboldRNG.setSeed(effectNbt.getLong("randomLightning"));
        int size = this.p_thunderboldRNG.nextInt(5) + 6;
        for (int i = 0; i < size; ++i) {
            randCoords.add(Triplet.with(this.p_thunderboldRNG.nextDouble() * 0.5 - 0.25, this.p_thunderboldRNG.nextDouble() * 0.5 - 0.25, this.p_thunderboldRNG.nextDouble() * 0.5 - 0.25));
        }
        randCoords.add(Triplet.with(0.0, 0.25, 0.0));
        ++size;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable((int)2896);
        GL11.glDisable((int)2884);
        GL11.glShadeModel((int)7425);
        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;
        int brightness = 240;
        int brightX = brightness % 65536;
        int brightY = brightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)brightX / 1.0f, (float)brightY / 1.0f);
        for (int i = 0; i < size; ++i) {
            Triplet origin = (Triplet)randCoords.get(i);
            Triplet target = (Triplet)randCoords.get(i + 1);
            double oX = originX + (targetX - originX) / (double)size * (double)i + (Double)origin.getValue0();
            double tX = originX + (targetX - originX) / (double)size * (double)(i + 1) + (Double)target.getValue0();
            double oY = originY + (targetY - originY) / (double)size * (double)i + (Double)origin.getValue1();
            double tY = originY + (targetY - originY) / (double)size * (double)(i + 1) + (Double)target.getValue1();
            double oZ = originZ + (targetZ - originZ) / (double)size * (double)i + (Double)origin.getValue2();
            double tZ = originZ + (targetZ - originZ) / (double)size * (double)(i + 1) + (Double)target.getValue2();
            SoldierBodyRenderHandler.drawThunderboldPart(Tessellator.instance, oX, oY, oZ, tX, tY, tZ);
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
        GL11.glShadeModel((int)7424);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
    }

    private void renderFeather(EntityClayMan clayMan, RenderClayMan renderer) {
        GL11.glPushMatrix();
        renderer.modelBipedMain.bipedBody.postRender(0.0625f);
        GL11.glTranslatef((float)0.0f, (float)-0.6f, (float)0.0f);
        float itemScale = 1.5f;
        GL11.glScalef((float)itemScale, (float)itemScale, (float)itemScale);
        GL11.glTranslatef((float)0.6f, (float)0.05f, (float)0.0f);
        GL11.glRotatef((float)22.5f, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)90.0f, (float)-1.0f, (float)0.0f, (float)1.0f);
        renderer.getItemRenderer().renderItem(clayMan, this.p_feather, 0);
        GL11.glPopMatrix();
    }

    private void renderGlass(EntityClayMan clayMan, RenderClayMan renderer) {
        GL11.glPushMatrix();
        renderer.modelBipedMain.bipedHead.postRender(0.0625f);
        GL11.glTranslatef((float)0.0f, (float)-0.6f, (float)0.0f);
        float itemScale = 0.18f;
        GL11.glScalef((float)itemScale, (float)itemScale, (float)itemScale);
        GL11.glTranslatef((float)0.84f, (float)1.5f, (float)-1.1f);
        short color = clayMan.getUpgrade(SoldierUpgrades.getUpgrade("glass")).getNbtTag().getShort("leftColor");
        if (color < 0) {
            renderer.getItemRenderer().renderItem(clayMan, this.p_glass, 0);
        } else {
            this.p_glassStained.setItemDamage(color);
            renderer.getItemRenderer().renderItem(clayMan, this.p_glassStained, 0);
        }
        GL11.glTranslatef((float)-1.68f, (float)0.0f, (float)0.0f);
        color = clayMan.getUpgrade(SoldierUpgrades.getUpgrade("glass")).getNbtTag().getShort("rightColor");
        if (color < 0) {
            renderer.getItemRenderer().renderItem(clayMan, this.p_glass, 0);
        } else {
            this.p_glassStained.setItemDamage(color);
            renderer.getItemRenderer().renderItem(clayMan, this.p_glassStained, 0);
        }
        GL11.glPopMatrix();
    }

    private static void drawThunderboldPart(Tessellator tessellator, double oX, double oY, double oZ, double tX, double tY, double tZ) {
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, 0);
        tessellator.addVertex(tX - 0.05, tY, tZ);
        tessellator.addVertex(oX - 0.05, oY, oZ);
        tessellator.setColorRGBA(255, 255, 255, 255);
        tessellator.addVertex(oX, oY, oZ);
        tessellator.addVertex(tX, tY, tZ);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, 255);
        tessellator.addVertex(oX, oY, oZ);
        tessellator.addVertex(tX, tY, tZ);
        tessellator.setColorRGBA(255, 255, 255, 0);
        tessellator.addVertex(oX + 0.05, oY, oZ);
        tessellator.addVertex(tX + 0.05, tY, tZ);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, 0);
        tessellator.addVertex(tX, tY - 0.05, tZ);
        tessellator.addVertex(oX, oY - 0.05, oZ);
        tessellator.setColorRGBA(255, 255, 255, 255);
        tessellator.addVertex(oX, oY, oZ);
        tessellator.addVertex(tX, tY, tZ);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, 255);
        tessellator.addVertex(oX, oY, oZ);
        tessellator.addVertex(tX, tY, tZ);
        tessellator.setColorRGBA(255, 255, 255, 0);
        tessellator.addVertex(oX, oY + 0.05, oZ);
        tessellator.addVertex(tX, tY + 0.05, tZ);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, 0);
        tessellator.addVertex(tX, tY, tZ - 0.05);
        tessellator.addVertex(oX, oY, oZ - 0.05);
        tessellator.setColorRGBA(255, 255, 255, 255);
        tessellator.addVertex(oX, oY, oZ);
        tessellator.addVertex(tX, tY, tZ);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, 255);
        tessellator.addVertex(oX, oY, oZ);
        tessellator.addVertex(tX, tY, tZ);
        tessellator.setColorRGBA(255, 255, 255, 0);
        tessellator.addVertex(oX, oY, oZ + 0.05);
        tessellator.addVertex(tX, tY, tZ + 0.05);
        tessellator.draw();
    }
}


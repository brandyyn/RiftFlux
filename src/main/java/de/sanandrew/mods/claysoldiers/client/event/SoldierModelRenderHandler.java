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
import de.sanandrew.mods.claysoldiers.client.event.SoldierRenderEvent;
import de.sanandrew.mods.claysoldiers.client.render.entity.RenderClayMan;
import de.sanandrew.mods.claysoldiers.client.util.Textures;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffects;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class SoldierModelRenderHandler {
    public ModelRenderer buffedBody;
    public ModelRenderer armorBody;
    public ModelRenderer armorRightArm;
    public ModelRenderer armorLeftArm;
    public ModelRenderer slimeRightLeg;
    public ModelRenderer slimeLeftLeg;
    public ModelRenderer crown;
    public ModelRenderer lilypantsRightLeg;
    public ModelRenderer lilypantsLeftLeg;
    public ModelRenderer lilypantsBody;
    public ModelRenderer glassStripes;
    private boolean p_isInitialized = false;

    @SubscribeEvent
    public void onSoldierRotationAngles(SoldierRenderEvent.SetRotationAnglesEvent event) {
        if (event.clayMan.hasUpgrade("feather") && !event.clayMan.onGround && event.clayMan.motionY < -0.1 && event.clayMan.fallDistance >= 1.3f) {
            event.model.bipedLeftArm.rotateAngleX = (float)Math.PI;
            event.model.bipedRightArm.rotateAngleX = (float)Math.PI;
        }
        if (event.clayMan.hasUpgrade("enderpearl")) {
            event.model.bipedLeftArm.rotateAngleX = -1.5707964f;
            event.model.bipedRightArm.rotateAngleX = -1.5707964f;
        }
    }

    @SubscribeEvent
    public void onSoldierRenderModel(SoldierRenderEvent.RenderModelEvent event) {
        if (!this.p_isInitialized) {
            this.p_isInitialized = true;
            this.initRenderer(event.clayManRender);
        }
        if (event.clayMan.hasUpgrade("gold_ingot")) {
            SoldierModelRenderHandler.renderGoldHoodie(event.clayMan, event.clayManRender, event.limbSwing, event.limbSwingAmount, event.rotFloat, event.renderYaw, event.pitch, event.partTicks);
        }
        if (event.clayMan.hasUpgrade("gunpowder")) {
            SoldierModelRenderHandler.renderGunpowder(event.clayMan, event.clayManRender, event.limbSwing, event.limbSwingAmount, event.rotFloat, event.renderYaw, event.pitch, event.partTicks);
        }
        if (event.clayMan.hasUpgrade("magmacream")) {
            SoldierModelRenderHandler.renderMagmacream(event.clayMan, event.clayManRender, event.limbSwing, event.limbSwingAmount, event.rotFloat, event.renderYaw, event.pitch, event.partTicks);
        }
        if (event.clayMan.hasUpgrade("diamond") || event.clayMan.hasUpgrade("diamond_block")) {
            SoldierModelRenderHandler.renderCape(event.clayMan, event.clayManRender, event.partTicks, true);
            this.renderCrown(event.clayManRender, event.partTicks, true);
        } else {
            if (event.clayMan.hasUpgrade("paper")) {
                SoldierModelRenderHandler.renderCape(event.clayMan, event.clayManRender, event.partTicks, false);
            }
            if (event.clayMan.hasUpgrade("gold_nugget")) {
                this.renderCrown(event.clayManRender, event.partTicks, false);
            }
        }
        if (event.clayMan.hasUpgrade("iron_ingot")) {
            this.renderIronCoreBuff(event.clayMan, event.clayManRender, event.partTicks);
        }
        if (event.clayMan.hasUpgrade("lilypads")) {
            this.renderLilyPants(event.clayManRender, event.partTicks);
        }
        if (event.clayMan.hasUpgrade("leather")) {
            this.renderHideArmor(event.clayMan, event.clayManRender, event.partTicks, "leather");
        }
        if (event.clayMan.hasUpgrade("glass")) {
            this.renderGoggleStripes(event.clayManRender, event.partTicks);
        }
        if (event.clayMan.hasEffect(SoldierEffects.getEffect("slimefeet"))) {
            this.renderSlimefeet(event.clayManRender, event.partTicks);
        }
    }

    public void initRenderer(RenderClayMan renderClayMan) {
        this.buffedBody = new ModelRenderer(renderClayMan.modelBipedMain, 16, 16);
        this.buffedBody.addBox(-4.0f, 0.0f, -2.0f, 8, 6, 4, 0.0f);
        this.buffedBody.setRotationPoint(0.0f, -0.4f, 0.0f);
        this.armorBody = new ModelRenderer(renderClayMan.modelBipedMain, 16, 16);
        this.armorBody.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, 0.0f);
        this.armorBody.setRotationPoint(0.0f, -0.4f, 0.0f);
        this.armorRightArm = new ModelRenderer(renderClayMan.modelBipedMain, 40, 16);
        this.armorRightArm.addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, 0.0f);
        this.armorRightArm.setRotationPoint(-5.0f, 1.6f, 0.0f);
        this.armorLeftArm = new ModelRenderer(renderClayMan.modelBipedMain, 40, 16);
        this.armorLeftArm.mirror = true;
        this.armorLeftArm.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, 0.0f);
        this.armorLeftArm.setRotationPoint(5.0f, 1.6f, 0.0f);
        this.slimeRightLeg = new ModelRenderer(renderClayMan.modelBipedMain, 0, 24);
        this.slimeRightLeg.addBox(-2.0f, 8.0f, -2.0f, 4, 4, 4, 0.0f);
        this.slimeRightLeg.setRotationPoint(-1.9f, 12.0f, 0.0f);
        this.slimeLeftLeg = new ModelRenderer(renderClayMan.modelBipedMain, 0, 24);
        this.slimeLeftLeg.mirror = true;
        this.slimeLeftLeg.addBox(-2.0f, 8.0f, -2.0f, 4, 4, 4, 0.0f);
        this.slimeLeftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
        this.crown = new ModelRenderer(renderClayMan.modelBipedMain, 0, 0);
        this.crown.addBox(-4.0f, -9.5f, -4.0f, 8, 8, 8, 0.5f);
        this.crown.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.glassStripes = new ModelRenderer(renderClayMan.modelBipedMain, 0, 0);
        this.glassStripes.addBox(-4.0f, -7.35f, -4.0f, 8, 8, 8, 0.01f);
        this.glassStripes.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.lilypantsBody = new ModelRenderer(renderClayMan.modelBipedMain, 16, 16);
        this.lilypantsBody.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, 0.25f);
        this.lilypantsBody.setRotationPoint(0.0f, -0.4f, 0.0f);
        this.lilypantsRightLeg = new ModelRenderer(renderClayMan.modelBipedMain, 0, 16);
        this.lilypantsRightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, 0.25f);
        this.lilypantsRightLeg.setRotationPoint(-1.9f, 12.0f, 0.0f);
        this.lilypantsLeftLeg = new ModelRenderer(renderClayMan.modelBipedMain, 0, 16);
        this.lilypantsLeftLeg.mirror = true;
        this.lilypantsLeftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, 0.25f);
        this.lilypantsLeftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
    }

    private void renderCrown(RenderClayMan clayManRender, float partTicks, boolean isSuper) {
        ModelBiped model = clayManRender.modelBipedMain;
        this.crown.rotateAngleX = model.bipedHead.rotateAngleX;
        this.crown.rotateAngleY = model.bipedHead.rotateAngleY;
        this.crown.rotateAngleZ = model.bipedHead.rotateAngleZ;
        clayManRender.bindTexture(Textures.CLAYMAN_CROWN);
        if (isSuper) {
            GL11.glColor3f((float)0.39f, (float)0.82f, (float)0.742f);
        } else {
            GL11.glColor3f((float)1.0f, (float)0.9f, (float)0.0f);
        }
        this.crown.render(partTicks);
        GL11.glColor3f((float)1.0f, (float)1.0f, (float)1.0f);
    }

    private void renderIronCoreBuff(EntityClayMan clayMan, RenderClayMan clayManRender, float partTicks) {
        ModelBiped model = clayManRender.modelBipedMain;
        this.buffedBody.rotateAngleX = model.bipedBody.rotateAngleX;
        this.buffedBody.rotateAngleY = model.bipedBody.rotateAngleY;
        this.buffedBody.rotateAngleZ = model.bipedBody.rotateAngleZ;
        clayManRender.bindTexture(clayMan.getTexture());
        GL11.glPushMatrix();
        GL11.glScalef((float)1.5f, (float)1.5f, (float)1.5f);
        this.buffedBody.render(partTicks);
        GL11.glPopMatrix();
    }

    private void renderLilyPants(RenderClayMan clayManRender, float partTicks) {
        ModelBiped model = clayManRender.modelBipedMain;
        this.lilypantsBody.rotateAngleX = model.bipedBody.rotateAngleX;
        this.lilypantsBody.rotateAngleY = model.bipedBody.rotateAngleY;
        this.lilypantsBody.rotateAngleZ = model.bipedBody.rotateAngleZ;
        this.lilypantsLeftLeg.rotateAngleX = model.bipedLeftLeg.rotateAngleX;
        this.lilypantsLeftLeg.rotateAngleY = model.bipedLeftLeg.rotateAngleY;
        this.lilypantsLeftLeg.rotateAngleZ = model.bipedLeftLeg.rotateAngleZ;
        this.lilypantsRightLeg.rotateAngleX = model.bipedRightLeg.rotateAngleX;
        this.lilypantsRightLeg.rotateAngleY = model.bipedRightLeg.rotateAngleY;
        this.lilypantsRightLeg.rotateAngleZ = model.bipedRightLeg.rotateAngleZ;
        clayManRender.bindTexture(Textures.CLAYMAN_LILYPANTS);
        this.lilypantsBody.render(partTicks);
        this.lilypantsLeftLeg.render(partTicks);
        this.lilypantsRightLeg.render(partTicks);
    }

    private void renderHideArmor(EntityClayMan clayMan, RenderClayMan clayManRender, float partTicks, String armorUpgrade) {
        ModelBiped model = clayManRender.modelBipedMain;
        this.armorBody.rotateAngleX = model.bipedBody.rotateAngleX;
        this.armorBody.rotateAngleY = model.bipedBody.rotateAngleY;
        this.armorBody.rotateAngleZ = model.bipedBody.rotateAngleZ;
        this.armorLeftArm.rotateAngleX = model.bipedLeftArm.rotateAngleX;
        this.armorLeftArm.rotateAngleY = model.bipedLeftArm.rotateAngleY;
        this.armorLeftArm.rotateAngleZ = model.bipedLeftArm.rotateAngleZ;
        this.armorRightArm.rotateAngleX = model.bipedRightArm.rotateAngleX;
        this.armorRightArm.rotateAngleY = model.bipedRightArm.rotateAngleY;
        this.armorRightArm.rotateAngleZ = model.bipedRightArm.rotateAngleZ;
        switch (armorUpgrade) {
            case "leather": {
                clayManRender.bindTexture(Textures.CLAYMAN_LEATHER_ARMOR);
                break;
            }
            case "not_implemented_rabbit_hide": {
                clayManRender.bindTexture(Textures.CLAYMAN_LEATHER_ARMOR);
                break;
            }
            default: {
                return;
            }
        }
        GL11.glPushMatrix();
        GL11.glScalef((float)1.2f, (float)1.2f, (float)1.2f);
        this.armorBody.render(partTicks);
        this.armorLeftArm.render(partTicks);
        this.armorRightArm.render(partTicks);
        if (clayMan.hasUpgrade("iron_ingot")) {
            this.buffedBody.render(partTicks);
        }
        GL11.glPopMatrix();
        if (clayMan.hasUpgrade("wool")) {
            float[] color = SoldierModelRenderHandler.getSplittedColor(clayMan.getMiscColor());
            clayManRender.bindTexture(Textures.CLAYMAN_PADDING);
            GL11.glPushMatrix();
            GL11.glScalef((float)1.1f, (float)1.1f, (float)1.1f);
            GL11.glColor3f((float)color[0], (float)color[1], (float)color[2]);
            this.armorBody.render(partTicks);
            this.armorLeftArm.render(partTicks);
            this.armorRightArm.render(partTicks);
            GL11.glPopMatrix();
        }
    }

    private void renderGoggleStripes(RenderClayMan clayManRender, float partTicks) {
        ModelBiped model = clayManRender.modelBipedMain;
        this.glassStripes.rotateAngleX = model.bipedHead.rotateAngleX;
        this.glassStripes.rotateAngleY = model.bipedHead.rotateAngleY;
        this.glassStripes.rotateAngleZ = model.bipedHead.rotateAngleZ;
        clayManRender.bindTexture(Textures.CLAYMAN_GOGGLES);
        this.glassStripes.render(partTicks);
    }

    private void renderSlimefeet(RenderClayMan clayManRender, float partTicks) {
        ModelBiped model = clayManRender.modelBipedMain;
        this.slimeLeftLeg.rotateAngleX = model.bipedLeftLeg.rotateAngleX;
        this.slimeLeftLeg.rotateAngleY = model.bipedLeftLeg.rotateAngleY;
        this.slimeLeftLeg.rotateAngleZ = model.bipedLeftLeg.rotateAngleZ;
        this.slimeRightLeg.rotateAngleX = model.bipedRightLeg.rotateAngleX;
        this.slimeRightLeg.rotateAngleY = model.bipedRightLeg.rotateAngleY;
        this.slimeRightLeg.rotateAngleZ = model.bipedRightLeg.rotateAngleZ;
        clayManRender.bindTexture(Textures.CLAYMAN_SLIMEFEET);
        GL11.glPushMatrix();
        GL11.glScalef((float)1.2f, (float)1.2f, (float)1.2f);
        GL11.glTranslatef((float)0.0f, (float)-0.2f, (float)0.0f);
        this.slimeLeftLeg.render(partTicks);
        this.slimeRightLeg.render(partTicks);
        GL11.glPopMatrix();
    }

    private static void renderGoldHoodie(EntityClayMan clayMan, RenderClayMan clayManRender, float limbSwing, float limbSwingAmount, float rotFloat, float yaw, float pitch, float partTicks) {
        clayManRender.bindTexture(Textures.CLAYMAN_GOLD_HOODIE);
        GL11.glPushMatrix();
        clayManRender.modelBipedMain.render(clayMan, limbSwing, limbSwingAmount, rotFloat, yaw, pitch, partTicks);
        GL11.glPopMatrix();
    }

    private static void renderGunpowder(EntityClayMan clayMan, RenderClayMan clayManRender, float limbSwing, float limbSwingAmount, float rotFloat, float yaw, float pitch, float partTicks) {
        clayManRender.bindTexture(Textures.CLAYMAN_GUNPOWDER);
        GL11.glPushMatrix();
        clayManRender.modelBipedMain.render(clayMan, limbSwing, limbSwingAmount, rotFloat, yaw, pitch, partTicks);
        GL11.glPopMatrix();
    }

    private static void renderMagmacream(EntityClayMan clayMan, RenderClayMan clayManRender, float limbSwing, float limbSwingAmount, float rotFloat, float yaw, float pitch, float partTicks) {
        clayManRender.bindTexture(Textures.CLAYMAN_MAGMACREAM);
        GL11.glPushMatrix();
        clayManRender.modelBipedMain.render(clayMan, limbSwing, limbSwingAmount, rotFloat, yaw, pitch, partTicks);
        GL11.glPopMatrix();
    }

    private static void renderCape(EntityClayMan clayMan, RenderClayMan clayManRender, float partTicks, boolean isSuper) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)0.175f);
        double swingProgX = SoldierModelRenderHandler.calcSwingProgress(clayMan.cloakHelper.swingPosX, clayMan.cloakHelper.prevSwingPosX, clayMan.posX, clayMan.prevPosX, partTicks);
        double swingProgY = SoldierModelRenderHandler.calcSwingProgress(clayMan.cloakHelper.swingPosY, clayMan.cloakHelper.prevSwingPosY, clayMan.posY, clayMan.prevPosY, partTicks) * 10.0;
        double swingProgZ = SoldierModelRenderHandler.calcSwingProgress(clayMan.cloakHelper.swingPosZ, clayMan.cloakHelper.prevSwingPosZ, clayMan.posZ, clayMan.prevPosZ, partTicks);
        float yawOffProg = clayMan.prevRenderYawOffset + (clayMan.renderYawOffset - clayMan.prevRenderYawOffset) * partTicks;
        double yawOffProgSin = MathHelper.sin(yawOffProg * (float)Math.PI / 180.0f);
        double yawOffProgNCos = -MathHelper.cos(yawOffProg * (float)Math.PI / 180.0f);
        if (swingProgY < -6.0) {
            swingProgY = -6.0;
        }
        if (swingProgY > 32.0) {
            swingProgY = 32.0;
        }
        float swingXZSin = (float)(swingProgX * yawOffProgSin + swingProgZ * yawOffProgNCos) * 100.0f;
        float swingXZNCos = (float)(swingProgX * yawOffProgNCos - swingProgZ * yawOffProgSin) * 100.0f;
        if (swingXZSin < 0.0f) {
            swingXZSin = 0.0f;
        }
        GL11.glRotatef((float)(6.0f + swingXZSin / 2.0f + (float)swingProgY), (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glRotatef((float)(swingXZNCos / 2.0f), (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glRotatef((float)(-swingXZNCos / 2.0f), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)180.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        if (isSuper) {
            clayManRender.bindTexture(Textures.CLAYMAN_CAPE_DIAMOND);
        } else {
            float[] color = SoldierModelRenderHandler.getSplittedColor(clayMan.getMiscColor());
            GL11.glColor3f((float)color[0], (float)color[1], (float)color[2]);
            clayManRender.bindTexture(Textures.CLAYMAN_CAPE_BLANK);
        }
        clayManRender.modelBipedMain.bipedCloak.render(0.0625f);
        GL11.glColor3f((float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopMatrix();
    }

    private static double calcSwingProgress(double swingPos, double prevSwingPos, double pos, double prevPos, float partTicks) {
        double swingProg = prevSwingPos + (swingPos - prevSwingPos) * (double)partTicks;
        double posProg = prevPos + (pos - prevPos) * (double)partTicks;
        return swingProg - posProg;
    }

    public static float[] getSplittedColor(int color) {
        float[] splitColor = new float[]{(float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f};
        return splitColor;
    }
}


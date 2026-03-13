/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.Vec3
 *  org.lwjgl.opengl.GL11
 */
package net.nmccoy.legendgear.render;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.nmccoy.legendgear.MiniParticle;
import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.entity.SpellDecorator;
import net.nmccoy.legendgear.render.RenderSpellReticle;
import org.lwjgl.opengl.GL11;

public class RenderSpellDecoration
extends Render {
    public void renderMiniParticle(MiniParticle mip, int mode, float subtick, double power) {
        GL11.glPushMatrix();
        GL11.glTranslated((double)(mip.x + mip.vx * (double)subtick), (double)(mip.y + mip.vy * (double)subtick), (double)(mip.z + mip.vz * (double)subtick));
        double age = mip.age + (double)(subtick / (float)mip.maxLife);
        if (mode == EntitySpellEffect.SpellType.Twinkle.ordinal()) {
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glColor4d((double)1.0, (double)(1.5 - age), (double)(0.5 + age), (double)1.0);
            GL11.glLineWidth((float)2.0f);
            this.drawCross(2, Math.sin(age * Math.PI) * (power - 1.0) / 4.0, 1.5707963267948966);
        }
        if (mode == EntitySpellEffect.SpellType.Fire1.ordinal()) {
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glColor4d((double)1.0, (double)(2.0 - age * 2.0), (double)(1.0 - age * 2.0), (double)1.0);
            this.drawPolySolid(4, Math.sin(age * Math.PI * 3.0 / 4.0 + 0.7853981633974483) * power / 8.0, 1.5707963267948966 + age * Math.PI * 2.0);
        }
        if (mode == EntitySpellEffect.SpellType.Lightning1.ordinal()) {
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glLineWidth((float)2.0f);
            boolean side = true;
            GL11.glPushMatrix();
            double s = power * 0.15;
            if (age < 0.5) {
                double f = Math.sin((age + mip.uniqueness) * 30.0) * 0.5 + 0.5;
                GL11.glColor3d((double)(f * 2.0), (double)1.0, (double)(2.0 - f * 2.0));
                this.diamondZig(s, s * 2.0, Math.sin(age * Math.PI * 4.0));
            } else {
                GL11.glBegin((int)1);
                double phase = age * 2.0 - 1.0;
                double f = Math.sin((age + mip.uniqueness) * 30.0) * 0.5 + 0.5;
                GL11.glColor3d((double)(f * 2.0), (double)1.0, (double)(2.0 - f * 2.0));
                double h = Math.sin(phase * Math.PI) * 2.5;
                GL11.glVertex3d((double)(-mip.x), (double)(-mip.y), (double)(-mip.z));
                GL11.glVertex3d((double)(mip.x * h - mip.x), (double)(mip.y * h - mip.y), (double)(mip.z * h - mip.z));
                GL11.glEnd();
            }
            GL11.glPopMatrix();
        }
        if (mode == EntitySpellEffect.SpellType.Ice1.ordinal()) {
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glColor4d((double)(0.5 + (1.0 - Math.sin(age * Math.PI)) * 0.5), (double)(0.5 * (1.0 - Math.sin(age * Math.PI)) + 0.5), (double)1.0, (double)1.0);
            double size = power / 4.0;
            if (age < 0.5) {
                this.drawCross(3, size * (1.0 - age * 2.0) * 2.0, 1.5707963267948966);
            } else if (age < 0.75) {
                this.drawPolySolid(6, size * (age - 0.5) * 4.0, 1.5707963267948966);
            } else {
                this.drawPolyOutline(6, size, 1.5707963267948966);
            }
        }
        if (mode == EntitySpellEffect.SpellType.SprinkleStardust.ordinal()) {
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glColor4d((double)1.0, (double)(1.5 - age), (double)(0.5 + age), (double)1.0);
            GL11.glLineWidth((float)2.0f);
            this.drawPinchDiamond(age, 0.25, 0.0);
        }
        if (mode == EntitySpellEffect.SpellType.ScytheWind.ordinal()) {
            double waxwane = Math.sin(age * Math.PI);
            GL11.glColor4d((double)(0.7 + 0.3 * waxwane), (double)(0.7 + 0.3 * waxwane), (double)(0.8 + 0.2 * waxwane), (double)1.0);
            Vec3 toAxis = Vec3.createVectorHelper((double)(-mip.x), (double)0.0, (double)(-mip.z));
            GL11.glTranslated((double)toAxis.xCoord, (double)0.0, (double)toAxis.zCoord);
            this.drawScythe(waxwane, toAxis.lengthVector() * 1.2 * waxwane, Math.atan2(mip.z, mip.x) * 180.0 / Math.PI + age * 360.0 * 4.0);
        }
        if (mode == EntitySpellEffect.SpellType.Rayfire.ordinal()) {
            GL11.glBlendFunc((int)770, (int)1);
            GL11.glColor4d((double)1.0, (double)(1.5 - age * 1.5), (double)(age * 1.5), (double)1.0);
            GL11.glPushMatrix();
            GL11.glScaled((double)1.0, (double)8.0, (double)1.0);
            this.drawPinchDiamond(age, 1.5, 0.0);
            GL11.glPopMatrix();
        }
        if (mode == EntitySpellEffect.SpellType.Exit.ordinal()) {
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glLineWidth((float)5.0f);
            GL11.glColor4d((double)(1.5 - age * 1.5), (double)(age * 1.5), (double)1.0, (double)1.0);
            GL11.glPushMatrix();
            this.drawPolyOutline(4, Math.sin(age * Math.PI) * 5.0, 0.0);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }

    public void drawScythe(double phase, double size, double tilt) {
        GL11.glPushMatrix();
        GL11.glScaled((double)size, (double)size, (double)size);
        GL11.glRotated((double)tilt, (double)0.0, (double)1.0, (double)0.0);
        GL11.glDisable((int)2884);
        GL11.glBegin((int)4);
        GL11.glVertex3d((double)1.0, (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.7, (double)0.0, (double)0.7);
        GL11.glVertex3d((double)(1.0 - phase * 0.3), (double)0.0, (double)0.0);
        GL11.glVertex3d((double)(1.0 - phase * 0.3), (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.7, (double)0.0, (double)-0.7);
        GL11.glVertex3d((double)1.0, (double)0.0, (double)0.0);
        GL11.glEnd();
        GL11.glEnable((int)2884);
        GL11.glPopMatrix();
    }

    public void drawDiamondCrescent(double phase, double size, double tilt) {
        GL11.glPushMatrix();
        this.billboardTransform();
        double top = 1.0;
        if (phase < 0.5) {
            top = -Math.cos(phase * 2.0 * Math.PI);
        }
        double bottom = -1.0;
        if (phase > 0.5) {
            bottom = -Math.cos((phase - 0.5) * 2.0 * Math.PI);
        }
        GL11.glBegin((int)4);
        GL11.glVertex3d((double)0.0, (double)(bottom * size), (double)0.0);
        GL11.glVertex3d((double)size, (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.0, (double)(top * size), (double)0.0);
        GL11.glVertex3d((double)0.0, (double)(top * size), (double)0.0);
        GL11.glVertex3d((double)(-size), (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.0, (double)(bottom * size), (double)0.0);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void drawPinchDiamond(double phase, double size, double tilt) {
        GL11.glPushMatrix();
        this.billboardTransform();
        double sy = phase * phase;
        double sx = (1.0 - phase) * (1.0 - phase);
        GL11.glScaled((double)size, (double)size, (double)size);
        GL11.glRotated((double)tilt, (double)0.0, (double)0.0, (double)1.0);
        GL11.glBegin((int)6);
        GL11.glVertex3d((double)0.0, (double)sy, (double)0.0);
        GL11.glVertex3d((double)(-sx), (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.0, (double)(-sy), (double)0.0);
        GL11.glVertex3d((double)sx, (double)0.0, (double)0.0);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void diamondZig(double w, double h, double phase) {
        GL11.glPushMatrix();
        this.billboardTransform();
        GL11.glBegin((int)3);
        GL11.glVertex3d((double)0.0, (double)((1.0 - Math.abs(phase)) * h), (double)0.0);
        GL11.glVertex3d((double)(-phase * w), (double)0.0, (double)0.0);
        GL11.glVertex3d((double)(phase * w), (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.0, (double)(-(1.0 - Math.abs(phase)) * h), (double)0.0);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void drawCross(int spikes, double size, double tilt) {
        GL11.glPushMatrix();
        this.billboardTransform();
        GL11.glBegin((int)1);
        double dTh = Math.PI / (double)spikes;
        for (int i = 0; i < spikes; ++i) {
            double theta = (double)i * dTh + tilt;
            double x = Math.cos(theta) * size;
            double y = Math.sin(theta) * size;
            GL11.glVertex3d((double)x, (double)y, (double)0.0);
            GL11.glVertex3d((double)(-x), (double)(-y), (double)0.0);
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void drawPolySolid(int spikes, double size, double tilt) {
        GL11.glPushMatrix();
        this.billboardTransform();
        GL11.glBegin((int)6);
        double dTh = Math.PI * 2 / (double)spikes;
        for (int i = 0; i < spikes; ++i) {
            double theta = (double)i * dTh + tilt;
            double x = Math.cos(theta) * size;
            double y = Math.sin(theta) * size;
            GL11.glVertex3d((double)x, (double)y, (double)0.0);
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void drawPolyOutline(int spikes, double size, double tilt) {
        GL11.glPushMatrix();
        this.billboardTransform();
        GL11.glBegin((int)2);
        double dTh = Math.PI * 2 / (double)spikes;
        for (int i = 0; i < spikes; ++i) {
            double theta = (double)i * dTh + tilt;
            double x = Math.cos(theta) * size;
            double y = Math.sin(theta) * size;
            GL11.glVertex3d((double)x, (double)y, (double)0.0);
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void drawStar(int spikes, double tightness, double size, double tilt) {
        GL11.glPushMatrix();
        this.billboardTransform();
        GL11.glBegin((int)6);
        GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
        double dTh = Math.PI * 2 / (double)spikes;
        for (int i = 0; i < spikes; ++i) {
            double theta = (double)i * dTh + tilt;
            double x = Math.cos(theta) * size;
            double y = Math.sin(theta) * size;
            GL11.glVertex3d((double)x, (double)y, (double)0.0);
            x = Math.cos(theta += dTh * 0.5) * size * tightness;
            y = Math.sin(theta) * size * tightness;
            GL11.glVertex3d((double)x, (double)y, (double)0.0);
        }
        double theta = tilt;
        double x = Math.cos(theta) * size;
        double y = Math.sin(theta) * size;
        GL11.glVertex3d((double)x, (double)y, (double)0.0);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    private void renderSpellEffect(SpellDecorator dec, float subtick) {
        if (dec.particles == null) {
            return;
        }
        for (MiniParticle mip : dec.particles) {
            if (mip.hibernateTime > 0) continue;
            this.renderMiniParticle(mip, dec.spellType, subtick, dec.power);
        }
    }

    private void billboardTransform() {
        GL11.glRotatef((float)(180.0f - this.renderManager.playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(-this.renderManager.playerViewX), (float)1.0f, (float)0.0f, (float)0.0f);
    }

    public void doRender(Entity entity, double par2, double par4, double par6, float notSubtick, float subtick) {
        float burstTime;
        float phase;
        float prevBrightnessX = OpenGlHelper.lastBrightnessX;
        float prevBrightnessY = OpenGlHelper.lastBrightnessY;
        GL11.glPushAttrib((int)GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            GL11.glTranslatef((float)((float)par2), (float)((float)par4), (float)((float)par6));
            GL11.glEnable((int)32826);
            GL11.glDisable((int)2896);
            GL11.glDisable((int)3553);
            GL11.glEnable((int)3042);
            OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
            SpellDecorator dec = (SpellDecorator)entity;
            this.renderSpellEffect(dec, subtick);
            if (dec.isCrit && (phase = (burstTime = (float)dec.longLife + subtick) / 10.0f) <= 1.0f) {
                GL11.glBlendFunc((int)770, (int)1);
                GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)(1.0f - phase));
                GL11.glLineWidth((float)2.0f);
                RenderSpellReticle.drawHorizontalRing(0.0, (double)phase * 0.3, 0.0, dec.radius, 8);
                RenderSpellReticle.drawHorizontalRing(0.0, (double)(-phase) * 0.3, 0.0, dec.radius, 8);
            }
        }
        finally {
            OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)prevBrightnessX, (float)prevBrightnessY);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return null;
    }
}

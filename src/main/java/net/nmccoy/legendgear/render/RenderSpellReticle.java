/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityClientPlayerMP
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Vec3
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  org.lwjgl.opengl.GL11
 */
package net.nmccoy.legendgear.render;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import com.voidsrift.riftflux.compat.BackhandCompat;
import com.voidsrift.riftflux.compat.BattlegearCompat;
import net.nmccoy.legendgear.item.spell.SpellItem;
import org.lwjgl.opengl.GL11;

public class RenderSpellReticle {
    public static void drawHorizontalRing(double x, double y, double z, double radius, int segments) {
        RenderSpellReticle.drawHorizontalRing(x, y, z, radius, segments, 0.0f);
    }

    public static void drawHorizontalRing(double x, double y, double z, double radius, int segments, float twist) {
        RenderSpellReticle.drawHorizontalRing(x, y, z, radius, segments, twist, 1);
    }

    public static void drawHorizontalRing(double x, double y, double z, double radius, int segments, float twist, int stride) {
        GL11.glBegin((int)2);
        for (int i = 0; i < segments; ++i) {
            double theta = Math.PI * 2 / (double)segments * (double)(i * stride) + Math.PI * 2 * ((double)twist + 0.125);
            double dx = Math.cos(theta) * radius;
            double dz = Math.sin(theta) * radius;
            GL11.glVertex3d((double)(x + dx), (double)y, (double)(z + dz));
        }
        GL11.glEnd();
    }

    public static void drawFancyReticle(EntityPlayer player, Vec3 pos, double radius, float progress, boolean tint, boolean casting) {
        float phase = (float)(Minecraft.getSystemTime() % 1000L) / 1000.0f;
        GL11.glAlphaFunc((int)516, (float)0.02f);
        GL11.glLineWidth((float)1.0f);
        GL11.glDepthFunc((int)515);
        GL11.glColor4ub((byte)-1, (byte)-1, (byte)-1, (byte)64);
        RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord + radius * (double)0.866f, pos.zCoord, radius / 2.0, 8, 0.0f, 5);
        RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord - radius * (double)0.866f, pos.zCoord, radius / 2.0, 8, 0.0f, 5);
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)pos.xCoord, (double)pos.yCoord, (double)pos.zCoord);
        GL11.glVertex3d((double)pos.xCoord, (double)(pos.yCoord - radius), (double)pos.zCoord);
        GL11.glEnd();
        if (!casting) {
            GL11.glLineWidth((float)2.0f);
            GL11.glDepthFunc((int)515);
            GL11.glColor4ub((byte)-1, (byte)-1, (byte)-1, (byte)-128);
            if (tint) {
                GL11.glColor4ub((byte)-1, (byte)-1, (byte)-128, (byte)-128);
            }
            RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord, pos.zCoord, radius, 8);
            GL11.glDepthFunc((int)516);
            GL11.glColor4ub((byte)-1, (byte)-1, (byte)-1, (byte)32);
            if (tint) {
                GL11.glColor4ub((byte)-1, (byte)-1, (byte)-128, (byte)32);
            }
            RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord, pos.zCoord, radius, 8);
        } else if (progress < 1.0f) {
            GL11.glLineWidth((float)3.0f);
            GL11.glDepthFunc((int)515);
            GL11.glColor4f((float)1.0f, (float)progress, (float)0.0f, (float)1.0f);
            RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord, pos.zCoord, radius, 8);
            GL11.glDepthFunc((int)516);
            GL11.glColor4f((float)1.0f, (float)progress, (float)0.0f, (float)0.3f);
            RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord, pos.zCoord, radius, 8);
            GL11.glLineWidth((float)2.0f);
            GL11.glDepthFunc((int)515);
            GL11.glColor4f((float)1.0f, (float)progress, (float)0.0f, (float)1.0f);
            RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord, pos.zCoord, radius * (double)progress, 8, -progress);
            GL11.glDepthFunc((int)516);
            GL11.glColor4f((float)1.0f, (float)progress, (float)0.0f, (float)0.3f);
            RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord, pos.zCoord, radius * (double)progress, 8, -progress);
            double flux1 = 1.0 - Math.cos((double)progress * Math.PI / 2.0);
            double flux2 = 1.0 - Math.cos((double)progress * Math.PI * 3.0 / 2.0);
            double flux3 = 1.0 - Math.cos((double)progress * Math.PI * 5.0 / 2.0);
            GL11.glLineWidth((float)2.0f);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)(0.5f * progress));
            GL11.glDepthFunc((int)515);
            RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord, pos.zCoord, radius * flux1, 8, progress / 2.0f, 5);
            RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord, pos.zCoord, radius * flux2, 8, progress, 5);
            RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord, pos.zCoord, radius * flux3, 8, -progress / 2.0f, 5);
        } else {
            phase = (float)Math.sin((double)phase * Math.PI * 2.0 * 10.0) / 2.0f + 0.5f;
            GL11.glLineWidth((float)3.0f);
            GL11.glDepthFunc((int)515);
            GL11.glColor4f((float)1.0f, (float)(phase * 0.5f + 0.5f), (float)((1.0f - phase) * 0.5f + 0.5f), (float)1.0f);
            RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord, pos.zCoord, radius, 8);
            GL11.glLineWidth((float)2.0f);
            RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord, pos.zCoord, radius, 8, 0.0f, 5);
            GL11.glDepthFunc((int)516);
            GL11.glColor4f((float)1.0f, (float)(phase * 0.5f + 0.5f), (float)((1.0f - phase) * 0.5f + 0.5f), (float)0.3f);
            GL11.glLineWidth((float)3.0f);
            RenderSpellReticle.drawHorizontalRing(pos.xCoord, pos.yCoord, pos.zCoord, radius, 8);
        }
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        if (IsometricPhotoModeController.instance().isActive()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        EntityClientPlayerMP player = mc.thePlayer;
        ActiveSpellSelection selection = this.selectActiveSpell(player);
        ItemStack weapon = selection.stack;
        if (weapon == null) {
            return;
        }

        boolean casting = this.isUsingSelectedSpell(player, weapon, selection.fromOffhand);
        SpellItem spell = (SpellItem)weapon.getItem();
        if (spell.hideIdleReticle && !casting) {
            return;
        }

        double playerX = player.prevPosX + (player.posX - player.prevPosX) * (double)event.partialTicks;
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * (double)event.partialTicks;
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)event.partialTicks;
        Vec3 playerLook = player.getLookVec();
        Vec3 playerEye = player.getPosition(event.partialTicks);
        Vec3 alsoFrom = Vec3.createVectorHelper((double)playerEye.xCoord, (double)playerEye.yCoord, (double)playerEye.zCoord);
        double castRange = spell.getEffectiveCastRange(weapon, player);
        double castRadius = spell.getEffectiveCastRadius(weapon, player);
        boolean rayhit = false;
        Vec3 targetPos = SpellItem.getRayTargetResult(playerEye, playerLook, castRange, player.worldObj, spell.hitsWater);
        if (targetPos.distanceTo(alsoFrom) < castRange) {
            rayhit = true;
        }
        float retreat = -0.015625f;
        targetPos = targetPos.addVector(playerLook.xCoord * (double)retreat, playerLook.yCoord * (double)retreat, playerLook.zCoord * (double)retreat);

        float prevBrightnessX = OpenGlHelper.lastBrightnessX;
        float prevBrightnessY = OpenGlHelper.lastBrightnessY;
        GL11.glPushAttrib((int)GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated((double)(-playerX), (double)(-playerY), (double)(-playerZ));
            GL11.glDisable((int)2896);
            GL11.glDisable((int)3553);
            GL11.glDepthMask((boolean)false);
            OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)771);
            float castProgress = casting ? spell.getCastingProgress(weapon, (EntityPlayer)player, event.partialTicks) : 0.0f;
            RenderSpellReticle.drawFancyReticle((EntityPlayer)player, targetPos, castRadius, castProgress, rayhit, casting);
        }
        finally {
            OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)prevBrightnessX, (float)prevBrightnessY);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static boolean isSpellItem(ItemStack stack) {
        return stack != null && stack.getItem() instanceof SpellItem;
    }

    private ActiveSpellSelection selectActiveSpell(EntityClientPlayerMP player) {
        ItemStack mainhand = player.getHeldItem();
        if (BackhandCompat.isAvailable()) {
            mainhand = BackhandCompat.getMainhandItem(player);
        }
        ItemStack offhand = resolveOffhandItem(player);
        if (isSpellItem(offhand) && BackhandCompat.isAvailable() && BackhandCompat.isUsingOffhand(player)) {
            return new ActiveSpellSelection(offhand, true);
        }

        boolean mainSpell = isSpellItem(mainhand);
        mainSpell = isSpellItem(mainhand);
        boolean offhandSpell = isSpellItem(offhand);
        ItemStack inUse = player.getItemInUse();

        if (mainSpell && offhandSpell) {
            if (inUse == offhand) {
                return new ActiveSpellSelection(offhand, true);
            }
            if (inUse == mainhand) {
                return new ActiveSpellSelection(mainhand, false);
            }
            return new ActiveSpellSelection(offhand, true);
        }
        if (mainSpell) {
            return new ActiveSpellSelection(mainhand, false);
        }
        if (offhandSpell) {
            return new ActiveSpellSelection(offhand, true);
        }
        return new ActiveSpellSelection(null, false);
    }

    private boolean isUsingSelectedSpell(EntityClientPlayerMP player, ItemStack selected, boolean fromOffhand) {
        if (player == null || selected == null) {
            return false;
        }

        ItemStack inUse = player.getItemInUse();
        if (inUse == selected) {
            return true;
        }

        if (!BackhandCompat.isAvailable() && !BattlegearCompat.isAvailable()) {
            return player.isUsingItem() && inUse != null && ItemStack.areItemStacksEqual(inUse, selected);
        }

        if (fromOffhand) {
            if (isOffhandInUse(player, selected) || inUse == selected) {
                return true;
            }
        } else if (BackhandCompat.isMainhandUsingItem(player)) {
            return true;
        }

        return player.isUsingItem() && inUse != null && ItemStack.areItemStacksEqual(inUse, selected);
    }

    private static ItemStack resolveOffhandItem(EntityClientPlayerMP player) {
        ItemStack backhandOffhand = null;
        if (BackhandCompat.isAvailable()) {
            ItemStack held = player == null ? null : player.getHeldItem();
            if (isSpellItem(held)
                    && (BackhandCompat.isUsingOffhand(player)
                    || BackhandCompat.isOffhandItemInUse(player)
                    || BackhandCompat.isOffhandStack(player, held))) {
                return held;
            }
            backhandOffhand = BackhandCompat.getOffhandItem(player);
        }

        ItemStack battlegearOffhand = null;
        if (BattlegearCompat.isAvailable() && BattlegearCompat.isBattlemode(player)) {
            battlegearOffhand = BattlegearCompat.getOffhandItem(player);
        }

        if (isSpellItem(backhandOffhand)) {
            return backhandOffhand;
        }
        if (isSpellItem(battlegearOffhand)) {
            return battlegearOffhand;
        }
        ItemStack held = player == null ? null : player.getHeldItem();
        if (isSpellItem(held) && BackhandCompat.isAvailable()) {
            if (BackhandCompat.isUsingOffhand(player) || BackhandCompat.isOffhandStack(player, held)) {
                return held;
            }
        }
        return backhandOffhand != null ? backhandOffhand : battlegearOffhand;
    }

    private static boolean isOffhandInUse(EntityClientPlayerMP player, ItemStack offhand) {
        if (player == null || offhand == null) {
            return false;
        }

        ItemStack inUse = player.getItemInUse();
        if (inUse == offhand || (inUse != null && ItemStack.areItemStacksEqual(inUse, offhand))) {
            return true;
        }

        if (BackhandCompat.isAvailable()
                && (BackhandCompat.isUsingOffhand(player) || BackhandCompat.isOffhandItemInUse(player))) {
            return true;
        }

        if (BattlegearCompat.isAvailable()) {
            ItemStack battlegearOffhand = BattlegearCompat.getOffhandItem(player);
            if (inUse != null && battlegearOffhand != null
                    && (inUse == battlegearOffhand || ItemStack.areItemStacksEqual(inUse, battlegearOffhand))) {
                return true;
            }
        }

        return false;
    }

    private static final class ActiveSpellSelection {
        private final ItemStack stack;
        private final boolean fromOffhand;

        private ActiveSpellSelection(ItemStack stack, boolean fromOffhand) {
            this.stack = stack;
            this.fromOffhand = fromOffhand;
        }
    }
}

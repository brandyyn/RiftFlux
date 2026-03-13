package com.voidsrift.riftflux.legendgear;

import com.voidsrift.riftflux.compat.BackhandCompat;
import com.voidsrift.riftflux.compat.BattlegearCompat;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.nmccoy.legendgear.item.spell.SpellItem;
import net.nmccoy.legendgear.render.RenderSpellReticle;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class LegendGearOffhandReticleFallbackRenderer {
    public boolean isEnabled() {
        return true;
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!isEnabled()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        EntityClientPlayerMP player = mc.thePlayer;
        ItemStack offhand = getOffhandSpell(player);
        if (offhand == null) {
            return;
        }

        SpellItem spell = (SpellItem) offhand.getItem();
        boolean casting = isUsingOffhandSpell(player, offhand);

        double playerX = player.prevPosX + (player.posX - player.prevPosX) * event.partialTicks;
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * event.partialTicks;
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.partialTicks;
        Vec3 playerLook = player.getLookVec();
        Vec3 playerEye = player.getPosition(event.partialTicks);
        Vec3 rayFrom = Vec3.createVectorHelper(playerEye.xCoord, playerEye.yCoord, playerEye.zCoord);
        double castRange = spell.getEffectiveCastRange(offhand, player);
        double castRadius = spell.getEffectiveCastRadius(offhand, player);
        Vec3 targetPos = SpellItem.getRayTargetResult(playerEye, playerLook, castRange, player.worldObj, spell.hitsWater);
        boolean rayHit = targetPos.distanceTo(rayFrom) < castRange;
        float retreat = -0.015625f;
        targetPos = targetPos.addVector(playerLook.xCoord * retreat, playerLook.yCoord * retreat, playerLook.zCoord * retreat);

        float prevBrightnessX = OpenGlHelper.lastBrightnessX;
        float prevBrightnessY = OpenGlHelper.lastBrightnessY;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(-playerX, -playerY, -playerZ);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(false);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            float castProgress = casting ? spell.getCastingProgress(offhand, player, event.partialTicks) : 0.0f;
            drawFallbackReticle(targetPos, castRadius, castProgress, rayHit, casting);
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightnessX, prevBrightnessY);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static ItemStack getOffhandSpell(EntityClientPlayerMP player) {
        if (player == null) {
            return null;
        }

        ItemStack held = player.getHeldItem();
        if (held != null
                && held.getItem() instanceof SpellItem
                && BackhandCompat.isAvailable()
                && (BackhandCompat.isUsingOffhand(player)
                || BackhandCompat.isOffhandItemInUse(player)
                || BackhandCompat.isOffhandStack(player, held))) {
            return held;
        }

        ItemStack backhandOffhand = BackhandCompat.isAvailable() ? BackhandCompat.getOffhandItem(player) : null;
        ItemStack battlegearOffhand = BattlegearCompat.isAvailable() && BattlegearCompat.isBattlemode(player)
                ? BattlegearCompat.getOffhandItem(player)
                : null;

        if (backhandOffhand != null && backhandOffhand.getItem() instanceof SpellItem) {
            return backhandOffhand;
        }
        if (battlegearOffhand != null && battlegearOffhand.getItem() instanceof SpellItem) {
            return battlegearOffhand;
        }

        if (held != null && held.getItem() instanceof SpellItem && BackhandCompat.isAvailable()) {
            if (BackhandCompat.isUsingOffhand(player) || BackhandCompat.isOffhandStack(player, held)) {
                return held;
            }
        }
        return null;
    }

    private static boolean isUsingOffhandSpell(EntityClientPlayerMP player, ItemStack offhand) {
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

        ItemStack held = player.getHeldItem();
        if (held != null && offhand == held && BackhandCompat.isAvailable() && BackhandCompat.isOffhandStack(player, held)) {
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

    private static void drawFallbackReticle(Vec3 targetPos,
                                            double castRadius,
                                            float castProgress,
                                            boolean rayHit,
                                            boolean casting) {
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.02f);
        GL11.glLineWidth(casting ? 2.5f : 2.0f);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        if (casting) {
            GL11.glColor4f(1.0f, Math.max(0.2f, castProgress), 0.0f, 1.0f);
        } else if (rayHit) {
            GL11.glColor4ub((byte) -1, (byte) -1, (byte) -1, (byte) -128);
        } else {
            GL11.glColor4ub((byte) -1, (byte) -1, (byte) -128, (byte) -128);
        }

        RenderSpellReticle.drawHorizontalRing(targetPos.xCoord, targetPos.yCoord, targetPos.zCoord, castRadius, 8);
        if (casting && castProgress < 1.0f) {
            RenderSpellReticle.drawHorizontalRing(targetPos.xCoord, targetPos.yCoord, targetPos.zCoord, castRadius * castProgress, 8, -castProgress);
        }
    }
}

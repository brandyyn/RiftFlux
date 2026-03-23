package com.voidsrift.riftflux.client.worldtooltips;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;

@SideOnly(Side.CLIENT)
public final class WorldTooltipRenderHandler {
    private static final double SEARCH_PADDING = 1.0D;
    private static final double ENTITY_Y_OFFSET = 0.15D;
    private static final double DISTANCE_EPSILON = 1.0E-4D;

    private WorldTooltip cachedTooltip;

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!ModConfig.enableWorldTooltips) {
            clearCachedTooltip();
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null || mc.fontRenderer == null || mc.renderViewEntity == null) {
            clearCachedTooltip();
            return;
        }

        EntityItem hoveredItem = getHoveredItem(mc, event.partialTicks);
        WorldTooltip tooltip = getOrCreateTooltip(hoveredItem);
        if (tooltip == null || !tooltip.isRenderable()) {
            return;
        }

        tooltip.render(mc, event.partialTicks);
    }

    private WorldTooltip getOrCreateTooltip(EntityItem hoveredItem) {
        if (hoveredItem == null || hoveredItem.getEntityItem() == null || hoveredItem.getEntityItem().getItem() == null) {
            clearCachedTooltip();
            return null;
        }

        if (cachedTooltip == null || !cachedTooltip.isStillValid(hoveredItem)) {
            cachedTooltip = new WorldTooltip(hoveredItem);
            if (!cachedTooltip.isRenderable()) {
                clearCachedTooltip();
                return null;
            }
        }

        return cachedTooltip;
    }

    private void clearCachedTooltip() {
        cachedTooltip = null;
    }

    private static EntityItem getHoveredItem(Minecraft mc, float partialTicks) {
        if (!(mc.renderViewEntity instanceof EntityLivingBase)) {
            return null;
        }

        EntityLivingBase viewer = (EntityLivingBase) mc.renderViewEntity;
        if (viewer.boundingBox == null) {
            return null;
        }

        mc.mcProfiler.startSection("riftfluxWorldTooltips");
        try {
            double maxDistance = ModConfig.worldTooltipsMaxDistance;
            Vec3 pickerOrigin = viewer.getPosition(partialTicks);
            Vec3 look = viewer.getLook(partialTicks);
            Vec3 reach = pickerOrigin.addVector(look.xCoord * maxDistance, look.yCoord * maxDistance, look.zCoord * maxDistance);
            List entityList = mc.theWorld.getEntitiesWithinAABB(
                    EntityItem.class,
                    viewer.boundingBox
                            .addCoord(look.xCoord * maxDistance, look.yCoord * maxDistance, look.zCoord * maxDistance)
                            .expand(SEARCH_PADDING, SEARCH_PADDING, SEARCH_PADDING)
            );

            double closestDistance = 0.0D;
            EntityItem closestItem = null;
            Vec3 closestHit = null;

            for (int i = 0; i < entityList.size(); i++) {
                Object candidateObject = entityList.get(i);
                if (!(candidateObject instanceof EntityItem)) {
                    continue;
                }

                EntityItem entity = (EntityItem) candidateObject;
                if (entity.isDead || entity.boundingBox == null || entity.getEntityItem() == null) {
                    continue;
                }

                double horizontalExpand = Math.max(0.0D, ModConfig.worldTooltipsHoverRadiusHorizontal);
                double verticalExpand = Math.max(0.0D, ModConfig.worldTooltipsHoverRadiusVertical);
                AxisAlignedBB box = entity.boundingBox.copy()
                        .offset(0.0D, ENTITY_Y_OFFSET, 0.0D)
                        .expand(horizontalExpand, verticalExpand, horizontalExpand);
                MovingObjectPosition intercept = box.calculateIntercept(pickerOrigin, reach);

                if (box.isVecInside(pickerOrigin)) {
                    closestItem = entity;
                    closestDistance = 0.0D;
                    closestHit = getBoxCenter(box);
                    break;
                }

                if (intercept == null) {
                    continue;
                }

                double hitDistance = pickerOrigin.distanceTo(intercept.hitVec);
                if (hitDistance < closestDistance || closestDistance == 0.0D) {
                    closestItem = entity;
                    closestDistance = hitDistance;
                    closestHit = intercept.hitVec;
                }
            }

            if (closestItem == null || closestHit == null) {
                return null;
            }

            return isOccluded(mc, viewer, partialTicks, closestHit) ? null : closestItem;
        } finally {
            mc.mcProfiler.endSection();
        }
    }

    private static boolean isOccluded(Minecraft mc, EntityLivingBase viewer, float partialTicks, Vec3 target) {
        Vec3 eyePosition = getEyePosition(viewer, partialTicks);
        double targetDistance = eyePosition.distanceTo(target);
        if (targetDistance <= DISTANCE_EPSILON) {
            return false;
        }

        MovingObjectPosition blockHit = mc.theWorld.func_147447_a(eyePosition, target, false, true, false);
        return blockHit != null
                && blockHit.hitVec != null
                && eyePosition.distanceTo(blockHit.hitVec) + DISTANCE_EPSILON < targetDistance;
    }

    private static Vec3 getEyePosition(EntityLivingBase viewer, float partialTicks) {
        double x = viewer.prevPosX + (viewer.posX - viewer.prevPosX) * partialTicks;
        double y = viewer.prevPosY + (viewer.posY - viewer.prevPosY) * partialTicks + viewer.getEyeHeight();
        double z = viewer.prevPosZ + (viewer.posZ - viewer.prevPosZ) * partialTicks;
        return Vec3.createVectorHelper(x, y, z);
    }

    private static Vec3 getBoxCenter(AxisAlignedBB box) {
        return Vec3.createVectorHelper(
                (box.minX + box.maxX) * 0.5D,
                (box.minY + box.maxY) * 0.5D,
                (box.minZ + box.maxZ) * 0.5D
        );
    }
}

package com.voidsrift.riftflux.terramine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class RenderEyeOfCthulhu extends RenderLiving {
    private static final ResourceLocation EYE_TEXTURE = new ResourceLocation("riftflux:textures/entities/eye_of_cthulhu.png");
    private static final String WDMLA_ENTITY_DRAWABLE = "com.gtnewhorizons.wdmla.impl.ui.drawable.EntityDrawable";
    private static final String WDMLA_GUI_DRAW = "com.gtnewhorizons.wdmla.overlay.GuiDraw";
    private static final String WAILA_GUI_DRAW = "mcp.mobius.waila.overlay.GuiDraw";
    private static final Map<Integer, YawSmoothingState> SMOOTH_YAWS = new HashMap<Integer, YawSmoothingState>();

    public RenderEyeOfCthulhu(ModelBase model, float shadowSize) {
        super(model, shadowSize);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (entity instanceof IBossDisplayData) {
            BossStatus.setBossStatus((IBossDisplayData) entity, false);
        }
        if (!(entity instanceof EntityLivingBase)) {
            super.doRender(entity, x, y, z, yaw, partialTicks);
            return;
        }

        EntityLivingBase living = (EntityLivingBase) entity;
        boolean wdmlaContext = isWdmlaRenderContext();
        float targetYaw = resolveFacingYaw(living, yaw);
        float resolvedYaw = smoothYaw(living, targetYaw, partialTicks);
        float appliedYaw = wdmlaContext ? resolvedYaw + 180.0F : resolvedYaw;
        float prevRenderYawOffset = living.prevRenderYawOffset;
        float renderYawOffset = living.renderYawOffset;
        float prevRotationYaw = living.prevRotationYaw;
        float rotationYaw = living.rotationYaw;
        float prevRotationYawHead = living.prevRotationYawHead;
        float rotationYawHead = living.rotationYawHead;

        try {
            living.prevRenderYawOffset = appliedYaw;
            living.renderYawOffset = appliedYaw;
            living.prevRotationYaw = appliedYaw;
            living.rotationYaw = appliedYaw;
            living.prevRotationYawHead = appliedYaw;
            living.rotationYawHead = appliedYaw;
            super.doRender(entity, x, y, z, appliedYaw, partialTicks);
        } finally {
            living.prevRenderYawOffset = prevRenderYawOffset;
            living.renderYawOffset = renderYawOffset;
            living.prevRotationYaw = prevRotationYaw;
            living.rotationYaw = rotationYaw;
            living.prevRotationYawHead = prevRotationYawHead;
            living.rotationYawHead = rotationYawHead;
            if (living.isDead) {
                SMOOTH_YAWS.remove(Integer.valueOf(living.getEntityId()));
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return EYE_TEXTURE;
    }

    private static float resolveFacingYaw(EntityLivingBase living, float fallbackYaw) {
        if (living == null) {
            return fallbackYaw;
        }
        Entity viewer = null;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null) {
            viewer = mc.thePlayer;
        }
        if (viewer == null) {
            return living.rotationYaw;
        }

        double dx = viewer.posX - living.posX;
        double dz = viewer.posZ - living.posZ;
        double lookLenSq = dx * dx + dz * dz;
        if (lookLenSq > 1.0E-6D) {
            return (float) (180.0D - Math.atan2(dx, dz) * 180.0D / Math.PI);
        }

        double motionX = living.motionX;
        double motionZ = living.motionZ;
        if (motionX * motionX + motionZ * motionZ > 1.0E-6D) {
            return (float) (180.0D - Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
        }
        return living.rotationYaw;
    }

    private static float smoothYaw(EntityLivingBase living, float target, float partialTicks) {
        if (living == null) {
            return target;
        }

        Integer id = Integer.valueOf(living.getEntityId());
        YawSmoothingState state = SMOOTH_YAWS.get(id);
        long tick = living.ticksExisted;

        if (state == null) {
            state = new YawSmoothingState(tick, target, target);
            SMOOTH_YAWS.put(id, state);
        } else if (state.lastTick != tick) {
            state.previousYaw = state.currentYaw;
            float delta = MathHelper.wrapAngleTo180_float(target - state.currentYaw);
            float step = delta * 0.32F;
            if (step > 14.0F) {
                step = 14.0F;
            } else if (step < -14.0F) {
                step = -14.0F;
            }
            state.currentYaw += step;
            state.lastTick = tick;
        } else {
            float delta = MathHelper.wrapAngleTo180_float(target - state.currentYaw);
            state.currentYaw += delta * 0.06F;
        }

        float clampedPartial = partialTicks;
        if (clampedPartial < 0.0F) {
            clampedPartial = 0.0F;
        } else if (clampedPartial > 1.0F) {
            clampedPartial = 1.0F;
        }
        float frameDelta = MathHelper.wrapAngleTo180_float(state.currentYaw - state.previousYaw);
        return state.previousYaw + frameDelta * clampedPartial;
    }

    private static boolean isWdmlaRenderContext() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            if (className == null) {
                continue;
            }
            if (WDMLA_ENTITY_DRAWABLE.equals(className)
                    || WDMLA_GUI_DRAW.equals(className)
                    || WAILA_GUI_DRAW.equals(className)
                    || className.startsWith("com.gtnewhorizons.wdmla.")
                    || className.startsWith("mcp.mobius.waila.")) {
                return true;
            }
        }
        return false;
    }

    private static final class YawSmoothingState {
        private long lastTick;
        private float previousYaw;
        private float currentYaw;

        private YawSmoothingState(long lastTick, float previousYaw, float currentYaw) {
            this.lastTick = lastTick;
            this.previousYaw = previousYaw;
            this.currentYaw = currentYaw;
        }
    }
}

package com.voidsrift.riftflux.client.photomode;

import com.voidsrift.riftflux.ModConfig;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public final class IsometricPhotoModeController {

    private static final float[] DIAGONAL_YAWS = new float[]{45.0F, 135.0F, 225.0F, 315.0F};
    private static final float DEFAULT_PITCH = 35.26439F;
    private static final float MIN_PITCH = 10.0F;
    private static final float MAX_PITCH = 80.0F;
    private static final float PITCH_STEP = 5.0F;
    private static final double CAMERA_PAN_SPEED = 0.5D;
    private static final double CAMERA_SPRINT_MULTIPLIER = 2.4D;
    private static final double INITIAL_CAMERA_DISTANCE = 16.0D;
    private static final double MIN_CAMERA_DISTANCE = 2.0D;
    private static final double DEFAULT_ORTHOGRAPHIC_VIEW_HEIGHT = 20.0D;
    private static final double MIN_ORTHOGRAPHIC_VIEW_HEIGHT = 4.0D;
    private static final double FALLBACK_MAX_ORTHOGRAPHIC_VIEW_HEIGHT = 256.0D;
    private static final double LOOK_TARGET_RAY_DISTANCE = 4096.0D;
    private static final double ORTHOGRAPHIC_FACE_CULLING_CAMERA_DISTANCE = 4096.0D;
    private static final int MAX_PIVOT_RAYCAST_STEPS = 12288;
    private static final double PIVOT_RAYCAST_STEP_EPSILON = 1.0E-7D;
    private static final double PIVOT_RAY_START_OFFSET = 0.0D;
    private static final double RENDERED_DEPTH_BLOCK_NUDGE = 0.03D;
    private static final double RENDERED_DEPTH_CAMERA_EPSILON = 1.0E-4D;
    private static final float RENDERED_DEPTH_ANGLE_EPSILON = 0.25F;
    private static final double ZOOM_STEP_FACTOR = 0.9D;
    private static final double ZOOM_SMOOTHING_FACTOR = 0.35D;
    private static final double ZOOM_SMOOTHING_EPSILON = 1.0E-3D;
    private static final float ROTATION_ANIMATION_TICKS = 12.0F;
    private static final int CONTROL_STATUS_MESSAGE_TICKS = 60;
    private static final int CONTROL_STATUS_MESSAGE_COLOR = 0xA0A0A0;
    private static final IsometricPhotoModeController INSTANCE = new IsometricPhotoModeController();

    private final Minecraft mc = Minecraft.getMinecraft();
    private final FloatBuffer depthPixelBuffer = BufferUtils.createFloatBuffer(1);
    private final FloatBuffer modelviewMatrixBuffer = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer unprojectedCenterBuffer = BufferUtils.createFloatBuffer(3);
    private final IntBuffer viewportBuffer = BufferUtils.createIntBuffer(16);
    private IsometricCameraEntity cameraEntity;
    private EntityLivingBase previousRenderViewEntity;
    private boolean active;
    private boolean playerControlled;
    private boolean clientStateCaptured;
    private int previousPerspective = 0;
    private boolean previousHideGui;
    private int yawIndex;
    private float pitch = DEFAULT_PITCH;
    private double focusX;
    private double focusY;
    private double focusZ;
    private double pivotX;
    private double pivotY;
    private double pivotZ;
    private double orbitDistance = INITIAL_CAMERA_DISTANCE;
    private double previousOrthographicViewHeight = DEFAULT_ORTHOGRAPHIC_VIEW_HEIGHT;
    private double orthographicViewHeight = DEFAULT_ORTHOGRAPHIC_VIEW_HEIGHT;
    private double targetOrthographicViewHeight = DEFAULT_ORTHOGRAPHIC_VIEW_HEIGHT;
    private int terrainRefreshToken;
    private float currentYaw;
    private float currentPitch = DEFAULT_PITCH;
    private float startYaw;
    private float startPitch = DEFAULT_PITCH;
    private float targetYaw;
    private float targetPitch = DEFAULT_PITCH;
    private float rotationProgress = 1.0F;
    private float previousRotationProgress = 1.0F;
    private boolean pivotDirty = true;
    private boolean hasPivot;
    private boolean rotationInputActive;
    private boolean hasRenderedDepthPivot;
    private double renderedDepthPivotX;
    private double renderedDepthPivotY;
    private double renderedDepthPivotZ;
    private double renderedDepthCameraX;
    private double renderedDepthCameraY;
    private double renderedDepthCameraZ;
    private float renderedDepthYaw;
    private float renderedDepthPitch;
    private boolean renderTweenStateApplied;
    private double savedRenderPosX;
    private double savedRenderPosY;
    private double savedRenderPosZ;
    private double savedRenderPrevPosX;
    private double savedRenderPrevPosY;
    private double savedRenderPrevPosZ;
    private double savedRenderLastTickPosX;
    private double savedRenderLastTickPosY;
    private double savedRenderLastTickPosZ;
    private float savedRenderYaw;
    private float savedRenderPrevYaw;
    private float savedRenderYawHead;
    private float savedRenderPrevYawHead;
    private float savedRenderPitch;
    private float savedRenderPrevPitch;
    private String controlStatusMessage = "";
    private int controlStatusMessageTicks;

    public static IsometricPhotoModeController instance() {
        return INSTANCE;
    }

    private IsometricPhotoModeController() {
    }

    public void toggle() {
        if (this.active) {
            this.disable(true);
        } else {
            this.enable();
        }
    }

    public void enable() {
        if (!ModConfig.enableIsometricPhotoMode) {
            return;
        }

        if (this.active) {
            if (this.isActive()) {
                return;
            }

            this.reset();
        }

        if (this.mc.thePlayer == null || this.mc.theWorld == null) {
            return;
        }

        this.previousRenderViewEntity = this.mc.renderViewEntity;
        this.previousPerspective = this.mc.gameSettings.thirdPersonView;
        this.previousHideGui = this.mc.gameSettings.hideGUI;
        this.clientStateCaptured = true;
        this.yawIndex = this.findClosestYawIndex(this.mc.thePlayer.rotationYaw);
        this.pitch = DEFAULT_PITCH;
        this.orthographicViewHeight = this.clamp(
                DEFAULT_ORTHOGRAPHIC_VIEW_HEIGHT,
                MIN_ORTHOGRAPHIC_VIEW_HEIGHT,
                this.getMaxOrthographicViewHeight()
        );
        this.previousOrthographicViewHeight = this.orthographicViewHeight;
        this.targetOrthographicViewHeight = this.orthographicViewHeight;
        this.orbitDistance = INITIAL_CAMERA_DISTANCE;
        this.targetYaw = DIAGONAL_YAWS[this.yawIndex];
        this.currentYaw = this.targetYaw;
        this.startYaw = this.targetYaw;
        this.targetPitch = this.pitch;
        this.currentPitch = this.pitch;
        this.startPitch = this.pitch;
        this.rotationProgress = 1.0F;
        this.previousRotationProgress = 1.0F;
        this.pivotDirty = true;
        this.hasPivot = false;
        this.playerControlled = false;
        this.rotationInputActive = false;

        this.cameraEntity = new IsometricCameraEntity(this.mc.theWorld, this.mc.thePlayer);
        this.active = true;
        this.playerControlled = false;
        this.centerOnPlayer();

        AngelicaPhotoModeCompat.enablePhotoModeOverrides();
        this.mc.renderViewEntity = this.cameraEntity;
        this.mc.gameSettings.thirdPersonView = 0;
        this.mc.gameSettings.hideGUI = true;
        AngelicaPhotoModeCompat.enforceNoFog();
    }

    public void disable(boolean restorePreviousPerspective) {
        this.deactivate(restorePreviousPerspective, false);
    }

    public void exitWithPerspectiveKey() {
        this.disable(false);
    }

    public void reset() {
        this.deactivate(true, false);
    }

    public void onDisconnect() {
        this.deactivate(true, false);
    }

    public void onWorldChange() {
        this.deactivate(true, false);
    }

    public void tick() {
        if (!this.active) {
            return;
        }

        if (!this.isActive()) {
            this.reset();
            return;
        }

        if (this.mc.theWorld == null || this.mc.thePlayer == null || this.mc.thePlayer.isDead) {
            this.deactivate(true, false);
            return;
        }

        if (this.cameraEntity == null || this.cameraEntity.worldObj != this.mc.theWorld) {
            this.deactivate(true, false);
            return;
        }

        this.cameraEntity.onUpdate();
        this.mc.gameSettings.thirdPersonView = 0;
        this.mc.gameSettings.hideGUI = true;
        AngelicaPhotoModeCompat.enablePhotoModeOverrides();
        AngelicaPhotoModeCompat.enforceNoFog();
        this.tickControlStatusMessage();
        this.tickAnimation();

        if (this.playerControlled || this.mc.currentScreen != null || this.rotationInputActive
                || this.previousRotationProgress < 1.0F || this.rotationProgress < 1.0F) {
            return;
        }

        GameSettings settings = this.mc.gameSettings;
        boolean rotateModifier = this.isPhysicalKeyDown(settings.keyBindSneak);
        boolean moveLeft = !rotateModifier && settings.keyBindLeft.getIsKeyPressed();
        boolean moveRight = !rotateModifier && settings.keyBindRight.getIsKeyPressed();
        boolean moveUp = !rotateModifier && settings.keyBindForward.getIsKeyPressed();
        boolean moveDown = !rotateModifier && settings.keyBindBack.getIsKeyPressed();
        boolean sprint = settings.keyBindSprint.getIsKeyPressed();
        double speed = CAMERA_PAN_SPEED * (sprint ? CAMERA_SPRINT_MULTIPLIER : 1.0D);
        double yawRad = Math.toRadians(this.currentYaw);
        double rightX = -Math.cos(yawRad);
        double rightZ = -Math.sin(yawRad);
        double dx = 0.0D;
        double dy = 0.0D;
        double dz = 0.0D;

        if (moveLeft ^ moveRight) {
            double direction = moveRight ? 1.0D : -1.0D;
            dx += rightX * direction * speed;
            dz += rightZ * direction * speed;
        }

        if (moveUp ^ moveDown) {
            dy += (moveUp ? 1.0D : -1.0D) * speed;
        }

        if (dx != 0.0D || dy != 0.0D || dz != 0.0D) {
            this.translateCameraAndFocus(dx, dy, dz);
        }
    }

    public void togglePlayerControl() {
        if (!this.active) {
            return;
        }

        this.playerControlled = !this.playerControlled;
        this.showControlStatusMessage(this.playerControlled ? "Photo Mode: Controlling Player" : "Photo Mode: Controlling Camera");
    }

    public void rotateHorizontal(int direction) {
        if (!this.active || this.cameraEntity == null) {
            return;
        }

        if (this.renderTweenStateApplied || this.previousRotationProgress < 1.0F || this.rotationProgress < 1.0F) {
            return;
        }

        if (!this.ensurePivotTarget()) {
            return;
        }
        this.lockFocusToPivot();

        float baseYaw = this.unwrapYawNear(this.snapYawToNearestDiagonal(this.currentYaw), this.currentYaw);
        this.beginRotationTween(baseYaw, this.clampPitch(this.currentPitch));
        this.targetYaw = this.unwrapYawNear(baseYaw + (float) (direction * 90), baseYaw);
        this.yawIndex = this.findClosestYawIndex(this.targetYaw);
    }

    public void adjustVerticalRotation(int direction) {
        if (!this.active || this.cameraEntity == null) {
            return;
        }

        if (this.renderTweenStateApplied || this.previousRotationProgress < 1.0F || this.rotationProgress < 1.0F) {
            return;
        }

        this.invalidatePivotTarget();
        if (!this.ensurePivotTarget()) {
            return;
        }
        this.lockFocusToPivot();
        this.beginRotationTween(this.currentYaw, this.clampPitch(this.currentPitch));
        this.pitch = this.clampPitch(this.pitch + direction * PITCH_STEP);
        this.targetPitch = this.pitch;
    }

    public void rotateHorizontalContinuous(int direction, float degreesPerTick) {
        if (!this.active || this.cameraEntity == null || degreesPerTick <= 0.0F) {
            return;
        }

        if (this.rotationProgress < 1.0F || !this.ensurePivotTarget()) {
            return;
        }
        this.lockFocusToPivot();

        float nextYaw = this.currentYaw + (float) direction * degreesPerTick;
        this.currentYaw = nextYaw;
        this.targetYaw = nextYaw;
        this.startYaw = nextYaw;
        this.currentPitch = this.clampPitch(this.currentPitch);
        this.targetPitch = this.currentPitch;
        this.startPitch = this.currentPitch;
        this.rotationProgress = 1.0F;
        this.yawIndex = this.findClosestYawIndex(nextYaw);
        this.applyCameraState();
    }

    public void snapHorizontalToNearestDiagonal(int directionPreference) {
        if (!this.active || this.cameraEntity == null) {
            return;
        }

        if (!this.ensurePivotTarget()) {
            return;
        }
        this.lockFocusToPivot();

        float startYawValue = this.currentYaw;
        float startPitchValue = this.clampPitch(this.currentPitch);
        float snappedYaw = directionPreference == 0
                ? this.findNearestDiagonalYaw(startYawValue, 0)
                : this.findNextDiagonalYaw(startYawValue, directionPreference);
        snappedYaw = this.unwrapYawNear(snappedYaw, startYawValue);
        this.beginRotationTween(startYawValue, startPitchValue);
        this.targetYaw = snappedYaw;
        this.targetPitch = startPitchValue;
        this.pitch = startPitchValue;
        this.yawIndex = this.findClosestYawIndex(snappedYaw);
    }

    public void adjustZoom(int scrollDelta) {
        if (!this.active || scrollDelta == 0) {
            return;
        }

        int steps = Math.max(1, Math.abs(scrollDelta) / 120);
        double nextTarget = this.targetOrthographicViewHeight;
        for (int index = 0; index < steps; index++) {
            if (scrollDelta > 0) {
                nextTarget *= ZOOM_STEP_FACTOR;
            } else {
                nextTarget /= ZOOM_STEP_FACTOR;
            }
        }
        this.targetOrthographicViewHeight = this.clamp(
                nextTarget,
                MIN_ORTHOGRAPHIC_VIEW_HEIGHT,
                this.getMaxOrthographicViewHeight()
        );
        this.requestTerrainRefresh();
    }

    public void centerOnPlayer() {
        if (this.mc.thePlayer == null || this.cameraEntity == null) {
            return;
        }

        this.focusX = this.mc.thePlayer.posX;
        this.focusY = this.mc.thePlayer.posY + this.mc.thePlayer.height * 0.5D;
        this.focusZ = this.mc.thePlayer.posZ;
        this.currentYaw = this.targetYaw;
        this.currentPitch = this.targetPitch;
        this.startYaw = this.currentYaw;
        this.startPitch = this.currentPitch;
        this.rotationProgress = 1.0F;
        this.orbitDistance = INITIAL_CAMERA_DISTANCE;
        this.invalidatePivotTarget();
        this.applyCameraState();
        this.syncCameraRenderState();
        this.captureLookTarget();
    }

    public boolean isActive() {
        return this.active
                && this.cameraEntity != null
                && this.mc.renderViewEntity == this.cameraEntity
                && this.mc.theWorld != null
                && this.cameraEntity.worldObj == this.mc.theWorld;
    }

    public boolean isPlayerControlled() {
        return this.playerControlled;
    }

    public boolean isCapturingInput() {
        return this.isActive() && !this.playerControlled;
    }

    public void setRotationInputActive(boolean rotationInputActive) {
        this.rotationInputActive = rotationInputActive;
    }

    public void panCamera(boolean moveLeft, boolean moveRight, boolean moveUp, boolean moveDown, boolean sprint) {
        if (!this.active || this.cameraEntity == null || this.mc.currentScreen != null
                || this.previousRotationProgress < 1.0F || this.rotationProgress < 1.0F) {
            return;
        }

        double speed = CAMERA_PAN_SPEED * (sprint ? CAMERA_SPRINT_MULTIPLIER : 1.0D);
        double yawRad = Math.toRadians(this.currentYaw);
        double rightX = -Math.cos(yawRad);
        double rightZ = -Math.sin(yawRad);
        double dx = 0.0D;
        double dy = 0.0D;
        double dz = 0.0D;

        if (moveLeft ^ moveRight) {
            double direction = moveRight ? 1.0D : -1.0D;
            dx += rightX * direction * speed;
            dz += rightZ * direction * speed;
        }

        if (moveUp ^ moveDown) {
            dy += (moveUp ? 1.0D : -1.0D) * speed;
        }

        if (dx != 0.0D || dy != 0.0D || dz != 0.0D) {
            this.translateCameraAndFocus(dx, dy, dz);
        }
    }

    public double getOrthographicViewHeight() {
        return this.orthographicViewHeight;
    }

    public double getOrthographicViewHeight(float partialTicks) {
        double progress = this.clamp((double) partialTicks, 0.0D, 1.0D);
        return this.previousOrthographicViewHeight
                + (this.orthographicViewHeight - this.previousOrthographicViewHeight) * progress;
    }

    public void renderControlStatusOverlay(float partialTicks) {
        if (!this.isActive() || this.mc.currentScreen != null || this.controlStatusMessageTicks <= 0
                || this.controlStatusMessage == null || this.controlStatusMessage.isEmpty()) {
            return;
        }

        FontRenderer fontRenderer = this.mc.fontRenderer;
        if (fontRenderer == null) {
            return;
        }

        ScaledResolution resolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        float remaining = (float) this.controlStatusMessageTicks - this.clampProgress(partialTicks);
        int alpha = (int) (remaining * 255.0F / 20.0F);
        if (alpha > 255) {
            alpha = 255;
        }
        if (alpha <= 8) {
            return;
        }

        int x = (width - fontRenderer.getStringWidth(this.controlStatusMessage)) / 2;
        int y = height - 72;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        fontRenderer.drawString(this.controlStatusMessage, x, y, CONTROL_STATUS_MESSAGE_COLOR | (alpha << 24));
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public int getTerrainRefreshToken() {
        return this.terrainRefreshToken;
    }

    public void applyRenderTweenState(float partialTicks) {
        if (!this.isActive() || this.cameraEntity == null || this.renderTweenStateApplied
                || this.previousRotationProgress >= 1.0F && this.rotationProgress >= 1.0F) {
            return;
        }

        float progress = this.previousRotationProgress
                + (this.rotationProgress - this.previousRotationProgress) * this.clampProgress(partialTicks);
        this.saveRenderCameraState();
        this.setCameraEntityToRenderState(progress);
        this.renderTweenStateApplied = true;
    }

    public void restoreRenderTweenState() {
        if (!this.renderTweenStateApplied || this.cameraEntity == null) {
            this.renderTweenStateApplied = false;
            return;
        }

        this.cameraEntity.posX = this.savedRenderPosX;
        this.cameraEntity.posY = this.savedRenderPosY;
        this.cameraEntity.posZ = this.savedRenderPosZ;
        this.cameraEntity.prevPosX = this.savedRenderPrevPosX;
        this.cameraEntity.prevPosY = this.savedRenderPrevPosY;
        this.cameraEntity.prevPosZ = this.savedRenderPrevPosZ;
        this.cameraEntity.lastTickPosX = this.savedRenderLastTickPosX;
        this.cameraEntity.lastTickPosY = this.savedRenderLastTickPosY;
        this.cameraEntity.lastTickPosZ = this.savedRenderLastTickPosZ;
        this.cameraEntity.rotationYaw = this.savedRenderYaw;
        this.cameraEntity.prevRotationYaw = this.savedRenderPrevYaw;
        this.cameraEntity.rotationYawHead = this.savedRenderYawHead;
        this.cameraEntity.prevRotationYawHead = this.savedRenderPrevYawHead;
        this.cameraEntity.rotationPitch = this.savedRenderPitch;
        this.cameraEntity.prevRotationPitch = this.savedRenderPrevPitch;
        this.cameraEntity.setPosition(this.savedRenderPosX, this.savedRenderPosY, this.savedRenderPosZ);
        this.cameraEntity.prevPosX = this.savedRenderPrevPosX;
        this.cameraEntity.prevPosY = this.savedRenderPrevPosY;
        this.cameraEntity.prevPosZ = this.savedRenderPrevPosZ;
        this.cameraEntity.lastTickPosX = this.savedRenderLastTickPosX;
        this.cameraEntity.lastTickPosY = this.savedRenderLastTickPosY;
        this.cameraEntity.lastTickPosZ = this.savedRenderLastTickPosZ;
        this.renderTweenStateApplied = false;
    }

    public void captureRenderedCenterDepth(float partialTicks) {
        if (!this.isActive() || this.cameraEntity == null || this.mc.displayWidth <= 0 || this.mc.displayHeight <= 0) {
            this.hasRenderedDepthPivot = false;
            return;
        }

        if (this.rotationProgress < 1.0F) {
            return;
        }

        try {
            this.viewportBuffer.clear();
            this.modelviewMatrixBuffer.clear();
            this.projectionMatrixBuffer.clear();
            this.depthPixelBuffer.clear();
            this.unprojectedCenterBuffer.clear();

            GL11.glGetInteger(GL11.GL_VIEWPORT, this.viewportBuffer);
            int centerX = this.viewportBuffer.get(0) + this.viewportBuffer.get(2) / 2;
            int centerY = this.viewportBuffer.get(1) + this.viewportBuffer.get(3) / 2;

            GL11.glReadPixels(centerX, centerY, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, this.depthPixelBuffer);
            float depth = this.depthPixelBuffer.get(0);
            if (depth <= 0.0F || depth >= 1.0F || Float.isNaN(depth) || Float.isInfinite(depth)) {
                this.hasRenderedDepthPivot = false;
                return;
            }

            GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, this.modelviewMatrixBuffer);
            GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, this.projectionMatrixBuffer);
            this.viewportBuffer.rewind();
            this.modelviewMatrixBuffer.rewind();
            this.projectionMatrixBuffer.rewind();
            this.unprojectedCenterBuffer.rewind();

            if (!GLU.gluUnProject(
                    (float) centerX,
                    (float) centerY,
                    depth,
                    this.modelviewMatrixBuffer,
                    this.projectionMatrixBuffer,
                    this.viewportBuffer,
                    this.unprojectedCenterBuffer
            )) {
                this.hasRenderedDepthPivot = false;
                return;
            }

            double cameraX = this.cameraEntity.posX;
            double cameraY = this.cameraEntity.posY;
            double cameraZ = this.cameraEntity.posZ;
            double hitX = cameraX + (double) this.unprojectedCenterBuffer.get(0);
            double hitY = cameraY + (double) this.unprojectedCenterBuffer.get(1);
            double hitZ = cameraZ + (double) this.unprojectedCenterBuffer.get(2);
            Vec3 lookDirection = this.getLookDirection(this.currentYaw, this.currentPitch);
            int[] block = this.findRenderedDepthBlock(hitX, hitY, hitZ, lookDirection);
            if (block == null) {
                this.hasRenderedDepthPivot = false;
                return;
            }

            this.renderedDepthPivotX = (double) block[0] + 0.5D;
            this.renderedDepthPivotY = (double) block[1] + 0.5D;
            this.renderedDepthPivotZ = (double) block[2] + 0.5D;
            this.renderedDepthCameraX = this.cameraEntity.posX;
            this.renderedDepthCameraY = this.cameraEntity.posY;
            this.renderedDepthCameraZ = this.cameraEntity.posZ;
            this.renderedDepthYaw = this.currentYaw;
            this.renderedDepthPitch = this.currentPitch;
            this.hasRenderedDepthPivot = true;
            if (this.pivotDirty) {
                this.applyRenderedDepthPivotIfFresh();
            }
        } catch (Throwable ignored) {
            this.hasRenderedDepthPivot = false;
        }
    }

    public double[] getSectionFaceCullingCameraPosition() {
        if (!this.active || this.cameraEntity == null) {
            return null;
        }

        Vec3 lookDirection = this.getLookDirection(this.currentYaw, this.currentPitch);
        return new double[]{
                this.cameraEntity.posX - lookDirection.xCoord * ORTHOGRAPHIC_FACE_CULLING_CAMERA_DISTANCE,
                this.cameraEntity.posY - lookDirection.yCoord * ORTHOGRAPHIC_FACE_CULLING_CAMERA_DISTANCE,
                this.cameraEntity.posZ - lookDirection.zCoord * ORTHOGRAPHIC_FACE_CULLING_CAMERA_DISTANCE
        };
    }

    public boolean isOutsideHorizontalPhotoModeRenderBoundary(int x, int z) {
        if (!this.isActive() || this.mc.gameSettings == null || this.cameraEntity == null) {
            return false;
        }

        int renderDistance = Math.max(1, this.mc.gameSettings.renderDistanceChunks);
        return this.isOutsideHorizontalRenderBoundary(x, z, this.cameraEntity.posX, this.cameraEntity.posZ, renderDistance);
    }

    public boolean beginHorizontalPhotoModeRenderBoundaryContext(int x, int y, int z) {
        if (!this.isActive() || this.mc.gameSettings == null || this.cameraEntity == null) {
            return false;
        }

        int renderDistance = Math.max(1, this.mc.gameSettings.renderDistanceChunks);
        int centerChunkX = MathHelper.floor_double(this.cameraEntity.posX) >> 4;
        int centerChunkZ = MathHelper.floor_double(this.cameraEntity.posZ) >> 4;
        int minX = (centerChunkX - renderDistance) << 4;
        int maxXExclusive = (centerChunkX + renderDistance + 1) << 4;
        int minZ = (centerChunkZ - renderDistance) << 4;
        int maxZExclusive = (centerChunkZ + renderDistance + 1) << 4;
        if (x != minX && x != maxXExclusive - 1 && z != minZ && z != maxZExclusive - 1) {
            return false;
        }

        PhotoModeBlockRenderContext.begin(x, y, z, minX, maxXExclusive, minZ, maxZExclusive);
        return true;
    }

    public int getHorizontalPhotoModeRenderDistanceChunks() {
        if (!this.isActive() || this.mc.gameSettings == null) {
            return 0;
        }

        return Math.max(1, this.mc.gameSettings.renderDistanceChunks);
    }

    public int getHorizontalPhotoModeCenterChunkX() {
        if (!this.isActive() || this.cameraEntity == null) {
            return 0;
        }

        return MathHelper.floor_double(this.cameraEntity.posX) >> 4;
    }

    public int getHorizontalPhotoModeCenterChunkZ() {
        if (!this.isActive() || this.cameraEntity == null) {
            return 0;
        }

        return MathHelper.floor_double(this.cameraEntity.posZ) >> 4;
    }

    public boolean touchesHorizontalPhotoModeRenderBoundary(int x, int z) {
        if (!this.isActive()) {
            return false;
        }

        return this.isOutsideHorizontalPhotoModeRenderBoundary(x + 1, z)
                || this.isOutsideHorizontalPhotoModeRenderBoundary(x - 1, z)
                || this.isOutsideHorizontalPhotoModeRenderBoundary(x, z + 1)
                || this.isOutsideHorizontalPhotoModeRenderBoundary(x, z - 1);
    }

    private void applyCameraState() {
        if (this.cameraEntity == null) {
            return;
        }

        double[] cameraPosition = this.calculateCameraPosition(this.currentYaw, this.currentPitch);
        this.cameraEntity.rotationYaw = this.currentYaw;
        this.cameraEntity.rotationYawHead = this.currentYaw;
        this.cameraEntity.rotationPitch = this.currentPitch;
        this.cameraEntity.setPosition(cameraPosition[0], cameraPosition[1], cameraPosition[2]);
    }

    private int findClosestYawIndex(float currentYaw) {
        float normalizedYaw = this.normalizeYaw(currentYaw);
        int bestIndex = 0;
        float bestDistance = Float.MAX_VALUE;

        for (int index = 0; index < DIAGONAL_YAWS.length; index++) {
            float distance = this.angularDistance(normalizedYaw, DIAGONAL_YAWS[index]);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestIndex = index;
            }
        }

        return bestIndex;
    }

    private float normalizeYaw(float yaw) {
        float normalized = yaw % 360.0F;
        return normalized < 0.0F ? normalized + 360.0F : normalized;
    }

    private float angularDistance(float first, float second) {
        float delta = Math.abs(first - second);
        return delta > 180.0F ? 360.0F - delta : delta;
    }

    private float clampPitch(float value) {
        return Math.max(MIN_PITCH, Math.min(MAX_PITCH, value));
    }

    private void translateCameraAndFocus(double dx, double dy, double dz) {
        this.focusX += dx;
        this.focusY += dy;
        this.focusZ += dz;
        this.invalidatePivotTarget();
        this.applyCameraState();
    }

    private boolean isOutsideHorizontalRenderBoundary(int x, int z, double centerX, double centerZ, int renderDistance) {
        int centerChunkX = MathHelper.floor_double(centerX) >> 4;
        int centerChunkZ = MathHelper.floor_double(centerZ) >> 4;
        int minBlockX = (centerChunkX - renderDistance) << 4;
        int maxBlockX = ((centerChunkX + renderDistance + 1) << 4) - 1;
        int minBlockZ = (centerChunkZ - renderDistance) << 4;
        int maxBlockZ = ((centerChunkZ + renderDistance + 1) << 4) - 1;
        return x < minBlockX || x > maxBlockX || z < minBlockZ || z > maxBlockZ;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double getMaxOrthographicViewHeight() {
        double configuredMax = ModConfig.isometricPhotoModeMaxZoomOut;
        if (configuredMax < MIN_ORTHOGRAPHIC_VIEW_HEIGHT || Double.isNaN(configuredMax) || Double.isInfinite(configuredMax)) {
            return FALLBACK_MAX_ORTHOGRAPHIC_VIEW_HEIGHT;
        }

        return configuredMax;
    }

    private void tickAnimation() {
        if (this.cameraEntity == null) {
            return;
        }

        this.tickZoomAnimation();
        this.previousRotationProgress = this.rotationProgress;
        if (this.rotationProgress < 1.0F) {
            this.rotationProgress = Math.min(1.0F, this.rotationProgress + 1.0F / ROTATION_ANIMATION_TICKS);
            float easedProgress = this.smootherStep(this.rotationProgress);
            float animationTargetYaw = this.unwrapYawNear(this.targetYaw, this.startYaw);
            this.currentYaw = this.startYaw + (animationTargetYaw - this.startYaw) * easedProgress;
            this.currentPitch = this.clampPitch(this.startPitch + (this.targetPitch - this.startPitch) * easedProgress);
        } else {
            this.previousRotationProgress = 1.0F;
            this.currentYaw = this.unwrapYawNear(this.targetYaw, this.startYaw);
            this.currentPitch = this.targetPitch;
        }

        this.applyCameraState();
    }

    private void tickZoomAnimation() {
        this.previousOrthographicViewHeight = this.orthographicViewHeight;
        double delta = this.targetOrthographicViewHeight - this.orthographicViewHeight;
        if (Math.abs(delta) <= ZOOM_SMOOTHING_EPSILON) {
            if (this.orthographicViewHeight != this.targetOrthographicViewHeight) {
                this.orthographicViewHeight = this.targetOrthographicViewHeight;
                this.requestTerrainRefresh();
            }
            return;
        }

        this.orthographicViewHeight += delta * ZOOM_SMOOTHING_FACTOR;
        this.requestTerrainRefresh();
    }

    private void beginRotationTween(float startYawValue, float startPitchValue) {
        this.currentYaw = startYawValue;
        this.currentPitch = startPitchValue;
        this.startYaw = this.currentYaw;
        this.startPitch = this.currentPitch;
        this.rotationProgress = 0.0F;
        this.previousRotationProgress = 0.0F;
    }

    private boolean ensurePivotTarget() {
        if (this.hasPivot && !this.pivotDirty) {
            return true;
        }

        return this.captureLookTarget();
    }

    private boolean captureLookTarget() {
        if (this.cameraEntity == null || this.mc.theWorld == null) {
            return false;
        }

        if (this.applyRenderedDepthPivotIfFresh()) {
            return true;
        }

        Vec3 lookDirection = this.getLookDirection(this.currentYaw, this.currentPitch);
        Vec3 rayStart = Vec3.createVectorHelper(
                this.cameraEntity.posX + lookDirection.xCoord * PIVOT_RAY_START_OFFSET,
                this.cameraEntity.posY + lookDirection.yCoord * PIVOT_RAY_START_OFFSET,
                this.cameraEntity.posZ + lookDirection.zCoord * PIVOT_RAY_START_OFFSET
        );

        MovingObjectPosition mouseOver = this.findNearestPivotHit(rayStart, lookDirection, LOOK_TARGET_RAY_DISTANCE);
        if (this.isValidPivotHit(mouseOver)) {
            this.pivotX = mouseOver.blockX + 0.5D;
            this.pivotY = mouseOver.blockY + 0.5D;
            this.pivotZ = mouseOver.blockZ + 0.5D;
            this.hasPivot = true;
            this.pivotDirty = false;
            return true;
        }

        this.hasPivot = false;
        return false;
    }

    private MovingObjectPosition findNearestPivotHit(Vec3 rayStart, Vec3 lookDirection, double rayDistance) {
        Vec3 rayEnd = rayStart.addVector(
                lookDirection.xCoord * rayDistance,
                lookDirection.yCoord * rayDistance,
                lookDirection.zCoord * rayDistance
        );

        double travelled = 0.0D;
        double currentX = rayStart.xCoord;
        double currentY = rayStart.yCoord;
        double currentZ = rayStart.zCoord;
        int blockX = MathHelper.floor_double(currentX);
        int blockY = MathHelper.floor_double(currentY);
        int blockZ = MathHelper.floor_double(currentZ);

        for (int step = 0; step < MAX_PIVOT_RAYCAST_STEPS && travelled <= rayDistance; step++) {
            MovingObjectPosition hit = this.tracePivotBlock(blockX, blockY, blockZ, rayStart, rayEnd);
            if (hit != null) {
                return hit;
            }

            double xStep = this.distanceToNextBlockBoundary(currentX, lookDirection.xCoord, blockX);
            double yStep = this.distanceToNextBlockBoundary(currentY, lookDirection.yCoord, blockY);
            double zStep = this.distanceToNextBlockBoundary(currentZ, lookDirection.zCoord, blockZ);
            double nextStep = Math.min(xStep, Math.min(yStep, zStep));
            if (Double.isNaN(nextStep) || Double.isInfinite(nextStep)) {
                return null;
            }

            travelled += Math.max(0.0D, nextStep) + PIVOT_RAYCAST_STEP_EPSILON;
            currentX = rayStart.xCoord + lookDirection.xCoord * travelled;
            currentY = rayStart.yCoord + lookDirection.yCoord * travelled;
            currentZ = rayStart.zCoord + lookDirection.zCoord * travelled;
            blockX = MathHelper.floor_double(currentX);
            blockY = MathHelper.floor_double(currentY);
            blockZ = MathHelper.floor_double(currentZ);
        }

        return null;
    }

    private MovingObjectPosition tracePivotBlock(int blockX, int blockY, int blockZ, Vec3 rayStart, Vec3 rayEnd) {
        if (!this.isRayTraceCollidableBlock(blockX, blockY, blockZ)) {
            return null;
        }

        Block block = this.mc.theWorld.getBlock(blockX, blockY, blockZ);
        return block.collisionRayTrace(this.mc.theWorld, blockX, blockY, blockZ, rayStart, rayEnd);
    }

    private double distanceToNextBlockBoundary(double coordinate, double direction, int blockCoordinate) {
        if (direction > 0.0D) {
            return ((double) blockCoordinate + 1.0D - coordinate) / direction;
        }

        if (direction < 0.0D) {
            return (coordinate - (double) blockCoordinate) / -direction;
        }

        return Double.POSITIVE_INFINITY;
    }

    private boolean isValidPivotHit(MovingObjectPosition hit) {
        if (hit == null || hit.blockY < 0 || hit.blockY >= 256 || this.mc.theWorld == null || this.cameraEntity == null) {
            return false;
        }

        return this.isRayTraceCollidableBlock(hit.blockX, hit.blockY, hit.blockZ);
    }

    private void lockFocusToPivot() {
        if (!this.hasPivot || this.cameraEntity == null) {
            return;
        }

        this.focusX = this.pivotX;
        this.focusY = this.pivotY;
        this.focusZ = this.pivotZ;
        double dx = this.cameraEntity.posX - this.focusX;
        double dy = this.cameraEntity.posY - this.focusY;
        double dz = this.cameraEntity.posZ - this.focusZ;
        Vec3 lookDirection = this.getLookDirection(this.currentYaw, this.currentPitch);
        double distanceAlongLook = -(dx * lookDirection.xCoord + dy * lookDirection.yCoord + dz * lookDirection.zCoord);
        double fallbackDistance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        this.orbitDistance = Math.max(MIN_CAMERA_DISTANCE, distanceAlongLook > MIN_CAMERA_DISTANCE ? distanceAlongLook : fallbackDistance);
        this.applyCameraState();
    }

    private void invalidatePivotTarget() {
        this.pivotDirty = true;
        this.hasPivot = false;
    }

    private boolean applyRenderedDepthPivotIfFresh() {
        if (!this.hasRenderedDepthPivot || this.cameraEntity == null) {
            return false;
        }

        double dx = this.cameraEntity.posX - this.renderedDepthCameraX;
        double dy = this.cameraEntity.posY - this.renderedDepthCameraY;
        double dz = this.cameraEntity.posZ - this.renderedDepthCameraZ;
        if (dx * dx + dy * dy + dz * dz > RENDERED_DEPTH_CAMERA_EPSILON) {
            return false;
        }

        if (this.angularDistance(this.normalizeYaw(this.currentYaw), this.normalizeYaw(this.renderedDepthYaw)) > RENDERED_DEPTH_ANGLE_EPSILON
                || Math.abs(this.currentPitch - this.renderedDepthPitch) > RENDERED_DEPTH_ANGLE_EPSILON) {
            return false;
        }

        this.pivotX = this.renderedDepthPivotX;
        this.pivotY = this.renderedDepthPivotY;
        this.pivotZ = this.renderedDepthPivotZ;
        this.hasPivot = true;
        this.pivotDirty = false;
        return true;
    }

    private int[] findRenderedDepthBlock(double hitX, double hitY, double hitZ, Vec3 lookDirection) {
        int[][] candidates = new int[][]{
                this.floorBlockCoordinates(
                        hitX + lookDirection.xCoord * RENDERED_DEPTH_BLOCK_NUDGE,
                        hitY + lookDirection.yCoord * RENDERED_DEPTH_BLOCK_NUDGE,
                        hitZ + lookDirection.zCoord * RENDERED_DEPTH_BLOCK_NUDGE
                ),
                this.floorBlockCoordinates(
                        hitX + lookDirection.xCoord * RENDERED_DEPTH_BLOCK_NUDGE * 3.0D,
                        hitY + lookDirection.yCoord * RENDERED_DEPTH_BLOCK_NUDGE * 3.0D,
                        hitZ + lookDirection.zCoord * RENDERED_DEPTH_BLOCK_NUDGE * 3.0D
                ),
                this.floorBlockCoordinates(hitX, hitY, hitZ),
                this.floorBlockCoordinates(
                        hitX - lookDirection.xCoord * RENDERED_DEPTH_BLOCK_NUDGE,
                        hitY - lookDirection.yCoord * RENDERED_DEPTH_BLOCK_NUDGE,
                        hitZ - lookDirection.zCoord * RENDERED_DEPTH_BLOCK_NUDGE
                )
        };

        for (int[] candidate : candidates) {
            if (this.isRayTraceCollidableBlock(candidate[0], candidate[1], candidate[2])) {
                return candidate;
            }
        }

        for (int[] candidate : candidates) {
            if (candidate[1] >= 0 && candidate[1] < 256) {
                return candidate;
            }
        }

        return null;
    }

    private int[] floorBlockCoordinates(double x, double y, double z) {
        return new int[]{
                MathHelper.floor_double(x),
                MathHelper.floor_double(y),
                MathHelper.floor_double(z)
        };
    }

    private boolean isRayTraceCollidableBlock(int blockX, int blockY, int blockZ) {
        if (this.mc.theWorld == null || blockY < 0 || blockY >= 256) {
            return false;
        }

        Block block = this.mc.theWorld.getBlock(blockX, blockY, blockZ);
        if (block == null || block == Blocks.air || block.isAir(this.mc.theWorld, blockX, blockY, blockZ)) {
            return false;
        }

        int metadata = this.mc.theWorld.getBlockMetadata(blockX, blockY, blockZ);
        return block.canCollideCheck(metadata, false);
    }

    private Vec3 getLookDirection(float yaw, float pitch) {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        double cosPitch = Math.cos(pitchRad);
        return Vec3.createVectorHelper(
                -Math.sin(yawRad) * cosPitch,
                -Math.sin(pitchRad),
                Math.cos(yawRad) * cosPitch
        );
    }

    private float snapYawToNearestDiagonal(float yaw) {
        float normalizedYaw = this.normalizeYaw(yaw);
        float snappedNormalized = DIAGONAL_YAWS[this.findClosestYawIndex(normalizedYaw)];
        return yaw + (snappedNormalized - normalizedYaw);
    }

    private float findNearestDiagonalYaw(float yaw, int directionPreference) {
        float bestYaw = this.unwrapYawNear(DIAGONAL_YAWS[0], yaw);
        float bestDelta = bestYaw - yaw;
        float bestDistance = Math.abs(bestDelta);

        for (int index = 1; index < DIAGONAL_YAWS.length; index++) {
            float candidateYaw = this.unwrapYawNear(DIAGONAL_YAWS[index], yaw);
            float candidateDelta = candidateYaw - yaw;
            float candidateDistance = Math.abs(candidateDelta);
            if (candidateDistance < bestDistance - 0.001F) {
                bestYaw = candidateYaw;
                bestDelta = candidateDelta;
                bestDistance = candidateDistance;
                continue;
            }

            if (Math.abs(candidateDistance - bestDistance) <= 0.001F
                    && this.matchesDirectionPreference(candidateDelta, bestDelta, directionPreference)) {
                bestYaw = candidateYaw;
                bestDelta = candidateDelta;
            }
        }

        return bestYaw;
    }

    private float findNextDiagonalYaw(float yaw, int directionPreference) {
        float direction = directionPreference > 0 ? 1.0F : -1.0F;
        float bestDelta = direction > 0.0F ? Float.MAX_VALUE : -Float.MAX_VALUE;

        for (float diagonalYaw : DIAGONAL_YAWS) {
            float candidateYaw = this.unwrapYawNear(diagonalYaw, yaw);
            float delta = candidateYaw - yaw;

            if (direction > 0.0F) {
                if (delta <= 0.001F) {
                    delta += 360.0F;
                }
                if (delta < bestDelta) {
                    bestDelta = delta;
                }
            } else {
                if (delta >= -0.001F) {
                    delta -= 360.0F;
                }
                if (delta > bestDelta) {
                    bestDelta = delta;
                }
            }
        }

        return yaw + bestDelta;
    }

    private boolean matchesDirectionPreference(float candidateDelta, float currentBestDelta, int directionPreference) {
        if (directionPreference == 0) {
            return false;
        }

        float preferredSign = directionPreference > 0 ? 1.0F : -1.0F;
        boolean candidateMatches = Math.abs(candidateDelta) > 0.001F && Math.signum(candidateDelta) == preferredSign;
        boolean currentMatches = Math.abs(currentBestDelta) > 0.001F && Math.signum(currentBestDelta) == preferredSign;
        return candidateMatches && !currentMatches;
    }

    private float unwrapYawNear(float yaw, float referenceYaw) {
        float unwrapped = yaw;
        while (unwrapped - referenceYaw > 180.0F) {
            unwrapped -= 360.0F;
        }
        while (referenceYaw - unwrapped > 180.0F) {
            unwrapped += 360.0F;
        }
        return unwrapped;
    }

    private float smootherStep(float progress) {
        float clamped = Math.max(0.0F, Math.min(1.0F, progress));
        return clamped * clamped * clamped * (clamped * (clamped * 6.0F - 15.0F) + 10.0F);
    }

    private float clampProgress(float progress) {
        return Math.max(0.0F, Math.min(1.0F, progress));
    }

    private void saveRenderCameraState() {
        this.savedRenderPosX = this.cameraEntity.posX;
        this.savedRenderPosY = this.cameraEntity.posY;
        this.savedRenderPosZ = this.cameraEntity.posZ;
        this.savedRenderPrevPosX = this.cameraEntity.prevPosX;
        this.savedRenderPrevPosY = this.cameraEntity.prevPosY;
        this.savedRenderPrevPosZ = this.cameraEntity.prevPosZ;
        this.savedRenderLastTickPosX = this.cameraEntity.lastTickPosX;
        this.savedRenderLastTickPosY = this.cameraEntity.lastTickPosY;
        this.savedRenderLastTickPosZ = this.cameraEntity.lastTickPosZ;
        this.savedRenderYaw = this.cameraEntity.rotationYaw;
        this.savedRenderPrevYaw = this.cameraEntity.prevRotationYaw;
        this.savedRenderYawHead = this.cameraEntity.rotationYawHead;
        this.savedRenderPrevYawHead = this.cameraEntity.prevRotationYawHead;
        this.savedRenderPitch = this.cameraEntity.rotationPitch;
        this.savedRenderPrevPitch = this.cameraEntity.prevRotationPitch;
    }

    private void setCameraEntityToRenderState(float progress) {
        float easedProgress = this.smootherStep(progress);
        float animationTargetYaw = this.unwrapYawNear(this.targetYaw, this.startYaw);
        float renderYaw = this.startYaw + (animationTargetYaw - this.startYaw) * easedProgress;
        float renderPitch = this.clampPitch(this.startPitch + (this.targetPitch - this.startPitch) * easedProgress);
        double[] cameraPosition = this.calculateCameraPosition(renderYaw, renderPitch);

        this.cameraEntity.setPosition(cameraPosition[0], cameraPosition[1], cameraPosition[2]);
        this.cameraEntity.prevPosX = this.cameraEntity.posX;
        this.cameraEntity.prevPosY = this.cameraEntity.posY;
        this.cameraEntity.prevPosZ = this.cameraEntity.posZ;
        this.cameraEntity.lastTickPosX = this.cameraEntity.posX;
        this.cameraEntity.lastTickPosY = this.cameraEntity.posY;
        this.cameraEntity.lastTickPosZ = this.cameraEntity.posZ;
        this.cameraEntity.rotationYaw = renderYaw;
        this.cameraEntity.prevRotationYaw = renderYaw;
        this.cameraEntity.rotationYawHead = renderYaw;
        this.cameraEntity.prevRotationYawHead = renderYaw;
        this.cameraEntity.rotationPitch = renderPitch;
        this.cameraEntity.prevRotationPitch = renderPitch;
    }

    private double[] calculateCameraPosition(float yaw, float pitch) {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        double cosPitch = Math.cos(pitchRad);
        double lookX = -Math.sin(yawRad) * cosPitch;
        double lookY = -Math.sin(pitchRad);
        double lookZ = Math.cos(yawRad) * cosPitch;
        return new double[]{
                this.focusX - lookX * this.orbitDistance,
                this.focusY - lookY * this.orbitDistance,
                this.focusZ - lookZ * this.orbitDistance
        };
    }

    private void syncCameraRenderState() {
        if (this.cameraEntity == null) {
            return;
        }

        this.cameraEntity.prevPosX = this.cameraEntity.posX;
        this.cameraEntity.prevPosY = this.cameraEntity.posY;
        this.cameraEntity.prevPosZ = this.cameraEntity.posZ;
        this.cameraEntity.lastTickPosX = this.cameraEntity.posX;
        this.cameraEntity.lastTickPosY = this.cameraEntity.posY;
        this.cameraEntity.lastTickPosZ = this.cameraEntity.posZ;
        this.cameraEntity.prevRotationYaw = this.currentYaw;
        this.cameraEntity.prevRotationYawHead = this.currentYaw;
        this.cameraEntity.prevRotationPitch = this.currentPitch;
    }

    private void requestTerrainRefresh() {
        this.terrainRefreshToken++;
    }

    private void deactivate(boolean restorePreviousPerspective, boolean reloadRenderers) {
        if (!this.active && !this.clientStateCaptured) {
            this.clearState();
            return;
        }

        this.restoreClientState(restorePreviousPerspective);
        AngelicaPhotoModeCompat.disablePhotoModeOverrides();
        if (reloadRenderers) {
            this.reloadRenderers();
        }
        this.clearState();
    }

    private void restoreClientState(boolean restorePreviousPerspective) {
        if (this.mc.gameSettings != null) {
            if (this.clientStateCaptured) {
                this.mc.gameSettings.thirdPersonView = restorePreviousPerspective ? Math.max(0, this.previousPerspective) : 0;
                this.mc.gameSettings.hideGUI = this.previousHideGui;
            } else {
                this.mc.gameSettings.thirdPersonView = 0;
                this.mc.gameSettings.hideGUI = false;
            }
        }

        if (this.mc.renderViewEntity == this.cameraEntity || this.mc.renderViewEntity == null) {
            this.mc.renderViewEntity = this.mc.thePlayer;
        }
    }

    private void clearState() {
        this.active = false;
        this.playerControlled = false;
        this.cameraEntity = null;
        this.previousRenderViewEntity = null;
        this.clientStateCaptured = false;
        this.previousPerspective = 0;
        this.previousHideGui = false;
        this.previousOrthographicViewHeight = DEFAULT_ORTHOGRAPHIC_VIEW_HEIGHT;
        this.orthographicViewHeight = DEFAULT_ORTHOGRAPHIC_VIEW_HEIGHT;
        this.targetOrthographicViewHeight = DEFAULT_ORTHOGRAPHIC_VIEW_HEIGHT;
        this.orbitDistance = INITIAL_CAMERA_DISTANCE;
        this.terrainRefreshToken = 0;
        this.currentYaw = 0.0F;
        this.startYaw = 0.0F;
        this.targetYaw = 0.0F;
        this.currentPitch = DEFAULT_PITCH;
        this.startPitch = DEFAULT_PITCH;
        this.targetPitch = DEFAULT_PITCH;
        this.rotationProgress = 1.0F;
        this.previousRotationProgress = 1.0F;
        this.pivotDirty = true;
        this.hasPivot = false;
        this.rotationInputActive = false;
        this.hasRenderedDepthPivot = false;
        this.renderTweenStateApplied = false;
        this.controlStatusMessage = "";
        this.controlStatusMessageTicks = 0;
    }

    private void reloadRenderers() {
        if (this.mc.renderGlobal != null && this.mc.theWorld != null) {
            this.mc.renderGlobal.loadRenderers();
        }
    }

    private void showControlStatusMessage(String message) {
        this.controlStatusMessage = message;
        this.controlStatusMessageTicks = CONTROL_STATUS_MESSAGE_TICKS;
    }

    private void tickControlStatusMessage() {
        if (this.controlStatusMessageTicks > 0) {
            this.controlStatusMessageTicks--;
        }
    }

    private boolean isPhysicalKeyDown(KeyBinding keyBinding) {
        int keyCode = keyBinding.getKeyCode();
        if (keyCode < 0) {
            return Mouse.isButtonDown(keyCode + 100);
        }

        return keyCode > 0 && Keyboard.isKeyDown(keyCode);
    }
}

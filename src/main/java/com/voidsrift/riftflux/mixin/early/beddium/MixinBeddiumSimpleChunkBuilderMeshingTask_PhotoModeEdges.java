package com.voidsrift.riftflux.mixin.early.beddium;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import com.voidsrift.riftflux.client.photomode.PhotoModeBlockRenderContext;
import java.util.Collection;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.ventooth.beddium.api.task.SimpleChunkBuilderMeshingTask", remap = false)
public abstract class MixinBeddiumSimpleChunkBuilderMeshingTask_PhotoModeEdges {

    @Unique
    private static final String BEDDIUM_WORLD_RENDERER =
            "com.ventooth.beddium.modules.TerrainRendering.CeleritasWorldRenderer";

    @Unique
    private static Field riftflux$renderField;

    @Unique
    private static Field riftflux$regionField;

    @Unique
    private boolean riftflux$startedPhotoModeBlockContext;

    @Unique
    private boolean riftflux$computedMissingHorizontalSectionSides;

    @Unique
    private int riftflux$missingHorizontalSectionSideMask;

    @Unique
    private boolean riftflux$computedRegionBounds;

    @Unique
    private int riftflux$regionMinX;

    @Unique
    private int riftflux$regionMaxX;

    @Unique
    private int riftflux$regionMinZ;

    @Unique
    private int riftflux$regionMaxZ;

    @Inject(method = "tryRenderBlock", at = @At("HEAD"), require = 0)
    private void riftflux$beginPhotoModeEdgeBlockContext(
            Tessellator tessellator,
            RenderBlocks renderer,
            int pass,
            Block block,
            int x,
            int y,
            int z,
            CallbackInfo ci
    ) {
        this.riftflux$startedPhotoModeBlockContext = false;
        if (this.riftflux$beginPhotoModeEdgeBlockContext(x, y, z)) {
            this.riftflux$startedPhotoModeBlockContext = true;
        }
    }

    @Inject(method = "tryRenderBlock", at = @At("RETURN"), require = 0)
    private void riftflux$endPhotoModeEdgeBlockContext(
            Tessellator tessellator,
            RenderBlocks renderer,
            int pass,
            Block block,
            int x,
            int y,
            int z,
            CallbackInfo ci
    ) {
        if (this.riftflux$startedPhotoModeBlockContext) {
            PhotoModeBlockRenderContext.end();
            this.riftflux$startedPhotoModeBlockContext = false;
        }
    }

    @Unique
    private boolean riftflux$beginPhotoModeEdgeBlockContext(int x, int y, int z) {
        try {
            if (!IsometricPhotoModeController.instance().isActive() || this.riftflux$isNetherWorld()) {
                return false;
            }

            int sideMask = this.riftflux$getForcedHorizontalSideMask(x, z);
            if (sideMask == 0) {
                return false;
            }

            PhotoModeBlockRenderContext.beginForcedSides(x, y, z, sideMask);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Unique
    private int riftflux$getForcedHorizontalSideMask(int x, int z) {
        if (!this.riftflux$ensureRegionBounds()) {
            return 0;
        }

        int sideMask = this.riftflux$getMissingHorizontalSectionSideMask();
        int forcedSideMask = 0;
        if ((sideMask & 1 << 4) != 0 && x == this.riftflux$regionMinX) {
            forcedSideMask |= 1 << 4;
        }
        if ((sideMask & 1 << 5) != 0 && x == this.riftflux$regionMaxX - 1) {
            forcedSideMask |= 1 << 5;
        }
        if ((sideMask & 1 << 2) != 0 && z == this.riftflux$regionMinZ) {
            forcedSideMask |= 1 << 2;
        }
        if ((sideMask & 1 << 3) != 0 && z == this.riftflux$regionMaxZ - 1) {
            forcedSideMask |= 1 << 3;
        }

        return forcedSideMask;
    }

    @Unique
    private int riftflux$getMissingHorizontalSectionSideMask() {
        if (!this.riftflux$computedMissingHorizontalSectionSides) {
            this.riftflux$missingHorizontalSectionSideMask = this.riftflux$computeMissingHorizontalSectionSideMask();
            this.riftflux$computedMissingHorizontalSectionSides = true;
        }

        return this.riftflux$missingHorizontalSectionSideMask;
    }

    @Unique
    private int riftflux$computeMissingHorizontalSectionSideMask() {
        try {
            Object renderSection = this.riftflux$getRenderSection();
            Collection<?> sections = this.riftflux$getAllRenderSections();
            if (renderSection == null || sections == null || sections.isEmpty()) {
                return 0;
            }

            int chunkX = this.riftflux$invokeInt(renderSection, "getChunkX");
            int chunkY = this.riftflux$invokeInt(renderSection, "getChunkY");
            int chunkZ = this.riftflux$invokeInt(renderSection, "getChunkZ");
            int sideMask = 0;
            if (!this.riftflux$hasRenderSection(sections, chunkX - 1, chunkY, chunkZ)) {
                sideMask |= 1 << 4;
            }
            if (!this.riftflux$hasRenderSection(sections, chunkX + 1, chunkY, chunkZ)) {
                sideMask |= 1 << 5;
            }
            if (!this.riftflux$hasRenderSection(sections, chunkX, chunkY, chunkZ - 1)) {
                sideMask |= 1 << 2;
            }
            if (!this.riftflux$hasRenderSection(sections, chunkX, chunkY, chunkZ + 1)) {
                sideMask |= 1 << 3;
            }

            return sideMask;
        } catch (Throwable ignored) {
            return 0;
        }
    }

    @Unique
    private boolean riftflux$hasRenderSection(Collection<?> sections, int chunkX, int chunkY, int chunkZ) {
        for (Object section : sections) {
            try {
                if (section != null
                        && this.riftflux$invokeInt(section, "getChunkX") == chunkX
                        && this.riftflux$invokeInt(section, "getChunkY") == chunkY
                        && this.riftflux$invokeInt(section, "getChunkZ") == chunkZ) {
                    return true;
                }
            } catch (Throwable ignored) {
            }
        }

        return false;
    }

    @Unique
    private boolean riftflux$ensureRegionBounds() {
        if (this.riftflux$computedRegionBounds) {
            return this.riftflux$regionMaxX > this.riftflux$regionMinX && this.riftflux$regionMaxZ > this.riftflux$regionMinZ;
        }

        this.riftflux$computedRegionBounds = true;
        try {
            Object region = this.riftflux$getRenderRegion();
            this.riftflux$regionMinX = this.riftflux$readIntField(region, "minX");
            this.riftflux$regionMaxX = this.riftflux$readIntField(region, "maxX");
            this.riftflux$regionMinZ = this.riftflux$readIntField(region, "minZ");
            this.riftflux$regionMaxZ = this.riftflux$readIntField(region, "maxZ");
            return this.riftflux$regionMaxX > this.riftflux$regionMinX && this.riftflux$regionMaxZ > this.riftflux$regionMinZ;
        } catch (Throwable ignored) {
            this.riftflux$regionMinX = 0;
            this.riftflux$regionMaxX = 0;
            this.riftflux$regionMinZ = 0;
            this.riftflux$regionMaxZ = 0;
            return false;
        }
    }

    @Unique
    private Collection<?> riftflux$getAllRenderSections() throws Exception {
        ClassLoader loader = MixinBeddiumSimpleChunkBuilderMeshingTask_PhotoModeEdges.class.getClassLoader();
        Class<?> worldRendererClass = Class.forName(BEDDIUM_WORLD_RENDERER, false, loader);
        Object worldRenderer = worldRendererClass.getMethod("instanceNullable").invoke(null);
        if (worldRenderer == null) {
            return null;
        }

        Object renderSectionManager = worldRenderer.getClass().getMethod("getRenderSectionManager").invoke(worldRenderer);
        if (renderSectionManager == null) {
            return null;
        }

        Object sections = renderSectionManager.getClass().getMethod("getAllRenderSections").invoke(renderSectionManager);
        return sections instanceof Collection<?> ? (Collection<?>) sections : null;
    }

    @Unique
    private Object riftflux$getRenderSection() throws IllegalAccessException, NoSuchFieldException {
        if (riftflux$renderField == null) {
            riftflux$renderField = this.riftflux$findField(this.getClass(), "render");
        }

        return riftflux$renderField.get(this);
    }

    @Unique
    private Object riftflux$getRenderRegion() throws IllegalAccessException, NoSuchFieldException {
        if (riftflux$regionField == null) {
            riftflux$regionField = this.riftflux$findField(this.getClass(), "region");
        }

        return riftflux$regionField.get(this);
    }

    @Unique
    private int riftflux$readIntField(Object instance, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field field = this.riftflux$findField(instance.getClass(), fieldName);
        return field.getInt(instance);
    }

    @Unique
    private int riftflux$invokeInt(Object instance, String methodName) throws Exception {
        Method method = instance.getClass().getMethod(methodName);
        return ((Integer) method.invoke(instance)).intValue();
    }

    @Unique
    private Field riftflux$findField(Class<?> type, String fieldName) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }

        throw new NoSuchFieldException(fieldName);
    }

    @Unique
    private boolean riftflux$isNetherWorld() {
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft == null ? null : minecraft.theWorld;
        return world != null
                && world.provider != null
                && (world.provider.isHellWorld || world.provider.dimensionId == -1);
    }
}

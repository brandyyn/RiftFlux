package com.voidsrift.riftflux.client.photomode;

import java.util.Collection;
import org.embeddedt.embeddium.impl.render.chunk.RenderSection;

public final class AngelicaPhotoModeRenderGrid {

    private static volatile Bounds bounds;
    private static volatile Bounds previousBounds;
    private static volatile int changedSideMask;
    private static long nextVersion;
    private static Bounds candidateBounds;
    private static int stableCandidateFrames;
    private static final int REQUIRED_STABLE_FRAMES = 3;

    private AngelicaPhotoModeRenderGrid() {
    }

    public static boolean update(Collection<RenderSection> sections, boolean photoModeActive) {
        if (sections == null || sections.isEmpty()) {
            if (photoModeActive && bounds != null) {
                return false;
            }

            boolean changed = bounds != null;
            previousBounds = bounds;
            bounds = null;
            changedSideMask = changed ? (1 << 2) | (1 << 3) | (1 << 4) | (1 << 5) : 0;
            candidateBounds = null;
            stableCandidateFrames = 0;
            if (changed) {
                nextVersion++;
            }
            return changed;
        }

        int minChunkX = Integer.MAX_VALUE;
        int maxChunkX = Integer.MIN_VALUE;
        int minChunkZ = Integer.MAX_VALUE;
        int maxChunkZ = Integer.MIN_VALUE;
        for (RenderSection section : sections) {
            int chunkX = section.getChunkX();
            int chunkZ = section.getChunkZ();
            minChunkX = Math.min(minChunkX, chunkX);
            maxChunkX = Math.max(maxChunkX, chunkX);
            minChunkZ = Math.min(minChunkZ, chunkZ);
            maxChunkZ = Math.max(maxChunkZ, chunkZ);
        }
        Bounds observed = new Bounds(
                0L,
                minChunkX,
                maxChunkX,
                minChunkZ,
                maxChunkZ
        );
        if (!photoModeActive) {
            previousBounds = bounds;
            bounds = observed.withVersion(++nextVersion);
            changedSideMask = (1 << 2) | (1 << 3) | (1 << 4) | (1 << 5);
            candidateBounds = null;
            stableCandidateFrames = 0;
            return false;
        }

        if (sameGrid(bounds, observed)) {
            candidateBounds = null;
            stableCandidateFrames = 0;
            return false;
        }

        if (bounds != null) {
            if (sameGrid(candidateBounds, observed)) {
                stableCandidateFrames++;
            } else {
                candidateBounds = observed;
                stableCandidateFrames = 1;
            }

            if (stableCandidateFrames < REQUIRED_STABLE_FRAMES) {
                return false;
            }

            previousBounds = bounds;
            changedSideMask = (1 << 2) | (1 << 3) | (1 << 4) | (1 << 5);
            bounds = observed.withVersion(++nextVersion);
            candidateBounds = null;
            stableCandidateFrames = 0;
            return true;
        }

        if (sameGrid(candidateBounds, observed)) {
            stableCandidateFrames++;
        } else {
            candidateBounds = observed;
            stableCandidateFrames = 1;
        }

        if (stableCandidateFrames < REQUIRED_STABLE_FRAMES) {
            return false;
        }

        previousBounds = bounds;
        changedSideMask = (1 << 2) | (1 << 3) | (1 << 4) | (1 << 5);
        bounds = observed.withVersion(++nextVersion);
        candidateBounds = null;
        stableCandidateFrames = 0;
        return true;
    }

    public static Bounds get() {
        return bounds;
    }

    public static long getVersion() {
        return nextVersion;
    }

    public static Bounds getPreviousBounds() {
        return previousBounds;
    }

    public static int getChangedSideMask() {
        return changedSideMask;
    }

    private static boolean sameHorizontalBounds(Bounds first, Bounds second) {
        return first != null
                && second != null
                && first.minChunkX == second.minChunkX
                && first.maxChunkX == second.maxChunkX
                && first.minChunkZ == second.minChunkZ
                && first.maxChunkZ == second.maxChunkZ;
    }

    private static boolean sameGrid(Bounds first, Bounds second) {
        return sameHorizontalBounds(first, second);
    }

    public static final class Bounds {
        public final long version;
        public final int minChunkX;
        public final int maxChunkX;
        public final int minChunkZ;
        public final int maxChunkZ;

        private Bounds(
                long version,
                int minChunkX,
                int maxChunkX,
                int minChunkZ,
                int maxChunkZ
        ) {
            this.version = version;
            this.minChunkX = minChunkX;
            this.maxChunkX = maxChunkX;
            this.minChunkZ = minChunkZ;
            this.maxChunkZ = maxChunkZ;
        }

        private Bounds withVersion(long version) {
            return new Bounds(
                    version,
                    this.minChunkX,
                    this.maxChunkX,
                    this.minChunkZ,
                    this.maxChunkZ
            );
        }
    }
}

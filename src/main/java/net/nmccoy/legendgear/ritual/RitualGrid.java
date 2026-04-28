/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.world.ChunkPosition
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.ritual;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.nmccoy.legendgear.ritual.Edge;

public class RitualGrid {
    public HashSet<Edge> edges;
    public ChunkPosition center;
    public ChunkPosition[] places;
    public int[] offsets;
    public static final List<Edge> legitEdges = new ArrayList<Edge>();
    public static final int NUM_EDGES = 6;

    public RitualGrid(int x, int y, int z) {
        if (legitEdges.isEmpty()) {
            for (int i = 0; i < 8; ++i) {
                for (int j = i + 1; j < 8; ++j) {
                    legitEdges.add(new Edge(i, j));
                }
            }
            legitEdges.remove(new Edge(0, 4));
            legitEdges.remove(new Edge(1, 5));
            legitEdges.remove(new Edge(2, 6));
            legitEdges.remove(new Edge(3, 7));
        }
        this.center = new ChunkPosition(x, y, z);
        this.edges = new HashSet();
        this.places = new ChunkPosition[8];
        this.offsets = new int[8];
        this.places[0] = new ChunkPosition(x + 3, y, z);
        this.places[1] = new ChunkPosition(x + 2, y, z + 2);
        this.places[2] = new ChunkPosition(x, y, z + 3);
        this.places[3] = new ChunkPosition(x - 2, y, z + 2);
        this.places[4] = new ChunkPosition(x - 3, y, z);
        this.places[5] = new ChunkPosition(x - 2, y, z - 2);
        this.places[6] = new ChunkPosition(x, y, z - 3);
        this.places[7] = new ChunkPosition(x + 2, y, z - 2);
        this.generateOffsets();
    }

    private void generateOffsets() {
        Random rand = new Random(this.center.hashCode() + 1);
        for (int i = 0; i < this.offsets.length; ++i) {
            int offset = rand.nextInt(6) + 1;
            if (offset >= 4) {
                ++offset;
            }
            this.offsets[i] = offset;
        }
    }

    public void clearEdges() {
        this.edges.clear();
    }

    public int makeEdgeFrom(int position, int offsetIndex) {
        int offset = this.offsets[offsetIndex];
        int end = (position + offset) % 8;
        if (offset != 0) {
            Edge edge = new Edge(position, end);
            if (this.edges.contains(edge)) {
                this.edges.remove(edge);
            } else {
                this.edges.add(edge);
            }
        }
        return end;
    }

    private void generateEdges() {
        Random rand = new Random(this.center.hashCode() + 3);
        while (this.edges.size() < 6) {
            this.edges.add(legitEdges.get(rand.nextInt(legitEdges.size())));
        }
        System.out.print("Edges: ");
        for (Edge e : this.edges) {
            System.out.print(e.toString() + " ");
        }
        System.out.println();
    }

    public List<Integer> pointsConnectedTo(int point) {
        ArrayList<Integer> points = new ArrayList<Integer>();
        for (Edge edge : this.edges) {
            if (!edge.hasPoint(point)) continue;
            points.add(edge.otherPoint(point));
        }
        return points;
    }

    public List<Integer> inhabitedPoints(World world) {
        ArrayList<Integer> points = new ArrayList<Integer>();
        for (int i = 0; i < 8; ++i) {
            ChunkPosition place = this.places[i];
            if (world.isAirBlock(place.chunkPosX, place.chunkPosY, place.chunkPosZ)) continue;
            points.add(i);
        }
        return points;
    }

    public boolean isGridStable(World world) {
        if (this.edges.size() == 0) {
            return false;
        }
        List<Integer> populated = this.inhabitedPoints(world);
        for (int i : populated) {
            List<Integer> links = this.pointsConnectedTo(i);
            for (int j : links) {
                if (populated.contains(j)) continue;
                return false;
            }
        }
        return populated.size() == 8;
    }

    public EdgeState getEdgeState(Edge edge, World world) {
        int occupied = 0;
        if (this.blockOnPoint(edge.first, world) != Blocks.air) {
            ++occupied;
        }
        if (this.blockOnPoint(edge.second, world) != Blocks.air) {
            ++occupied;
        }
        if (occupied == 0) {
            return EdgeState.EMPTY;
        }
        if (occupied == 2) {
            return EdgeState.STABLE;
        }
        return EdgeState.UNSTABLE;
    }

    public Block blockOnPoint(int point, World world) {
        ChunkPosition pos = this.places[point];
        return world.getBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
    }

    public int metaOnPoint(int point, World world) {
        ChunkPosition pos = this.places[point];
        return world.getBlockMetadata(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
    }

    public static enum EdgeState {
        EMPTY,
        UNSTABLE,
        STABLE;

    }
}

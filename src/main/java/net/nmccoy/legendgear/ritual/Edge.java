/*
 * Decompiled with CFR 0.152.
 */
package net.nmccoy.legendgear.ritual;

public class Edge {
    public final int first;
    public final int second;

    public Edge(int pointA, int pointB) throws IllegalArgumentException {
        if (pointB < pointA) {
            this.first = pointB;
            this.second = pointA;
        } else {
            this.first = pointA;
            this.second = pointB;
        }
        if (pointA == pointB) {
            throw new IllegalArgumentException("can't connect point " + pointA + " to itself");
        }
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Edge) {
            Edge other = (Edge)obj;
            return other.first == this.first && other.second == this.second;
        }
        return false;
    }

    public int hashCode() {
        return (this.first + "," + this.second).hashCode();
    }

    public int otherPoint(int point) {
        if (this.first == point) {
            return this.second;
        }
        if (this.second == point) {
            return this.first;
        }
        throw new IllegalArgumentException("point " + point + " not on edge [" + this.first + ", " + this.second + "]");
    }

    public boolean hasPoint(int point) {
        return this.first == point || this.second == point;
    }

    public String toString() {
        return "[" + this.first + ", " + this.second + "]";
    }
}


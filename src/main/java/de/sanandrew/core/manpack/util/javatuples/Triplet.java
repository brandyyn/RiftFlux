/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.util.javatuples;

import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue0;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue1;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public final class Triplet<A, B, C>
extends Tuple
implements IValue0<A>,
IValue1<B>,
IValue2<C> {
    private static final long serialVersionUID = -1877265551599483740L;
    private static final int SIZE = 3;
    private final A val0;
    private final B val1;
    private final C val2;

    public static <A, B, C> Triplet<A, B, C> with(A value0, B value1, C value2) {
        return new Triplet<A, B, C>(value0, value1, value2);
    }

    public static <X> Triplet<X, X, X> fromArray(X[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length != 3) {
            throw new IllegalArgumentException("Array must have exactly 3 elements in order to create a Triplet. Size is " + array.length);
        }
        return new Triplet<X, X, X>(array[0], array[1], array[2]);
    }

    public static <X> Triplet<X, X, X> fromCollection(Collection<X> collection) {
        return Triplet.fromIterable(collection);
    }

    public static <X> Triplet<X, X, X> fromIterable(Iterable<X> iterable) {
        return Triplet.fromIterable(iterable, 0, true);
    }

    public static <X> Triplet<X, X, X> fromIterable(Iterable<X> iterable, int index) {
        return Triplet.fromIterable(iterable, index, false);
    }

    private static <X> Triplet<X, X, X> fromIterable(Iterable<X> iterable, int index, boolean checkSize) {
        if (iterable == null) {
            throw new IllegalArgumentException("Iterable cannot be null");
        }
        ArrayList<X> elements = new ArrayList<X>(3);
        Iterator<X> iter = iterable.iterator();
        int lastIndex = index + 3 - 1;
        for (int i = 0; i <= lastIndex; ++i) {
            if (iter.hasNext()) {
                X element = iter.next();
                if (i < index) continue;
                if (checkSize && i == lastIndex && iter.hasNext()) {
                    throw new IllegalArgumentException("Iterable must have exactly 3 elements in order to create a Triplet.");
                }
                elements.add(element);
                continue;
            }
            if (i < index) {
                throw new IllegalArgumentException(String.format("Iterable has not enough elements to grab a value from index %d", index));
            }
            throw new IllegalArgumentException(String.format("Not enough elements for creating a Triplet (3 needed, %d given)", i));
        }
        return new Triplet(elements.get(0), elements.get(1), elements.get(2));
    }

    public Triplet(A value0, B value1, C value2) {
        super(value0, value1, value2);
        this.val0 = value0;
        this.val1 = value1;
        this.val2 = value2;
    }

    @Override
    public A getValue0() {
        return this.val0;
    }

    @Override
    public B getValue1() {
        return this.val1;
    }

    @Override
    public C getValue2() {
        return this.val2;
    }

    @Override
    public int getSize() {
        return 3;
    }
}


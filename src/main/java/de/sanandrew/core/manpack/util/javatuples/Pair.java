/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.util.javatuples;

import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue0;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue1;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public final class Pair<A, B>
extends Tuple
implements IValue0<A>,
IValue1<B> {
    private static final long serialVersionUID = 2438099850625502138L;
    private static final int SIZE = 2;
    private final A val0;
    private final B val1;

    public static <A, B> Pair<A, B> with(A value0, B value1) {
        return new Pair<A, B>(value0, value1);
    }

    public static <X> Pair<X, X> fromArray(X[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length != 2) {
            throw new IllegalArgumentException(String.format("Array must have exactly 2 elements in order to create a Pair. Size is %d", array.length));
        }
        return new Pair<X, X>(array[0], array[1]);
    }

    public static <X> Pair<X, X> fromCollection(Collection<X> collection) {
        return Pair.fromIterable(collection);
    }

    public static <X> Pair<X, X> fromIterable(Iterable<X> iterable) {
        return Pair.fromIterable(iterable, 0, true);
    }

    public static <X> Pair<X, X> fromIterable(Iterable<X> iterable, int index) {
        return Pair.fromIterable(iterable, index, false);
    }

    private static <X> Pair<X, X> fromIterable(Iterable<X> iterable, int index, boolean checkSize) {
        if (iterable == null) {
            throw new IllegalArgumentException("Iterable cannot be null");
        }
        ArrayList<X> elements = new ArrayList<X>(2);
        Iterator<X> iter = iterable.iterator();
        int lastIndex = index + 2 - 1;
        for (int i = 0; i <= lastIndex; ++i) {
            if (iter.hasNext()) {
                X element = iter.next();
                if (i < index) continue;
                if (checkSize && i == lastIndex && iter.hasNext()) {
                    throw new IllegalArgumentException("Iterable must have exactly 2 elements in order to create a Pair.");
                }
                elements.add(element);
                continue;
            }
            if (i < index) {
                throw new IllegalArgumentException(String.format("Iterable has not enough elements to grab a value from index %d", index));
            }
            throw new IllegalArgumentException(String.format("Not enough elements for creating a Pair (2 needed, %d given)", i));
        }
        return new Pair(elements.get(0), elements.get(1));
    }

    public Pair(A value0, B value1) {
        super(value0, value1);
        this.val0 = value0;
        this.val1 = value1;
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
    public int getSize() {
        return 2;
    }
}


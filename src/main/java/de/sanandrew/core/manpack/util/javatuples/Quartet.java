/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.util.javatuples;

import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue0;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue1;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue2;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue3;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public final class Quartet<A, B, C, D>
extends Tuple
implements IValue0<A>,
IValue1<B>,
IValue2<C>,
IValue3<D> {
    private static final long serialVersionUID = 2445136048617019549L;
    private static final int SIZE = 4;
    private final A val0;
    private final B val1;
    private final C val2;
    private final D val3;

    public static <A, B, C, D> Quartet<A, B, C, D> with(A value0, B value1, C value2, D value3) {
        return new Quartet<A, B, C, D>(value0, value1, value2, value3);
    }

    public static <X> Quartet<X, X, X, X> fromArray(X[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length != 4) {
            throw new IllegalArgumentException("Array must have exactly 4 elements in order to create a Quartet. Size is " + array.length);
        }
        return new Quartet<X, X, X, X>(array[0], array[1], array[2], array[3]);
    }

    public static <X> Quartet<X, X, X, X> fromCollection(Collection<X> collection) {
        return Quartet.fromIterable(collection);
    }

    public static <X> Quartet<X, X, X, X> fromIterable(Iterable<X> iterable) {
        return Quartet.fromIterable(iterable, 0, true);
    }

    public static <X> Quartet<X, X, X, X> fromIterable(Iterable<X> iterable, int index) {
        return Quartet.fromIterable(iterable, index, false);
    }

    private static <X> Quartet<X, X, X, X> fromIterable(Iterable<X> iterable, int index, boolean checkSize) {
        if (iterable == null) {
            throw new IllegalArgumentException("Iterable cannot be null");
        }
        ArrayList<X> elements = new ArrayList<X>(4);
        Iterator<X> iter = iterable.iterator();
        int lastIndex = index + 4 - 1;
        for (int i = 0; i <= lastIndex; ++i) {
            if (iter.hasNext()) {
                X element = iter.next();
                if (i < index) continue;
                if (checkSize && i == lastIndex && iter.hasNext()) {
                    throw new IllegalArgumentException("Iterable must have exactly 4 elements in order to create a Quartet.");
                }
                elements.add(element);
                continue;
            }
            if (i < index) {
                throw new IllegalArgumentException(String.format("Iterable has not enough elements to grab a value from index %d", index));
            }
            throw new IllegalArgumentException(String.format("Not enough elements for creating a Quartet (4 needed, %d given)", i));
        }
        return new Quartet(elements.get(0), elements.get(1), elements.get(2), elements.get(3));
    }

    public Quartet(A value0, B value1, C value2, D value3) {
        super(value0, value1, value2, value3);
        this.val0 = value0;
        this.val1 = value1;
        this.val2 = value2;
        this.val3 = value3;
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
    public D getValue3() {
        return this.val3;
    }

    @Override
    public int getSize() {
        return 4;
    }
}


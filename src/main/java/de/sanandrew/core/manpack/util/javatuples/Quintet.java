/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.util.javatuples;

import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue0;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue1;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue2;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue3;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue4;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public final class Quintet<A, B, C, D, E>
extends Tuple
implements IValue0<A>,
IValue1<B>,
IValue2<C>,
IValue3<D>,
IValue4<E> {
    private static final long serialVersionUID = -1579008485383872628L;
    private static final int SIZE = 5;
    private final A val0;
    private final B val1;
    private final C val2;
    private final D val3;
    private final E val4;

    public static <A, B, C, D, E> Quintet<A, B, C, D, E> with(A value0, B value1, C value2, D value3, E value4) {
        return new Quintet<A, B, C, D, E>(value0, value1, value2, value3, value4);
    }

    public static <X> Quintet<X, X, X, X, X> fromArray(X[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length != 5) {
            throw new IllegalArgumentException("Array must have exactly 5 elements in order to create a Quintet. Size is " + array.length);
        }
        return new Quintet<X, X, X, X, X>(array[0], array[1], array[2], array[3], array[4]);
    }

    public static <X> Quintet<X, X, X, X, X> fromCollection(Collection<X> collection) {
        return Quintet.fromIterable(collection);
    }

    public static <X> Quintet<X, X, X, X, X> fromIterable(Iterable<X> iterable) {
        return Quintet.fromIterable(iterable, 0, true);
    }

    public static <X> Quintet<X, X, X, X, X> fromIterable(Iterable<X> iterable, int index) {
        return Quintet.fromIterable(iterable, index, false);
    }

    private static <X> Quintet<X, X, X, X, X> fromIterable(Iterable<X> iterable, int index, boolean checkSize) {
        if (iterable == null) {
            throw new IllegalArgumentException("Iterable cannot be null");
        }
        ArrayList<X> elements = new ArrayList<X>(5);
        Iterator<X> iter = iterable.iterator();
        int lastIndex = index + 5 - 1;
        for (int i = 0; i <= lastIndex; ++i) {
            if (iter.hasNext()) {
                X element = iter.next();
                if (i < index) continue;
                if (checkSize && i == lastIndex && iter.hasNext()) {
                    throw new IllegalArgumentException("Iterable must have exactly 5 elements in order to create a Quintet.");
                }
                elements.add(element);
                continue;
            }
            if (i < index) {
                throw new IllegalArgumentException(String.format("Iterable has not enough elements to grab a value from index %d", index));
            }
            throw new IllegalArgumentException(String.format("Not enough elements for creating a Quintet (5 needed, %d given)", i));
        }
        return new Quintet(elements.get(0), elements.get(1), elements.get(2), elements.get(3), elements.get(4));
    }

    public Quintet(A value0, B value1, C value2, D value3, E value4) {
        super(value0, value1, value2, value3, value4);
        this.val0 = value0;
        this.val1 = value1;
        this.val2 = value2;
        this.val3 = value3;
        this.val4 = value4;
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
    public E getValue4() {
        return this.val4;
    }

    @Override
    public int getSize() {
        return 5;
    }
}


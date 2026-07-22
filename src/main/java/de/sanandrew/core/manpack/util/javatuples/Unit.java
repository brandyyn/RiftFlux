/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.util.javatuples;

import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue0;
import java.util.Collection;
import java.util.Iterator;

public final class Unit<A>
extends Tuple
implements IValue0<A> {
    private static final long serialVersionUID = -9113114724069537096L;
    private static final int SIZE = 1;
    private final A val0;

    public static <A> Unit<A> with(A value0) {
        return new Unit<A>(value0);
    }

    public static <X> Unit<X> fromArray(X[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length != 1) {
            throw new IllegalArgumentException("Array must have exactly 1 element in order to create a Unit. Size is " + array.length);
        }
        return new Unit<X>(array[0]);
    }

    public static <X> Unit<X> fromCollection(Collection<X> collection) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection cannot be null");
        }
        if (collection.size() != 1) {
            throw new IllegalArgumentException("Collection must have exactly 1 element in order to create a Unit. Size is " + collection.size());
        }
        return new Unit<X>(collection.iterator().next());
    }

    public static <X> Unit<X> fromIterable(Iterable<X> iterable) {
        return Unit.fromIterable(iterable, 0, true);
    }

    public static <X> Unit<X> fromIterable(Iterable<X> iterable, int index) {
        return Unit.fromIterable(iterable, index, false);
    }

    private static <X> Unit<X> fromIterable(Iterable<X> iterable, int index, boolean checkSize) {
        if (iterable == null) {
            throw new IllegalArgumentException("Iterable cannot be null");
        }
        Iterator<X> iter = iterable.iterator();
        for (int i = 0; i <= index; ++i) {
            if (iter.hasNext()) {
                X element = iter.next();
                if (i != index) continue;
                if (checkSize && iter.hasNext()) {
                    throw new IllegalArgumentException("Iterable must have exactly 1 element in order to create a Unit.");
                }
                return new Unit<X>(element);
            }
            if (i < index) {
                throw new IllegalArgumentException(String.format("Iterable has not enough elements to grab a value from index %d", index));
            }
            throw new IllegalArgumentException(String.format("Not enough elements for creating a Unit (1 needed, %d given)", i));
        }
        return null;
    }

    public Unit(A value0) {
        super(value0);
        this.val0 = value0;
    }

    @Override
    public A getValue0() {
        return this.val0;
    }

    @Override
    public int getSize() {
        return 1;
    }
}


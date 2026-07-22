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
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue5;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue6;
import de.sanandrew.core.manpack.util.javatuples.valueintf.IValue7;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public final class Octet<A, B, C, D, E, F, G, H>
extends Tuple
implements IValue0<A>,
IValue1<B>,
IValue2<C>,
IValue3<D>,
IValue4<E>,
IValue5<F>,
IValue6<G>,
IValue7<H> {
    private static final long serialVersionUID = -1187955276020306879L;
    private static final int SIZE = 8;
    private final A val0;
    private final B val1;
    private final C val2;
    private final D val3;
    private final E val4;
    private final F val5;
    private final G val6;
    private final H val7;

    public static <A, B, C, D, E, F, G, H> Octet<A, B, C, D, E, F, G, H> with(A value0, B value1, C value2, D value3, E value4, F value5, G value6, H value7) {
        return new Octet<A, B, C, D, E, F, G, H>(value0, value1, value2, value3, value4, value5, value6, value7);
    }

    public static <X> Octet<X, X, X, X, X, X, X, X> fromArray(X[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length != 8) {
            throw new IllegalArgumentException("Array must have exactly 8 elements in order to create an Octet. Size is " + array.length);
        }
        return new Octet<X, X, X, X, X, X, X, X>(array[0], array[1], array[2], array[3], array[4], array[5], array[6], array[7]);
    }

    public static <X> Octet<X, X, X, X, X, X, X, X> fromCollection(Collection<X> collection) {
        return Octet.fromIterable(collection);
    }

    public static <X> Octet<X, X, X, X, X, X, X, X> fromIterable(Iterable<X> iterable) {
        return Octet.fromIterable(iterable, 0, true);
    }

    public static <X> Octet<X, X, X, X, X, X, X, X> fromIterable(Iterable<X> iterable, int index) {
        return Octet.fromIterable(iterable, index, false);
    }

    private static <X> Octet<X, X, X, X, X, X, X, X> fromIterable(Iterable<X> iterable, int index, boolean checkSize) {
        if (iterable == null) {
            throw new IllegalArgumentException("Iterable cannot be null");
        }
        ArrayList<X> elements = new ArrayList<X>(8);
        Iterator<X> iter = iterable.iterator();
        int lastIndex = index + 8 - 1;
        for (int i = 0; i <= lastIndex; ++i) {
            if (iter.hasNext()) {
                X element = iter.next();
                if (i < index) continue;
                if (checkSize && i == lastIndex && iter.hasNext()) {
                    throw new IllegalArgumentException("Iterable must have exactly 8 elements in order to create a Octet.");
                }
                elements.add(element);
                continue;
            }
            if (i < index) {
                throw new IllegalArgumentException(String.format("Iterable has not enough elements to grab a value from index %d", index));
            }
            throw new IllegalArgumentException(String.format("Not enough elements for creating a Octet (8 needed, %d given)", i));
        }
        return new Octet(elements.get(0), elements.get(1), elements.get(2), elements.get(3), elements.get(4), elements.get(5), elements.get(6), elements.get(7));
    }

    public Octet(A value0, B value1, C value2, D value3, E value4, F value5, G value6, H value7) {
        super(value0, value1, value2, value3, value4, value5, value6, value7);
        this.val0 = value0;
        this.val1 = value1;
        this.val2 = value2;
        this.val3 = value3;
        this.val4 = value4;
        this.val5 = value5;
        this.val6 = value6;
        this.val7 = value7;
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
    public F getValue5() {
        return this.val5;
    }

    @Override
    public G getValue6() {
        return this.val6;
    }

    @Override
    public H getValue7() {
        return this.val7;
    }

    @Override
    public int getSize() {
        return 8;
    }
}


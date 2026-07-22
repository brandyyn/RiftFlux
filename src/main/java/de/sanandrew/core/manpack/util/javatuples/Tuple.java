/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.buffer.ByteBufOutputStream
 *  io.netty.handler.codec.serialization.ObjectDecoderInputStream
 *  io.netty.handler.codec.serialization.ObjectEncoderOutputStream
 *  org.apache.logging.log4j.Level
 */
package de.sanandrew.core.manpack.util.javatuples;

import de.sanandrew.core.manpack.init.ManPackLoadingPlugin;
import de.sanandrew.core.manpack.util.javatuples.Decade;
import de.sanandrew.core.manpack.util.javatuples.Ennead;
import de.sanandrew.core.manpack.util.javatuples.Octet;
import de.sanandrew.core.manpack.util.javatuples.Pair;
import de.sanandrew.core.manpack.util.javatuples.Quartet;
import de.sanandrew.core.manpack.util.javatuples.Quintet;
import de.sanandrew.core.manpack.util.javatuples.Septet;
import de.sanandrew.core.manpack.util.javatuples.Sextet;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import de.sanandrew.core.manpack.util.javatuples.Unit;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.Level;

public abstract class Tuple
implements Iterable<Object>,
Serializable,
Comparable<Tuple> {
    private static final long serialVersionUID = 5431085632328343101L;
    private final Object[] valueArray;
    private final List<Object> valueList;

    protected Tuple(Object ... values) {
        this.valueArray = values;
        this.valueList = Arrays.asList(values);
    }

    public abstract int getSize();

    public final Object getValue(int pos) {
        if (pos >= this.getSize()) {
            throw new IllegalArgumentException(String.format("Cannot retrieve value index %1$d in %2$s. Indices of %2$s range from 0 to %3$d", pos, this.getClass().getSimpleName(), this.getSize() - 1));
        }
        return this.valueArray[pos];
    }

    @Override
    public final Iterator<Object> iterator() {
        return this.valueList.iterator();
    }

    public final String toString() {
        return this.valueList.toString();
    }

    public final boolean contains(Object value) {
        for (Object val : this.valueList) {
            if (!(val == null ? value == null : val.equals(value))) continue;
            return true;
        }
        return false;
    }

    public final boolean containsAll(Collection<?> collection) {
        for (Object value : collection) {
            if (this.contains(value)) continue;
            return false;
        }
        return true;
    }

    public final boolean containsAll(Object ... values) {
        if (values == null) {
            throw new IllegalArgumentException("containsAll needs at least 1 parameter or array cannot be null.");
        }
        for (Object value : values) {
            if (this.contains(value)) continue;
            return false;
        }
        return true;
    }

    public final int indexOf(Object value) {
        int i = 0;
        for (Object val : this.valueList) {
            if (val == null ? value == null : val.equals(value)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public final int lastIndexOf(Object value) {
        for (int i = this.getSize() - 1; i >= 0; --i) {
            Object val = this.valueList.get(i);
            if (!(val == null ? value == null : val.equals(value))) continue;
            return i;
        }
        return -1;
    }

    public final List<Object> toList() {
        return Collections.unmodifiableList(new ArrayList<Object>(this.valueList));
    }

    public final Object[] toArray() {
        return (Object[])this.valueArray.clone();
    }

    public final int hashCode() {
        return 31 + this.valueList.hashCode();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Tuple other = (Tuple)obj;
        return this.valueList.equals(other.valueList);
    }

    @Override
    public int compareTo(Tuple o) {
        int tLen = this.valueArray.length;
        Object[] oValues = o.valueArray;
        int oLen = oValues.length;
        for (int i = 0; i < tLen && i < oLen; ++i) {
            Comparable tElement = (Comparable)this.valueArray[i];
            Comparable oElement = (Comparable)oValues[i];
            int comparison = tElement.compareTo(oElement);
            if (comparison == 0) continue;
            return comparison;
        }
        return Integer.valueOf(tLen).compareTo(oLen);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Tuple readFromByteBufStream(ByteBufInputStream stream) {
        try (ObjectDecoderInputStream odis = new ObjectDecoderInputStream((InputStream)stream);){
            Tuple tuple = (Tuple)odis.readObject();
            return tuple;
        }
        catch (IOException | ClassNotFoundException ex) {
            ManPackLoadingPlugin.MOD_LOG.log(Level.ERROR, "Cannot deserialize Tuple!", (Throwable)ex);
            return null;
        }
    }

    public static void writeToByteBufStream(Tuple tuple, ByteBufOutputStream stream) {
        try (ObjectEncoderOutputStream oeos = new ObjectEncoderOutputStream((OutputStream)stream);){
            oeos.writeObject((Object)tuple);
        }
        catch (IOException ex) {
            ManPackLoadingPlugin.MOD_LOG.log(Level.ERROR, "Cannot serialize Tuple!", (Throwable)ex);
        }
    }

    public static Tuple from(Object ... values) {
        switch (values.length) {
            case 1: {
                return Unit.fromArray(values);
            }
            case 2: {
                return Pair.fromArray(values);
            }
            case 3: {
                return Triplet.fromArray(values);
            }
            case 4: {
                return Quartet.fromArray(values);
            }
            case 5: {
                return Quintet.fromArray(values);
            }
            case 6: {
                return Sextet.fromArray(values);
            }
            case 7: {
                return Septet.fromArray(values);
            }
            case 8: {
                return Octet.fromArray(values);
            }
            case 9: {
                return Ennead.fromArray(values);
            }
            case 10: {
                return Decade.fromArray(values);
            }
        }
        throw new RuntimeException(String.format("Cannot create Tuple with size %d!", values.length));
    }
}


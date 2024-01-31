package org.davidmoten.kool;

import java.util.Objects;

import com.github.davidmoten.guavamini.Preconditions;

public final class Indexed<T> {

    private final T t;
    private final long index;

    private Indexed(T t, long index) {
        Preconditions.checkNotNull(t);
        this.t = t;
        this.index = index;
    }

    public static <T> Indexed<T> create(T t, long index) {
        return new Indexed<T>(t, index);
    }

    public T value() {
        return t;
    }

    public long index() {
        return index;
    }
    

    @Override
    public int hashCode() {
        return Objects.hash(index, t);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Indexed<?> other = (Indexed<?>) obj;
        if (index != other.index)
            return false;
        if (!t.equals(other.t))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Indexed[index=" + index + ", value=" + t + "]";
    }

}

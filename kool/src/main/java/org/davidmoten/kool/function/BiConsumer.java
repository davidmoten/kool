package org.davidmoten.kool.function;

@FunctionalInterface
public interface BiConsumer<T, R> {

    void accept(T t, R r) throws Exception;

}

package org.davidmoten.kool.function;

public final class Consumers {

    private Consumers() {
        // prevent instantiation
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> doNothing() {
        return (Consumer<T>) DoNothingHolder.INSTANCE;
    }
    
    private static final class DoNothingHolder {
        static final Consumer<Object> INSTANCE = t -> {};
    }
}

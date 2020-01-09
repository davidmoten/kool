package org.davidmoten.kool;

import java.util.concurrent.Callable;

import org.davidmoten.kool.function.BiFunction;

public interface Collector<T, R> extends Callable<R>, BiFunction<R, T, R> {

}

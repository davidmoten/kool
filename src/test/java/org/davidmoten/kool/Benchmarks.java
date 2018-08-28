package org.davidmoten.kool;

import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class Benchmarks {

    @Benchmark
    public long rangeOneTo100CountJava() {
        return IntStream.range(1,  100).count();
    }
    
    @Benchmark
    public long rangeOneTo100CountKool() {
        return Stream.range(1,  100).count();
    }
}
package org.davidmoten.kool;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

//@State(Scope.Benchmark)
public class Benchmarks {

//    @Benchmark
    public long rangeOneTo100CountJava() {
        return IntStream.range(1,  100).count();
    }
    
//    @Benchmark
    public long rangeOneTo100CountKool() {
        return Stream.range(1,  100).count();
    }
    
//    @Benchmark
    public int toListJava() {
        return java.util.stream.Stream.of(1, 2, 3, 4).collect(Collectors.toList()).size();
    }
    
//    @Benchmark
    public int toListKool() {
        return Stream.of(1, 2, 3, 4).toList().size();
    }
}
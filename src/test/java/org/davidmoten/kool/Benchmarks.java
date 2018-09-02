package org.davidmoten.kool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;

//@State(Scope.Benchmark)
public class Benchmarks {

//    @Benchmark
    public long rangeOneTo100CountJava() {
        return IntStream.range(1, 100).count();
    }

//    @Benchmark
    public long rangeOneTo100CountKool() {
        return Stream.range(1, 100).count();
    }

//    @Benchmark
    public int toListJava() {
        return java.util.stream.Stream.of(1, 2, 3, 4).collect(Collectors.toList()).size();
    }

//    @Benchmark
    public int toListKool() {
        return Stream.of(1, 2, 3, 4).toList().size();
    }

    @Benchmark
    public List<String> readFileJava() throws IOException {
        return Files.lines(Paths.get("src/test/resources/test.txt")) //
                .filter(x -> x.length() % 2 == 0).collect(Collectors.toList());
    }
    
    @Benchmark
    public List<String> readFileKool() throws IOException {
        return Stream.lines(new File("src/test/resources/test.txt")) //
                .filter(x -> x.length() % 2 == 0) //
                .toList();
    }
}
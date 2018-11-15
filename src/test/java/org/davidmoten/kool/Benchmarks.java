package org.davidmoten.kool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;

//@State(Scope.Benchmark)
public class Benchmarks {

    // @Benchmark
    public long rangeOneTo100CountJava() {
        return IntStream.range(1, 100).count();
    }

    // @Benchmark
    public long rangeOneTo100CountKool() {
        return Stream.range(1, 100).count().get();
    }

    // @Benchmark
    public int toListJava() {
        return java.util.stream.Stream.of(1, 2, 3, 4).collect(Collectors.toList()).size();
    }

    // @Benchmark
    public int toListKool() {
        return Stream.of(1, 2, 3, 4).toList().size();
    }

//    @Benchmark
    public List<String> readFileJava() throws IOException {
        try (@SuppressWarnings("resource")
        java.util.stream.Stream<String> stream = //
                new BufferedReader(new InputStreamReader(new FileInputStream(new File("src/test/resources/test2.txt"))))
                        .lines()) { //
            return stream.filter(x -> x.length() % 2 == 0).collect(Collectors.toList());
        }
    }

//    @Benchmark
    public List<String> readFileKool() throws IOException {
        return Stream.lines(new File("src/test/resources/test2.txt")) //
                .filter(x -> x.length() % 2 == 0) //
                .toList();
    }

    public static void main(String[] args) throws IOException {
        Benchmarks b = new Benchmarks();
        while (true) {
            b.readFileJava();
        }
    }
}
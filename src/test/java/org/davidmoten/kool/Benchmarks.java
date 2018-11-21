package org.davidmoten.kool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class Benchmarks {

    @Benchmark
    public long rangeOneTo100CountJava() {
        return IntStream.range(1, 100).count();
    }

    @Benchmark
    public long rangeOneTo100CountKool() {
        return Stream.range(1, 100).count().get();
    }

    @Benchmark
    public int toListJava() {
        return java.util.stream.Stream.of(1, 2, 3, 4).collect(Collectors.toList()).size();
    }

    @Benchmark
    public int toListKool() {
        return Stream.of(1, 2, 3, 4).toList().size();
    }

    @Benchmark
    public List<String> readFileJava() throws IOException {
        try (@SuppressWarnings("resource")
        java.util.stream.Stream<String> stream = //
                new BufferedReader(new InputStreamReader(new FileInputStream(new File("src/test/resources/test2.txt"))))
                        .lines()) { //
            return stream.filter(x -> x.length() % 2 == 0).collect(Collectors.toList());
        }
    }

    @Benchmark
    public List<String> readFileKool() throws IOException {
        return Stream.lines(new File("src/test/resources/test2.txt")) //
                .filter(x -> x.length() % 2 == 0) //
                .toList();
    }

    @Benchmark
    public long flatMapMinMapReduceKool() {
        return Stream.range(1, 1000) //
                // .flatMap(x -> Stream.of(x, x + 1, x + 4) //
                // .min(Comparator.naturalOrder())) //
                .flatMap(x -> Stream.of(x).min(Comparator.naturalOrder())) //
                .map(Function.identity()) //
                .reduce((x, y) -> x + y) //
                .get() //
                .get();
    }

    @Benchmark
    public long flatMapMinMapReduceJavaStreams() {
        return LongStream.range(1, 1000) //
                .boxed().flatMap(x -> java.util.stream.Stream.of(LongStream.of(x, x + 1, x + 4).min().getAsLong())) //
                .map(Function.identity()) //
                .reduce((x, y) -> x + y) //
                .get();
    }

    public static void main(String[] args) throws IOException {
        Benchmarks b = new Benchmarks();
        Stream.range(1, 1).flatMap(x -> Maybe.of(x)).count().get();
        // b.flatMapMinMapReduceKool();
        System.exit(0);
        while (true) {
            b.readFileJava();
        }
    }
}
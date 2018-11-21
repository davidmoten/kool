/*
 * Copyright (C) 2015 José Paumard
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.davidmoten.kool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

/**
 * <p>
 * Shakespeare plays Scrabble with Kool.
 * 
 * <p>
 * Factories used: of, from(Iterable), chars
 * <p>
 * Operators used: map, flatMap, collect, reduce, take, filter
 * 
 */
public class ShakespearePlaysScrabbleWithKool extends ShakespearePlaysScrabble {

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5, time = ShakespearePlaysScrabble.DURATION_SECONDS)
    @Measurement(iterations = 5, time = ShakespearePlaysScrabble.DURATION_SECONDS)
    @Fork(1)
    public List<Entry<Integer, List<String>>> measureThroughput() throws InterruptedException {

        // Function to compute the score of a given word
        Function<Integer, Integer> scoreOfALetter = letter -> letterScores[letter - 'a'];

        // score of the same letters in a word
        Function<Entry<Integer, LongWrapper>, Integer> letterScore = //
                entry -> //
                letterScores[entry.getKey() - 'a'] * //
                        Integer.min( //
                                (int) entry.getValue().get(), //
                                scrabbleAvailableLetters[entry.getKey() - 'a']);

        Function<String, Stream<Integer>> toIntegerStream = string -> Stream.from(toIterable(string.chars().boxed()));

        // Histogram of the letters in a given word
        Function<String, Map<Integer, LongWrapper>> histoOfLetters = word -> toIntegerStream.apply(word) //
                .collect(() -> new HashMap<>(), (Map<Integer, LongWrapper> map, Integer value) -> {
                    LongWrapper newValue = map.get(value);
                    if (newValue == null) {
                        newValue = () -> 0L;
                    }
                    map.put(value, newValue.incAndSet());
                }).get();

        // number of blanks for a given letter
        Function<Entry<Integer, LongWrapper>, Stream<Long>> blank = //
                entry -> //
                Stream.of( //
                        Long.max( //
                                0L, //
                                entry.getValue().get() - scrabbleAvailableLetters[entry.getKey() - 'a']));

        // number of blanks for a given word
        Function<String, Long> nBlanks = word -> //
        Stream.from(histoOfLetters.apply(word).entrySet()) //
                .flatMap(blank) //
                .reduce(0L, Long::sum) //
                .get();

        // can a word be written with 2 blanks?
        Predicate<String> checkBlanks = word -> nBlanks.apply(word) <= 2;

        // score taking blanks into account letterScore1
        Function<String, Integer> score2 = word -> //
        Stream.from(histoOfLetters.apply(word).entrySet()) //
                .map(letterScore) //
                .reduce(0, Integer::sum) //
                .get();

        // Placing the word on the board
        // Building the streams of first and last letters
        Function<String, Stream<Integer>> first3 = word -> Stream.chars(word).take(3);
        Function<String, Stream<Integer>> last3 = word -> Stream.chars(word).skip(3);

        // Stream to be maxed
        Function<String, Stream<Integer>> toBeMaxed = word -> Stream //
                .of(first3.apply(word), last3.apply(word)) //
                .flatMap(Functions.identity());

        // Bonus for double letter
        Function<String, Integer> bonusForDoubleLetter = word -> toBeMaxed //
                .apply(word) //
                .map(scoreOfALetter) //
                .reduce(Integer::max).get() //
                .orElse(0);

        // score of the word put on the board
        // score of the word put on the board
        Function<String, Integer> score3 = word -> 2 * (score2.apply(word) + bonusForDoubleLetter.apply(word))
                + (word.length() == 7 ? 50 : 0);

        Function<Function<String, Integer>, Single<TreeMap<Integer, List<String>>>> buildHistoOnScore = score -> Stream
                .from(shakespeareWords) //
                .filter(scrabbleWords::contains) //
                .filter(checkBlanks) //
                .collect(() -> new TreeMap<Integer, List<String>>(Comparator.reverseOrder()), //
                        (TreeMap<Integer, List<String>> map, String word) -> {
                            Integer key = score.apply(word);
                            List<String> list = map.get(key);
                            if (list == null) {
                                list = new ArrayList<>();
                                map.put(key, list);
                            }
                            list.add(word);
                        });

        // best key / value pairs
        List<Entry<Integer, List<String>>> finalList = buildHistoOnScore //
                .apply(score3) //
                .flatMap(map -> Stream.from(map.entrySet())) //
                .take(3) //
                .toList();

        return finalList;
    }

    private static <T> Iterable<T> toIterable(java.util.stream.Stream<T> stream) {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return stream.iterator();
            }

        };
    }

    public static void main(String[] args) throws Exception {
        ShakespearePlaysScrabbleWithKool s = new ShakespearePlaysScrabbleWithKool();
        s.init();
        for (;;) {
            s.measureThroughput();
        }
    }
}
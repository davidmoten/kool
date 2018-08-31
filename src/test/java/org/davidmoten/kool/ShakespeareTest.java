package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;

public class ShakespeareTest {

    @Test
    public void testKoolAgreesWithJavaStreams() throws InterruptedException {
        ShakespearePlaysScrabbleWithNonParallelStreams s = new ShakespearePlaysScrabbleWithNonParallelStreams();
        s.init();
        List<Entry<Integer, List<String>>> a = s.measureThroughput();
        ShakespearePlaysScrabbleWithKool s2 = new ShakespearePlaysScrabbleWithKool();
        s2.init();
        List<Entry<Integer, List<String>>> b = s2.measureThroughput();
        assertEquals(a, b);
    }

}

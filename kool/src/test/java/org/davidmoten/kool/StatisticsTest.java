package org.davidmoten.kool;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StatisticsTest {
    
    private static final double PRECISION = 0.0001;

    @Test
    public void test() {
      Statistics s = new Statistics();
      s = s.add(1).add(2).add(3).add(8);
      assertEquals(4, s.count());
      assertEquals(3.5, s.mean(), PRECISION);
      assertEquals(2.6925824, s.standardDeviation(), PRECISION);
      assertEquals(7.25, s.variance(), PRECISION);
      assertEquals(2.16052318, s.kurtosis(), PRECISION);
      assertEquals(0.9220734033619128, s.skewness(), PRECISION);
      assertEquals(1, s.min(), PRECISION);
      assertEquals(8, s.max(), PRECISION);
      assertEquals(7, s.range(), PRECISION);
      System.out.println(s.toString("","\n"));
    }
    
}

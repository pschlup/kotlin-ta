package com.pschlup.ta.helpers;

import com.pschlup.ta.timeseries.Bar;
import com.pschlup.ta.timeseries.TimeFrame;
import com.pschlup.ta.timeseries.TimeSeries;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class MockTimeSeries {

  public static TimeSeries of(double[] prices) {
    List<Bar> bars = IntStream.range(0, prices.length)
        .mapToObj(i ->
            // TODO: Allow mocking volume as well
            new Bar(TimeFrame.M5, barTime(i), prices[i], prices[i], prices[i], prices[i], 0))
        .collect(toList());
    return TimeSeries.of(bars);
  }

  private static Instant barTime(int index) {
    return Instant.EPOCH.plus(Duration.ofMinutes(5 * index));
  }
}

package com.pschlup.ta.indicators;

import com.pschlup.ta.helpers.MockTimeSeries;
import com.pschlup.ta.timeseries.TimeSeries;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MeanDeviationTest {

  private static final double[] CLOSE_PRICE = {
      1, 2, 7, 6, 3, 4, 5, 11, 3, 0
  };

  @Test
  void shouldComputeValue() {
    TimeSeries timeSeries = MockTimeSeries.of(CLOSE_PRICE);
    MeanDeviation meanDeviation = MeanDeviation.of(timeSeries.closePrice()).withLength(5);
    assertThat(meanDeviation.valueAt(0))
        .isWithin(0.0001).of(2.72);
    assertThat(meanDeviation.valueAt(1))
        .isWithin(0.0001).of(2.32);
  }
}

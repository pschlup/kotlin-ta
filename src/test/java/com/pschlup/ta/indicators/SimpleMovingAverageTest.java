package com.pschlup.ta.indicators;

import com.pschlup.ta.helpers.MockTimeSeries;
import com.pschlup.ta.timeseries.UnstablePeriodException;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SimpleMovingAverageTest {

  private double[] PRICES = new double[] {
      1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
  };

  @Test
  void shouldComputeAverage() {
    SimpleMovingAverage sma = SimpleMovingAverage.of(MockTimeSeries.of(PRICES).closePrice()).withLength(10);
    assertThat(sma.valueAt(1)).isEqualTo(5.5);
    assertThat(sma.valueAt(0)).isEqualTo(6.5);
  }

  @Test
  void shouldThrowIfUnstable() {
    SimpleMovingAverage sma = SimpleMovingAverage.of(MockTimeSeries.of(PRICES).closePrice()).withLength(20);
    assertThrows(UnstablePeriodException.class, () -> sma.valueAt(0));
  }
}

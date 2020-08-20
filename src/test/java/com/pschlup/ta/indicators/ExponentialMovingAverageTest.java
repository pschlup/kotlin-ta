package com.pschlup.ta.indicators;

import com.pschlup.ta.helpers.MockTimeSeries;
import com.pschlup.ta.timeseries.UnstablePeriodException;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ExponentialMovingAverageTest {

  private double[] PRICES = new double[] {
      6, 5, 4, 3, 2, 1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
  };

  @Test
  void shouldComputeAverage() {
    Indicator ema = ExponentialMovingAverage.of(MockTimeSeries.of(PRICES).closePrice()).withLength(10);
    assertThat(ema.valueAt(1)).isWithin(0.0001).of(3.61416);
    assertThat(ema.valueAt(0)).isWithin(0.0001).of(4.13886);
  }

  @Test
  void shouldThrowIfUnstable() {
    Indicator ema = ExponentialMovingAverage.of(MockTimeSeries.of(PRICES).closePrice()).withLength(30);
    assertThrows(UnstablePeriodException.class, () -> ema.valueAt(0));
  }
}

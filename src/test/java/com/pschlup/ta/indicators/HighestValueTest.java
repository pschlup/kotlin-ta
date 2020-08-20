package com.pschlup.ta.indicators;

import com.pschlup.ta.helpers.MockTimeSeries;
import com.pschlup.ta.timeseries.UnstablePeriodException;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HighestValueTest {

  private double[] PRICES = new double[] {
      6, 5, 4, 22, 2, 1, 0, -30, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
  };

  @Test
  void shouldComputeHighestValue() {
    Indicator highest = HighestValue.of(MockTimeSeries.of(PRICES).closePrice()).withLength(20);
    assertThat(highest.valueAt(0)).isEqualTo(22);
  }
}

package com.pschlup.ta.indicators;

import com.pschlup.ta.helpers.MockTimeSeries;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class LowestValueTest {

  private double[] PRICES = new double[] {
      6, 5, 4, 22, 2, 1, 0, -30, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
  };

  @Test
  void shouldComputeLowestValue() {
    Indicator lowest = LowestValue.of(MockTimeSeries.of(PRICES).closePrice()).withLength(15);
    assertThat(lowest.valueAt(0)).isEqualTo(-30);
  }
}

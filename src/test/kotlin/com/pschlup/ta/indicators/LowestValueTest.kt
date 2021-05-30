package com.pschlup.ta.indicators

import com.google.common.truth.Truth.assertThat
import com.pschlup.ta.helpers.FakeTimeSeries
import org.junit.jupiter.api.Test

internal class LowestValueTest {
  private val timeSeries = FakeTimeSeries.of(
    doubleArrayOf(6.0, 5.0, 4.0, 22.0, 2.0, 1.0, 0.0, -30.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0)
  )

  @Test
  fun shouldComputeLowestValue() {
    val lowest: Indicator = timeSeries.closePrice.lowestValue(15)

    assertThat(lowest.valueAt(0)).isEqualTo(-30)
  }
}

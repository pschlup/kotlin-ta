package com.pschlup.ta.indicators

import com.google.common.truth.Truth.assertThat
import com.pschlup.ta.helpers.FakeTimeSeries
import org.junit.jupiter.api.Test

internal class MeanDeviationTest {
  private val timeSeries = FakeTimeSeries.of(
    doubleArrayOf(1.0, 2.0, 7.0, 6.0, 3.0, 4.0, 5.0, 11.0, 3.0, 0.0)
  )

  @Test
  fun shouldComputeValue() {
    val meanDeviation = timeSeries.closePrice.meanDeviation(5)

    assertThat(meanDeviation.valueAt(0))
      .isWithin(0.0001).of(2.72)
    assertThat(meanDeviation.valueAt(1))
      .isWithin(0.0001).of(2.32)
  }
}
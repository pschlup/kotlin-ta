package com.pschlup.ta.indicators

import com.google.common.truth.Truth.assertThat
import com.pschlup.ta.helpers.FakeTimeSeries
import com.pschlup.ta.timeseries.UnstablePeriodException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class SimpleMovingAverageTest {
  private val timeSeries = FakeTimeSeries.of(
    doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0)
  )

  @Test
  fun shouldComputeAverage() {
    val sma = timeSeries.closePrice.sma(10)

    assertThat(sma.valueAt(1)).isEqualTo(5.5)
    assertThat(sma.valueAt(0)).isEqualTo(6.5)
  }

  @Test
  fun shouldThrowIfUnstable() {
    val sma = timeSeries.closePrice.sma(20)

    assertThrows(UnstablePeriodException::class.java) { sma.valueAt(0) }
  }
}

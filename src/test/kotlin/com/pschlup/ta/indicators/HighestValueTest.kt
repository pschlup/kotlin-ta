package com.pschlup.ta.indicators

import com.google.common.truth.Truth.assertThat
import com.pschlup.ta.helpers.FakeTimeSeries
import org.junit.jupiter.api.Test

internal class HighestValueTest {
  private val timeSeries = FakeTimeSeries.of(
    doubleArrayOf(39.0, 6.0, 5.0, 4.0, 22.0, 2.0, 1.0, 0.0, -30.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0)
  )

  @Test
  fun `should compute highest value`() {
    val highest = timeSeries.closePrice.highestValue(15)

    assertThat(highest.valueAt(0)).isEqualTo(22.0)
    assertThat(highest.valueAt(1)).isEqualTo(39.0)
  }
}

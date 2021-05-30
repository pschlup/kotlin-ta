package com.pschlup.ta.indicators

import com.google.common.truth.Truth.assertThat
import com.pschlup.ta.timeseries.CsvBarSource.readCsvData
import com.pschlup.ta.timeseries.TimeSeries
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AverageTrueRangeTest {
  private lateinit var timeSeries: TimeSeries

  @BeforeEach
  fun setup() {
    val bars = readCsvData(
      javaClass.getResourceAsStream("/test_chart_data.csv") ?: error("CSV not found")
    )
    timeSeries = TimeSeries.of(bars)
  }

  @Test
  fun `should compute value`() {
    val atr = timeSeries.averageTrueRange(14)

    // Double-check values with another source
    assertThat(atr.valueAt(0))
      .isWithin(0.0001).of(47.8222)
    assertThat(atr.valueAt(1))
      .isWithin(0.0001).of(48.4050)
  }
}

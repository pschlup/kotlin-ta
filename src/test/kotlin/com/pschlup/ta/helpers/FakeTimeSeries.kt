package com.pschlup.ta.helpers

import com.pschlup.ta.timeseries.Bar
import com.pschlup.ta.timeseries.TimeFrame
import com.pschlup.ta.timeseries.TimeSeries
import java.time.Duration
import java.time.Instant

object FakeTimeSeries {
  @JvmStatic
  fun of(prices: DoubleArray): TimeSeries {
    // TODO: Allow mocking volume as well
    val bars = prices.indices
      .map { i ->
        Bar(
          timeFrame = TimeFrame.M5,
          openTime = barTime(i),
          open = prices[i],
          high = prices[i],
          low = prices[i],
          close = prices[i],
          volume = .0
        )
      }
    return TimeSeries.of(bars)
  }

  private fun barTime(index: Int): Instant = Instant.EPOCH.plus(Duration.ofMinutes((5 * index).toLong()))
}

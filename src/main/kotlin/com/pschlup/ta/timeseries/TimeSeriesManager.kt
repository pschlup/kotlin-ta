package com.pschlup.ta.timeseries

import com.pschlup.ta.timeseries.TimeSeries.Companion.downSample
import java.util.*

/** A class that receives price information and generates time series at specific time frames. */
class TimeSeriesManager(
  /** The time series from which the others are downsampled from.  */
  private val inputTimeFrame: TimeFrame
) {
  private val series = EnumMap<TimeFrame, TimeSeries>(TimeFrame::class.java)

  init {
    series[inputTimeFrame] = TimeSeries(inputTimeFrame)
  }

  fun getTimeSeries(timeframe: TimeFrame): TimeSeries {
    require(timeframe.duration >= inputTimeFrame.duration) {
      "Time series can only be downsampled from the base time frame."
    }
    return series.computeIfAbsent(timeframe) { timeFrame: TimeFrame ->
      downSample(timeFrame, baseTimeSeries)
    }
  }

  /** Merges the bar into all time series. */
  fun addBar(bar: Bar) {
    require(bar.timeFrame === inputTimeFrame)
    series.values.forEach { s ->
      s += bar
    }
  }

  private val baseTimeSeries: TimeSeries
    get() = series[inputTimeFrame] ?: error("Input timeFrame should always be available")

  // Convenience methods for retrieval of series in specific time frames.

  val m5: TimeSeries
    get() = getTimeSeries(TimeFrame.M5)
  val m10: TimeSeries
    get() = getTimeSeries(TimeFrame.M10)
  val m15: TimeSeries
    get() = getTimeSeries(TimeFrame.M15)
  val m30: TimeSeries
    get() = getTimeSeries(TimeFrame.M30)
  val h1: TimeSeries
    get() = getTimeSeries(TimeFrame.H1)
  val h2: TimeSeries
    get() = getTimeSeries(TimeFrame.H2)
  val h4: TimeSeries
    get() = getTimeSeries(TimeFrame.H4)
  val d: TimeSeries
    get() = getTimeSeries(TimeFrame.D)
}

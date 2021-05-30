package com.pschlup.ta.timeseries

import java.time.Instant
import java.time.temporal.ChronoField
import java.util.*
import java.util.function.Consumer

/** Represents a bar series at a specific time frame (e.g. 5 minutes, 4 hours). */
@Suppress("MoveVariableDeclarationIntoWhen")
class TimeSeries internal constructor(private val timeFrame: TimeFrame) {
  // New bars are added at the beginning of the list.
  private val bars = LinkedList<Bar>()

  /**
   * Updates the current bar with the specified tick price.
   * Bars are created automatically if needed.
   */
  fun addTickPrice(tickTimestamp: Instant, price: Double) {
    val currentBar = getCurrentBar(tickTimestamp)
    if (currentBar == null) {
      val openTime = latestBar?.closeTime ?: tickTimestamp
      bars.add(Bar(timeFrame = timeFrame, openTime = openTime, open = price))
      return
    }
    currentBar += price
  }

  private fun truncateTimestamp(timestamp: Instant): Instant {
    val moduloSeconds = timestamp.epochSecond % timeFrame.duration.seconds
    return timestamp.minusSeconds(moduloSeconds).with(ChronoField.NANO_OF_SECOND, 0)
  }

  private fun getCurrentBar(timestamp: Instant): Bar? = latestBar?.takeIf { it.includesTimestamp(timestamp) }

  val latestBar: Bar?
    get() = bars.firstOrNull()

  val oldestBar: Bar?
    get() = bars.lastOrNull()

  /** Merges the bars from a lower time frame into this time series  */
  fun addBars(bars: Collection<Bar>) {
    bars.forEach { bar -> this += bar }
  }

  /** Merges the bar from a lower time frame into this time series  */
  operator fun plusAssign(bar: Bar) {
    val currentBar = getCurrentBar(bar.openTime)
    // Adds a new bar if the open time is after the latest bar
    when (currentBar) {
      null -> bars.add(0, Bar(timeFrame, bar))
      else -> currentBar += bar
    }
  }

  // TODO: Solve unstable periods without throwing an exception (maybe return a null).
  operator fun get(index: Int): Bar =
    bars.takeIf { index <= bars.lastIndex }?.get(index) ?: throw UnstablePeriodException()

  companion object {
    @JvmStatic
    fun of(bars: List<Bar>): TimeSeries {
      val timeSeries = TimeSeries(bars[0].timeFrame)
      timeSeries.addBars(bars)
      return timeSeries
    }

    @JvmStatic
    fun downSample(timeFrame: TimeFrame, otherTimeSeries: TimeSeries): TimeSeries {
      val timeSeries = TimeSeries(timeFrame)
      timeSeries.addBars(otherTimeSeries.bars)
      return timeSeries
    }
  }
}
package com.pschlup.ta.timeseries

import java.time.Instant

/** Represents a time series bar.  */
data class Bar(
  val timeFrame: TimeFrame,
  /** The bar's open timestamp, inclusive */
  val openTime: Instant,
  var open: Double,
  var high: Double = open,
  var low: Double = open,
  var close: Double = open,
  var volume: Double = 0.0,
  /* The bar's close timestamp, exclusive */
  val closeTime: Instant = openTime.plus(timeFrame.duration),
) {
  /** Creates a bar based on a bar from a different timeframe  */
  internal constructor(
    timeFrame: TimeFrame,
    otherBar: Bar,
  ) : this(
    timeFrame,
    openTime = otherBar.openTime,
    closeTime = otherBar.openTime.plus(timeFrame.duration),
    open = otherBar.open,
    high = otherBar.high,
    low = otherBar.low,
    close = otherBar.close,
    volume = otherBar.volume,
  )

  operator fun plusAssign(otherBar: Bar) {
    close = otherBar.close
    volume += otherBar.volume
    if (otherBar.high > high) {
      high = otherBar.high
    }
    if (otherBar.low < low) {
      low = otherBar.low
    }
  }

  operator fun plusAssign(price: Double) {
    close = price
    if (open == 0.0) {
      open = price
    }
    if (price > high) {
      high = price
    }
    if (price < low) {
      low = price
    }
  }

  @Deprecated("Use += priceTick instead", ReplaceWith("+="))
  fun addTickPrice(price: Double) {
    this += price
  }

  fun includesTimestamp(instant: Instant): Boolean = !instant.isBefore(openTime) && instant.isBefore(closeTime)
}

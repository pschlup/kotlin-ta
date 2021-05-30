@file:Suppress("unused")

package com.pschlup.ta.indicators

/** Provides a single value at the specified time series bar index. */
fun interface Indicator {
  fun valueAt(index: Int): Double

  operator fun get(index: Int) = valueAt(index)
}

/** Convenience getter for the latest indicator value.  */
val Indicator.latestValue: Double get() = valueAt(0)

/** The value of the indicator delayed by the specified number of bars. */
fun Indicator.previousValue(bars: Int): Indicator = Indicator { index -> valueAt(index + bars) }

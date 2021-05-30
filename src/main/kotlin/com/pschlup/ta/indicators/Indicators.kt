@file:Suppress("unused", "DuplicatedCode")

package com.pschlup.ta.indicators

import com.pschlup.ta.timeseries.TimeSeries
import com.pschlup.ta.timeseries.UnstablePeriodException
import kotlin.math.abs
import kotlin.math.max

// ===========================================================
// Basic price indicators
// ===========================================================

val TimeSeries.openPrice: Indicator
  get() = Indicator { index -> this[index].open }

val TimeSeries.highPrice: Indicator
  get() = Indicator { index -> this[index].high }

val TimeSeries.lowPrice: Indicator
  get() = Indicator { index -> this[index].low }

val TimeSeries.closePrice: Indicator
  get() = Indicator { index -> this[index].close }

val TimeSeries.volume: Indicator
  get() = Indicator { index -> this[index].volume }

val TimeSeries.hlc3Price: Indicator
  get() = typicalPrice

val TimeSeries.typicalPrice: Indicator
  get() = Indicator { index ->
    with(this[index]) {
      (high + low + close) / 3
    }
  }

// ===========================================================
// Simple indicators
// ===========================================================

/**
 * Finds the lowest value of another indicator.
 *
 * Usage:
 *    val support = h1.closePrice.lowestValue(length = 20)
 */
fun Indicator.lowestValue(length: Int = 20): Indicator =
  Indicator { index ->
    (index until index + length).minOf { i -> this[i] }
  }

/**
 * Finds the highest value of another indicator.
 *
 * Usage:
 *    val resistance = h1.closePrice.highestValue(length = 20)
 */
fun Indicator.highestValue(length: Int = 20): Indicator =
  Indicator { index ->
    (index until index + length).maxOf { i -> this[i] }
  }

/**
 * Calculates the Simple Moving Average (SMA) of another indicator.
 *
 * Usage:
 *    val h1Sma30 = h1.closePrice.sma(length = 30)
 */
fun Indicator.simpleMovingAverage(length: Int = 20) =
  Indicator { index ->
    (index until index + length).map { i -> this[i] }.average()
  }

/** Alias to Indicator.simpleMovingAverage(length). */
fun Indicator.sma(length: Int = 20) = simpleMovingAverage(length)

/**
 * Calculates the Exponential Moving Average (EMA) of another indicator.
 *
 * Usage:
 *    val h1ema30 = h1.closePrice.exponentialMovingAverage(length = 30);
 */
fun Indicator.exponentialMovingAverage(length: Int = 20): Indicator {
  val sma = this.simpleMovingAverage(length)
  val multiplier = 2.0 / (length.toDouble() + 1.0)

  fun valueAt(index: Int, relevantBars: Int): Double {
    return try {
      if (relevantBars == 0) {
        return sma[index]
      }
      // TODO: Implement memoization of the previous value (however the index moves with every new bar)
      val previousValue = valueAt(index = index + 1, relevantBars = relevantBars - 1)
      val value = (sma[index] - previousValue) * multiplier + previousValue
      if (value.isNaN()) { sma[index] } else value
    } catch (e: UnstablePeriodException) {
      sma[index]
    }
  }

  return Indicator { index ->
    valueAt(index, (length * 2).coerceAtLeast(30))
  }
}

/** Alias to Indicator.exponentialMovingAverage(length). */
fun Indicator.ema(length: Int = 20) = exponentialMovingAverage(length)

/**
 * Calculates the Modified Moving Average (MMA, RMA, or SMMA) of another indicator.
 *
 * Usage:
 *    val mma30 = h1.closePrice.modifiedMovingAverage(length = 30);
 */
fun Indicator.modifiedMovingAverage(length: Int = 14): Indicator {
  val sma = this.simpleMovingAverage(length)
  val multiplier = 1.0 / length.toDouble()

  fun valueAt(index: Int, relevantBars: Int): Double {
    return try {
      if (relevantBars == 0) {
        return sma[index]
      }
      // TODO: Implement memoization of the previous value (however the index moves with every new bar)
      val previousValue = valueAt(index = index + 1, relevantBars = relevantBars - 1)
      val value = (sma[index] - previousValue) * multiplier + previousValue
      if (value.isNaN()) { sma[index] } else value
    } catch (e: UnstablePeriodException) {
      sma[index]
    }
  }

  return Indicator { index ->
    valueAt(index, (length * 2).coerceAtLeast(30))
  }
}

/**
 * Average True range (ATR) indicator.
 * {@see https://www.investopedia.com/terms/a/atr.asp}
 */
fun TimeSeries.averageTrueRange(length: Int = 14): Indicator {
  return this.trueRange().modifiedMovingAverage(length)
}

/** True Range indicator. */
fun TimeSeries.trueRange(): Indicator =
  Indicator { index ->
    val bar = this[index]
    val high = bar.high
    val low = bar.low
    val close = this[index + 1].close
    val ts = abs(high - low)
    val ys = abs(high - close)
    val yst = abs(close - low)
    return@Indicator max(ts, max(ys, yst))
  }

/** The Commodity Channel Index (CCI) indicator. */
fun TimeSeries.cci(length: Int = 20): Indicator {
  val typicalPrice = typicalPrice
  val priceSma = typicalPrice.simpleMovingAverage(length)
  val meanDeviation = typicalPrice.meanDeviation(length)

  return Indicator { index ->
    return@Indicator (typicalPrice[index] - priceSma[index]) / (meanDeviation[index] * CCI_FACTOR)
  }
}

private const val CCI_FACTOR = 0.015

/**
 * The Stochastic %K price indicator.
 *
 * The %D indicator can be obtained from the %K one:
 * Indicator k = Stochastic.of(series).withLength(10);
 * Indicator d = k.smoothed();
 *
 * {@see https://www.investopedia.com/terms/s/stochasticoscillator.asp}
 */
fun TimeSeries.stochastic(length: Int = 20): Indicator {
  val closePrice = closePrice
  val maxPrice = highPrice.highestValue(length)
  val minPrice = lowPrice.lowestValue(length)

  return Indicator { index ->
    val price = closePrice[index]
    val highestPrice = maxPrice[index]
    if (highestPrice - price == 0.0) {
      100.0
    } else {
      (100.0 * (price - minPrice[index]) / (highestPrice - price))
        .coerceAtMost(100.0)
    }
  }
}

/** The Stochastic Relative Strength Index (Stochastic RSI) indicator. */
fun Indicator.stochasticRsi(length: Int = 14): Indicator {
  val rsi = this.rsi(length = length)
  val maxRsi = rsi.highestValue(length = length)
  val minRsi = rsi.lowestValue(length = length)

  return Indicator { index ->
    (rsi[index] - minRsi[index]) / (maxRsi[index] - minRsi[index])
  }
}

/**
 * Mean Deviation indicator.
 * {@see https://en.wikipedia.org/wiki/Average_absolute_deviation}
 */
fun Indicator.meanDeviation(length: Int = 20): Indicator {
  val sma = this.simpleMovingAverage(length)
  return Indicator { index ->
    val average = sma[index]
    val sumDeviations = (index until index + length)
      .sumOf { i -> abs(this[i] - average) }
    return@Indicator sumDeviations / length
  }
}

// ===========================================================
// Stop loss indicators
// ===========================================================

/** Volatility (ATR) -based stop loss indicator.  */
fun TimeSeries.volatilityStop(length: Int = 22, multiplier: Double = 2.0): Indicator {
  val atr = this.averageTrueRange(length)
  val price = this.lowPrice.lowestValue(length = length)

  return Indicator { index ->
    price[index] - atr[index] * multiplier
  }
}

/** Defines a stop loss some distance below the highest value of the previous [length] bars. */
fun TimeSeries.chandelierStop(length: Int = 10, atrDistance: Double = 3.0): Indicator {
  val atr = this.averageTrueRange(length)
  val minPrice = highPrice.highestValue(length)

  return Indicator { index ->
    minPrice[index] - atr[index] * atrDistance
  }
}

package com.pschlup.ta.indicators

/** The Relative Strength Index (RSI) indicator. */
fun Indicator.rsi(length: Int = 14): Indicator {
  val gainMma = gainIndicator().modifiedMovingAverage(length)
  val lossMma = lossIndicator().modifiedMovingAverage(length)

  return Indicator { index ->
    val avgGain = gainMma.valueAt(index)
    val avgLoss = lossMma.valueAt(index)
    if (avgLoss == 0.0) {
      return@Indicator if (avgGain == 0.0) 0.0 else 100.0
    }
    val relativeStrength = avgGain / avgLoss
    // Compute Relative Strength Index
    return@Indicator 100.0 - 100.0 / (1.0 + relativeStrength)
  }
}

private fun Indicator.gainIndicator(): Indicator =
  Indicator { index ->
    (this[index] - this[index + 1]).coerceAtLeast(.0)
  }

private fun Indicator.lossIndicator(): Indicator =
  Indicator { index ->
    (this[index + 1] - this[index]).coerceAtLeast(.0)
  }

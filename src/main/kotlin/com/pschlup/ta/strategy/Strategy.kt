package com.pschlup.ta.strategy

import com.pschlup.ta.timeseries.UnstablePeriodException

/** Translates the individual signal expressions into a resulting trade signal at each time series bar. */
class Strategy(
  /** Identifies when to enter a trade. This depends on the trend signal if one is specified. */
  private val entrySignal: Signal,
  /** Identifies when to exit all active trades. */
  private val exitSignal: Signal,
  /**
   * Identifies if the price is trending in the desired direction.
   *
   * If specified, the entry signal will only be checked if the trend signal is active. This is useful for
   * trend-following strategies where trending detection logic is distinct from the entry logic.
   */
  private val trendSignal: Signal? = null
) {

  /** Computes the current trading signal. */
  val signal: SignalType
    get() = get(0)

  /**
   * Computes the trading signal at the specified timeseries bar index.
   */
  operator fun get(index: Int): SignalType = try {
    when {
      entrySignal.value(index) && (trendSignal?.value(index) ?: true) -> SignalType.ENTRY
      exitSignal.value(index) -> SignalType.EXIT
      else -> SignalType.NO_OP
    }
  } catch (e: UnstablePeriodException) {
    // Avoids operating with unstable data.
    SignalType.NO_OP
  }
}

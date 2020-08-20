package com.pschlup.ta.strategy;

import com.pschlup.ta.timeseries.UnstablePeriodException;
import lombok.Builder;
import lombok.NonNull;

import javax.annotation.Nullable;

/**
 * Translates the individual signal expressions into a resulting trade signal at each time series bar.
 */
@Builder
@SuppressWarnings({"unused", "WeakerAccess"})
public class Strategy {

  /** The types of signals a strategy can generate. */
  public enum SignalType {
    /** Do nothing. */
    NO_OP,
    /** Enter a new trade. */
    ENTRY,
    /** Exit all active trades. */
    EXIT
  }

  /**
   * Identifies if the price is trending in the desired direction.
   * <p>
   * If specified, the entry signal will only be checked if the trend signal is active. This is useful for
   * trend-following strategies where trending detection logic is distinct from the entry logic.
   */
  @Nullable
  private Signal trendSignal;

  /**
   * Identifies when to enter a trade. This depends on the trend signal if one is specified.
   */
  @NonNull
  private Signal entrySignal;

  /**
   * Identifies when to exit all active trades.
   */
  @NonNull
  private Signal exitSignal;

  /**
   * Computes the current trading signal.
   */
  public SignalType getSignal() {
    return getSignalAtIndex(0);
  }

  /**
   * Computes the trading signal at the specified timeseries bar index.
   */
  public SignalType getSignalAtIndex(int index) {
    try {
      if (entrySignal.isActive(index)
          && ((trendSignal == null) || trendSignal.isActive(index))) {
        return SignalType.ENTRY;
      }
      if (exitSignal.isActive(index)) {
        return SignalType.EXIT;
      }
      return SignalType.NO_OP;
    } catch (UnstablePeriodException e) {
      // Avoids operating with unstable data.
      return SignalType.NO_OP;
    }
  }
}

package com.pschlup.ta.indicators;

import com.pschlup.ta.timeseries.UnstablePeriodException;
import lombok.experimental.Wither;

/**
 * Calculates the Modified Moving Average (MMA, RMA, or SMMA) of another indicator.
 * <p>
 *   Usage:
 *
 *   Indicator mma30 = ModifiedMovingAverage.of(h1.closePrice()).withLength(30);
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class ModifiedMovingAverage implements Indicator {

  private static final int DEFAULT_LENGTH = 14;

  private final Indicator source;

  @Wither
  private int length;

  private double $multiplier;
  private final SimpleMovingAverage $sma;

  public static ModifiedMovingAverage of(Indicator source) {
    return new ModifiedMovingAverage(source, DEFAULT_LENGTH);
  }

  private ModifiedMovingAverage(Indicator source, int length) {
    this.source = source;
    this.$sma = SimpleMovingAverage.of(source);
    this.length = length;
    this.$multiplier = 1.0 / length;
  }

  @Override
  public double valueAt(int index) {
    return valueAt(index, Math.max(30, this.length * 2));
  }

  private double valueAt(int index, int relevantBars) {
    try {
      if (relevantBars == 0) {
        return $sma.valueAt(index);
      }
      // TODO: Implement memoization of the previous value (however the index moves with every new bar)
      double prevValue = valueAt(index + 1, relevantBars - 1);
      return ($sma.valueAt(index) - prevValue) * $multiplier + prevValue;
    } catch (UnstablePeriodException e) {
      return $sma.valueAt(index);
    }
  }
}

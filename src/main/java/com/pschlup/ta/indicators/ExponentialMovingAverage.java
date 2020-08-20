package com.pschlup.ta.indicators;

import com.pschlup.ta.timeseries.UnstablePeriodException;
import lombok.experimental.Wither;

/**
 * Calculates the Exponential Moving Average (EMA) of another indicator.
 * <p>
 *   Usage:
 *
 *   Indicator h1ema30 = ExponentialMovingAverage.of(h1.closePrice()).withLength(30);
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ExponentialMovingAverage implements Indicator {

  private static final int DEFAULT_LENGTH = 20;

  private final Indicator source;

  @Wither
  private final int length;

  private final SimpleMovingAverage $sma;

  /** Exponential decay multiplier */
  private final double $multiplier;

  public static ExponentialMovingAverage of(Indicator source) {
    return new ExponentialMovingAverage(source, DEFAULT_LENGTH);
  }

  private ExponentialMovingAverage(Indicator source, int length) {
    this.source = source;
    this.length = length;
    this.$sma = SimpleMovingAverage.of(source).withLength(length);
    this.$multiplier = 2.0 / (length + 1);
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
      double prevValue = valueAt(index + 1,  relevantBars - 1);
      return ($sma.valueAt(index) - prevValue) * this.$multiplier + prevValue;
    } catch (UnstablePeriodException e) {
      return $sma.valueAt(index);
    }
  }
}

package com.pschlup.ta.indicators;

import lombok.experimental.Wither;

/**
 * The Relative Strength Index (RSI) indicator.
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class StochasticRSI implements Indicator {

  private static final int DEFAULT_LENGTH = 14;

  private final Indicator source;

  @Wither
  private final int length;

  private final RSI $rsi;
  private final HighestValue $maxRsi;
  private final LowestValue $minRsi;

  public static StochasticRSI of(Indicator source) {
    return new StochasticRSI(source, DEFAULT_LENGTH);
  }

  private StochasticRSI(Indicator source, int length) {
    this.source = source;
    this.length = length;
    this.$rsi = RSI.of(source).withLength(length);
    this.$maxRsi = HighestValue.of($rsi).withLength(length);
    this.$minRsi = LowestValue.of($rsi).withLength(length);
  }

  @Override
  public double valueAt(int index) {
    return ($rsi.valueAt(index) - $minRsi.valueAt(index)) / ($maxRsi.valueAt(index) - $minRsi.valueAt(index));
  }
}

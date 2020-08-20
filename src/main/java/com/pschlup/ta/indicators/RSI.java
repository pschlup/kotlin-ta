package com.pschlup.ta.indicators;

import lombok.experimental.Wither;

/**
 * The Relative Strength Index (RSI) indicator.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class RSI implements Indicator {

  private static final int DEFAULT_LENGTH = 14;

  private final Indicator source;

  @Wither
  private final int length;

  private final ModifiedMovingAverage $gainMma;
  private final ModifiedMovingAverage $lossMma;

  public static RSI of(Indicator source) {
    return new RSI(source, DEFAULT_LENGTH);
  }

  private RSI(Indicator source, int length) {
    this.source = source;
    this.length = length;
    this.$gainMma = ModifiedMovingAverage.of(RSI.gainIndicator(source)).withLength(length);
    this.$lossMma = ModifiedMovingAverage.of(RSI.lossIndicator(source)).withLength(length);
  }

  private static Indicator gainIndicator(Indicator source) {
    return index -> Math.max(0, source.valueAt(index) - source.valueAt(index + 1));
  }

  private static Indicator lossIndicator(Indicator source) {
    return index -> Math.max(0, source.valueAt(index + 1) - source.valueAt(index));
  }

  @Override
  public double valueAt(int index) {
    double avgGain = $gainMma.valueAt(index);
    double avgLoss = $lossMma.valueAt(index);
    if (avgLoss == 0.) {
      return avgGain == 0. ? 0. : 100.;
    }
    double relativeStrength = avgGain / avgLoss;
    // Compute Relative Strength Index
    return 100. - 100. / (1. + relativeStrength);
  }
}

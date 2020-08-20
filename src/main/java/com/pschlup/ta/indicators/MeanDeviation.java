package com.pschlup.ta.indicators;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

import java.util.stream.IntStream;

/**
 * Mean Deviation indicator.
 * <p>
 * {@see https://en.wikipedia.org/wiki/Average_absolute_deviation}
 */
@SuppressWarnings("unused")
public class MeanDeviation implements Indicator {

  private static final int DEFAULT_LENGTH = 20;

  private final Indicator source;
  private final SimpleMovingAverage $sma;

  @Wither
  private final int length;

  public static MeanDeviation of(Indicator source) {
    return new MeanDeviation(source, DEFAULT_LENGTH);
  }

  private MeanDeviation(Indicator source, int length) {
    this.source = source;
    this.length = length;
    this.$sma = SimpleMovingAverage.of(source).withLength(length);
  }

  @Override
  public double valueAt(int index) {
    double average = $sma.valueAt(index);
    double sumDeviations = IntStream.range(index, index + length)
        .mapToDouble(source::valueAt)
        .map(v -> Math.abs(v - average))
        .sum();
    return sumDeviations / length;
  }
}

package com.pschlup.ta.indicators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;

import java.util.stream.IntStream;

/**
 * Calculates the Simple Moving Average (SMA) of another indicator.
 * <p>
 *   Usage:
 * <br>
 *   Indicator h1Sma30 = SimpleMovingAverage.of(h1.closePrice()).withLength(30);
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleMovingAverage implements Indicator {

  private static final int DEFAULT_LENGTH = 20;

  private final Indicator source;

  @Wither
  private final int length;

  public static SimpleMovingAverage of(Indicator source) {
    return new SimpleMovingAverage(source, DEFAULT_LENGTH);
  }

  @Override
  public double valueAt(int index) {
    return IntStream.range(index, index + length)
        .mapToDouble(source::valueAt)
        .average()
        .orElseThrow(IllegalStateException::new);
  }
}

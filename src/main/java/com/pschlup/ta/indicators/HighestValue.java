package com.pschlup.ta.indicators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;

import java.util.stream.IntStream;

/**
 * Finds the highest value of another indicator.
 * <p>
 * Usage:
 * <br>
 * Indicator resistance = HighestValue.of(h1.highPrice()).withLength(20);
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HighestValue implements Indicator {

  private static final int DEFAULT_LENGTH = 20;

  private final Indicator source;

  @Wither
  private final int length;

  public static HighestValue of(Indicator source) {
    return new HighestValue(source, DEFAULT_LENGTH);
  }

  @Override
  public double valueAt(int index) {
    return IntStream.range(index, index + length)
        .mapToDouble(source::valueAt)
        .max()
        .orElseThrow(IllegalStateException::new);
  }
}

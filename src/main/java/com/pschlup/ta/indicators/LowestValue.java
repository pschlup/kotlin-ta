package com.pschlup.ta.indicators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;

import java.util.stream.IntStream;

/**
 * Finds the lowest value of another indicator.
 * <p>
 * Usage:
 * <br>
 * Indicator support = LowestValue.of(h1.lowPrice()).withLength(20);
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LowestValue implements Indicator {

  private static final int DEFAULT_LENGTH = 20;

  private final Indicator source;

  @Wither
  private final int length;

  public static LowestValue of(Indicator source) {
    return new LowestValue(source, DEFAULT_LENGTH);
  }

  @Override
  public double valueAt(int index) {
    return IntStream.range(index, index + length)
        .mapToDouble(source::valueAt)
        .min()
        .orElseThrow(IllegalStateException::new);
  }
}

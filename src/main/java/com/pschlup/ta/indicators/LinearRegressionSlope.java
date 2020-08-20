package com.pschlup.ta.indicators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;

/**
 * Simple linear regression slope indicator.
 * <p>
 * {@see http://introcs.cs.princeton.edu/java/97data/LinearRegression.java.html}
 */
@SuppressWarnings("unused")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LinearRegressionSlope implements Indicator {

  private static final int DEFAULT_LENGTH = 20;

  private final Indicator source;

  @Wither
  private final int length;

  public static LinearRegressionSlope of(Indicator source) {
    return new LinearRegressionSlope(source, DEFAULT_LENGTH);
  }

  @Override
  public double valueAt(int index) {

    // First pass: compute xBar and yBar.
    double sumX = 0.;
    double sumY = 0.;

    for (int i = index; i < index + length; i++) {
      // index grows in reverse direction so "i" is negative
      sumX = sumX - i;
      sumY = sumY + source.valueAt(i);
    }
    double xBar = sumX / length;
    double yBar = sumY / length;

    // Second pass: compute slope.
    double xxBar = 0.;
    double xyBar = 0.;
    for (int i = index; i < index + length; i++) {
      // index grows in reverse direction so "i" is negative
      double dX = -i - xBar;
      double dY = source.valueAt(i) - yBar;
      xxBar = xxBar + (dX * dX);
      xyBar = xyBar + (dX * dY);
    }

    // Returns the slope only.
    return xyBar / xxBar;
  }
}

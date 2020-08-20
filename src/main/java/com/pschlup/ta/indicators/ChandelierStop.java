package com.pschlup.ta.indicators;

import com.pschlup.ta.timeseries.TimeSeries;
import lombok.experimental.Wither;

/**
 * Defines a stop loss some distance below the highest value of the previous {@code length} bars.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ChandelierStop implements Indicator {

  private static final int DEFAULT_LENGTH = 10;
  private static final double DEFAULT_ATR_DISTANCE = 3.0;

  private final TimeSeries timeSeries;

  @Wither
  private final int length;
  @Wither
  private double atrDistance;

  private final AverageTrueRange $atr;
  private final Indicator $minPrice;

  public static ChandelierStop of(TimeSeries timeSeries) {
    return new ChandelierStop(timeSeries, DEFAULT_LENGTH, DEFAULT_ATR_DISTANCE);
  }

  private ChandelierStop(TimeSeries timeSeries, int length, double atrDistance) {
    this.timeSeries = timeSeries;
    this.length = length;
    this.atrDistance = atrDistance;
    this.$atr = AverageTrueRange.of(timeSeries).withLength(length);
    this.$minPrice = HighestValue.of(timeSeries.highPrice()).withLength(length);
  }

  @Override
  public double valueAt(int index) {
    return $minPrice.valueAt(index) - $atr.valueAt(index) * atrDistance;
  }
}

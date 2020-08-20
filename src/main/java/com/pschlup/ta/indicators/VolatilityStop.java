package com.pschlup.ta.indicators;

import com.pschlup.ta.timeseries.TimeSeries;
import lombok.experimental.Wither;

/** Volatility (ATR) -based stop loss indicator. */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class VolatilityStop implements Indicator {

  private static final int DEFAULT_LENGTH = 22;
  private static final double DEFAULT_MULTIPLIER = 1.0;

  private final TimeSeries timeSeries;

  @Wither
  private final int length;
  @Wither
  private double multiplier;

  private final AverageTrueRange $atr;
  private final Indicator $price;

  public static VolatilityStop of(TimeSeries timeSeries) {
    return new VolatilityStop(timeSeries, DEFAULT_LENGTH, DEFAULT_MULTIPLIER);
  }

  private VolatilityStop(TimeSeries timeSeries, int length, double multiplier) {
    this.timeSeries = timeSeries;
    this.length = length;
    this.multiplier = multiplier;
    this.$atr = AverageTrueRange.of(timeSeries).withLength(length);
    this.$price = LowestValue.of(timeSeries.lowPrice()).withLength(length);
  }

  @Override
  public double valueAt(int index) {
    return $price.valueAt(index) - $atr.valueAt(index) * multiplier;
  }
}

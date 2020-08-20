package com.pschlup.ta.indicators;

import com.pschlup.ta.timeseries.TimeSeries;
import lombok.experimental.Wither;

/**
 * The Commodity Channel Index (CCI) indicator.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class CCI implements Indicator {

  private static final double FACTOR = 0.015;
  private static final int DEFAULT_LENGTH = 20;

  private final TimeSeries timeSeries;
  private final Indicator $typicalPrice;
  private final SimpleMovingAverage $priceSma;
  private final MeanDeviation $meanDeviation;

  @Wither
  private int length;

  public static CCI of(TimeSeries timeSeries) {
    return new CCI(timeSeries, DEFAULT_LENGTH);
  }

  private CCI(TimeSeries timeSeries, int length) {
    this.timeSeries = timeSeries;
    this.length = length;
    this.$typicalPrice = timeSeries.typicalPrice();
    this.$priceSma = SimpleMovingAverage.of(this.$typicalPrice).withLength(length);
    this.$meanDeviation = MeanDeviation.of(this.$typicalPrice).withLength(length);
  }

  @Override
  public double valueAt(int index) {
    final double typicalPrice = this.$typicalPrice.valueAt(index);
    final double typicalPriceAvg = this.$priceSma.valueAt(index);
    final double meanDeviation = this.$meanDeviation.valueAt(index);
    return (typicalPrice - typicalPriceAvg) / (meanDeviation * FACTOR);
  }
}

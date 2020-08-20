package com.pschlup.ta.indicators;

import com.pschlup.ta.timeseries.Bar;
import com.pschlup.ta.timeseries.TimeSeries;
import lombok.experimental.Wither;

/**
 * Average True range (ATR) indicator.
 * <p>
 * {@see https://www.investopedia.com/terms/a/atr.asp}
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class AverageTrueRange implements Indicator {

  private static final int DEFAULT_LENGTH = 14;

  private final TimeSeries timeSeries;

  @Wither
  private final int length;

  private final ModifiedMovingAverage $mma;

  public static AverageTrueRange of(TimeSeries timeSeries) {
    return new AverageTrueRange(timeSeries, DEFAULT_LENGTH);
  }

  private AverageTrueRange(TimeSeries timeSeries, int length) {
    this.timeSeries = timeSeries;
    this.length = length;
    TrueRange trueRange = TrueRange.of(timeSeries);
    this.$mma = ModifiedMovingAverage.of(trueRange).withLength(length);
  }

  @Override
  public double valueAt(int index) {
    return this.$mma.valueAt(index);
  }

  /** True Range indicator */
  public static class TrueRange implements Indicator {
    private final TimeSeries timeSeries;

    private TrueRange(TimeSeries timeSeries) {
      this.timeSeries = timeSeries;
    }

    public static TrueRange of(TimeSeries timeSeries) {
      return new TrueRange(timeSeries);
    }

    @Override
    public double valueAt(int index) {
      Bar bar = timeSeries.barAt(index);
      Bar previousBar = timeSeries.barAt(index + 1);
      double ts = Math.abs(bar.getHigh() - bar.getLow());
      double ys = Math.abs(bar.getHigh() - previousBar.getClose());
      double yst = Math.abs(previousBar.getClose() - bar.getLow());
      return Math.max(ts, Math.max(ys, yst));
    }
  }
}

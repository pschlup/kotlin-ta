package com.pschlup.ta.indicators;

import com.pschlup.ta.timeseries.TimeSeries;
import lombok.experimental.Wither;

/**
 * The Stochastic %K price indicator.
 *
 * <p>
 *   The %D indicator can be obtained from the %K one:
 *   Indicator k = Stochastic.of(series).withLength(10);
 *   Indicator d = k.smoothed();
 *
 * {@see https://www.investopedia.com/terms/s/stochasticoscillator.asp}
 */
@SuppressWarnings("unused")
public class Stochastic implements Indicator {

  private static final int DEFAULT_LENGTH = 20;

  @SuppressWarnings("FieldCanBeLocal")
  private final TimeSeries timeSeries;

  @Wither
  private final int length;

  private final Indicator $closePrice;
  private final Indicator $maxPrice;
  private final Indicator $minPrice;

  public static Stochastic of(TimeSeries timeSeries) {
    return new Stochastic(timeSeries, DEFAULT_LENGTH);
  }

  /** Obtains the smoothed (%D) version of the stochastic. */
  public SimpleMovingAverage smoothed() {
    return SimpleMovingAverage.of(this).withLength(3);
  }

  private Stochastic(TimeSeries timeSeries, int length) {
    this.timeSeries = timeSeries;
    this.length = length;
    this.$closePrice = timeSeries.closePrice();
    this.$maxPrice = HighestValue.of(timeSeries.highPrice()).withLength(length);
    this.$minPrice = LowestValue.of(timeSeries.lowPrice()).withLength(length);
  }

  @Override
  public double valueAt(int index) {
    double price = $closePrice.valueAt(index);
    double maxPrice = $maxPrice.valueAt(index);
    double minPrice = $minPrice.valueAt(index);
    if (maxPrice - price == 0.) {
      return 100;
    }
    return 100. * (price - minPrice) / (maxPrice - price);
  }
}

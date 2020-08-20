package com.pschlup.ta.indicators;

import com.pschlup.ta.timeseries.TimeSeries;
import lombok.AccessLevel;
import lombok.experimental.Wither;

/**
 * Keltner channel indicators.
 * <p>
 *   Usage:
 *   KeltnerChannel keltner = KeltnerChannel.of(series);
 *   Indicator upperChannel = keltner.upperChannel();
 *   Indicator lowerChannel = keltner.lowerChannel();
 *   Indicator evenLowerChannel = keltner.lowerChannel().withAtrMultiplier(3.0);
 * </p>
 * {@see https://school.stockcharts.com/doku.php?id=technical_indicators:keltner_channels}
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class KeltnerChannel implements Indicator {

  private static final int DEFAULT_LENGTH = 20;
  private static final int DEFAULT_ATR_LENGTH = 10;
  private static final double DEFAULT_ATR_MULTIPLIER = 2.0;

  private static final int CHANNEL_OFFSET_MIDDLE = 0;
  private static final int CHANNEL_OFFSET_UPPER = +1;
  private static final int CHANNEL_OFFSET_LOWER = -1;

  private final TimeSeries timeSeries;

  @Wither
  private final int length;

  @Wither
  private final int atrLength;

  @Wither
  private final double atrMultiplier;

  /**
   * The channel offset:
   * Middle = 0, Upper band = +1, Lower band = -1.
   */
  @Wither(AccessLevel.PRIVATE)
  private final int channelOffset;

  private final Indicator $ema;
  private final Indicator $atr;

  public static KeltnerChannel of(TimeSeries timeSeries) {
    return new KeltnerChannel(
        timeSeries, DEFAULT_LENGTH, DEFAULT_ATR_LENGTH, DEFAULT_ATR_MULTIPLIER, CHANNEL_OFFSET_MIDDLE);
  }

  private KeltnerChannel(TimeSeries timeSeries, int length, int atrLength, double atrMultiplier, int channelOffset) {
    this.timeSeries = timeSeries;
    this.length = length;
    this.atrLength = atrLength;
    this.atrMultiplier = atrMultiplier;
    this.channelOffset = channelOffset;
    this.$ema = ExponentialMovingAverage.of(timeSeries.typicalPrice()).withLength(length);
    this.$atr = AverageTrueRange.of(timeSeries).withLength(atrLength);
  }

  public KeltnerChannel upperChannel() {
    return this.withChannelOffset(CHANNEL_OFFSET_UPPER);
  }

  public KeltnerChannel lowerChannel() {
    return this.withChannelOffset(CHANNEL_OFFSET_LOWER);
  }

  @Override
  public double valueAt(int index) {
    double middleLine = $ema.valueAt(index);
    if (this.channelOffset == CHANNEL_OFFSET_MIDDLE) {
      return middleLine;
    }
    return middleLine + this.atrMultiplier * this.$atr.valueAt(index) * this.channelOffset;
  }
}

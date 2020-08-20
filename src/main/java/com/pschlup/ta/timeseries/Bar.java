package com.pschlup.ta.timeseries;

import lombok.Getter;

import java.time.Instant;

/** Represents a time series bar. */
@Getter
@SuppressWarnings("WeakerAccess")
public class Bar {

  private final TimeFrame timeFrame;

  /* The bar's open timestamp, inclusive */
  private final Instant openTime;
  /* The bar's close timestamp, exclusive */
  private final Instant closeTime;

  private double open, high, low, close, volume;

  public Bar(TimeFrame timeFrame, Instant openTime, double open, double high, double low, double close, double volume) {
    this.timeFrame = timeFrame;
    this.openTime = openTime;
    this.closeTime = openTime.plus(timeFrame.duration());
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.volume = volume;
  }

  /** Creates a new bar with just the specified open price. */
  Bar(TimeFrame timeFrame, Instant openTime, double openPrice) {
    this.timeFrame = timeFrame;
    this.openTime = openTime;
    this.closeTime = openTime.plus(timeFrame.duration());
    this.open = openPrice;
    this.high = openPrice;
    this.low = openPrice;
    this.close = openPrice;
  }

  /** Creates a bar based on a bar from a different timeframe */
  Bar(TimeFrame timeFrame, Bar otherBar) {
    this.timeFrame = timeFrame;
    this.openTime = otherBar.openTime;
    this.closeTime = otherBar.openTime.plus(timeFrame.duration());
    this.open = otherBar.open;
    this.high = otherBar.high;
    this.low = otherBar.low;
    this.close = otherBar.close;
    this.volume = otherBar.volume;
  }

  void addTickPrice(double price) {
    this.close = price;
    if (this.open == 0.) {
      this.open = price;
    }
    if (price > this.high) {
      this.high = price;
    }
    if (price < this.low) {
      this.low = price;
    }
  }

  void merge(Bar otherBar) {
    this.close = otherBar.close;
    this.volume += otherBar.volume;
    if (otherBar.high > this.high) {
      this.high = otherBar.high;
    }
    if (otherBar.low < this.low) {
      this.low = otherBar.low;
    }
  }

  boolean includesTimestamp(Instant instant) {
    return !instant.isBefore(this.openTime) && instant.isBefore(this.closeTime);
  }
}

package com.pschlup.ta.timeseries;

import java.util.EnumMap;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A class that receives price information and generates time series at specific time frames.
 */
@SuppressWarnings("unused")
public class TimeSeriesManager {

  /** The time series from which the others are downsampled from. */
  private final TimeFrame inputTimeFrame;

  private EnumMap<TimeFrame, TimeSeries> series = new EnumMap<>(TimeFrame.class);

  public TimeSeriesManager(TimeFrame inputTimeFrame) {
    this.inputTimeFrame = inputTimeFrame;
    series.put(this.inputTimeFrame, new TimeSeries(this.inputTimeFrame));
  }

  public TimeSeries getTimeSeries(TimeFrame timeframe) {
    checkArgument(
        timeframe.duration().compareTo(inputTimeFrame.duration()) >= 0,
        "Time series can only be downsampled from the base time frame.");
    return series.computeIfAbsent(timeframe, tf -> TimeSeries.downSample(tf, this.getBaseTimeSeries()));
  }

  /** Merges the bar into all time series. */
  public void addBar(Bar bar) {
    checkArgument(bar.getTimeFrame() == inputTimeFrame);
    for (TimeSeries s : series.values()) {
      s.addBar(bar);
    }
  }

  private TimeSeries getBaseTimeSeries() {
    return this.series.get(inputTimeFrame);
  }

  /* Convenience methods for retrieval of series in specific time frames. */

  public TimeSeries m5() {
    return getTimeSeries(TimeFrame.M5);
  }

  public TimeSeries m10() {
    return getTimeSeries(TimeFrame.M10);
  }

  public TimeSeries m15() {
    return getTimeSeries(TimeFrame.M15);
  }

  public TimeSeries m30() {
    return getTimeSeries(TimeFrame.M30);
  }

  public TimeSeries h1() {
    return getTimeSeries(TimeFrame.H1);
  }

  public TimeSeries h2() {
    return getTimeSeries(TimeFrame.H2);
  }

  public TimeSeries h4() {
    return getTimeSeries(TimeFrame.H4);
  }

  public TimeSeries d() {
    return getTimeSeries(TimeFrame.D);
  }
}

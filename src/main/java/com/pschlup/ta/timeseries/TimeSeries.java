package com.pschlup.ta.timeseries;

import com.pschlup.ta.indicators.Indicator;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a bar series at a specific time frame (e.g. 5 minutes, 4 hours).
 */
@SuppressWarnings("unused, WeakerAccess")
public class TimeSeries {

  private final TimeFrame timeFrame;

  // New bars are added at the beginning of the list.
  private final LinkedList<Bar> bars = new LinkedList<>();

  public static TimeSeries of(List<Bar> bars) {
    TimeSeries timeSeries = new TimeSeries(bars.get(0).getTimeFrame());
    timeSeries.addBars(bars);
    return timeSeries;
  }

  static TimeSeries downSample(TimeFrame timeFrame, TimeSeries otherTimeSeries) {
    TimeSeries timeSeries = new TimeSeries(timeFrame);
    timeSeries.addBars(otherTimeSeries.bars);
    return timeSeries;
  }

  TimeSeries(TimeFrame timeFrame) {
    this.timeFrame = timeFrame;
  }

  /** Merges the bar from a lower time frame into this time series */
  void addBar(Bar bar) {
    // Adds a new bar if the open time is after the latest bar
    Optional<Bar> currentBar = getCurrentBar(bar.getOpenTime());
    if (!currentBar.isPresent()) {
      this.bars.add(0, new Bar(timeFrame, bar));
      return;
    }
    currentBar.get().merge(bar);
  }

  /**
   * Updates the current bar with the specified tick price.
   * Bars are created automatically if needed.
   */
  public void addTickPrice(Instant tickTimestamp, double price) {
    Optional<Bar> currentBar = getCurrentBar(tickTimestamp);
    if (!currentBar.isPresent()) {
      bars.add(new Bar(timeFrame, tickTimestamp, price));
      return;
    }
    currentBar.get().addTickPrice(price);
  }

  private Instant truncateTimestamp(Instant timestamp) {
    long moduloSeconds = timestamp.getEpochSecond() % this.timeFrame.duration().getSeconds();
    return timestamp.minusSeconds(moduloSeconds).with(ChronoField.NANO_OF_SECOND, 0);
  }

  private Optional<Bar> getCurrentBar(Instant timestamp) {
    return getLatestBar()
        .filter(b -> b.includesTimestamp(timestamp));
  }

  public Optional<Bar> getLatestBar() {
    if (bars.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(bars.getFirst());
  }

  public Optional<Bar> getOldestBar() {
    if (bars.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(bars.getLast());
  }

  /** Merges the bars from a lower time frame into this time series */
  void addBars(Collection<Bar> bars) {
    bars.forEach(this::addBar);
  }

  public Bar barAt(int index) {
    if (index > bars.size() - 1) {
      // TODO: Solve this without throwing an exception (maybe return an optional).
      throw new UnstablePeriodException();
    }
    return bars.get(index);
  }

  /* Basic price indicators */

  public Indicator openPrice() {
    return index -> barAt(index).getOpen();
  }

  public Indicator highPrice() {
    return index -> barAt(index).getHigh();
  }

  public Indicator lowPrice() {
    return index -> barAt(index).getLow();
  }

  public Indicator closePrice() {
    return index -> barAt(index).getClose();
  }

  public Indicator volume() {
    return index -> barAt(index).getVolume();
  }

  public Indicator hlc3Price() {
    return typicalPrice();
  }

  public Indicator typicalPrice() {
    return index -> {
      Bar bar = barAt(index);
      return (bar.getHigh() + bar.getLow() + bar.getClose()) / 3;
    };
  }
}

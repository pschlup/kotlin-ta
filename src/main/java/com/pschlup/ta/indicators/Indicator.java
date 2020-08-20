package com.pschlup.ta.indicators;

import com.pschlup.ta.strategy.Signal;

/**
 * Provides a single value at the specified time series bar index.
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface Indicator {

  double valueAt(int index);

  /** Convenience getter for the latest indicator value. */
  default double latestValue() {
    return valueAt(0);
  }

  /* Derived indicators */

  /**
   * The value of the indicator delayed by the specified number of bars.
   */
  default Indicator previousValue(int bars) {
    return index -> this.valueAt(index + bars);
  }

  /* Basic signal expressions. */

  default Signal isOver(Indicator otherIndicator) {
    return index -> this.valueAt(index) > otherIndicator.valueAt(index);
  }

  default Signal isOver(double fixedValue) {
    return index -> this.valueAt(index) > fixedValue;
  }

  default Signal isPositive() {
    return index -> this.valueAt(index) > 0.;
  }

  default Signal isNegative() {
    return index -> this.valueAt(index) < 0.;
  }

  default Signal isUnder(Indicator otherIndicator) {
    return index -> this.valueAt(index) < otherIndicator.valueAt(index);
  }

  default Signal isUnder(double fixedValue) {
    return index -> this.valueAt(index) < fixedValue;
  }

  default Signal crossedOver(Indicator otherIndicator) {
    return index -> (this.valueAt(index) > otherIndicator.valueAt(index))
        && (this.valueAt(index + 1) <= otherIndicator.valueAt(index + 1));
  }

  default Signal crossedOver(double fixedValue) {
    return index -> (this.valueAt(index) > fixedValue)
        && (this.valueAt(index + 1) <= fixedValue);
  }

  default Signal crossedUnder(Indicator otherIndicator) {
    return index -> (this.valueAt(index) < otherIndicator.valueAt(index))
        && (this.valueAt(index + 1) >= otherIndicator.valueAt(index + 1));
  }

  default Signal crossedUnder(double fixedValue) {
    return index -> (this.valueAt(index) < fixedValue)
        && (this.valueAt(index + 1) >= fixedValue);
  }
}

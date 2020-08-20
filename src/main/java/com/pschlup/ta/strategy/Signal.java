package com.pschlup.ta.strategy;

/**
 * Represents one or more conditions that evaluate to a true/false result signal.
 * <p>
 *   Example:
 *   Signal entrySignal = ema8.crossedOver(sma21).and(sma21.isOver(sma30)).and(slope.isPositive());
 * </p>
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface Signal {

  boolean isActive(int index);

  // Boolean operations used to combine multiple signals.

  /** Combines the value of the signal with the specified signal using the logical AND operation. */
  default Signal and(Signal otherSignal) {
    return index -> this.isActive(index) && otherSignal.isActive(index);
  }

  /** Combines the value of the signal with the specified signal using the logical OR operation. */
  default Signal or(Signal otherSignal) {
    return index -> this.isActive(index) || otherSignal.isActive(index);
  }

  /** Inverts the value of the signal. */
  default Signal inverted() {
    return index -> !this.isActive(index);
  }
}

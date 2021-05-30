package com.pschlup.ta.strategy

/** The types of signals a strategy can generate.  */
enum class SignalType {
  /** Do nothing.  */
  NO_OP,
  /** Enter a new trade.  */
  ENTRY,
  /** Exit all active trades.  */
  EXIT
}

package com.pschlup.ta.backtest

/** Manages the history of active and inactive trades during a backtest.  */
internal class TradeHistory(
  /** The simulated initial account balance.  */
  var balance: Double,
  /** The fee % charged by the exchange in each trade  */
  private val feePerTrade: Double,
) {
  val trades: MutableList<TradeRecord> = mutableListOf()

  /** The maximum account equity value (balance + active trade value) reached.  */
  var maxEquity = balance
    private set

  /** The maximum account drawdown reached.  */
  var maxDrawDown = 0.0
    private set

  operator fun plusAssign(trade: TradeRecord) {
    require(trade.amount > 0.0)
    trades.add(trade)
    balance -= trade.entryPrice * trade.amount * (1.0 + feePerTrade)
  }

  private val activeTrades: List<TradeRecord>
    get() = trades.filter { it.isOpen }

  val activeTradeCount: Int
    get() = activeTrades.size

  fun exitActiveTrades(currentPrice: Double) {
    activeTrades.forEach { trade ->
      trade.exit(currentPrice)
      balance += trade.amount * currentPrice * (1.0 - feePerTrade)
    }
  }

  fun closeStoppedTrades(currentPrice: Double) {
    activeTrades
      .filter { it.shouldStop(currentPrice) }
      .forEach { trade ->
        trade.stop(currentPrice)
        balance += trade.amount * currentPrice * (1.0 - feePerTrade)
      }
  }

  fun updateEquity(currentPrice: Double) {
    val equity = balance + getCurrentInvestedValue(currentPrice)
    if (equity > maxEquity) {
      maxEquity = equity
    }
    val drawDown = (maxEquity - equity) / maxEquity
    if (drawDown > maxDrawDown) {
      maxDrawDown = drawDown
    }
  }

  fun trailStops(currentPrice: Double) {
    activeTrades.forEach { it.updateTrailingStop(currentPrice)  }
  }

  private fun getCurrentInvestedValue(currentPrice: Double): Double = activeTrades.sumOf { it.amount * currentPrice }
}

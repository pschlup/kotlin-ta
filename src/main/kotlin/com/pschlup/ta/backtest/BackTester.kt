package com.pschlup.ta.backtest

import com.pschlup.ta.indicators.latestValue
import com.pschlup.ta.timeseries.TimeSeriesManager
import com.pschlup.ta.strategy.SignalType
import com.pschlup.ta.timeseries.UnstablePeriodException

object BackTester {
  @JvmStatic
  fun run(spec: BackTestSpec): BackTestReport {
    val inputBars = spec.inputBars
    val inputTimeFrame = inputBars[0].timeFrame
    val timeSeriesManager = TimeSeriesManager(inputTimeFrame)
    val strategy = spec.strategyFactory.buildStrategy(timeSeriesManager)
    val stopLoss = spec.stopLoss.buildIndicator(timeSeriesManager)
    val runTimeSeries = timeSeriesManager.getTimeSeries(spec.runTimeFrame)
    val tradeHistory = TradeHistory(
      balance = spec.startingBalance,
      feePerTrade = spec.feePerTrade
    )

    for (inputBar in inputBars) {
      timeSeriesManager.addBar(inputBar)
      val currentPrice = inputBar.close

      // Closes stopped trades
      tradeHistory.closeStoppedTrades(currentPrice)

      // Updates trade history equity, drawdown.
      tradeHistory.updateEquity(currentPrice)

      // Avoids trading before the next close.
      // TODO: improve detection of new bars in the run time series.
      if (runTimeSeries.latestBar?.closeTime != inputBar.closeTime) {
        continue
      }
      try {
        // Moves trailing stops.
        if (spec.trailingStops) {
          tradeHistory.trailStops(stopLoss.latestValue)
        }
        val signal = strategy.signal
        if (signal === SignalType.EXIT) {
          tradeHistory.exitActiveTrades(currentPrice)
        } else if (signal === SignalType.ENTRY
          && tradeHistory.activeTradeCount < spec.pyramidingLimit
          && tradeHistory.balance > 0
        ) {

          // Calculates 1R risk value.
          val stopLossPrice = stopLoss.latestValue
          val risk = currentPrice - stopLossPrice

          // Computes risk factor. Risks max betSize% of the account on each trade.
          val maxBalanceRisk = tradeHistory.balance * spec.betSize
          var amount = maxBalanceRisk / risk

          // Avoids spending more than the available balance.
          if (amount > tradeHistory.balance / currentPrice) {
            amount = tradeHistory.balance / currentPrice
          }
          tradeHistory +=
            TradeRecord(
              type = spec.tradeType,
              timestamp = inputBar.closeTime,
              entryPrice = currentPrice,
              amount = amount,
              stopLossPrice = stopLossPrice,
              trailingStopDistance = currentPrice - stopLossPrice,
            )
        }
      } catch (e: UnstablePeriodException) {
        // Ignores trade signals during unstable period.
      }
    }

    // Closes trades that remain open in the end.
    val lastPrice: Double = runTimeSeries.latestBar?.close ?: Double.NaN
    tradeHistory.exitActiveTrades(lastPrice)
    val startPrice: Double = runTimeSeries.oldestBar?.open ?: Double.NaN
    return BackTestReport(
      spec = spec,
      trades = tradeHistory.trades,
      finalBalance = tradeHistory.balance,
      maxDrawDown = tradeHistory.maxDrawDown,
      startPrice = startPrice,
      endPrice = lastPrice,
    )
  }
}
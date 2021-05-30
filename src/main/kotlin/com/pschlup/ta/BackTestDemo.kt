package com.pschlup.ta

import com.pschlup.ta.backtest.BackTestReport
import com.pschlup.ta.backtest.BackTestSpec
import com.pschlup.ta.backtest.BackTester
import com.pschlup.ta.backtest.TradeType
import com.pschlup.ta.indicators.*
import com.pschlup.ta.strategy.*
import com.pschlup.ta.timeseries.CsvBarSource
import com.pschlup.ta.timeseries.TimeFrame
import com.pschlup.ta.timeseries.TimeSeriesManager

/** A sample back tester and strategy implementation. */
object BackTestDemo {
  @JvmStatic
  fun main(args: Array<String>) {

    // Sets up the backtest settings.
    val spec = BackTestSpec(
      tradeType = TradeType.LONG,
      // Defines the timeframe the strategy will run on. In this case the strategy will be evaluated every 1 hour,
      // even though the input data consists of 5-minute bars.
      runTimeFrame = TimeFrame.H1,
      // Whether to move the stop loss as the price moves up.
      trailingStops = false,
      // The limit of simultaneous trades.
      pyramidingLimit = 2,
      // Starting account balance, in counter currency units. E.g. USD for a BTC/USD pair.
      startingBalance = 20000.0,
      // Risks 2% of the account in each trade.
      betSize = 0.02,
      // The % fee charged by the exchange in each trade. E.g. Binance charges 0.1% per trade.
      feePerTrade = 0.001,
      // Defines the historical data to run the backtest over.
      inputBars = CsvBarSource.readCsvData("chart_data_BTC_USDT_p5_730d.csv"),
      // Defines the factory method that builds the trading strategy when needed.
      strategyFactory = { seriesManager ->
        makeStrategy(seriesManager)
      },
      // Defines the factory method that builds the stop-loss price indicator.
      stopLoss = { timeSeries ->
        timeSeries.h4.volatilityStop(length = 4, multiplier = 0.2)
      }
    )

    // Runs the backtest over the whole test dataset.
    val report = BackTester.run(spec)

    // Prints out the report
    printReport(report)
  }

  private fun makeStrategy(seriesManager: TimeSeriesManager): Strategy {

    // The timeframes our indicators will use.
    val h1 = seriesManager.h1 // 1 hour
    val h4 = seriesManager.h4 // 4 hours

    // Creates the 1-hour indicators.
    val h1price = h1.closePrice
    val h1cci = h1.cci(10)
    val h1ema4 = h1price.ema(4)
    val h1sma20 = h1price.sma(20)
    val h1ema30 = h1price.ema(30)

    // Creates the 4-hour indicators.
    val h4price: Indicator = h4.closePrice
    val h4ema4: Indicator = h4price.ema(4)
    val h4ema8: Indicator = h4price.ema(8)
    val h4ema15: Indicator = h4price.ema(15)

    return Strategy(
      // Identifies if the market is trending upwards (optional)
      trendSignal = (h4ema4 isOver h4ema8) and (h4ema8 isOver h4ema15) and (h1ema4 isOver h1ema30),
      // Identifies when to enter a trade. This is ignored if the trend signal is not positive.
      entrySignal = h1cci crossedOver +100.0,
      // Identifies when to exit active trades.
      exitSignal = h1ema4 crossedUnder h1sma20,
    )
  }

  private fun printReport(report: BackTestReport) {
    println("----------------------------------")
    println(String.format("Finished backtest with %d trades", report.tradeCount))
    println("----------------------------------")

    // Input parameters
    println(String.format("Pyramiding limit   : %d", report.pyramidingLimit))
    println(String.format("Account risk/trade : %.1f%%", 100.0 * report.betSize))
    println("----------------------------------")

    // Analysis
    println(String.format("Profitability      : %.2f%%", 100.0 * report.profitability))
    println(String.format("Buy-and-hold       : %.2f%%", 100.0 * report.buyAndHoldProfitability))
    println(String.format("Vs buy-and-hold    : %.2f%%", 100.0 * report.vsBuyAndHold))
    println(String.format("Win rate           : %.2f%%", 100.0 * report.winRate))
    println(String.format("Max drawdown       : %.2f%%", 100.0 * report.maxDrawDown))
    println(String.format("Risk/reward ratio  : %.2f", report.riskReward))
    println(String.format("Sortino ratio      : %.2f", report.sortinoRatio))
    println("----------------------------------")

    // Financial result
    println(String.format("Start balance      : $%,.2f", report.initialBalance))
    println(String.format("Profit/loss        : $%,.2f", report.profitLoss))
    println(String.format("Final balance      : $%,.2f", report.finalBalance))
    println("----------------------------------")
  }
}

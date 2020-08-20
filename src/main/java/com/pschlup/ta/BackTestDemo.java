package com.pschlup.ta;

import com.pschlup.ta.backtest.BackTestReport;
import com.pschlup.ta.backtest.BackTestSpec;
import com.pschlup.ta.backtest.BackTester;
import com.pschlup.ta.backtest.TradeType;
import com.pschlup.ta.indicators.*;
import com.pschlup.ta.strategy.Signal;
import com.pschlup.ta.strategy.Strategy;
import com.pschlup.ta.timeseries.CsvBarSource;
import com.pschlup.ta.timeseries.TimeFrame;
import com.pschlup.ta.timeseries.TimeSeries;
import com.pschlup.ta.timeseries.TimeSeriesManager;

import java.io.IOException;

/**
 * A sample back tester and strategy implementation.
 */
public class BackTestDemo {

  public static void main(String[] args) throws IOException {

    // Sets up the backtest's settings.
    BackTestSpec spec = BackTestSpec.builder()
        .tradeType(TradeType.LONG)
        // Defines the timeframe the strategy will run on. In this case the strategy will be evaluated every 1 hour,
        // even though the input data consists of 5-minute bars.
        .runTimeFrame(TimeFrame.H1)
        // Whether to move the stop loss as the price moves up.
        .trailingStop(false)
        // The limit of simultaneous trades.
        .pyramidingLimit(2)
        // Starting account balance, in counter currency units. E.g. USD for a BTC/USD pair.
        .startingBalance(20_000)
        // Risks 2% of the account in each trade.
        .betSize(0.02)
        // The % fee charged by the exchange in each trade. E.g. Binance charges 0.1% per trade.
        .feePerTrade(0.001)
        // Defines the historical data to run the backtest over.
        .inputBars(CsvBarSource.readCsvData("chart_data_BTC_USDT_p5_730d.csv"))
        // Defines the factory method that builds the trading strategy when needed.
        .strategyFactory(BackTestDemo::buildStrategy)
        // Defines the factory method that builds the stop-loss price indicator.
        .stopLoss(timeSeries ->
            VolatilityStop.of(timeSeries.h4())
                .withLength(4)
                .withMultiplier(0.2))
        .build();

    // Runs the backtest over the whole test dataset.
    BackTestReport report = BackTester.run(spec);

    // Prints out the report
    BackTestDemo.printReport(report);
  }

  private static Strategy buildStrategy(TimeSeriesManager seriesManager) {

    // Identifies the timeframes our indicators will use.
    TimeSeries h1 = seriesManager.h1(); // 1 hour
    TimeSeries h4 = seriesManager.h4(); // 4 hours

    // Creates the 1-hour indicators.
    Indicator h1price = h1.closePrice();
    CCI h1cci = CCI.of(h1).withLength(10);
    Indicator h1ema4 = ExponentialMovingAverage.of(h1price).withLength(4);
    Indicator h1sma20 = SimpleMovingAverage.of(h1price).withLength(20);
    Indicator h1ema30 = ExponentialMovingAverage.of(h1price).withLength(30);

    // Creates the 4-hour indicators.
    Indicator h4price = h4.closePrice();
    Indicator h4ema4 = ExponentialMovingAverage.of(h4price).withLength(4);
    Indicator h4ema8 = ExponentialMovingAverage.of(h4price).withLength(8);
    Indicator h4ema15 = ExponentialMovingAverage.of(h4price).withLength(15);

    // Identifies if the market is trending upwards.
    Signal trendSignal = h4ema4.isOver(h4ema8)
        .and(h4ema8.isOver(h4ema15))
        .and(h1ema4.isOver(h1ema30));

    // Identifies when to enter a trade.
    // This is ignored if the trend signal is not positive.
    Signal entrySignal = h1cci.crossedOver(+100);

    // Identifies when to exit active trades.
    Signal exitSignal = h1ema4.crossedUnder(h1sma20);

    return Strategy.builder()
        .trendSignal(trendSignal)
        .entrySignal(entrySignal)
        .exitSignal(exitSignal)
        .build();
  }

  private static void printReport(BackTestReport report) {
    System.out.println("----------------------------------");
    System.out.println(String.format("Finished backtest with %d trades", report.getTradeCount()));
    System.out.println("----------------------------------");

    // Input parameters
    System.out.println(String.format("Pyramiding limit   : %d", report.getPyramidingLimit()));
    System.out.println(String.format("Account risk/trade : %.1f%%", 100. * report.getBetSize()));
    System.out.println("----------------------------------");

    // Analysis
    System.out.println(String.format("Profitability      : %.2f%%", 100. * report.getProfitability()));
    System.out.println(String.format("Buy-and-hold       : %.2f%%", 100 * report.getBuyAndHoldProfitability()));
    System.out.println(String.format("Vs buy-and-hold    : %.2f%%", 100 * report.getVsBuyAndHold()));
    System.out.println(String.format("Win rate           : %.2f%%", 100. * report.getWinRate()));
    System.out.println(String.format("Max drawdown       : %.2f%%", 100. * report.getMaxDrawDown()));
    System.out.println(String.format("Risk/reward ratio  : %.2f", report.getRiskReward()));
    System.out.println(String.format("Sortino ratio      : %.2f", report.getSortinoRatio()));
    System.out.println("----------------------------------");

    // Financial result
    System.out.println(String.format("Start balance      : $%,.2f", report.getInitialBalance()));
    System.out.println(String.format("Profit/loss        : $%,.2f", report.getProfitLoss()));
    System.out.println(String.format("Final balance      : $%,.2f", report.getFinalBalance()));
    System.out.println("----------------------------------");
  }
}

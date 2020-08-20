package com.pschlup.ta.backtest;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/** Manages the history of active and inactive trades during a backtest. */
class TradeHistory {

  private final List<TradeRecord> trades = new ArrayList<>();

  /** The fee % charged by the exchange in each trade */
  private final double feePerTrade;

  /** The simulated account balance. */
  private double balance;

  /** The maximum account equity value (balance + active trade value) reached. */
  private double maxEquity;

  /** The maximum account drawdown reached. */
  private double maxDrawDown = 0.;

  TradeHistory(double startingBalance, double feePerTrade) {
    this.balance = startingBalance;
    this.maxEquity = startingBalance;
    this.feePerTrade = feePerTrade;
  }

  double getBalance() {
    return balance;
  }

  double getMaxDrawDown() {
    return maxDrawDown;
  }

  void add(TradeRecord trade) {
    Preconditions.checkArgument(trade.getAmount() > 0.);
    this.trades.add(trade);
    this.balance -= trade.getEntryPrice() * trade.getAmount() * (1. + feePerTrade);
  }

  List<TradeRecord> getTrades() {
    return ImmutableList.copyOf(this.trades);
  }

  long getActiveTradeCount() {
    return this.trades.stream()
        .filter(TradeRecord::isOpen)
        .count();
  }

  void exitActiveTrades(double currentPrice) {
    for (TradeRecord trade : this.getActiveTrades()) {
      trade.exit(currentPrice);
      this.balance += trade.getAmount() * currentPrice * (1. - feePerTrade);
    }
  }

  void closeStoppedTrades(double currentPrice) {
    for (TradeRecord trade : this.getActiveTrades()) {
      if (trade.shouldStop(currentPrice)) {
        trade.stop(currentPrice);
        this.balance += trade.getAmount() * currentPrice * (1. - feePerTrade);
      }
    }
  }

  void updateEquity(double currentPrice) {
    double equity = this.balance + getCurrentInvestedValue(currentPrice);
    if (equity > this.maxEquity) {
      this.maxEquity = equity;
    }
    double drawDown = (this.maxEquity - equity) / this.maxEquity;
    if (drawDown > this.maxDrawDown) {
      this.maxDrawDown = drawDown;
    }
  }

  void trailStops(double currentPrice) {
    for (TradeRecord trade : getActiveTrades()) {
      trade.updateTrailingStop(currentPrice);
    }
  }

  private List<TradeRecord> getActiveTrades() {
    return this.trades.stream()
        .filter(TradeRecord::isOpen)
        .collect(toList());
  }

  private double getCurrentInvestedValue(double currentPrice) {
    return trades.stream()
        .filter(TradeRecord::isOpen)
        .mapToDouble(TradeRecord::getAmount)
        .map(amount -> amount * currentPrice)
        .sum();
  }
}

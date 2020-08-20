package com.pschlup.ta.backtest;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class BackTestReport {

  /**
   * The risk-free rate to use when calculating Sharpe and Sortino ratios.
   * <p>
   * Current personal savings interest ratio is near zero so this value is mostly included for clarity.
   */
  private static final double RISK_FREE_RATE = 0.;

  @NonNull
  private final BackTestSpec spec;
  @NonNull
  private final List<TradeRecord> trades;

  private final double finalBalance;
  private final double maxDrawDown;
  private final double startPrice;
  private final double endPrice;

  public int getTradeCount() {
    return trades.size();
  }

  public int getPyramidingLimit() {
    return spec.getPyramidingLimit();
  }

  public double getBetSize() {
    return spec.getBetSize();
  }

  public double getInitialBalance() {
    return spec.getStartingBalance();
  }

  public double getFinalBalance() {
    return finalBalance;
  }

  public double getMaxDrawDown() {
    return maxDrawDown;
  }

  /**
   * The Sortino ratio with a risk-free rate of 0%.
   * <p>{@see https://www.investopedia.com/terms/s/sortinoratio.asp}
   */
  public double getSortinoRatio() {
    return (getProfitability() - RISK_FREE_RATE) / getMaxDrawDown();
  }

  public double getProfitability() {
    return getProfitLoss() / getInitialBalance();
  }

  public double getProfitLoss() {
    return trades.stream()
        .mapToDouble(TradeRecord::getProfitLoss)
        .sum();
  }

  public double getWinRate() {
    double wins = trades.stream()
        .filter(TradeRecord::isProfitable)
        .count();
    return wins / trades.size();
  }

  public double getVsBuyAndHold() {
    return getProfitability() / getBuyAndHoldProfitability() - 1.;
  }

  public double getBuyAndHoldProfitability() {
    return spec.getTradeType() == TradeType.LONG
        ? endPrice / startPrice
        : startPrice / endPrice;
  }

  public double getRiskReward() {
    double riskReward = trades.stream()
        .mapToDouble(this::getRiskReward)
        .sum();
    return riskReward / this.getTradeCount();
  }

  private double getRiskReward(TradeRecord tradeRecord) {
    double positionSize = tradeRecord.getEntryPrice() * tradeRecord.getAmount();
    double profitLoss = tradeRecord.getProfitLoss();
    double percentageProfitLoss = 1 + profitLoss / (positionSize - Math.abs(profitLoss));
    return percentageProfitLoss / this.getMaxDrawDown();
  }
}

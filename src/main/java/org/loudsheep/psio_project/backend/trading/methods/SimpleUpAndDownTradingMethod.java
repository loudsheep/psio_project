package org.loudsheep.psio_project.backend.trading.methods;

import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.StrategyResult;
import org.loudsheep.psio_project.backend.observers.StrategyResultObserver;
import org.loudsheep.psio_project.backend.trading.TradingMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleUpAndDownTradingMethod implements TradingMethod {
    private static final String name = "SimpleUpAndDown Strategy";
    private static final String description = "Simple strategy that sells when downward trend, and buys when upward";
    private boolean stopExecution = false;

    private final double budget;
    private final int daysBackToCheck;
    private final StrategyResult result;

    public SimpleUpAndDownTradingMethod(double budget, int daysBackToCheck) {
        this.budget = budget;
        this.daysBackToCheck = daysBackToCheck;
        this.result = new StrategyResult(budget);

        System.out.println("NEW SimpleUpAndDownStrategy created");
    }

    private int getLastDaysTrend(StockData data, int currentDayIdx, int daysBack) {
        int trend = 0;
        DayStockData currentData = data.getDailyData().get(currentDayIdx);
        for (int i = currentDayIdx; i >= Math.max(0, currentDayIdx - daysBack); i--) {
            DayStockData dayData = data.getDailyData().get(i);

            if (dayData.getOpen() == currentData.getOpen()) trend = 0;
            else trend = (dayData.getOpen() - currentData.getOpen() > 0) ? 1 : -1;
        }
        return trend;
    }

    @Override
    public StrategyResult execute(StockData data) {
        this.result.resetState();
        this.stopExecution = false;

        System.out.println("EXECUTING THE STRATEGY");
        for (int i = 0; i < data.getDailyData().size(); i++) {
            DayStockData dayData = data.getDailyData().get(i);
            double price = dayData.getOpen();

            int trend = this.getLastDaysTrend(data, i, this.daysBackToCheck);

            if (trend > 0) {
                this.result.buyStock(this.result.maxStockToBuy(price), price, dayData.getTimestamp());
            } else {
                this.result.sellAllStock(price, dayData.getTimestamp());
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException _) {
            }

            if (this.stopExecution) {
                this.result.sellAllStock(price, dayData.getTimestamp());
                break;
            }
        }

        this.result.sellAllStock(data.getDailyData().getLast().getClose(), data.getLastDataPointTimestamp());

        System.out.println("END OF STRATEGY");
        System.out.println("Transactions: " + this.result.getNumberOfTransactions() );
        System.out.println("ROI: " + this.result.getROI() );

        return this.result;
    }

    @Override
    public boolean isReadyToExecute() {
        if (this.budget <= 0) return false;
        if (this.daysBackToCheck <= 0) return false;
        return true;
    }

    @Override
    public void stopExecution() {
        this.stopExecution = true;
    }

    @Override
    public void addStrategyResultObserver(StrategyResultObserver observer) {
        this.result.addObserver(observer);
    }

    @Override
    public void removeStrategyResultObserver(StrategyResultObserver observer) {
        this.result.removeObserver(observer);
    }

    @Override
    public String getDescription() {
        return SimpleUpAndDownTradingMethod.description;
    }

    @Override
    public String getName() {
        return SimpleUpAndDownTradingMethod.name;
    }

    @Override
    public Map<String, Object> getMethodParams() {
        Map<String ,Object> result = new java.util.HashMap<>();

        result.put("budget", budget);
        result.put("daysBackToCheck", daysBackToCheck);

        return result;
    }
}

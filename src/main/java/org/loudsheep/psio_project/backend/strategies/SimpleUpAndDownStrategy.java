package org.loudsheep.psio_project.backend.strategies;

import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.StrategyResult;
import org.loudsheep.psio_project.backend.observers.StrategyResultObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleUpAndDownStrategy implements TradingStrategy {
    private static final String name = "SimpleUpAndDown Strategy";
    private static final String description = "Simple strategy that sells when downward trend, and buys when upward";

    private final double budget;
    private final int daysBackToCheck;
    private StrategyResult result;

    public SimpleUpAndDownStrategy(double budget, int daysBackToCheck) {
        this.budget = budget;
        this.daysBackToCheck = daysBackToCheck;
        this.result = new StrategyResult(budget);

        System.out.println("NEW SimpleUpAndDownStrategy created");
    }

    private int getLastDaysTrend(StockData data, int currentDayIdx, int daysBack) {
        int trend = 0;
        DayStockData currentData = data.getDailyData().get(currentDayIdx);
        for (int i = currentDayIdx; i >= 0; i--) {
            DayStockData dayData = data.getDailyData().get(i);

            if (dayData.getOpen() == currentData.getOpen()) trend = 0;
            else trend = (dayData.getOpen() - currentData.getOpen() > 0) ? 1 : -1;
        }
        return trend;
    }

    @Override
    public StrategyResult execute(StockData data) {
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
                Thread.sleep(100);
            } catch (InterruptedException _) {
            }
        }

        return this.result;
    }

    @Override
    public boolean isReadyToExecute() {
        if (this.budget <= 0) return false;
        if (this.daysBackToCheck <= 0) return false;
        return true;
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
        return SimpleUpAndDownStrategy.description;
    }

    @Override
    public String getName() {
        return SimpleUpAndDownStrategy.name;
    }

    public static String[] validateData(Map<String, Object> formData) {
        List<String> errors = new ArrayList<>();

        System.out.println(formData);

        if (!formData.containsKey("budget") || !(formData.get("budget") instanceof Double)) {
            errors.add("Budget is required and must be a number value.");
        }

        if (!formData.containsKey("daysBackToCheck") || !(formData.get("daysBackToCheck") instanceof Integer)) {
            errors.add("daysBackToCheck is required and must be an integer.");
        } else {
            int age = (int) formData.get("daysBackToCheck");
            if (age <= 0) {
                errors.add("daysBackToCheck must be non-negative.");
            }
        }

        return errors.toArray(new String[0]);
    }

    public static SimpleUpAndDownStrategy create(Map<String, Object> formData) {
        return new SimpleUpAndDownStrategy((Double) formData.get("budget"), (Integer) formData.get("daysBackToCheck"));
    }
}

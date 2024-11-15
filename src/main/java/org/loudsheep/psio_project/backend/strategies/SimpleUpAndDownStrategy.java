package org.loudsheep.psio_project.backend.strategies;

import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;

public class SimpleUpAndDownStrategy extends Strategy {
    private final int daysBackToCheck;

    public SimpleUpAndDownStrategy(double budget, int daysBackToCheck) {
        super(budget);
        this.STRATEGY_DESCRIPTION = "Simple strategy that sells when downward trend, and buys when upward";
        this.daysBackToCheck = daysBackToCheck;
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
    public void execute(StockData data) {
        for (int i = 0; i < data.getDailyData().size(); i++) {
            DayStockData dayData = data.getDailyData().get(i);
            double price = dayData.getOpen();

            int trend = this.getLastDaysTrend(data, i, this.daysBackToCheck);

            if (trend > 0) {
                this.buyStock(this.maxStockToBuy(price), price, dayData.getTimestamp());
            } else {
                this.sellAllStock(price, dayData.getTimestamp());
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException _) {
            }
        }
    }
}

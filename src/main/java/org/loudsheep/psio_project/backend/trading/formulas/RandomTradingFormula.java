package org.loudsheep.psio_project.backend.trading.formulas;

import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.SimulationResult;
import org.loudsheep.psio_project.backend.observers.SimulationResultObserver;
import org.loudsheep.psio_project.backend.trading.TradingFormula;

import java.util.Map;

public class RandomTradingFormula implements TradingFormula {
    private static final String name = "Random Strategy";
    private static final String description = "Random decisions";

    private final double budget;
    private final SimulationResult result;
    private boolean stopExecution = false;

    public RandomTradingFormula(double budget) {
        this.budget = budget;
        this.result = new SimulationResult(budget);

        System.out.println("NEW RandomStrategy created");
    }

    @Override
    public void execute(StockData data) {
        this.result.resetState();
        this.stopExecution = false;

        for (int i = 0; i < data.dailyData().size(); i++) {
            DayStockData dayData = data.dailyData().get(i);
            double price = dayData.getOpen();

            double rand = Math.random();
            // 10% -> buy Transaction
            // 10% -> sell Transaction
            // 80% -> no action at all

            if (rand < 0.1) {
                int maxToBuy = this.result.maxStockToBuy(price);
                int randomBuyAmount = (int) Math.floor(Math.random() * maxToBuy);

                this.result.buyStock(randomBuyAmount, price, dayData.getTimestamp());
            } else if (rand < 0.2) {
                int randomToSell = (int) Math.floor(this.result.getStockOwned() * Math.random());

                this.result.sellStock(randomToSell, price, dayData.getTimestamp());
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

        this.result.sellAllStock(data.dailyData().getLast().getClose(), data.getLastDataPointTimestamp());
    }

    @Override
    public boolean isReadyToExecute() {
        if (this.budget <= 0) return false;
        return true;
    }

    @Override
    public void stopExecution() {
        this.stopExecution = true;
    }

    @Override
    public void addStrategyResultObserver(SimulationResultObserver observer) {
        this.result.addObserver(observer);
    }

    @Override
    public void removeStrategyResultObserver(SimulationResultObserver observer) {
        this.result.removeObserver(observer);
    }

    @Override
    public String getDescription() {
        return RandomTradingFormula.description;
    }

    @Override
    public String getName() {
        return RandomTradingFormula.name;
    }

    @Override
    public String getSignature() {
        return "Random";
    }

    @Override
    public Map<String, Object> getMethodParams() {
        Map<String, Object> result = new java.util.HashMap<>();

        result.put("budget", budget);

        return result;
    }
}

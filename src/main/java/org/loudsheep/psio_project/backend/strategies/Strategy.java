package org.loudsheep.psio_project.backend.strategies;

import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.StrategyResult;

public abstract class Strategy implements Validatable {
    protected String STRATEGY_DESCRIPTION = "Strategy description";
    protected int stockOwned;
    protected StrategyResult result;

    public Strategy(double initialBudget) {
        this.stockOwned = 0;
        this.result = new StrategyResult(initialBudget);
    }

    protected boolean hasEnoughMoneyToBuy(int amount, double price) {
        return this.result.canAddTransaction(amount * price);
    }

    protected int maxStockToBuy(double price) {
        return (int) Math.floor(this.result.getCurrentBudget() / price);
    }

    protected boolean buyStock(int amount, double price, long timestamp) {
        if (!hasEnoughMoneyToBuy(amount, price)) return false;

        this.stockOwned += amount;
        // negative price means buy
        this.result.addTransaction(-amount, price, timestamp);

        return true;
    }

    protected boolean sellStock(int amount, double price, long timestamp) {
        if (amount >= this.stockOwned) return false;

        this.stockOwned -= amount;
        // positive price means sell
        this.result.addTransaction(amount, price, timestamp);

        return true;
    }

    protected void sellAllStock(double price, long timestamp) {
        this.result.addTransaction(this.stockOwned, price, timestamp);
        this.stockOwned = 0;
    }

    public String getDescription() {
        return this.STRATEGY_DESCRIPTION;
    }

    public int getStockOwned() {
        return stockOwned;
    }

    public abstract void execute(StockData data);
}

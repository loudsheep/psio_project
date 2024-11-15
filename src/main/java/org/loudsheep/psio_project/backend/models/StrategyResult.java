package org.loudsheep.psio_project.backend.models;

import java.util.ArrayList;
import java.util.List;

public class StrategyResult {
    private final List<Transaction> transactions = new ArrayList<>();
    private final double initialBudget;
    private double currentBudget;

    public StrategyResult(double initialBudget) {
        this.initialBudget = Math.max(initialBudget, 0);
        this.currentBudget = this.initialBudget;
    }

    public boolean canAddTransaction(double value) {
        return this.currentBudget + value >= 0;
    }

    public boolean addTransaction(int volume, double price, long timestamp) {
        if (!this.canAddTransaction(volume * price)) return false;

        this.transactions.add(
                new Transaction(volume, price, timestamp)
        );
        this.currentBudget += volume * price;

        return true;
    }

    public int getNumberOfTransactions() {
        return this.transactions.size();
    }

    public double getROI() {
        return this.currentBudget / this.initialBudget;
    }

    public double getCurrentBudget() {
        return currentBudget;
    }

    public void setCurrentBudget(double currentBudget) {
        this.currentBudget = currentBudget;
    }
}

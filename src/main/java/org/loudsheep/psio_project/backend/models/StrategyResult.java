package org.loudsheep.psio_project.backend.models;

import org.loudsheep.psio_project.backend.observers.StrategyResultObserver;

import java.util.ArrayList;
import java.util.List;

public class StrategyResult {
    private final List<Transaction> transactions = new ArrayList<>();
    private final double initialBudget;
    private double currentBudget;
    private List<StrategyResultObserver> observers = new ArrayList<>();

    public StrategyResult(double initialBudget) {
        this.initialBudget = Math.max(initialBudget, 0);
        this.currentBudget = this.initialBudget;
    }

    public void addObserver(StrategyResultObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(StrategyResultObserver observer) {
        this.observers.remove(observer);
    }

    private void notifyObserversWithNewTransaction(Transaction transaction) {
        for (StrategyResultObserver o : this.observers) {
            o.onTransactionAdd(transaction);
        }
    }

    public boolean canAddTransaction(double value) {
        return this.currentBudget + value >= 0;
    }

    public boolean addTransaction(int volume, double price, long timestamp) {
        if (!this.canAddTransaction(volume * price)) return false;

        Transaction newTransaction = new Transaction(volume, price, timestamp);
        this.transactions.add(newTransaction);
        this.notifyObserversWithNewTransaction(newTransaction);
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

    @Override
    public String toString() {
        return "StrategyResult{" +
                "initialBudget=" + initialBudget +
                ", currentBudget=" + currentBudget +
                ", numOfTransactions=" + this.getNumberOfTransactions() +
                ", roi=" + this.getROI() +
                '}';
    }
}

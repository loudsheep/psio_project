package org.loudsheep.psio_project.backend.services;

import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.observers.StockDataObserver;
import org.loudsheep.psio_project.backend.strategies.SimpleUpAndDownStrategy;
import org.loudsheep.psio_project.backend.strategies.Strategy;
import org.loudsheep.psio_project.backend.strategies.TradingStrategy;

import java.util.Map;

public class TradingManager implements StockDataObserver {
    private static TradingManager instance;

    private final StockService stockService;
    private StockData stockData;
    private TradingStrategy strategyInstance;

    private TradingManager() {
        this.stockService = new StockService();
        this.stockService.addObserver(this);
    }

    public void setStockData(String symbol, long startTime, long endTime) {
        // use StockService to fetch data (async)
        new Thread(() -> stockService.getStockData(symbol, startTime, endTime)).start();
    }

    public StockData getStockData() {
        return this.stockData;
    }

    public String[] setStrategy(String strategyName, Map<String, Object> params) {
        switch (strategyName) {
            case "SimpleUpAndDown":
                String[] errors = SimpleUpAndDownStrategy.validateData(params);
                if (errors.length > 0) return errors;

                this.strategyInstance = SimpleUpAndDownStrategy.create(params);
                return new String[0];
            default:
                throw new IllegalArgumentException("Unknown strategy: " + strategyName);
        }
    }

    public TradingStrategy getStrategyInstance() {
        return this.strategyInstance;
    }

    public void addStockDataObserver(StockDataObserver observer) {
        this.stockService.addObserver(observer);
    }

    public void removeStockDataObserver(StockDataObserver observer) {
        this.stockService.removeObserver(observer);
    }

    public boolean isReadyToExecute() {
        if (this.strategyInstance == null || !this.strategyInstance.isReadyToExecute()) return false;
        if (this.stockData == null) return false;

        return true;
    }

    @Override
    public void onDataChanged(StockData data) {
        this.stockData = data;
        System.out.println("Set new Stock Data - " + data.getDailyData().size() + " data points");
    }

    @Override
    public void setError(String error) {
    }

    public static TradingManager getInstance() {
        if (instance == null) {
            instance = new TradingManager();
        }
        return instance;
    }
}

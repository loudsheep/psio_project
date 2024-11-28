package org.loudsheep.psio_project.backend.services;

import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.observers.StockDataObserver;
import org.loudsheep.psio_project.backend.trading.methods.RandomTradingMethod;
import org.loudsheep.psio_project.backend.trading.methods.SimpleUpAndDownTradingMethod;
import org.loudsheep.psio_project.backend.trading.TradingMethod;

import java.util.Map;

public class TradingManager implements StockDataObserver {
    private static TradingManager instance;

    private final StockService stockService;
    private StockData stockData;
    private TradingMethod strategyInstance;

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
                String[] errors1 = SimpleUpAndDownTradingMethod.validateData(params);
                if (errors1.length > 0) return errors1;

                this.strategyInstance = SimpleUpAndDownTradingMethod.create(params);
                return new String[0];
            case "Random":
                String[] errors2 = RandomTradingMethod.validateData(params);
                if (errors2.length > 0) return errors2;

                this.strategyInstance = RandomTradingMethod.create(params);
                return new String[0];
            default:
                throw new IllegalArgumentException("Unknown strategy: " + strategyName);
        }
    }

    public TradingMethod getStrategyInstance() {
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

    public void execute() {
        if (!this.isReadyToExecute()) return;

        new Thread(() -> {
            this.strategyInstance.execute(this.stockData);
        }).start();
    }

    public void stopExecution() {
        this.strategyInstance.stopExecution();
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

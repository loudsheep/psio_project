package org.loudsheep.psio_project.backend.services;

import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.observers.StockDataObserver;
import org.loudsheep.psio_project.backend.strategies.SimpleUpAndDownStrategy;
import org.loudsheep.psio_project.backend.strategies.Strategy;
import org.loudsheep.psio_project.backend.strategies.Validatable;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class TradingManager implements StockDataObserver {
    private final StockService stockService;
    private StockData stockData;
    private Strategy strategyInstance;

    public TradingManager() {
        this.stockService = new StockService();
        this.stockService.addObserver(this);
    }

    public void getStockData(String symbol, long startTime, long endTime) throws Exception {
        // Use StockService to fetch data
        stockService.getStockData(symbol, startTime, endTime);
    }

    public String[] setStrategy(String strategyName, Map<String, Object> params) throws Exception {
        switch (strategyName) {
            case "SimpleUpAndDown":
                Validatable instance = SimpleUpAndDownStrategy.class.getDeclaredConstructor().newInstance();

                String[] errors = instance.validateData(params);
                if (errors.length > 0) return errors;

                this.strategyInstance = (SimpleUpAndDownStrategy) instance.create(params);
            default:
                throw new IllegalArgumentException("Unknown strategy: " + strategyName);
        }
    }

    public void addStockDataObserver(StockDataObserver observer) {
        this.stockService.addObserver(observer);
    }

    public void removeStockDataObserver(StockDataObserver observer) {
        this.stockService.removeObserver(observer);
    }

    @Override
    public void onDataChanged(StockData data) {
        this.stockData = data;
    }
}

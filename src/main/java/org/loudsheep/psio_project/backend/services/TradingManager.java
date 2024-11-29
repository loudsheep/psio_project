package org.loudsheep.psio_project.backend.services;

import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.observers.StockDataObserver;
import org.loudsheep.psio_project.backend.trading.TradingMethodValidator;
import org.loudsheep.psio_project.backend.trading.TradingMethod;
import org.loudsheep.psio_project.backend.trading.validators.RandomTradingMethodValidator;
import org.loudsheep.psio_project.backend.trading.validators.SimpleUpAndDownTradingMethodValidator;

import java.util.Map;

public class TradingManager implements StockDataObserver {
    private static TradingManager instance;

    private final StockService stockService;
    private StockData stockData;
    private TradingMethod tradingMethodInstance;

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
        TradingMethodValidator validator;

        if (strategyName.equals("SimpleUpAndDown")) validator = new SimpleUpAndDownTradingMethodValidator();
        else if (strategyName.equals("Random")) validator = new RandomTradingMethodValidator();
        else return new String[]{"Unknown strategy name '" + strategyName + "'"};

        String[] errors = validator.validate(params);
        if (errors.length > 0) return errors;

        this.tradingMethodInstance = validator.create(params);
        return new String[0];
    }

    public TradingMethod getTradingMethodInstance() {
        return this.tradingMethodInstance;
    }

    public void addStockDataObserver(StockDataObserver observer) {
        this.stockService.addObserver(observer);
    }

    public void removeStockDataObserver(StockDataObserver observer) {
        this.stockService.removeObserver(observer);
    }

    public boolean isReadyToExecute() {
        if (this.tradingMethodInstance == null || !this.tradingMethodInstance.isReadyToExecute()) return false;
        if (this.stockData == null) return false;

        return true;
    }

    public void execute() {
        if (!this.isReadyToExecute()) return;
        this.tradingMethodInstance.stopExecution();

        // Execute trading method async
        new Thread(() -> this.tradingMethodInstance.execute(this.stockData)).start();
    }

    public void stopExecution() {
        this.tradingMethodInstance.stopExecution();
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

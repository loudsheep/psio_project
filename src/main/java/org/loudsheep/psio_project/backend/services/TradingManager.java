package org.loudsheep.psio_project.backend.services;

import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.observers.StockDataObserver;
import org.loudsheep.psio_project.backend.trading.TradingMethodValidator;
import org.loudsheep.psio_project.backend.trading.TradingMethod;
import org.loudsheep.psio_project.backend.trading.validators.RandomTradingMethodValidator;
import org.loudsheep.psio_project.backend.trading.validators.MultiIndicatorFusionTradingMethodValidator;
import org.loudsheep.psio_project.backend.trading.validators.SimpleUpAndDownTradingMethodValidator;

import java.util.Map;

public class TradingManager implements StockDataObserver {
    private static TradingManager instance;

    // services and instances of StockData and
    private final StockService stockService;
    private StockData stockData;
    private TradingMethod tradingMethodInstance;

    private TradingManager() {
        this.stockService = new StockService();
        this.stockService.addObserver(this);
    }

    // fetch stock data using service
    public void setStockData(String symbol, long startTime, long endTime) {
        // use StockService to fetch data (async)
        new Thread(() -> stockService.getStockData(symbol, startTime, endTime)).start();
    }

    public StockData getStockData() {
        return this.stockData;
    }

    // initialize new method with given params, create validator, and return errors if occurred
    public String[] setMethod(String methodName, Map<String, Object> params) {
        TradingMethodValidator validator;

        switch (methodName) {
            case "SimpleUpAndDown" -> validator = new SimpleUpAndDownTradingMethodValidator();
            case "Random" -> validator = new RandomTradingMethodValidator();
            case "MultiIndicatorFusion" -> validator = new MultiIndicatorFusionTradingMethodValidator();
            default -> {
                return new String[]{"Unknown strategy name '" + methodName + "'"};
            }
        }

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

    // check if all instance are initialized and ready to execute the simulation
    public boolean isReadyToExecute() {
        if (this.tradingMethodInstance == null || !this.tradingMethodInstance.isReadyToExecute()) return false;
        if (this.stockData == null) return false;

        return true;
    }

    // execute the simulation
    public void execute() {
        if (!this.isReadyToExecute()) return;
        this.tradingMethodInstance.stopExecution();

        // Execute trading method async
        new Thread(() -> this.tradingMethodInstance.execute(this.stockData)).start();

        System.out.println(SaveMethodService.getSavedTradingMethods());
    }

    // halt execution of the simulation
    public void stopExecution() {
        if (this.tradingMethodInstance != null) this.tradingMethodInstance.stopExecution();
    }

    // save current method to file
    public boolean saveCurrentMethodToFile(String name) {
        if (this.tradingMethodInstance == null) return false;

        return SaveMethodService.saveTradingMethodToFile(this.tradingMethodInstance, name);
    }

    // receive new stock data
    @Override
    public void onDataChanged(StockData data) {
        this.stockData = data;
    }

    // handle stock service errors
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

package org.loudsheep.psio_project.backend.strategies;

import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.StrategyResult;

import java.util.Map;

public interface TradingStrategy {
    String getDescription();
    String getName();
    StrategyResult execute(StockData data);
    boolean isReadyToExecute();

    static String[] validateData(Map<String, Object> formData) {
        return new String[]{"Error validating - method not implemented"};
    }

    static TradingStrategy create(Map<String, Object> formData) {
        return null;
    }
}

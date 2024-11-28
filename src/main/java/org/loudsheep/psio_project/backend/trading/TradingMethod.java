package org.loudsheep.psio_project.backend.trading;

import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.StrategyResult;
import org.loudsheep.psio_project.backend.observers.StrategyResultObserver;

import java.util.Map;

public interface TradingMethod {
    String getDescription();
    String getName();
    StrategyResult execute(StockData data);
    boolean isReadyToExecute();
    void stopExecution();

    void addStrategyResultObserver(StrategyResultObserver observer);
    void removeStrategyResultObserver(StrategyResultObserver observer);
}

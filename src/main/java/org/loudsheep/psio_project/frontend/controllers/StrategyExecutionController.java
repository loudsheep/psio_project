package org.loudsheep.psio_project.frontend.controllers;

import javafx.scene.control.Label;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.services.TradingManager;
import org.loudsheep.psio_project.backend.strategies.TradingStrategy;

import java.util.Date;

public class StrategyExecutionController {
    public Label startegyNameLabel;
    public Label strategyDescriptionLabel;
    public Label stockSymbolLabel;
    public Label stockDataRangeLabel;
    public Label stockDataPointsLabel;

    public void initialize() {
        StockData data = TradingManager.getInstance().getStockData();
        TradingStrategy strategy = TradingManager.getInstance().getStrategyInstance();

        this.startegyNameLabel.setText("Strategy: " + strategy.getName());
        this.strategyDescriptionLabel.setText("Description: " + strategy.getDescription());

        this.stockSymbolLabel.setText("Stock symbol: " + data.getSymbol());
        this.stockDataPointsLabel.setText("Total data points: " + data.getDailyData().size());

        Date start = new Date(data.getFirstDataPointTimestamp() * 1000);
        Date end = new Date(data.getLastDataPointTimestamp() * 1000);
        this.stockDataRangeLabel.setText("Date range: " + start + " - " + end);
    }
}

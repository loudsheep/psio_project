package org.loudsheep.psio_project.backend.models;

import java.util.List;

public class StockData {
    private String symbol;
    private List<DayStockData> dailyData;

    // Constructor
    public StockData(String symbol, List<DayStockData> dailyData) {
        this.symbol = symbol;
        this.dailyData = dailyData;
    }

    // Getters
    public String getSymbol() { return symbol; }
    public List<DayStockData> getDailyData() { return dailyData; }

    @Override
    public String toString() {
        return "StockData{" +
                "symbol='" + symbol + '\'' +
                ", dailyData=" + dailyData +
                '}';
    }

}

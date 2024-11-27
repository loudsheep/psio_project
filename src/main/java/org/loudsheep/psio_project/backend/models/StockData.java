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

    public long getFirstDataPointTimestamp() {
        if (!this.dailyData.isEmpty()) return this.dailyData.getFirst().getTimestamp();
        return 0;
    }

    public long getLastDataPointTimestamp() {
        if (!this.dailyData.isEmpty()) return this.dailyData.getLast().getTimestamp();
        return 0;
    }

    public DayStockData getFirstDataPoint() {
        return this.dailyData.getFirst();
    }

    public DayStockData getLastDataPoint() {
        return this.dailyData.getLast();
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

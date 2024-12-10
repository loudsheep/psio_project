package org.loudsheep.psio_project.backend.models;

import java.util.List;

public record StockData(String symbol, List<DayStockData> dailyData) {
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

    @Override
    public String toString() {
        return "StockData{" +
                "symbol='" + symbol + '\'' +
                ", dailyData=" + dailyData +
                '}';
    }
}

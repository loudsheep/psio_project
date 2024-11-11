package org.loudsheep.psio_project.backend.models;

import java.util.Date;

public class DayStockData {
    private long timestamp;
    private double low;
    private double high;
    private double open;
    private double close;

    // Constructor
    public DayStockData(long timestamp, double low, double high, double open, double close) {
        this.timestamp = timestamp;
        this.low = low;
        this.high = high;
        this.open = open;
        this.close = close;
    }

    // Getters for each field
    public long getTimestamp() {
        return timestamp;
    }

    public double getLow() {
        return low;
    }

    public double getHigh() {
        return high;
    }

    public double getOpen() {
        return open;
    }

    public double getClose() {
        return close;
    }

    // Convert timestamp to Date object
    public Date getDate() {
        return new Date(timestamp * 1000); // Convert seconds to milliseconds
    }

    @Override
    public String toString() {
        return "DayStockData{" +
                "timestamp=" + timestamp +
                ", low=" + low +
                ", high=" + high +
                ", open=" + open +
                ", close=" + close +
                '}';
    }
}

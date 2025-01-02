package org.loudsheep.psio_project.backend.models;

import java.util.Date;

public class DayStockData {
    private final long timestamp;
    private final double low;
    private final double high;
    private final double open;
    private final double close;

    // constructor
    public DayStockData(long timestamp, double low, double high, double open, double close) {
        this.timestamp = timestamp;
        this.low = low;
        this.high = high;
        this.open = open;
        this.close = close;
    }

    // getters for each field
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

    // convert timestamp to Date object
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

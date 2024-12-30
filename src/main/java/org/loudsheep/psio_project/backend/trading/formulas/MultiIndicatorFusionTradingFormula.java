package org.loudsheep.psio_project.backend.trading.formulas;

import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.SimulationResult;
import org.loudsheep.psio_project.backend.observers.SimulationResultObserver;
import org.loudsheep.psio_project.backend.trading.TradingFormula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiIndicatorFusionTradingFormula implements TradingFormula {
    private static final String name = "MultiIndicator Fusion Strategy";
    private static final String description = "Combines RSI, EMA, Bollinger Bands, and MACD for complex trading decisions";
    private boolean stopExecution = false;

    private double budget;
    private final int rsiPeriod;
    private final int shortEmaPeriod;
    private final int longEmaPeriod;
    private final int bollingerPeriod;
    private final double bollingerMultiplier;
    private final SimulationResult result;

    public MultiIndicatorFusionTradingFormula(int rsiPeriod, int shortEmaPeriod, int longEmaPeriod, int bollingerPeriod, double bollingerMultiplier) {
        this.result = new SimulationResult(0);
        this.rsiPeriod = rsiPeriod;
        this.shortEmaPeriod = shortEmaPeriod;
        this.longEmaPeriod = longEmaPeriod;
        this.bollingerPeriod = bollingerPeriod;
        this.bollingerMultiplier = bollingerMultiplier;
    }

    private double calculateEMA(List<Double> prices, int period, int currentIndex) {
        if (currentIndex + 1 < period) return -1; // Not enough data for EMA

        double smoothing = 2.0 / (period + 1);
        double ema = prices.subList(currentIndex - period + 1, currentIndex + 1).stream()
                .mapToDouble(Double::doubleValue).average().orElse(0);

        for (int i = currentIndex - period + 1; i <= currentIndex; i++) {
            ema = (prices.get(i) - ema) * smoothing + ema;
        }
        return ema;
    }

    private double calculateRSI(List<Double> prices, int period, int currentIndex) {
        if (currentIndex + 1 < period) return -1; // Not enough data for RSI

        int startIndex = currentIndex - period;
        if (startIndex < 0) startIndex = 0;

        double gains = 0, losses = 0;
        for (int i = startIndex + 1; i <= currentIndex; i++) {
            double change = prices.get(i) - prices.get(i - 1);
            if (change > 0) gains += change;
            else losses -= change;
        }

        double avgGain = gains / period;
        double avgLoss = losses / period;

        if (avgLoss == 0) return 100; // No losses, RSI is 100
        double rs = avgGain / avgLoss;
        return 100 - (100 / (1 + rs));
    }

    private double[] calculateBollingerBands(List<Double> prices, int period, int currentIndex, double multiplier) {
        if (currentIndex + 1 < period) return null; // Not enough data

        double mean = prices.subList(currentIndex - period + 1, currentIndex + 1).stream()
                .mapToDouble(Double::doubleValue).average().orElse(0);

        double variance = prices.subList(currentIndex - period + 1, currentIndex + 1).stream()
                .mapToDouble(p -> Math.pow(p - mean, 2)).average().orElse(0);

        double stdDev = Math.sqrt(variance);
        return new double[]{mean + multiplier * stdDev, mean - multiplier * stdDev}; // Upper and Lower Bands
    }

    private double calculateMACD(List<Double> prices, int shortPeriod, int longPeriod, int currentIndex) {
        double shortEMA = calculateEMA(prices, shortPeriod, currentIndex);
        double longEMA = calculateEMA(prices, longPeriod, currentIndex);

        if (shortEMA == -1 || longEMA == -1) return -1; // Not enough data
        return shortEMA - longEMA;
    }

    @Override
    public void execute(StockData data, double budget) {
        this.budget = Math.max(budget, 0.0);
        this.result.resetState(this.budget);

        this.stopExecution = false;

        List<Double> prices = new ArrayList<>();
        for (DayStockData dayData : data.dailyData()) {
            prices.add(dayData.getClose());
        }

        boolean cooldown = false; // A cooldown flag to avoid rapid buy/sell cycles

        for (int i = 1; i < prices.size(); i++) {
            double price = prices.get(i);
            DayStockData dayData = data.dailyData().get(i);

            double rsi = calculateRSI(prices, rsiPeriod, i);
            double shortEma = calculateEMA(prices, shortEmaPeriod, i);
            double longEma = calculateEMA(prices, longEmaPeriod, i);
            double macd = calculateMACD(prices, shortEmaPeriod, longEmaPeriod, i);
            double[] bollingerBands = calculateBollingerBands(prices, bollingerPeriod, i, bollingerMultiplier);

            if (rsi == -1 || shortEma == -1 || longEma == -1 || macd == -1 || bollingerBands == null) {
                continue; // skip until all indicators are available
            }

            boolean buySignal = !cooldown && rsi < 35 && price < bollingerBands[1] && shortEma > longEma && macd > 0;
            boolean sellSignal = this.result.getStockOwned() > 0 &&
                    (rsi > 65 || price > bollingerBands[0] || shortEma < longEma || macd < 0);

            if (buySignal) {
                this.result.buyStock(this.result.maxStockToBuy(price), price, dayData.getTimestamp());
                System.out.println("Buy Signal detected. Buying stock at " + price);
                cooldown = true; // Trigger cooldown
            } else if (sellSignal) {
                this.result.sellAllStock(price, dayData.getTimestamp());
                System.out.println("Sell Signal detected. Selling stock at " + price);
                cooldown = false; // Reset cooldown after selling
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException _) {
            }

            if (this.stopExecution) {
                this.result.sellAllStock(price, dayData.getTimestamp());
                break;
            }
        }

        // ensure all stocks are sold at the end
        this.result.sellAllStock(data.dailyData().getLast().getClose(), data.getLastDataPointTimestamp());
    }

    @Override
    public boolean isReadyToExecute() {
        return rsiPeriod > 0 &&
                shortEmaPeriod > 0 &&
                longEmaPeriod > shortEmaPeriod &&
                bollingerPeriod > 0 &&
                bollingerMultiplier > 0;
    }

    @Override
    public void stopExecution() {
        this.stopExecution = true;
    }

    @Override
    public void addStrategyResultObserver(SimulationResultObserver observer) {
        this.result.addObserver(observer);
    }

    @Override
    public void removeStrategyResultObserver(SimulationResultObserver observer) {
        this.result.removeObserver(observer);
    }

    @Override
    public String getDescription() {
        return MultiIndicatorFusionTradingFormula.description;
    }

    @Override
    public String getName() {
        return MultiIndicatorFusionTradingFormula.name;
    }

    @Override
    public String getSignature() {
        return "MultiIndicatorFusion";
    }

    @Override
    public Map<String, Object> getMethodParams() {
        Map<String, Object> result = new java.util.HashMap<>();

        result.put("rsiPeriod", rsiPeriod);
        result.put("shortEmaPeriod", shortEmaPeriod);
        result.put("longEmaPeriod", longEmaPeriod);
        result.put("bollingerPeriod", bollingerPeriod);
        result.put("bollingerMultiplier", bollingerMultiplier);

        return result;
    }
}

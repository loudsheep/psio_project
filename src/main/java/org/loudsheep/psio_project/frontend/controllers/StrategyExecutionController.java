package org.loudsheep.psio_project.frontend.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.StrategyResult;
import org.loudsheep.psio_project.backend.models.Transaction;
import org.loudsheep.psio_project.backend.observers.StrategyResultObserver;
import org.loudsheep.psio_project.backend.services.TradingManager;
import org.loudsheep.psio_project.backend.strategies.TradingStrategy;

import java.util.Date;

public class StrategyExecutionController implements StrategyResultObserver {
    public Label startegyNameLabel;
    public Label strategyDescriptionLabel;
    public Label stockSymbolLabel;
    public Label stockDataRangeLabel;
    public Label stockDataPointsLabel;
    public Pane chartPane;

    private XYChart.Series<String, Number> series2; // Green points
    private XYChart.Series<String, Number> series3;

    public void initialize() {
        StockData data = TradingManager.getInstance().getStockData();
        TradingStrategy strategy = TradingManager.getInstance().getStrategyInstance();

        this.startegyNameLabel.setText("Strategy: " + strategy.getName());
        this.strategyDescriptionLabel.setText("Description: " + strategy.getDescription());

        this.stockSymbolLabel.setText("Stock symbol: " + data.getSymbol().toUpperCase());
        this.stockDataPointsLabel.setText("Total data points: " + data.getDailyData().size());

        Date start = new Date(data.getFirstDataPointTimestamp() * 1000);
        Date end = new Date(data.getLastDataPointTimestamp() * 1000);
        this.stockDataRangeLabel.setText("Date range: " + start + " - " + end);

        LineChart<String, Number> chart = this.createChart(data);
        chart.prefWidthProperty().bind(this.chartPane.widthProperty());
        chart.prefHeightProperty().bind(this.chartPane.heightProperty());

        this.chartPane.getChildren().add(chart);

        TradingManager.getInstance().getStrategyInstance().addStrategyResultObserver(this);
    }

    private LineChart<String, Number> createChart(StockData stockData) {
        // Axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Timestamp");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Price");

        // LineChart
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Multi Dataset Chart");
        lineChart.setVerticalZeroLineVisible(false);
        lineChart.setHorizontalZeroLineVisible(false);
        lineChart.setAnimated(false);

        // Dataset 1 (Line chart)
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Line Chart Data");

        for (DayStockData day : stockData.getDailyData()) {
            series1.getData().add(new XYChart.Data<>(day.getTimestamp() + "", day.getClose()));
        }
//        series1.getData().add(new XYChart.Data<>(1, 100));
//        series1.getData().add(new XYChart.Data<>(2, 200));
//        series1.getData().add(new XYChart.Data<>(3, 150));

        // Dataset 2 (Green points)
        series2 = new XYChart.Series<>();
        series2.setName("Green Points");
//        series2.getData().add(new XYChart.Data<>(1, 120));
//        series2.getData().add(new XYChart.Data<>(2, 220));
//        series2.getData().add(new XYChart.Data<>(3, 180));

        // Dataset 3 (Red points)
        series3 = new XYChart.Series<>();
        series3.setName("Red Points");
//        series3.getData().add(new XYChart.Data<>(1, 90));
//        series3.getData().add(new XYChart.Data<>(2, 190));
//        series3.getData().add(new XYChart.Data<>(3, 140));

        // Add series to chart
        lineChart.getData().addAll(series1, series2, series3);

        // Style series2 and series3 to show only points
        series2.getNode().setStyle("-fx-stroke: transparent;"); // No connecting lines
        series2.getData().forEach(data ->
                data.getNode().setStyle("-fx-background-color: green, white; -fx-background-radius: 5px;"));

        series3.getNode().setStyle("-fx-stroke: transparent;"); // No connecting lines
        series3.getData().forEach(data ->
                data.getNode().setStyle("-fx-background-color: red, white; -fx-background-radius: 5px;"));

        return lineChart;
    }

    @Override
    public void onStrategyResultUpdate(StrategyResult result) {

    }

    @Override
    public void onTransactionAdd(Transaction transaction) {
        System.out.println("ADDDD " +  transaction);
        Platform.runLater(() -> {
            XYChart.Series<String, Number> targetSeries = transaction.volume() < 0 ? series2 : series3;
            targetSeries.getData().add(new XYChart.Data<>(transaction.timestamp() + "", transaction.price()));
        });
    }

    public void handleTestButtonClick(ActionEvent actionEvent) {
        TradingManager.getInstance().execute();
    }
}

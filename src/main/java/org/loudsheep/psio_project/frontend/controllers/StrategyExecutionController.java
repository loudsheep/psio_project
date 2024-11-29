package org.loudsheep.psio_project.frontend.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.loudsheep.psio_project.App;
import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.StrategyResult;
import org.loudsheep.psio_project.backend.models.Transaction;
import org.loudsheep.psio_project.backend.observers.StrategyResultObserver;
import org.loudsheep.psio_project.backend.services.TradingManager;
import org.loudsheep.psio_project.backend.trading.TradingMethod;
import org.loudsheep.psio_project.frontend.SceneManager;

import java.util.Date;

public class StrategyExecutionController implements StrategyResultObserver {
    public Label startegyNameLabel;
    public Label strategyDescriptionLabel;
    public Label stockSymbolLabel;
    public Label stockDataRangeLabel;
    public Label stockDataPointsLabel;
    public Pane chartPane;
    public Label roiLabel;
    public Label transactionsLabel;
    public Label stockIncreaseLabel;

    private XYChart.Series<String, Number> series2; // Green points
    private XYChart.Series<String, Number> series3;

    public void initialize() {
        StockData data = TradingManager.getInstance().getStockData();
        TradingMethod strategy = TradingManager.getInstance().getTradingMethodInstance();

        this.startegyNameLabel.setText(strategy.getName());
        this.strategyDescriptionLabel.setText(strategy.getDescription());

        this.stockSymbolLabel.setText(data.getSymbol().toUpperCase());
        this.stockDataPointsLabel.setText(data.getDailyData().size() + "");

        Date start = new Date(data.getFirstDataPointTimestamp() * 1000);
        Date end = new Date(data.getLastDataPointTimestamp() * 1000);
        this.stockDataRangeLabel.setText(start + " - " + end);

        double increase = (double)Math.round((data.getLastDataPoint().getClose() - data.getFirstDataPoint().getClose()) / data.getFirstDataPoint().getClose() * 100 * 100)/100;
        this.stockIncreaseLabel.setText("Stock value: " + increase + "%");

        LineChart<String, Number> chart = this.createChart(data);
        chart.prefWidthProperty().bind(this.chartPane.widthProperty());
        chart.prefHeightProperty().bind(this.chartPane.heightProperty());

        this.chartPane.getChildren().add(chart);

        TradingManager.getInstance().getTradingMethodInstance().addStrategyResultObserver(this);
    }

    private LineChart<String, Number> createChart(StockData stockData) {
        // Axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        xAxis.setTickLabelsVisible(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Price");
        yAxis.setForceZeroInRange(false);

        // LineChart
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Trading Chart");
        lineChart.setAnimated(false);
        lineChart.getStylesheets().add(App.class.getResource("styles/chart-styles.css").toExternalForm());
        lineChart.setLegendVisible(false);


        // Dataset 1 (Line chart)
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Stock Price");

        for (DayStockData day : stockData.getDailyData()) {
            series1.getData().add(new XYChart.Data<>(day.getTimestamp() + "", day.getClose()));
        }

        for (XYChart.Data<String, Number> data : series1.getData()) {
            Circle symbol = new Circle(1); // Set the radius of the circle (size)
            symbol.setFill(Color.BLUE); // Set the color of the symbol
            data.setNode(symbol); // Set the custom node as the symbol
        }


        // Dataset 2 (Green points)
        series2 = new XYChart.Series<>();
        series2.setName("Buy transactions");

        // Dataset 3 (Red points)
        series3 = new XYChart.Series<>();
        series3.setName("Sell transactions");

        // Add series to chart
        lineChart.getData().addAll(series1, series2, series3);

        series2.getNode().setStyle("-fx-stroke: transparent;");
        series3.getNode().setStyle("-fx-stroke: transparent;");
        series2.getData().forEach(data ->
                data.getNode().setStyle("-fx-background-color: green, white; -fx-background-radius: 5px;"));
        series3.getData().forEach(data ->
                data.getNode().setStyle("-fx-background-color: red, white; -fx-background-radius: 5px;"));

        return lineChart;
    }

    @Override
    public void onStrategyResultUpdate(StrategyResult result) {
        double roi = (double) Math.round(result.getROI() * 100 * 1000) / 1000;

        Platform.runLater(() -> {
            this.roiLabel.setText("ROI: " + roi + "%");
            this.transactionsLabel.setText("No. of transactions: " + result.getNumberOfTransactions());
        });
    }

    @Override
    public void onTransactionAdd(Transaction transaction) {
        Platform.runLater(() -> {
            boolean isBuy = transaction.volume() < 0;

            if (isBuy) {
                XYChart.Data<String, Number> data = new XYChart.Data<>(transaction.timestamp() + "", transaction.price());
                Circle symbol = new Circle(5); // Set the radius of the circle (size)
                symbol.setFill(Color.LIGHTGREEN); // Set the color of the symbol
                data.setNode(symbol);

                this.series2.getData().add(data);
            } else {
                XYChart.Data<String, Number> data = new XYChart.Data<>(transaction.timestamp() + "", transaction.price());
                Circle symbol = new Circle(5); // Set the radius of the circle (size)
                symbol.setFill(Color.RED); // Set the color of the symbol
                data.setNode(symbol);

                this.series3.getData().add(data);
            }

        });
    }

    public void handleTestButtonClick(ActionEvent actionEvent) {
        this.series2.getData().clear();
        this.series3.getData().clear();
        TradingManager.getInstance().execute();
    }

    public void handleStopTradingButton() {
        TradingManager.getInstance().stopExecution();
    }

    public void handleBackToSelection() {
        TradingManager.getInstance().stopExecution();

        SceneManager.switchScene("views/strategy-select-view.fxml", "Select Strategy");
    }
}

package org.loudsheep.psio_project.frontend.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.loudsheep.psio_project.App;
import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.SimulationResult;
import org.loudsheep.psio_project.backend.models.Transaction;
import org.loudsheep.psio_project.backend.observers.SimulationResultObserver;
import org.loudsheep.psio_project.backend.services.TradingManager;
import org.loudsheep.psio_project.backend.trading.TradingMethod;
import org.loudsheep.psio_project.frontend.SceneManager;
import org.loudsheep.psio_project.frontend.util.Epoch;

import java.util.Date;
import java.util.Optional;

public class SimulationExecutionController implements SimulationResultObserver {
    public Label strategyNameLabel;
    public Label strategyDescriptionLabel;
    public Label stockSymbolLabel;
    public Label stockDataRangeLabel;
    public Label stockDataPointsLabel;
    public Pane chartPane;
    public Label roiLabel;
    public Label transactionsLabel;
    public Label stockIncreaseLabel;
    public Label budgetIncreaseLabel;

    private XYChart.Series<String, Number> buyTransactionSeries; // Green points
    private XYChart.Series<String, Number> sellTransactionSeries;

    public void initialize() {
        StockData data = TradingManager.getInstance().getStockData();
        TradingMethod strategy = TradingManager.getInstance().getTradingMethodInstance();

        this.strategyNameLabel.setText(strategy.getName());
        this.strategyDescriptionLabel.setText(strategy.getDescription());

        this.stockSymbolLabel.setText(data.symbol().toUpperCase());
        this.stockDataPointsLabel.setText(data.dailyData().size() + "");

        Date start = Epoch.toDate(data.getFirstDataPointTimestamp() * 1000);
        Date end = Epoch.toDate(data.getLastDataPointTimestamp() * 1000);
        this.stockDataRangeLabel.setText(start + " - " + end);

        double increase = (double)Math.round((data.getLastDataPoint().getClose() - data.getFirstDataPoint().getClose()) / data.getFirstDataPoint().getClose() * 100 * 100)/100;
        this.stockIncreaseLabel.setText("Stock increase: " + increase + "%");

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

        for (DayStockData day : stockData.dailyData()) {
            series1.getData().add(new XYChart.Data<>(day.getTimestamp() + "", day.getClose()));
        }

        for (XYChart.Data<String, Number> data : series1.getData()) {
            Circle symbol = new Circle(1); // Set the radius of the circle (size)
            symbol.setFill(Color.BLUE); // Set the color of the symbol
            data.setNode(symbol); // Set the custom node as the symbol
        }


        // Dataset 2 (Green points)
        buyTransactionSeries = new XYChart.Series<>();
        buyTransactionSeries.setName("Buy transactions");

        // Dataset 3 (Red points)
        sellTransactionSeries = new XYChart.Series<>();
        sellTransactionSeries.setName("Sell transactions");

        // Add series to chart
        lineChart.getData().addAll(series1, buyTransactionSeries, sellTransactionSeries);

        buyTransactionSeries.getNode().setStyle("-fx-stroke: transparent;");
        sellTransactionSeries.getNode().setStyle("-fx-stroke: transparent;");
        buyTransactionSeries.getData().forEach(data ->
                data.getNode().setStyle("-fx-background-color: green, white; -fx-background-radius: 5px;"));
        sellTransactionSeries.getData().forEach(data ->
                data.getNode().setStyle("-fx-background-color: red, white; -fx-background-radius: 5px;"));

        return lineChart;
    }

    @Override
    public void onStrategyResultUpdate(SimulationResult result) {
        double roi = (double) Math.round(result.getROI() * 100 * 1000) / 1000;
        double budgetIncrease = (double) Math.round((result.getCurrentBudget() - result.getInitialBudget()) / result.getInitialBudget() * 100 * 100) / 100;

        Platform.runLater(() -> {
            this.roiLabel.setText("ROI: " + roi + "%");
            this.transactionsLabel.setText("No. of transactions: " + result.getNumberOfTransactions());
            this.budgetIncreaseLabel.setText("Budget increase: " + budgetIncrease + "%");
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

                this.buyTransactionSeries.getData().add(data);
            } else {
                XYChart.Data<String, Number> data = new XYChart.Data<>(transaction.timestamp() + "", transaction.price());
                Circle symbol = new Circle(5); // Set the radius of the circle (size)
                symbol.setFill(Color.RED); // Set the color of the symbol
                data.setNode(symbol);

                this.sellTransactionSeries.getData().add(data);
            }

        });
    }

    public void handleExecuteButtonClick(ActionEvent actionEvent) {
        this.buyTransactionSeries.getData().clear();
        this.sellTransactionSeries.getData().clear();
        TradingManager.getInstance().execute();
    }

    public void handleStopTradingButton() {
        TradingManager.getInstance().stopExecution();
    }

    public void handleBackToSelection() {
        TradingManager.getInstance().stopExecution();

        SceneManager.switchScene("views/method-select-view.fxml", "Select Strategy");
    }

    public void handleMethodSave() {
        TextInputDialog dialog = new TextInputDialog("method");
        dialog.setTitle("Enter name");
        dialog.setHeaderText("Give your method a name");
        dialog.setContentText("Please enter name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (TradingManager.getInstance().saveCurrentMethodToFile(name)){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Saved successfully", ButtonType.OK);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not save", ButtonType.OK);
                alert.showAndWait();
            }
        });
    }
}

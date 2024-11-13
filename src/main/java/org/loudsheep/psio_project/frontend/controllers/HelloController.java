package org.loudsheep.psio_project.frontend.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.observers.StockDataObserver;
import org.loudsheep.psio_project.backend.services.StockService;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class HelloController implements StockDataObserver {
    private final StockService stockService = new StockService();

    @FXML
    private Label welcomeText;
    @FXML
    public LineChart lineChart;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private TextField symbol;

    public void initialize() {
        // register this controller as an observer
        stockService.addObserver(this);
    }

    private void fetchStockData(String symbol, long startTime, long endTime) {
        new Thread(() -> {
            try {
                stockService.getStockData(symbol, startTime, endTime);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    protected void onHelloButtonClick() {
        // Update label text immediately
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onGetButtonClick() {
        welcomeText.setText("Loading...");
        long startEpoch = toEpochSeconds(startDate.getValue().getYear(), startDate.getValue().getMonthValue(), startDate.getValue().getDayOfMonth());
        long endEpoch = toEpochSeconds(endDate.getValue().getYear(), endDate.getValue().getMonthValue(), endDate.getValue().getDayOfMonth());

        fetchStockData(symbol.getText(), startEpoch, endEpoch);
    }

    @Override
    public void onDataChanged(StockData data) {
        // This gets executed in another thread so use Platform.runLater
        Platform.runLater(() -> {
            XYChart.Series series = new XYChart.Series();
            series.setName(symbol.getText() + " chart");

            for (DayStockData day : data.getDailyData()) {
                series.getData().add(new XYChart.Data<>(day.getTimestamp() + "", day.getClose()));
            }

            lineChart.getData().clear();
            lineChart.getData().add(series);

            welcomeText.setText("Chart loaded!");
        });
    }

    public static long toEpochSeconds(int year, int month, int day) {
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, 0, 0);
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }
}

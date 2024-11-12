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
import org.loudsheep.psio_project.backend.services.StockService;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class HelloController {
    public static long toEpochSeconds(int year, int month, int day) {
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, 0, 0);
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }

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

    @FXML
    protected void onHelloButtonClick() {
        // Update label text immediately
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onGetButtonClick() {
        long startEpoch = toEpochSeconds(startDate.getValue().getYear(), startDate.getValue().getMonthValue(), startDate.getValue().getDayOfMonth());
        long endEpoch = toEpochSeconds(endDate.getValue().getYear(), endDate.getValue().getMonthValue(), endDate.getValue().getDayOfMonth());

        // Create a background task for StockService call
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                StockService s = new StockService();
                try {
                    StockData stockData = s.getStockData("IBM", startEpoch, endEpoch);

//                    System.out.println("Stock data size: " + stockData.getDailyData().size());
//                    System.out.println(stockData);
                    XYChart.Series series = new XYChart.Series();
                    series.setName(symbol.getText() + " chart");

                    for (DayStockData day : stockData.getDailyData()) {
                        series.getData().add(new XYChart.Data<>(day.getTimestamp() + "", day.getClose()));
                    }

                    Platform.runLater(() -> lineChart.getData().add(series));
                } catch (IOException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> welcomeText.setText("Failed to retrieve stock data."));
                }
                return null;
            }
        };

        // Start the background task on a separate thread
        Thread thread = new Thread(task);
        thread.setDaemon(true); // Ensure the thread exits when the application closes
        thread.start();
    }
}

package org.loudsheep.psio_project.frontend.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    protected void onHelloButtonClick() {
        // Update label text immediately
        welcomeText.setText("Welcome to JavaFX Application!");

        // Create a background task for StockService call
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                StockService s = new StockService();
                try {

                    // Run StockService and print result to console
                    long startTime = toEpochSeconds(2024, 2, 1);
                    long endTime = toEpochSeconds(2024, 10, 1);

                    System.out.println(startTime);
                    System.out.println(endTime);

                    StockData stockData = s.getStockData("IBM", startTime, endTime);
                    System.out.println("Stock data size: " + stockData.getDailyData().size());
                    System.out.println(stockData);

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

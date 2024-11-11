package org.loudsheep.psio_project.frontend.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.loudsheep.psio_project.backend.services.StockService;

import java.io.IOException;

public class HelloController {
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
                    String stockData = s.getStockData("IBM").toString();
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

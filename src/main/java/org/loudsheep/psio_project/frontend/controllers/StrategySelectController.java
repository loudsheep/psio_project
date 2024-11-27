package org.loudsheep.psio_project.frontend.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.loudsheep.psio_project.App;
import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.observers.StockDataObserver;
import org.loudsheep.psio_project.backend.services.TradingManager;
import org.loudsheep.psio_project.frontend.SceneManager;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

public class StrategySelectController implements StockDataObserver {
    public MenuItem simpleUDStrategyButton;
    public VBox parametersVBox;
    public MenuButton strategyMenuButton;
    public Pane formPane;

    // Stock data fields
    public TextField symbolField;
    public DatePicker startDateField;
    public DatePicker endDateField;
    public Label errorLabel;
    public LineChart lineChart;
    public MenuItem randomStrategyButton;

    // Handles getting stock data
    public void initialize() {
        TradingManager.getInstance().addStockDataObserver(this);
        this.errorLabel.setText("");
    }

    // Handles Simple Up & Down Strategy selection
    @FXML
    private void handleSimpleStrategy() {
        loadStrategyForm("views/forms/simple-strategy-form.fxml", Map.of());
        strategyMenuButton.setText(simpleUDStrategyButton.getText());
    }

    public void handleRandomStrategy() {
        loadStrategyForm("views/forms/random-strategy-form.fxml", Map.of());
        strategyMenuButton.setText(randomStrategyButton.getText());
    }

    private void loadStrategyForm(String fxmlPath, Map<String, Object> initParams) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            Node formNode = loader.load();

            // Pass initialization parameters to the form controller
            FormControllerInterface formController = loader.getController();
            formController.setParams(initParams);
            formController.setSubmitCallback(this::handleFormSubmit);

            parametersVBox.getChildren().clear();
            parametersVBox.getChildren().add(formNode);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Exception while loading data", ButtonType.OK);
            alert.showAndWait();

            e.printStackTrace();
        }
    }

    private void handleFormSubmit(Map<String, Object> formData) {
        // Handle the submitted form data
        System.out.println("Form submitted with data: " + formData);
        parametersVBox.getChildren().clear();
    }

    public void handleStockData() {
        long startEpoch = toEpochSeconds(startDateField.getValue().getYear(), startDateField.getValue().getMonthValue(), startDateField.getValue().getDayOfMonth());
        long endEpoch = toEpochSeconds(endDateField.getValue().getYear(), endDateField.getValue().getMonthValue(), endDateField.getValue().getDayOfMonth());

        TradingManager.getInstance().setStockData(symbolField.getText(), startEpoch, endEpoch);
        this.errorLabel.setText("");
    }

    public static long toEpochSeconds(int year, int month, int day) {
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, 0, 0);
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public void onDataChanged(StockData data) {
        Platform.runLater(() -> {
            XYChart.Series series = new XYChart.Series();
            series.setName(symbolField.getText() + " chart");

            for (DayStockData day : data.getDailyData()) {
                series.getData().add(new XYChart.Data<>(day.getTimestamp() + "", day.getClose()));
            }

            lineChart.getData().clear();
            lineChart.getData().add(series);
        });
    }

    @Override
    public void setError(String error) {
        Platform.runLater(() -> {
            this.errorLabel.setText(error);
        });
    }

    public void handleExecuteButtonClick() {
        if (!TradingManager.getInstance().isReadyToExecute()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Not enough data to execute the strategy", ButtonType.OK);
            alert.showAndWait();
        } else {
            SceneManager.switchScene("views/strategy-execution-view.fxml", "Execute Strategy");
        }
    }
}

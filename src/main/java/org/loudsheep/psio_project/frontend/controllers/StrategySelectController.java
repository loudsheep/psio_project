package org.loudsheep.psio_project.frontend.controllers;

import javafx.application.Platform;
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
import org.loudsheep.psio_project.backend.services.SaveMethodService;
import org.loudsheep.psio_project.backend.services.TradingManager;
import org.loudsheep.psio_project.backend.trading.TradingMethod;
import org.loudsheep.psio_project.frontend.SceneManager;

import java.time.*;
import java.util.List;
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
    public VBox savedVbox;

    // Handles getting stock data
    public void initialize() {
        TradingManager.getInstance().addStockDataObserver(this);
        StockData data = TradingManager.getInstance().getStockData();
        if (data != null) {
            this.onDataChanged(data);
            this.symbolField.setText(data.getSymbol().toUpperCase());


            this.startDateField.setValue(Instant.ofEpochSecond(data.getFirstDataPointTimestamp()).atZone(ZoneId.systemDefault()).toLocalDate());
            this.endDateField.setValue(Instant.ofEpochSecond(data.getLastDataPointTimestamp()).atZone(ZoneId.systemDefault()).toLocalDate());
        }

        this.showMethodParams();
        this.showSavedMethods();

        this.errorLabel.setText("");
    }

    private void showMethodParams() {
        TradingMethod method = TradingManager.getInstance().getTradingMethodInstance();
        if (method != null) {
            this.strategyMenuButton.setText(method.getName());

            Label tmp = new Label();
            tmp.setWrapText(true);

            String text = "";
            for (Map.Entry<String, Object> set : method.getMethodParams().entrySet()) {
                text += set.getKey() + ": " + set.getValue() + "\n";
            }
            tmp.setText(text);
            parametersVBox.getChildren().clear();
            parametersVBox.getChildren().add(tmp);
        }
    }

    private void showSavedMethods() {
        List<Map<String, Object>> methods = SaveMethodService.getSavedTradingMethods();

        this.savedVbox.getChildren().clear();
        for (Map<String, Object> method: methods) {
            VBox box = new VBox();

//            Label header = new Label(method.get("name").toString());
//            Label strategy = new Label(method.get("strategyName").toString());
            Button loadBtn = new Button("Load");
            String text = "";
            for (Map.Entry<String, Object> set : method.entrySet()) {
                text += set.getKey() + ": " + set.getValue() + "\n";
            }

            Label label = new Label(text);
            box.getChildren().addAll(label, loadBtn);

            this.savedVbox.getChildren().add(box);
        }
    }

    // Handles Simple Up & Down Strategy selection
    @FXML
    private void handleSimpleMethod() {
        loadStrategyForm("views/forms/simple-strategy-form.fxml", Map.of());
        strategyMenuButton.setText(simpleUDStrategyButton.getText());
    }

    public void handleRandomMethod() {
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
        showMethodParams();
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

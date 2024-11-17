package org.loudsheep.psio_project.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.loudsheep.psio_project.backend.services.TradingManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class SimpleStrategyFormController implements FormControllerInterface, FormErrorCallback {

    public VBox VBoxPane;
    public TextField daysField;
    public TextField budgetField;
    private Label errorLabel;

    private FormSubmitCallback submitCallback;

    @FXML
    private void initialize() {
        // Restrict input to integers using a TextFormatter
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?\\d*")) { // Allow digits and an optional leading "-"
                return change;
            }
            return null; // Reject the change
        };
        daysField.setTextFormatter(new TextFormatter<>(integerFilter));
        budgetField.setTextFormatter(new TextFormatter<>(integerFilter));
    }

    @Override
    public void setParams(Map<String, Object> params) {
        // Initialize form fields using params if necessary
    }

    @Override
    public void setSubmitCallback(FormSubmitCallback callback) {
        this.submitCallback = callback;
    }

    @Override
    public void setError(String text) {
        if (Objects.equals(text, "") && this.errorLabel != null) {
            this.VBoxPane.getChildren().remove(errorLabel);
            this.errorLabel = null;
            return;
        }

        if (this.errorLabel == null) {
            this.errorLabel = new Label(text);
            this.errorLabel.setTextFill(Color.RED);
            this.errorLabel.setWrapText(true);
            this.VBoxPane.getChildren().add(4, this.errorLabel);
        } else {
            this.errorLabel.setText(text);
        }
    }

    int x= 0;
    @FXML
    private void handleSubmit() throws Exception {
        if (submitCallback != null) {
            Map<String, Object> formData = new HashMap<>();
            formData.put("daysBackToCheck", Integer.parseInt(daysField.getText()));
            formData.put("budget", Double.parseDouble(budgetField.getText()));

            String[] errors = TradingManager.getInstance().setStrategy("SimpleUpAndDown", formData);
            if (errors.length > 0) this.setError(errors[0]);

            submitCallback.onSubmit(formData);
        }
    }
}

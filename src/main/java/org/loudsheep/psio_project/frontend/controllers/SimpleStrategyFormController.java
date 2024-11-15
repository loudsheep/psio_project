package org.loudsheep.psio_project.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class SimpleStrategyFormController implements FormController {

    public VBox VBoxPane;
    public TextField daysField;
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
            return;
        }

        if (this.errorLabel == null) {
            this.errorLabel = new Label(text);
            this.VBoxPane.getChildren().add(2, this.errorLabel);
        } else {
            this.errorLabel.setText(text);
        }
    }

    @FXML
    private void handleSubmit() {
        if (submitCallback != null) {
            Map<String, Object> formData = new HashMap<>();
            formData.put("daysField", daysField.getText());
            submitCallback.onSubmit(formData);
        }
    }
}

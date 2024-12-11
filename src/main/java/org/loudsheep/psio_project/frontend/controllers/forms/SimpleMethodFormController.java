package org.loudsheep.psio_project.frontend.controllers.forms;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.loudsheep.psio_project.TradingController;
import org.loudsheep.psio_project.frontend.controllers.FormControllerInterface;
import org.loudsheep.psio_project.frontend.interfaces.FormErrorCallback;
import org.loudsheep.psio_project.frontend.interfaces.FormSubmitCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class SimpleMethodFormController implements FormControllerInterface, FormErrorCallback {

    public VBox VBoxPane;
    public TextField daysField;
    public TextField budgetField;
    private Label errorLabel;

    private FormSubmitCallback submitCallback;

    @FXML
    private void initialize() {
        // restrict input to integers using a TextFormatter
        UnaryOperator<TextFormatter.Change> integerFilter = change -> change.getControlNewText().matches("-?\\d*") ? change : null;
        UnaryOperator<TextFormatter.Change> doubleFilter = change -> change.getControlNewText().matches("-?\\d*(\\.\\d*)?") ? change : null;

        daysField.setTextFormatter(new TextFormatter<>(integerFilter));
        budgetField.setTextFormatter(new TextFormatter<>(doubleFilter));
    }

    @Override
    public void setParams(Map<String, Object> params) {
        if (params.containsKey("budget")) {
            this.budgetField.setText(params.get("budget").toString());
        }
        if (params.containsKey("daysBackToCheck")) {
            this.daysField.setText(params.get("daysBackToCheck").toString());
        }
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

    @FXML
    private void handleSubmit() throws Exception {
        if (submitCallback != null) {
            Map<String, Object> formData = new HashMap<>();
            formData.put("daysBackToCheck", Integer.parseInt(daysField.getText()));
            formData.put("budget", Double.parseDouble(budgetField.getText()));

            String[] errors = TradingController.getInstance().setMethod("SimpleUpAndDown", formData);
            if (errors.length > 0) this.setError(errors[0]);

            submitCallback.onSubmit(formData);
        }
    }
}

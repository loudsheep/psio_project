package org.loudsheep.psio_project.frontend.controllers.forms;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.loudsheep.psio_project.backend.services.TradingManager;
import org.loudsheep.psio_project.frontend.controllers.FormControllerInterface;
import org.loudsheep.psio_project.frontend.interfaces.FormErrorCallback;
import org.loudsheep.psio_project.frontend.interfaces.FormSubmitCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class RandomMethodFormController implements FormControllerInterface, FormErrorCallback {

    public VBox VBoxPane;
    public TextField budgetField;
    private Label errorLabel;

    private FormSubmitCallback submitCallback;

    @FXML
    private void initialize() {
        // restrict input to integers using a TextFormatter
        UnaryOperator<TextFormatter.Change> doubleFilter = change -> change.getControlNewText().matches("-?\\d*(\\.\\d*)?") ? change : null;

        budgetField.setTextFormatter(new TextFormatter<>(doubleFilter));
    }

    @Override
    public void setParams(Map<String, Object> params) {
        if (params.containsKey("budget")) {
            this.budgetField.setText(params.get("budget").toString());
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
            formData.put("budget", Double.parseDouble(budgetField.getText()));

            String[] errors = TradingManager.getInstance().setMethod("Random", formData);
            if (errors.length > 0) this.setError(errors[0]);

            submitCallback.onSubmit(formData);
        }
    }
}

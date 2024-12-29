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

public class MultiIndicatorFusionFormulaFormController implements FormControllerInterface, FormErrorCallback {

    public VBox VBoxPane;
    public TextField rsiPeriodField;
    public TextField shortEmaField;
    public TextField longEmaField;
    public TextField bollingerPeriodField;
    public TextField bollingerMultiplierField;
    private Label errorLabel;

    private FormSubmitCallback submitCallback;

    @FXML
    private void initialize() {
        // restrict input to integers for periods and doubles for budget/multiplier
        UnaryOperator<TextFormatter.Change> integerFilter = change -> change.getControlNewText().matches("-?\\d*") ? change : null;
        UnaryOperator<TextFormatter.Change> doubleFilter = change -> change.getControlNewText().matches("-?\\d*(\\.\\d*)?") ? change : null;

        rsiPeriodField.setTextFormatter(new TextFormatter<>(integerFilter));
        shortEmaField.setTextFormatter(new TextFormatter<>(integerFilter));
        longEmaField.setTextFormatter(new TextFormatter<>(integerFilter));
        bollingerPeriodField.setTextFormatter(new TextFormatter<>(integerFilter));
        bollingerMultiplierField.setTextFormatter(new TextFormatter<>(doubleFilter));
    }

    @Override
    public void setParams(Map<String, Object> params) {
        if (params.containsKey("rsiPeriod")) {
            this.rsiPeriodField.setText(params.get("rsiPeriod").toString());
        }
        if (params.containsKey("shortEmaPeriod")) {
            this.shortEmaField.setText(params.get("shortEmaPeriod").toString());
        }
        if (params.containsKey("longEmaPeriod")) {
            this.longEmaField.setText(params.get("longEmaPeriod").toString());
        }
        if (params.containsKey("bollingerPeriod")) {
            this.bollingerPeriodField.setText(params.get("bollingerPeriod").toString());
        }
        if (params.containsKey("bollingerMultiplier")) {
            this.bollingerMultiplierField.setText(params.get("bollingerMultiplier").toString());
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
            this.VBoxPane.getChildren().add(this.VBoxPane.getChildren().size() - 1, this.errorLabel);
        } else {
            this.errorLabel.setText(text);
        }
    }

    @FXML
    private void handleSubmit() throws Exception {
        if (submitCallback != null) {
            Map<String, Object> formData = new HashMap<>();
            formData.put("rsiPeriod", Integer.parseInt(rsiPeriodField.getText()));
            formData.put("shortEmaPeriod", Integer.parseInt(shortEmaField.getText()));
            formData.put("longEmaPeriod", Integer.parseInt(longEmaField.getText()));
            formData.put("bollingerPeriod", Integer.parseInt(bollingerPeriodField.getText()));
            formData.put("bollingerMultiplier", Double.parseDouble(bollingerMultiplierField.getText()));

            String[] errors = TradingController.getInstance().setMethod("MultiIndicatorFusion", formData);
            if (errors.length > 0) {
                this.setError(errors[0]);
                return;
            }

            submitCallback.onSubmit(formData);
        }
    }
}

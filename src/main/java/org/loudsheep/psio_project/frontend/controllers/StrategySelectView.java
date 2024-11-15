package org.loudsheep.psio_project.frontend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.loudsheep.psio_project.App;

import java.io.IOException;
import java.util.Map;

public class StrategySelectView {
    public MenuItem simpleUDStrategyButton;
    public VBox parametersVBox;
    public MenuButton strategyMenuButton;
    public Pane formPane;

    // Handles Simple Up & Down Strategy selection
    @FXML
    private void handleSimpleStrategy() {
        loadStrategyForm("views/forms/simple-strategy-form.fxml", Map.of());
        strategyMenuButton.setText(simpleUDStrategyButton.getText());
    }

    private void loadStrategyForm(String fxmlPath, Map<String, Object> initParams) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            Node formNode = loader.load();

            // Pass initialization parameters to the form controller
            FormController formController = loader.getController();
            formController.setParams(initParams);
            formController.setSubmitCallback(this::handleFormSubmit);

            parametersVBox.getChildren().clear();
            parametersVBox.getChildren().add(formNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleFormSubmit(Map<String, Object> formData) {
        // Handle the submitted form data
        System.out.println("Form submitted with data: " + formData);
    }
}

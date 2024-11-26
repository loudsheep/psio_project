package org.loudsheep.psio_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.loudsheep.psio_project.frontend.SceneManager;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.setStage(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/strategy-select-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Stock Strategy Simulation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
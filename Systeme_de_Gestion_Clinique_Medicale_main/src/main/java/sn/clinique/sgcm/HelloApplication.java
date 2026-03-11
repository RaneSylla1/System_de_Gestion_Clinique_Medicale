package sn.clinique.sgcm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sn.clinique.sgcm.util.configs.SceneManager;
import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.setPrimaryStage(stage);


        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/sn/clinique/sgcm/hello-view.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setTitle("SGCM — Connexion");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
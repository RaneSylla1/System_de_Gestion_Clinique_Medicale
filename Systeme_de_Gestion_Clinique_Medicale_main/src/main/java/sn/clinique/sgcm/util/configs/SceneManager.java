package sn.clinique.sgcm.util.configs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.layout.VBox;
import java.util.Objects;

public class SceneManager {

    private static Stage primaryStage;
    private static VBox contentArea;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void setContentArea(javafx.scene.layout.VBox area) {
        contentArea = area;
    }

    // Changer de scène complète (expmle Login → Dashboard)
    public static void switchTo(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(
                            "/sn/clinique/sgcm/" + fxmlFile
                    )
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Charger une vue dans la zone centrale du dashboard
    public static void loadInto(String fxmlFile) {
        if (contentArea == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(
                            "/sn/clinique/sgcm/" + fxmlFile
                    )
            );
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
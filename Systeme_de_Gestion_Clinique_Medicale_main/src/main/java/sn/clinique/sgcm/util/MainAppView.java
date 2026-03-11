package sn.clinique.sgcm.util;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sn.clinique.sgcm.model.Patient;
import sn.clinique.sgcm.service.PatientService;

public class MainAppView {

    private PatientService patientService = new PatientService();

    public void start(Stage stage) {
        stage.setTitle("SGCM - Enregistrement Patient");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));

        TextField nomField = new TextField();
        nomField.setPromptText("Nom complet");
        TextField telField = new TextField();
        telField.setPromptText("Téléphone");

        Button addButton = new Button("Enregistrer en Base");
        addButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        // == ACTION DU BOUTON ====
        addButton.setOnAction(e -> {
            try {
                //  Créer l'objet Patient
                Patient p = new Patient();
                p.setNom(nomField.getText()); // Vérifie les noms des setters dans ta classe Patient
                p.setTelephone(telField.getText());

                //  Appeler le service pour sauvegarder
                patientService.save(p);

                //  Feedback
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Patient enregistré avec succès !");
                alert.show();

                nomField.clear();
                telField.clear();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur : " + ex.getMessage());
                alert.show();
            }
        });

        form.getChildren().addAll(new Label("Nouveau Patient"), nomField, telField, addButton);
        Scene scene = new Scene(form, 400, 300);
        stage.setScene(scene);
        stage.show();
    }
}
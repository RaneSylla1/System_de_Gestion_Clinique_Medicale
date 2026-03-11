package sn.clinique.sgcm.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sn.clinique.sgcm.model.Patient;
import sn.clinique.sgcm.service.PatientService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PatientController {

    @FXML private TextField searchField;
    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, Long>   colId;
    @FXML private TableColumn<Patient, String> colNom;
    @FXML private TableColumn<Patient, String> colPrenom;
    @FXML private TableColumn<Patient, String> colSexe;
    @FXML private TableColumn<Patient, String> colTel;
    @FXML private TableColumn<Patient, String> colSanguin;
    @FXML private TableColumn<Patient, Void>   colActions;
    @FXML private Label totalLabel;

    private final PatientService patientService = new PatientService();

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerPatients();
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colNom.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNom()));

        colPrenom.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPrenom()));

        colSexe.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getSexe() != null ?
                                data.getValue().getSexe().name() : "—"));

        colTel.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getTelephone()));

        colSanguin.setCellValueFactory(data -> {
            Patient p = data.getValue();
            String gs = p.getGroupeSanguin() != null ? p.getGroupeSanguin().name() : "—";
            String rh = p.getRehsus() != null ?
                    (p.getRehsus().name().equals("POSITIF") ? "+" : "-") : "";
            return new javafx.beans.property.SimpleStringProperty(gs + rh);
        });

        //  Colonne Actions avec boutons Modifier / Supprimer
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("✏");
            private final Button btnDelete = new Button("🗑");
            private final HBox   box       = new HBox(8, btnEdit, btnDelete);

            {
                btnEdit.setStyle(
                        "-fx-background-color: #0f3460; -fx-text-fill: white;" +
                                "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 4 10 4 10;");
                btnDelete.setStyle(
                        "-fx-background-color: #e94560; -fx-text-fill: white;" +
                                "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 4 10 4 10;");

                btnEdit.setOnAction(e -> {
                    Patient p = getTableView().getItems().get(getIndex());
                    ouvrirFormulaireModification(p);
                });

                btnDelete.setOnAction(e -> {
                    Patient p = getTableView().getItems().get(getIndex());
                    confirmerSuppression(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    @FXML
    public void chargerPatients() {
        List<Patient> patients = patientService.findAll();
        patientTable.setItems(FXCollections.observableArrayList(patients));
        totalLabel.setText("Total : " + patients.size() + " patient(s)");
    }

    @FXML
    public void rechercher() {
        String kw = searchField.getText().trim();
        List<Patient> resultats = patientService.rechercher(kw);
        patientTable.setItems(FXCollections.observableArrayList(resultats));
        totalLabel.setText("Résultats : " + resultats.size() + " patient(s)");
    }

    @FXML
    public void ouvrirFormulaire() {
        ouvrirFenetre(null);
    }

    private void ouvrirFormulaireModification(Patient patient) {
        ouvrirFenetre(patient);
    }

    private void ouvrirFenetre(Patient patient) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sn/clinique/sgcm/patient-form.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(patient == null ? "Nouveau Patient" : "Modifier Patient");
            stage.initModality(Modality.APPLICATION_MODAL);

            PatientFormController ctrl = loader.getController();
            ctrl.setStage(stage);
            ctrl.setOnSaved(this::chargerPatients);
            if (patient != null) ctrl.setPatient(patient);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void confirmerSuppression(Patient patient) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le patient ?");
        alert.setContentText(
                "Voulez-vous supprimer  " + patient.getNomComplet() + " ????"
        );
        // ==Style dark====
        alert.getDialogPane().setStyle("-fx-background-color: #16213e;");
        alert.getDialogPane().lookup(".content.label")
                .setStyle("-fx-text-fill: white;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                patientService.delete(patient.getId());
                chargerPatients();
            } catch (Exception e) {
                showError("Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
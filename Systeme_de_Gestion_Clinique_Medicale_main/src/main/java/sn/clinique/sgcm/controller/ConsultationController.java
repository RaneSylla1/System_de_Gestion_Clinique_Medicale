package sn.clinique.sgcm.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sn.clinique.sgcm.model.Consultation;
import sn.clinique.sgcm.service.ConsultationService;
import sn.clinique.sgcm.service.PatientService;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsultationController {

    @FXML private TextField searchField;
    @FXML private TableView<Consultation> consultationTable;
    @FXML private TableColumn<Consultation, String> colDate;
    @FXML private TableColumn<Consultation, String> colPatient;
    @FXML private TableColumn<Consultation, String> colMedecin;
    @FXML private TableColumn<Consultation, String> colDiagnostic;
    @FXML private TableColumn<Consultation, String> colPrix;
    @FXML private TableColumn<Consultation, String> colFacture;
    @FXML private TableColumn<Consultation, Void>   colActions;
    @FXML private Label totalLabel;

    private final ConsultationService consultationService = new ConsultationService();
    private final PatientService      patientService      = new PatientService();
    private final DateTimeFormatter   dateFmt =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerConsultations();
    }

    private void configurerColonnes() {
        colDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDateConsultation() != null ?
                                data.getValue().getDateConsultation().format(dateFmt) : "—"));

        colPatient.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPatient().getNomComplet()));

        colMedecin.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getMedecin().getNomComplet()));

        colDiagnostic.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDiagnostic()));

        colPrix.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("%,.0f FCFA", data.getValue().getPrix())));

        // ✅ Colonne facture — indique si facture générée
        colFacture.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null ||
                        getTableRow().getItem() == null) {
                    setText(null); setStyle(""); return;
                }
                Consultation c = (Consultation) getTableRow().getItem();
                boolean aFacture = c.getFacture() != null;
                setText(aFacture ? " Générée" : " En attente");
                setStyle("-fx-text-fill: " + (aFacture ? "#4caf50" : "#ff9800") + ";");
            }
        });
        colFacture.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(""));

        //  Actions : Voir détail + Générer facture
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnVoir    = new Button("👁");
            private final Button btnFacture = new Button("🧾");
            private final HBox   box        = new HBox(6, btnVoir, btnFacture);

            {
                btnVoir.setStyle(
                        "-fx-background-color: #0f3460; -fx-text-fill: white;" +
                                "-fx-background-radius: 6; -fx-cursor: hand;" +
                                "-fx-padding: 4 10 4 10;");
                btnFacture.setStyle(
                        "-fx-background-color: #4caf50; -fx-text-fill: white;" +
                                "-fx-background-radius: 6; -fx-cursor: hand;" +
                                "-fx-padding: 4 10 4 10;");

                btnVoir.setOnAction(e -> {
                    Consultation c = getTableView().getItems().get(getIndex());
                    ouvrirDetail(c);
                });

                btnFacture.setOnAction(e -> {
                    Consultation c = getTableView().getItems().get(getIndex());
                    ouvrirFormulaireFacture(c);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Consultation c = getTableView().getItems().get(getIndex());
                // Désactiver bouton facture si déjà générée
                btnFacture.setDisable(c.getFacture() != null);
                setGraphic(box);
            }
        });
    }

    @FXML
    public void chargerConsultations() {
        List<Consultation> consultations = consultationService.findAll();
        consultationTable.setItems(FXCollections.observableArrayList(consultations));
        totalLabel.setText("Total : " + consultations.size() + " consultation(s)");
    }

    @FXML
    public void rechercher() {
        String kw = searchField.getText().trim().toLowerCase();
        List<Consultation> filtrees = consultationService.findAll().stream()
                .filter(c -> c.getPatient().getNomComplet().toLowerCase().contains(kw))
                .toList();
        consultationTable.setItems(FXCollections.observableArrayList(filtrees));
        totalLabel.setText("Résultats : " + filtrees.size() + " consultation(s)");
    }

    @FXML
    public void ouvrirFormulaire() {
        ouvrirFenetre(null);
    }

    private void ouvrirFenetre(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sn/clinique/sgcm/consultation-form.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Nouvelle Consultation");
            stage.initModality(Modality.APPLICATION_MODAL);

            ConsultationFormController ctrl = loader.getController();
            ctrl.setStage(stage);
            ctrl.setOnSaved(this::chargerConsultations);
            if (consultation != null) ctrl.setConsultation(consultation);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ouvrirDetail(Consultation c) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détail Consultation");
        alert.setHeaderText("Consultation du " +
                (c.getDateConsultation() != null ?
                        c.getDateConsultation().format(dateFmt) : "—"));
        alert.setContentText(
                "Patient     : " + c.getPatient().getNomComplet() + "\n" +
                        "Médecin     : " + c.getMedecin().getNomComplet() + "\n\n" +
                        "Diagnostic  : " + c.getDiagnostic() + "\n\n" +
                        "Observation : " + (c.getObservation() != null ?
                        c.getObservation() : "—") + "\n\n" +
                        "Prescription: " + (c.getPrescription() != null ?
                        c.getPrescription() : "—") + "\n\n" +
                        "Prix        : " + String.format("%,.0f FCFA", c.getPrix())
        );
        alert.getDialogPane().setStyle("-fx-background-color: #16213e;");
        alert.getDialogPane().lookup(".content.label")
                .setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        alert.showAndWait();
    }

    private void ouvrirFormulaireFacture(Consultation c) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sn/clinique/sgcm/facture-form.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Générer une Facture");
            stage.initModality(Modality.APPLICATION_MODAL);

            FactureFormController ctrl = loader.getController();
            ctrl.setStage(stage);
            ctrl.setConsultation(c);
            ctrl.setOnSaved(this::chargerConsultations);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
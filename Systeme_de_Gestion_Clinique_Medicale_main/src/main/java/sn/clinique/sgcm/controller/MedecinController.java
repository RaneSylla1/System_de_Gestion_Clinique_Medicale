package sn.clinique.sgcm.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sn.clinique.sgcm.model.Medecin;
import sn.clinique.sgcm.service.MedecinService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MedecinController {

    @FXML private TextField searchField;
    @FXML private TableView<Medecin> medecinTable;
    @FXML private TableColumn<Medecin, String> colMatricule;
    @FXML private TableColumn<Medecin, String> colNom;
    @FXML private TableColumn<Medecin, String> colPrenom;
    @FXML private TableColumn<Medecin, String> colSpecialite;
    @FXML private TableColumn<Medecin, String> colTel;
    @FXML private TableColumn<Medecin, String> colUsername;
    @FXML private TableColumn<Medecin, Void>   colActions;
    @FXML private Label totalLabel;

    private final MedecinService medecinService = new MedecinService();

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerMedecins();
    }

    private void configurerColonnes() {
        colMatricule.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getMatricule()));
        colNom.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNom()));
        colPrenom.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPrenom()));
        colSpecialite.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getSpecialite()));
        colTel.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getTelephone() != null ?
                                data.getValue().getTelephone() : "—"));
        colUsername.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getUsername()));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("✏");
            private final Button btnDelete = new Button("🗑");
            private final HBox   box       = new HBox(8, btnEdit, btnDelete);
            {
                btnEdit.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white;" +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 4 10 4 10;");
                btnDelete.setStyle("-fx-background-color: #e94560; -fx-text-fill: white;" +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 4 10 4 10;");
                btnEdit.setOnAction(e -> ouvrirFenetre(
                        getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> confirmerSuppression(
                        getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    @FXML
    public void chargerMedecins() {
        List<Medecin> medecins = medecinService.findAll();
        medecinTable.setItems(FXCollections.observableArrayList(medecins));
        totalLabel.setText("Total : " + medecins.size() + " médecin(s)");
    }

    @FXML
    public void rechercher() {
        String kw = searchField.getText().trim().toLowerCase();
        List<Medecin> filtres = medecinService.findAll().stream()
                .filter(m -> m.getNomComplet().toLowerCase().contains(kw) ||
                        m.getSpecialite().toLowerCase().contains(kw))
                .toList();
        medecinTable.setItems(FXCollections.observableArrayList(filtres));
        totalLabel.setText("Résultats : " + filtres.size() + " médecin(s)");
    }

    @FXML
    public void ouvrirFormulaire() { ouvrirFenetre(null); }

    private void ouvrirFenetre(Medecin medecin) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sn/clinique/sgcm/medecin-form.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(medecin == null ? "Nouveau Médecin" : "Modifier Médecin");
            stage.initModality(Modality.APPLICATION_MODAL);

            MedecinFormController ctrl = loader.getController();
            ctrl.setStage(stage);
            ctrl.setOnSaved(this::chargerMedecins);
            if (medecin != null) ctrl.setMedecin(medecin);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void confirmerSuppression(Medecin medecin) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer Dr. " + medecin.getNomComplet() + " ?");
        alert.getDialogPane().setStyle("-fx-background-color: #16213e;");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            medecinService.delete(medecin.getId());
            chargerMedecins();
        }
    }
}
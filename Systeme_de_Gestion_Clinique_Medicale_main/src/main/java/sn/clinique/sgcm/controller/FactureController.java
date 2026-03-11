package sn.clinique.sgcm.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sn.clinique.sgcm.enums.StatutFacture;
import sn.clinique.sgcm.model.Facture;
import sn.clinique.sgcm.service.FactureService;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class FactureController {

    @FXML private TextField              searchField;
    @FXML private ComboBox<StatutFacture> filtreStatut;
    @FXML private TableView<Facture>     factureTable;
    @FXML private TableColumn<Facture, String> colNumero;
    @FXML private TableColumn<Facture, String> colDate;
    @FXML private TableColumn<Facture, String> colPatient;
    @FXML private TableColumn<Facture, String> colMontant;
    @FXML private TableColumn<Facture, String> colMode;
    @FXML private TableColumn<Facture, String> colStatut;
    @FXML private TableColumn<Facture, Void>   colActions;
    @FXML private Label totalLabel;
    @FXML private Label totalPayeLabel;
    @FXML private Label totalImpayeLabel;

    private final FactureService    factureService = new FactureService();
    private final DateTimeFormatter dateFmt =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        filtreStatut.getItems().setAll(StatutFacture.values());
        configurerColonnes();
        chargerFactures();
    }

    private void configurerColonnes() {
        colNumero.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNumeroFacture()));

        colDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDateFacture() != null ?
                                data.getValue().getDateFacture().format(dateFmt) : "—"));

        colPatient.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getConsultation()
                                .getPatient().getNomComplet()));

        colMontant.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("%,.0f FCFA",
                                data.getValue().getMontant())));

        colMode.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getModePaiement().name()));

        // Colonne statut colorée
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null ||
                        getTableRow().getItem() == null) {
                    setText(null); setStyle(""); return;
                }
                StatutFacture statut =
                        ((Facture) getTableRow().getItem()).getStatutFacture();
                setText(statut == StatutFacture.PAYE ? " PAYÉ" : " IMPAYÉ!!");
                setStyle("-fx-text-fill: " +
                        (statut == StatutFacture.PAYE ? "#4caf50" : "#e94560") +
                        "; -fx-font-weight: bold;");
            }
        });
        colStatut.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(""));

        //   Marquer payé + Supprimer
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnPayer    = new Button(" Payer");
            private final Button btnSupp     = new Button("🗑");
            private final HBox   box         = new HBox(6, btnPayer, btnSupp);

            {
                btnPayer.setStyle(
                        "-fx-background-color: #4caf50; -fx-text-fill: white;" +
                                "-fx-background-radius: 6; -fx-cursor: hand;" +
                                "-fx-padding: 4 8 4 8; -fx-font-size: 11px;");
                btnSupp.setStyle(
                        "-fx-background-color: #e94560; -fx-text-fill: white;" +
                                "-fx-background-radius: 6; -fx-cursor: hand;" +
                                "-fx-padding: 4 10 4 10;");

                btnPayer.setOnAction(e -> {
                    Facture f = getTableView().getItems().get(getIndex());
                    marquerPayee(f);
                });
                btnSupp.setOnAction(e -> {
                    Facture f = getTableView().getItems().get(getIndex());
                    confirmerSuppression(f);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Facture f = getTableView().getItems().get(getIndex());
                btnPayer.setDisable(f.getStatutFacture() == StatutFacture.PAYE);
                setGraphic(box);
            }
        });
    }

    @FXML
    public void chargerFactures() {
        List<Facture> factures = factureService.findAll();
        majTable(factures);
        majStats(factures);
    }

    @FXML
    public void filtrer() {
        String kw            = searchField.getText().trim().toLowerCase();
        StatutFacture statut = filtreStatut.getValue();

        List<Facture> filtrees = factureService.findAll().stream()
                .filter(f -> {
                    boolean matchKw = kw.isEmpty() ||
                            f.getNumeroFacture().toLowerCase().contains(kw) ||
                            f.getConsultation().getPatient()
                                    .getNomComplet().toLowerCase().contains(kw);
                    boolean matchStatut = statut == null ||
                            f.getStatutFacture() == statut;
                    return matchKw && matchStatut;
                })
                .toList();

        majTable(filtrees);
        majStats(filtrees);
    }

    private void marquerPayee(Facture facture) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Marquer comme payée ?");
        alert.setContentText("Facture " + facture.getNumeroFacture());
        alert.getDialogPane().setStyle("-fx-background-color: #16213e;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            factureService.marquerPayee(facture.getId());
            chargerFactures();
        }
    }

    private void confirmerSuppression(Facture facture) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la facture ?");
        alert.setContentText("Facture : " + facture.getNumeroFacture());
        alert.getDialogPane().setStyle("-fx-background-color: #16213e;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            factureService.delete(facture.getId());
            chargerFactures();
        }
    }

    private void majTable(List<Facture> factures) {
        factureTable.setItems(FXCollections.observableArrayList(factures));
        totalLabel.setText("Total : " + factures.size() + " facture(s)");
    }

    private void majStats(List<Facture> factures) {
        double paye = factures.stream()
                .filter(f -> f.getStatutFacture() == StatutFacture.PAYE)
                .mapToDouble(Facture::getMontant).sum();
        double impaye = factures.stream()
                .filter(f -> f.getStatutFacture() == StatutFacture.NONPAYE)
                .mapToDouble(Facture::getMontant).sum();

        totalPayeLabel.setText(String.format("%,.0f FCFA", paye));
        totalImpayeLabel.setText(String.format("%,.0f FCFA", impaye));
    }
}
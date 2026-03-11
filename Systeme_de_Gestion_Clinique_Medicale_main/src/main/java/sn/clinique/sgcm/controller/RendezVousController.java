package sn.clinique.sgcm.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sn.clinique.sgcm.enums.StatutRendezVous;
import sn.clinique.sgcm.model.Medecin;
import sn.clinique.sgcm.model.RendezVous;
import sn.clinique.sgcm.service.MedecinService;
import sn.clinique.sgcm.service.RendezVousService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class RendezVousController {

    @FXML private DatePicker              filtreDate;
    @FXML private ComboBox<Medecin>       filtreMedecin;
    @FXML private TableView<RendezVous>   rdvTable;
    @FXML private TableColumn<RendezVous, String> colDate;
    @FXML private TableColumn<RendezVous, String> colHeure;
    @FXML private TableColumn<RendezVous, String> colPatient;
    @FXML private TableColumn<RendezVous, String> colMedecin;
    @FXML private TableColumn<RendezVous, String> colMotif;
    @FXML private TableColumn<RendezVous, String> colStatut;
    @FXML private TableColumn<RendezVous, Void>   colActions;
    @FXML private Label totalLabel;

    private final RendezVousService rendezVousService = new RendezVousService();
    private final MedecinService    medecinService    = new MedecinService();

    private final DateTimeFormatter dateFmt  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter heureFmt = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        chargerMedecins();
        configurerColonnes();
        chargerRdv();
    }

    private void chargerMedecins() {
        filtreMedecin.setItems(
                FXCollections.observableArrayList(medecinService.findAll())
        );
        // Permet d'afficherr le nom complet dans la ComboBox
        filtreMedecin.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Medecin m) {
                return m == null ? "" : m.getNomComplet();
            }
            @Override public Medecin fromString(String s) { return null; }
        });
    }

    private void configurerColonnes() {
        colDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDate().format(dateFmt)));

        colHeure.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDate().format(heureFmt)));

        colPatient.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPatient().getNomComplet()));

        colMedecin.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getMedecin().getNomComplet()));

        colMotif.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getMotif() != null ?
                                data.getValue().getMotif() : "—"));

        // = Colonne statut colorée=
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null ||
                        getTableRow().getItem() == null) {
                    setText(null); setStyle("");
                    return;
                }
                StatutRendezVous statut =
                        ((RendezVous) getTableRow().getItem()).getStatutRendezVous();
                setText(statut.name());
                String color = switch (statut) {
                    case PROGRAMME -> "#4caf50";
                    case ANNULE    -> "#e94560";
                    case TERMINE   -> "#a0a0b0";
                };
                setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
            }
        });
        colStatut.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStatutRendezVous().name()));

        //  Colonne Actions
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit    = new Button("✏ modif");
            private final Button btnAnnuler = new Button("✖ supp");
            private final HBox   box        = new HBox(6, btnEdit, btnAnnuler);

            {
                btnEdit.setStyle(
                        "-fx-background-color: #0f3460; -fx-text-fill: white;" +
                                "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 4 10 4 10;");
                btnAnnuler.setStyle(
                        "-fx-background-color: #e94560; -fx-text-fill: white;" +
                                "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 4 10 4 10;");

                btnEdit.setOnAction(e -> {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    ouvrirFormulaireModification(rdv);
                });

                btnAnnuler.setOnAction(e -> {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    confirmerAnnulation(rdv);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                RendezVous rdv = getTableView().getItems().get(getIndex());
                // Désactiver si déjà annulé ou terminé
                boolean actif = rdv.getStatutRendezVous() == StatutRendezVous.PROGRAMME;
                btnEdit.setDisable(!actif);
                btnAnnuler.setDisable(!actif);
                setGraphic(box);
            }
        });
    }

    @FXML
    public void chargerRdv() {
        List<RendezVous> rdvs = rendezVousService.findAll();
        majTable(rdvs);
    }

    @FXML
    public void afficherAujourdhui() {
        filtreDate.setValue(LocalDate.now());
        List<RendezVous> rdvs = rendezVousService.findDuJour();
        majTable(rdvs);
    }

    @FXML
    public void filtrer() {
        List<RendezVous> rdvs;
        LocalDate date    = filtreDate.getValue();
        Medecin   medecin = filtreMedecin.getValue();

        if (date != null && medecin != null) {
            // Filtre combiné date + médecin
            rdvs = rendezVousService.findByDate(date).stream()
                    .filter(r -> r.getMedecin().getId().equals(medecin.getId()))
                    .toList();
        } else if (date != null) {
            rdvs = rendezVousService.findByDate(date);
        } else if (medecin != null) {
            rdvs = rendezVousService.findByMedecin(medecin);
        } else {
            rdvs = rendezVousService.findAll();
        }
        majTable(rdvs);
    }

    @FXML
    public void ouvrirFormulaire() {
        ouvrirFenetre(null);
    }

    private void ouvrirFormulaireModification(RendezVous rdv) {
        ouvrirFenetre(rdv);
    }

    private void ouvrirFenetre(RendezVous rdv) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sn/clinique/sgcm/rendezvous-form.fxml")
            );
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(rdv == null ? "Nouveau Rendez-vous" : "Modifier Rendez-vous");
            stage.initModality(Modality.APPLICATION_MODAL);

            RendezVousFormController ctrl = loader.getController();
            ctrl.setStage(stage);
            ctrl.setOnSaved(this::chargerRdv);
            if (rdv != null) ctrl.setRendezVous(rdv);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void confirmerAnnulation(RendezVous rdv) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Annuler le rendez-vous ?");
        alert.setContentText("Voulez-vous annuler le RDV de  "
                + rdv.getPatient().getNomComplet() + " !!");
        alert.getDialogPane().setStyle("-fx-background-color: #16213e;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                rendezVousService.annuler(rdv.getId());
                chargerRdv();
            } catch (Exception e) {
                showError("Erreur : " + e.getMessage());
            }
        }
    }

    private void majTable(List<RendezVous> rdvs) {
        rdvTable.setItems(FXCollections.observableArrayList(rdvs));
        totalLabel.setText("Total : " + rdvs.size() + " rendez-vous");
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
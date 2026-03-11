package sn.clinique.sgcm.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import sn.clinique.sgcm.enums.StatutFacture;
import sn.clinique.sgcm.model.RendezVous;
import sn.clinique.sgcm.service.*;
import sn.clinique.sgcm.util.configs.SceneManager;
import sn.clinique.sgcm.util.security.SessionUtilisateur;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    // Topbar
    @FXML private Label topbarTitle;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;

    // Stats
    @FXML private Label totalPatients;
    @FXML private Label rdvDuJour;
    @FXML private Label totalConsultations;
    @FXML private Label facturesNonPayees;

    // Table RDV
    @FXML private TableView<RendezVous> rdvTable;
    @FXML private TableColumn<RendezVous, String> colHeure;
    @FXML private TableColumn<RendezVous, String> colPatientRdv;
    @FXML private TableColumn<RendezVous, String> colMedecinRdv;
    @FXML private TableColumn<RendezVous, String> colMotif;
    @FXML private TableColumn<RendezVous, String> colStatutRdv;

    // Nav buttons
    @FXML private Button btnDashboard;
    @FXML private Button btnPatients;
    @FXML private Button btnRendezVous;
    @FXML private Button btnConsultations;
    @FXML private Button btnFactures;
    @FXML private Button btnMedecins;
    @FXML private Button btnUtilisateurs;

    // Services
    private final PatientService patientService           = new PatientService();
    private final RendezVousService rendezVousService     = new RendezVousService();
    private final ConsultationService consultationService = new ConsultationService();
    private final FactureService factureService           = new FactureService();

    @FXML private VBox contentArea;
    @FXML
    public void initialize() {
        SceneManager.setContentArea(contentArea);
        afficherInfoUtilisateur();
        chargerStats();
        configurerTableRdv();
        chargerRdvDuJour();
        gererVisibiliteAdmin();
    }

    private void afficherInfoUtilisateur() {
        userNameLabel.setText(SessionUtilisateur.getNomComplet());
        userRoleLabel.setText(SessionUtilisateur.getRole().name());
    }

    private void chargerStats() {
        totalPatients.setText(String.valueOf(patientService.findAll().size()));
        rdvDuJour.setText(String.valueOf(rendezVousService.findDuJour().size()));
        totalConsultations.setText(String.valueOf(consultationService.findAll().size()));
        facturesNonPayees.setText(String.valueOf(
                factureService.findByStatut(StatutFacture.NONPAYE).size()
        ));
    }

    private void configurerTableRdv() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        colHeure.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDate().format(fmt)
                ));
        colPatientRdv.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPatient().getNomComplet()
                ));
        colMedecinRdv.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getMedecin().getNomComplet()
                ));
        colMotif.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getMotif()
                ));
        colStatutRdv.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStatutRendezVous().name()
                ));
    }

    private void chargerRdvDuJour() {
        List<RendezVous> rdvs = rendezVousService.findDuJour();
        rdvTable.setItems(FXCollections.observableArrayList(rdvs));
    }

    // Cacher menu admin si pas ADMIN========
    private void gererVisibiliteAdmin() {
        boolean isAdmin = SessionUtilisateur.isAdmin();
        btnUtilisateurs.setVisible(isAdmin);
        btnUtilisateurs.setManaged(isAdmin);
    }

    // ===== NAVIGATION ==============
    @FXML public void showDashboard()      { setActive(btnDashboard);      topbarTitle.setText("Dashboard"); }
    @FXML public void showPatients()       { setActive(btnPatients);        topbarTitle.setText("Patients");
        SceneManager.loadInto("patients-view.fxml"); }
    @FXML public void showRendezVous()     { setActive(btnRendezVous);      topbarTitle.setText("Rendez-vous");
        SceneManager.loadInto("rendezvous-view.fxml"); }
    @FXML public void showConsultations()  { setActive(btnConsultations);   topbarTitle.setText("Consultations");
        SceneManager.loadInto("consultations-view.fxml"); }
    @FXML public void showFactures()       { setActive(btnFactures);        topbarTitle.setText("Factures");
        SceneManager.loadInto("factures-view.fxml"); }
    @FXML public void showMedecins()       { setActive(btnMedecins);        topbarTitle.setText("Médecins");
        SceneManager.loadInto("medecin-view.fxml"); }
    @FXML public void showUtilisateurs()   { setActive(btnUtilisateurs);    topbarTitle.setText("Utilisateurs");
        SceneManager.loadInto("utilisateurs-view.fxml"); }

    @FXML
    public void handleLogout() {
        new UtilisateurService().logout();
        SceneManager.switchTo("login-view.fxml", "SGCM — Connexion");
    }

    private void setActive(Button active) {
        List.of(btnDashboard, btnPatients, btnRendezVous,
                        btnConsultations, btnFactures, btnMedecins, btnUtilisateurs)
                .forEach(b -> {
                    b.getStyleClass().remove("nav-btn-active");
                    if (!b.getStyleClass().contains("nav-btn"))
                        b.getStyleClass().add("nav-btn");
                });
        active.getStyleClass().add("nav-btn-active");
    }
}
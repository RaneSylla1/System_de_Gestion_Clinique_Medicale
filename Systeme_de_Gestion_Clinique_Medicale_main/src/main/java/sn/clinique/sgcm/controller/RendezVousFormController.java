package sn.clinique.sgcm.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sn.clinique.sgcm.model.Medecin;
import sn.clinique.sgcm.model.Patient;
import sn.clinique.sgcm.model.RendezVous;
import sn.clinique.sgcm.service.MedecinService;
import sn.clinique.sgcm.service.PatientService;
import sn.clinique.sgcm.service.RendezVousService;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class RendezVousFormController {

    @FXML private Label              formTitle;
    @FXML private ComboBox<Patient>  patientField;
    @FXML private ComboBox<Medecin>  medecinField;
    @FXML private DatePicker         dateField;
    @FXML private TextField          heureField;
    @FXML private TextArea           motifField;
    @FXML private Label              errorLabel;
    @FXML private Button             saveBtn;

    private Stage      stage;
    private RendezVous rdvEnCours;
    private Runnable   onSaved;

    private final RendezVousService rendezVousService = new RendezVousService();
    private final PatientService    patientService    = new PatientService();
    private final MedecinService    medecinService    = new MedecinService();

    @FXML
    public void initialize() {
        // Charger patients
        patientField.setItems(
                FXCollections.observableArrayList(patientService.findAll()));
        patientField.setConverter(new StringConverter<>() {
            @Override public String toString(Patient p) {
                return p == null ? "" : p.getNomComplet();
            }
            @Override public Patient fromString(String s) { return null; }
        });

        // Charger médecins
        medecinField.setItems(
                FXCollections.observableArrayList(medecinService.findAll()));
        medecinField.setConverter(new StringConverter<>() {
            @Override public String toString(Medecin m) {
                return m == null ? "" : m.getNomComplet();
            }
            @Override public Medecin fromString(String s) { return null; }
        });
    }

    public void setRendezVous(RendezVous rdv) {
        this.rdvEnCours = rdv;
        formTitle.setText("Modifier Rendez-vous");
        saveBtn.setText("Mettre à jour");

        patientField.setValue(rdv.getPatient());
        medecinField.setValue(rdv.getMedecin());
        dateField.setValue(rdv.getDate().toLocalDate());
        heureField.setText(rdv.getDate().format(DateTimeFormatter.ofPattern("HH:mm")));
        motifField.setText(rdv.getMotif());
    }

    @FXML
    public void sauvegarder() {
        errorLabel.setText("");
        try {
            // Validation
            if (patientField.getValue() == null)
                throw new IllegalArgumentException("Veuillez sélectionner un patient.");
            if (medecinField.getValue() == null)
                throw new IllegalArgumentException("Veuillez sélectionner un médecin.");
            if (dateField.getValue() == null)
                throw new IllegalArgumentException("Veuillez sélectionner une date.");
            if (heureField.getText().isBlank())
                throw new IllegalArgumentException("Veuillez saisir une heure.");

            // Parser l'heure
            LocalTime heure;
            try {
                heure = LocalTime.parse(
                        heureField.getText().trim(),
                        DateTimeFormatter.ofPattern("HH:mm")
                );
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Format heure invalide. Utilisez HH:mm");
            }

            LocalDateTime dateHeure = LocalDateTime.of(dateField.getValue(), heure);

            RendezVous rdv = rdvEnCours != null ? rdvEnCours : new RendezVous();
            rdv.setPatient(patientField.getValue());
            rdv.setMedecin(medecinField.getValue());
            rdv.setDate(dateHeure);
            rdv.setMotif(motifField.getText().trim());

            if (rdvEnCours != null) {
                rendezVousService.update(rdv);
            } else {
                rendezVousService.save(rdv);
            }

            if (onSaved != null) onSaved.run();
            stage.close();

        } catch (IllegalArgumentException e) {
            errorLabel.setText("⚠ " + e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("⚠ Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void annuler() { stage.close(); }

    public void setStage(Stage stage)        { this.stage = stage; }
    public void setOnSaved(Runnable onSaved) { this.onSaved = onSaved; }
}
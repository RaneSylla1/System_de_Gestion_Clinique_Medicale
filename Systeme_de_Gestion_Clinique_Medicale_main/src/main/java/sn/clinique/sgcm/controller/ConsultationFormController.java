package sn.clinique.sgcm.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sn.clinique.sgcm.enums.StatutRendezVous;
import sn.clinique.sgcm.model.Consultation;
import sn.clinique.sgcm.model.RendezVous;
import sn.clinique.sgcm.service.ConsultationService;
import sn.clinique.sgcm.service.RendezVousService;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsultationFormController {

    @FXML private Label               formTitle;
    @FXML private ComboBox<RendezVous> rendezVousField;
    @FXML private TextField            patientLabel;
    @FXML private TextField            medecinLabel;
    @FXML private TextArea             diagnosticField;
    @FXML private TextArea             observationField;
    @FXML private TextArea             prescriptionField;
    @FXML private TextField            prixField;
    @FXML private Label                errorLabel;
    @FXML private Button               saveBtn;

    private Stage       stage;
    private Consultation consultationEnCours;
    private Runnable    onSaved;

    private final ConsultationService consultationService = new ConsultationService();
    private final RendezVousService   rendezVousService   = new RendezVousService();
    private final DateTimeFormatter   fmt =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        //  Charger seulement les RDV PROGRAMMES
        List<RendezVous> rdvs = rendezVousService.findAll().stream()
                .filter(r -> r.getStatutRendezVous() == StatutRendezVous.PROGRAMME)
                .toList();

        rendezVousField.setItems(FXCollections.observableArrayList(rdvs));
        rendezVousField.setConverter(new StringConverter<>() {
            @Override public String toString(RendezVous r) {
                if (r == null) return "";
                return r.getDate().format(fmt) + " — " +
                        r.getPatient().getNomComplet();
            }
            @Override public RendezVous fromString(String s) { return null; }
        });

        // Auto-remplir patient & médecin quand RDV sélectionné
        rendezVousField.setOnAction(e -> {
            RendezVous rdv = rendezVousField.getValue();
            if (rdv != null) {
                patientLabel.setText(rdv.getPatient().getNomComplet());
                medecinLabel.setText(rdv.getMedecin().getNomComplet());
            }
        });
    }

    public void setConsultation(Consultation c) {
        this.consultationEnCours = c;
        formTitle.setText("Modifier Consultation");
        saveBtn.setText("Mettre à jour");

        rendezVousField.setValue(c.getRendezVous());
        patientLabel.setText(c.getPatient().getNomComplet());
        medecinLabel.setText(c.getMedecin().getNomComplet());
        diagnosticField.setText(c.getDiagnostic());
        observationField.setText(c.getObservation());
        prescriptionField.setText(c.getPrescription());
        prixField.setText(String.valueOf(c.getPrix()));
    }

    @FXML
    public void sauvegarder() {
        errorLabel.setText("");
        try {
            if (rendezVousField.getValue() == null)
                throw new IllegalArgumentException("Veuillez sélectionner un rendez-vous.");
            if (diagnosticField.getText().isBlank())
                throw new IllegalArgumentException("Le diagnostic est obligatoire.");
            if (prixField.getText().isBlank())
                throw new IllegalArgumentException("Le prix est obligatoire.");

            double prix;
            try {
                prix = Double.parseDouble(prixField.getText().trim());
                if (prix <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Prix invalide.");
            }

            RendezVous rdv = rendezVousField.getValue();
            Consultation c = consultationEnCours != null ?
                    consultationEnCours : new Consultation();

            c.setRendezVous(rdv);
            c.setPatient(rdv.getPatient());
            c.setMedecin(rdv.getMedecin());
            c.setDiagnostic(diagnosticField.getText().trim());
            c.setObservation(observationField.getText().trim());
            c.setPrescription(prescriptionField.getText().trim());
            c.setPrix(prix);

            if (consultationEnCours != null) {
                consultationService.update(c);
            } else {
                consultationService.save(c);
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
package sn.clinique.sgcm.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sn.clinique.sgcm.enums.GroupeSanguin;
import sn.clinique.sgcm.enums.Rehsus;
import sn.clinique.sgcm.enums.Sexe;
import sn.clinique.sgcm.model.Patient;
import sn.clinique.sgcm.service.PatientService;

public class PatientFormController {

    @FXML private Label       formTitle;
    @FXML private TextField   nomField;
    @FXML private TextField   prenomField;
    @FXML private DatePicker  dateNaissanceField;
    @FXML private ComboBox<Sexe>          sexeField;
    @FXML private TextField   telephoneField;
    @FXML private TextField   emailField;
    @FXML private TextField   adresseField;
    @FXML private ComboBox<GroupeSanguin> groupeSanguinField;
    @FXML private ComboBox<Rehsus>        rehsusField;
    @FXML private TextArea    antecedentsField;
    @FXML private Label       errorLabel;
    @FXML private Button      saveBtn;

    private Stage stage;
    private Patient patientEnCours;
    private Runnable onSaved;

    private final PatientService patientService = new PatientService();

    @FXML
    public void initialize() {
        // Remplir les ComboBox avec les enums
        sexeField.getItems().setAll(Sexe.values());
        groupeSanguinField.getItems().setAll(GroupeSanguin.values());
        rehsusField.getItems().setAll(Rehsus.values());
    }

    // Pré-remplir le formulaire en mode modification ( charger formulaire )
    public void setPatient(Patient patient) {
        this.patientEnCours = patient;
        formTitle.setText("Modifier Patient");
        saveBtn.setText("Mettre à jour");

        nomField.setText(patient.getNom());
        prenomField.setText(patient.getPrenom());
        dateNaissanceField.setValue(patient.getDateNaissance());
        sexeField.setValue(patient.getSexe());
        telephoneField.setText(patient.getTelephone());
        emailField.setText(patient.getEmail());
        adresseField.setText(patient.getAdresse());
        groupeSanguinField.setValue(patient.getGroupeSanguin());
        rehsusField.setValue(patient.getRehsus());
        antecedentsField.setText(patient.getAntecedentsMedicaux());
    }

    @FXML
    public void sauvegarder() {
        errorLabel.setText("");
        try {
            Patient patient = patientEnCours != null ? patientEnCours : new Patient();

            patient.setNom(nomField.getText().trim());
            patient.setPrenom(prenomField.getText().trim());
            patient.setDateNaissance(dateNaissanceField.getValue());
            patient.setSexe(sexeField.getValue());
            patient.setTelephone(telephoneField.getText().trim());
            patient.setEmail(emailField.getText().trim());
            patient.setAdresse(adresseField.getText().trim());
            patient.setGroupeSanguin(groupeSanguinField.getValue());
            patient.setRehsus(rehsusField.getValue());
            patient.setAntecedentsMedicaux(antecedentsField.getText().trim());

            if (patientEnCours != null) {
                patientService.update(patient);
            } else {
                patientService.save(patient);
            }

            if (onSaved != null) onSaved.run();
            stage.close();

        } catch (IllegalArgumentException e) {
            errorLabel.setText("⚠ " + e.getMessage());
        } catch (Exception e) {
            errorLabel.setText(" Erreur inattendue : " + e.getMessage());
        }
    }

    @FXML
    public void annuler() {
        stage.close();
    }

    public void setStage(Stage stage)       { this.stage = stage; }
    public void setOnSaved(Runnable onSaved) { this.onSaved = onSaved; }
}

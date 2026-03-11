package sn.clinique.sgcm.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sn.clinique.sgcm.enums.Role;
import sn.clinique.sgcm.enums.Sexe;
import sn.clinique.sgcm.model.Medecin;
import sn.clinique.sgcm.service.MedecinService;
import sn.clinique.sgcm.util.security.PasswordUtil;

public class MedecinFormController {

    @FXML private Label           formTitle;
    @FXML private TextField       nomField;
    @FXML private TextField       prenomField;
    @FXML private TextField       matriculeField;
    @FXML private TextField       specialiteField;
    @FXML private DatePicker      dateNaissanceField;
    @FXML private ComboBox<Sexe>  sexeField;
    @FXML private TextField       telephoneField;
    @FXML private TextField       emailField;
    @FXML private TextField       usernameField;
    @FXML private PasswordField   passwordField;
    @FXML private Label           passwordLabel;
    @FXML private Label           errorLabel;
    @FXML private Button          saveBtn;

    private Stage    stage;
    private Medecin  medecinEnCours;
    private Runnable onSaved;

    private final MedecinService medecinService = new MedecinService();

    @FXML
    public void initialize() {
        sexeField.getItems().setAll(Sexe.values());
    }

    //  Pré-remplir en mode modification
    public void setMedecin(Medecin medecin) {
        this.medecinEnCours = medecin;
        formTitle.setText("Modifier Médecin");
        saveBtn.setText("Mettre à jour");
        passwordLabel.setText("Nouveau mot de passe (vide = inchangé)");
        passwordField.setPromptText("Laisser vide pour ne pas changer");

        nomField.setText(medecin.getNom());
        prenomField.setText(medecin.getPrenom());
        matriculeField.setText(medecin.getMatricule());
        specialiteField.setText(medecin.getSpecialite());
        dateNaissanceField.setValue(medecin.getDateNaissance());
        sexeField.setValue(medecin.getSexe());
        telephoneField.setText(medecin.getTelephone());
        emailField.setText(medecin.getEmail());
        usernameField.setText(medecin.getUsername());
    }

    @FXML
    public void sauvegarder() {
        errorLabel.setText("");
        try {
            // ===== Validations=========
            if (nomField.getText().isBlank())
                throw new IllegalArgumentException("Le nom est obligatoire.");
            if (prenomField.getText().isBlank())
                throw new IllegalArgumentException("Le prénom est obligatoire.");
            if (matriculeField.getText().isBlank())
                throw new IllegalArgumentException("Le matricule est obligatoire.");
            if (specialiteField.getText().isBlank())
                throw new IllegalArgumentException("La spécialité est obligatoire.");
            if (usernameField.getText().isBlank())
                throw new IllegalArgumentException("Le username est obligatoire.");
            if (medecinEnCours == null && passwordField.getText().isBlank())
                throw new IllegalArgumentException("Le mot de passe est obligatoire.");

            Medecin medecin = medecinEnCours != null ? medecinEnCours : new Medecin();
            medecin.setNom(nomField.getText().trim());
            medecin.setPrenom(prenomField.getText().trim());
            medecin.setMatricule(matriculeField.getText().trim());
            medecin.setSpecialite(specialiteField.getText().trim());
            medecin.setDateNaissance(dateNaissanceField.getValue());
            medecin.setSexe(sexeField.getValue());
            medecin.setTelephone(telephoneField.getText().trim());
            medecin.setEmail(emailField.getText().trim());
            medecin.setUsername(usernameField.getText().trim());
            medecin.setRole(Role.MEDECIN);
            medecin.setActif(true);

            //  Hasher le mot de passe seulement si renseigné
            if (!passwordField.getText().isBlank()) {
                medecin.setPassword(PasswordUtil.hash(passwordField.getText()));
            }

            if (medecinEnCours != null) {
                medecinService.update(medecin);
            } else {
                medecinService.save(medecin);
            }

            if (onSaved != null) onSaved.run();
            stage.close();

        } catch (IllegalArgumentException e) {
            errorLabel.setText("⚠ " + e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void annuler() { stage.close(); }

    public void setStage(Stage stage)        { this.stage = stage; }
    public void setOnSaved(Runnable onSaved) { this.onSaved = onSaved; }
}
package sn.clinique.sgcm.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sn.clinique.sgcm.enums.ModePaiement;
import sn.clinique.sgcm.enums.StatutFacture;
import sn.clinique.sgcm.model.Consultation;
import sn.clinique.sgcm.model.Facture;
import sn.clinique.sgcm.service.FactureService;

public class FactureFormController {

    @FXML private Label                    infoPatient;
    @FXML private Label                    infoMedecin;
    @FXML private Label                    infoDiagnostic;
    @FXML private TextField                montantField;
    @FXML private ComboBox<ModePaiement>   modePaiementField;
    @FXML private ComboBox<StatutFacture>  statutField;
    @FXML private Label                    errorLabel;

    private Stage       stage;
    private Consultation consultation;
    private Runnable    onSaved;

    private final FactureService factureService = new FactureService();

    @FXML
    public void initialize() {
        modePaiementField.getItems().setAll(ModePaiement.values());
        statutField.getItems().setAll(StatutFacture.values());
        statutField.setValue(StatutFacture.NONPAYE);
    }

    //  Pré-remplir depuis la consultation
    public void setConsultation(Consultation c) {
        this.consultation = c;
        infoPatient.setText(c.getPatient().getNomComplet());
        infoMedecin.setText(c.getMedecin().getNomComplet());
        infoDiagnostic.setText(c.getDiagnostic());
        // Pré-remplir montant depuis le prix de la consultation
        montantField.setText(String.valueOf(c.getPrix()));
    }

    @FXML
    public void sauvegarder() {
        errorLabel.setText("");
        try {
            if (montantField.getText().isBlank())
                throw new IllegalArgumentException("Le montant est obligatoire.");
            if (modePaiementField.getValue() == null)
                throw new IllegalArgumentException("Le mode de paiement est obligatoire.");
            if (statutField.getValue() == null)
                throw new IllegalArgumentException("Le statut est obligatoire.");

            double montant;
            try {
                montant = Double.parseDouble(montantField.getText().trim());
                if (montant <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Montant invalide.");
            }

            Facture facture = new Facture();
            facture.setConsultation(consultation);
            facture.setMontant(montant);
            facture.setModePaiement(modePaiementField.getValue());
            facture.setStatutFacture(statutField.getValue());

            factureService.save(facture);

            if (onSaved != null) onSaved.run();
            stage.close();

        } catch (IllegalArgumentException e) {
            errorLabel.setText("⚠ " + e.getMessage());
        } catch (Exception e) {
            errorLabel.setText(" Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void annuler() { stage.close(); }

    public void setStage(Stage stage)          { this.stage = stage; }
    public void setOnSaved(Runnable onSaved)   { this.onSaved = onSaved; }
}
package sn.clinique.sgcm.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import sn.clinique.sgcm.service.UtilisateurService;
import sn.clinique.sgcm.util.configs.SceneManager;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginBtn;

    private final UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    public void initialize() {
        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText(" Veuillez remplir tous les champs.");
            return;
        }

        boolean success = utilisateurService.login(username, password);

        if (success) {
            errorLabel.setText("");
            // Redirige vers dashboard après login
            SceneManager.switchTo("dashboard-view.fxml", "SGCM — Dashboard");
        } else {
            errorLabel.setText("⚠ Vos Identifiants incorrects  Réessayez.");
            passwordField.clear();
        }
    }
}
package sn.clinique.sgcm.util.security;

import sn.clinique.sgcm.enums.Role;
import sn.clinique.sgcm.model.Utilisateur;

public class SessionUtilisateur {

    // Instance unique ( le Singleton)
    private static SessionUtilisateur instance;

    private static Long id;
    private static String username;
    private static String nomComplet;
    private static Role role;
    private static boolean connecte = false;

    private SessionUtilisateur() {}

    public static SessionUtilisateur getInstance() {
        if (instance == null) {
            instance = new SessionUtilisateur();
        }
        return instance;
    }

    // Ouvrir la session
    public static void ouvrir(Utilisateur utilisateur) {
        id          = utilisateur.getId();
        username    = utilisateur.getUsername();
        nomComplet  = utilisateur.getPrenom() + " " + utilisateur.getNom();
        role        = utilisateur.getRole();
        connecte    = true;
    }

    // Fermer la session (logout)
    public static void fermer() {
        id         = null;
        username   = null;
        nomComplet = null;
        role       = null;
        connecte   = false;
    }

    // Vérifications de rôle
    public static boolean isAdmin() {
        return connecte && role == Role.ADMIN;
    }

    public static boolean isMedecin() {
        return connecte && role == Role.MEDECIN;
    }

    public static boolean isReceptionniste() {
        return connecte && role == Role.RECEPTIONNISTE;
    }

    // Getters
    public static Long getId()           { return id; }
    public static String getUsername()   { return username; }
    public static String getNomComplet() { return nomComplet; }
    public static Role getRole()         { return role; }
    public static boolean isConnecte()   { return connecte; }
}
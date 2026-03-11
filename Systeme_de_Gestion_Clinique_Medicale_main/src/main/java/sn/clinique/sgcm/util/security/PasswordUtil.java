package sn.clinique.sgcm.util.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private PasswordUtil() {} // empêche l'instanciation

    // Hash du mot de passe
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    // Vérification mot de passe
    public static boolean verify(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
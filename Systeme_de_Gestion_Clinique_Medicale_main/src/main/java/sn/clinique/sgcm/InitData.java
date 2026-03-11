package sn.clinique.sgcm;

import sn.clinique.sgcm.util.configs.JpaUtil;
import sn.clinique.sgcm.util.security.PasswordUtil;
import jakarta.persistence.EntityManager;

public class InitData {

    public static void main(String[] args) {
        System.out.println("Démarrage InitData...");

        String hashPassword = PasswordUtil.hash("admin123");
        System.out.println("Hash généré : " + hashPassword);

        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();


            em.createNativeQuery(
                            "INSERT INTO utilisateurs " +
                                    "(nom, prenom, username, password, role, sexe, " +
                                    "dateNaissance, telephone, actif) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    )
                    .setParameter(1, "Admin")
                    .setParameter(2, "Super")
                    .setParameter(3, "admin")
                    .setParameter(4, hashPassword)
                    .setParameter(5, "ADMIN")
                    .setParameter(6, "MASCULIN")
                    .setParameter(7, java.time.LocalDate.of(1990, 1, 1))
                    .setParameter(8, "770000000")
                    .setParameter(9, true)
                    .executeUpdate();

            em.getTransaction().commit();
            System.out.println(" Admin créé avec succès !");
            System.out.println("Username : admin");
            System.out.println("Password : admin123");

        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println(" Erreur : " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            JpaUtil.close();
        }
    }
}
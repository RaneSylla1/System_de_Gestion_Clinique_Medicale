package sn.clinique.sgcm.repository;

import jakarta.persistence.EntityManager;
import sn.clinique.sgcm.model.Utilisateur;
import sn.clinique.sgcm.util.configs.JpaUtil;
import java.util.Optional;

public class UtilisateurRepository extends AbstractRepository<Utilisateur, Long> {

    public Optional<Utilisateur> findByUsername(String username) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT u FROM Utilisateur u WHERE u.username = :username", Utilisateur.class)
                    .setParameter("username", username)
                    .getResultStream().findFirst();
        } finally {
            em.close();
        }
    }
}
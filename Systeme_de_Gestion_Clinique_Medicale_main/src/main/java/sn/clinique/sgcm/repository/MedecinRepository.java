package sn.clinique.sgcm.repository;

import jakarta.persistence.EntityManager;
import sn.clinique.sgcm.model.Medecin;
import sn.clinique.sgcm.util.configs.JpaUtil;
import java.util.List;
import java.util.Optional;

public class MedecinRepository extends AbstractRepository<Medecin, Long> {

    public Optional<Medecin> findByMatricule(String matricule) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT m FROM Medecin m WHERE m.matricule = :mat", Medecin.class)
                    .setParameter("mat", matricule)
                    .getResultStream().findFirst();
        } finally {
            em.close();
        }
    }

    public List<Medecin> findBySpecialite(String specialite) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT m FROM Medecin m WHERE LOWER(m.specialite) = :spec", Medecin.class)
                    .setParameter("spec", specialite.toLowerCase())
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
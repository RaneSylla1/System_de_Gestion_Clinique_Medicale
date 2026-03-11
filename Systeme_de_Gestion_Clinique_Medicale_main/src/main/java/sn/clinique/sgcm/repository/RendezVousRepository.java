package sn.clinique.sgcm.repository;

import jakarta.persistence.EntityManager;
import sn.clinique.sgcm.model.Medecin;
import sn.clinique.sgcm.model.RendezVous;
import sn.clinique.sgcm.util.configs.JpaUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class RendezVousRepository extends AbstractRepository<RendezVous, Long> {

    // Rendez-vous du jour
    public List<RendezVous> findByDate(LocalDate date) {
        EntityManager em = JpaUtil.getEntityManager();
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = date.atTime(23, 59, 59);
        try {
            return em.createQuery(
                            "SELECT r FROM RendezVous r WHERE r.date BETWEEN :debut AND :fin", RendezVous.class)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Rendez-vous par médecin
    public List<RendezVous> findByMedecin(Medecin medecin) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM RendezVous r WHERE r.medecin = :med", RendezVous.class)
                    .setParameter("med", medecin)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    //  Vérifier conflit horaire médecin (règle métier)
    public boolean existsConflitHoraire(Medecin medecin, LocalDateTime dateHeure, Long excludeId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String query = "SELECT COUNT(r) FROM RendezVous r WHERE r.medecin = :med " +
                    "AND r.date = :date AND r.id <> :excludeId";
            Long count = em.createQuery(query, Long.class)
                    .setParameter("med", medecin)
                    .setParameter("date", dateHeure)
                    .setParameter("excludeId", excludeId == null ? -1L : excludeId)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
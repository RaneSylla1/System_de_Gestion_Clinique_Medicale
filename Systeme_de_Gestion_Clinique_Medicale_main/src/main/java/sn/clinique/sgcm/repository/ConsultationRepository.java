package sn.clinique.sgcm.repository;

import jakarta.persistence.EntityManager;
import sn.clinique.sgcm.model.Consultation;
import sn.clinique.sgcm.model.Patient;
import sn.clinique.sgcm.util.configs.JpaUtil;
import java.util.List;

public class ConsultationRepository extends AbstractRepository<Consultation, Long> {

    public List<Consultation> findByPatient(Patient patient) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Consultation c WHERE c.patient = :p", Consultation.class)
                    .setParameter("p", patient)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
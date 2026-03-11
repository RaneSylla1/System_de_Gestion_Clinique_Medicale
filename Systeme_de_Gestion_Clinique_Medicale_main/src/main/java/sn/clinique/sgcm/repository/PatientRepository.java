package sn.clinique.sgcm.repository;

import jakarta.persistence.EntityManager;
import sn.clinique.sgcm.model.Patient;
import sn.clinique.sgcm.util.configs.JpaUtil;
import java.util.List;

public class PatientRepository extends AbstractRepository<Patient, Long> {

    // Recherche par le nom ou prénom
    public List<Patient> findByNomOuPrenom(String keyword) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Patient p WHERE " +
                                    "LOWER(p.nom) LIKE :kw OR LOWER(p.prenom) LIKE :kw", Patient.class)
                    .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Recherche par  le téléphone
    public List<Patient> findByTelephone(String telephone) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Patient p WHERE p.telephone = :tel", Patient.class)
                    .setParameter("tel", telephone)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
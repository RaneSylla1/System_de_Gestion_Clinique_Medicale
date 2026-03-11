package sn.clinique.sgcm.repository;

import jakarta.persistence.EntityManager;
import sn.clinique.sgcm.model.Facture;
import sn.clinique.sgcm.enums.StatutFacture;
import sn.clinique.sgcm.util.configs.JpaUtil;
import java.util.List;
import java.util.Optional;

public class FactureRepository extends AbstractRepository<Facture, Long> {

    public List<Facture> findByStatut(StatutFacture statut) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT f FROM Facture f WHERE f.statutFacture = :statut", Facture.class)
                    .setParameter("statut", statut)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Facture> findByNumero(String numero) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT f FROM Facture f WHERE f.numeroFacture = :num", Facture.class)
                    .setParameter("num", numero)
                    .getResultStream().findFirst();
        } finally {
            em.close();
        }
    }
}

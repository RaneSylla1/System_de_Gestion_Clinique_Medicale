package sn.clinique.sgcm.service;

import sn.clinique.sgcm.enums.StatutFacture;
import sn.clinique.sgcm.model.Facture;
import sn.clinique.sgcm.repository.FactureRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class FactureService implements GenericService<Facture, Long> {

    private final FactureRepository factureRepository = new FactureRepository();

    @Override
    public Facture save(Facture facture) {
        valider(facture);
        // Générer numéro facture automatiquement
        facture.setNumeroFacture(genererNumero());
        facture.setDateFacture(LocalDate.now());
        return factureRepository.save(facture);
    }

    @Override
    public Facture update(Facture facture) {
        valider(facture);
        return factureRepository.update(facture);
    }

    @Override
    public void delete(Long id) {
        factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture introuvable avec l'id : " + id));
        factureRepository.delete(id);
    }

    @Override
    public Optional<Facture> findById(Long id) {
        return factureRepository.findById(id);
    }

    @Override
    public List<Facture> findAll() {
        return factureRepository.findAll();
    }

    public List<Facture> findByStatut(StatutFacture statut) {
        return factureRepository.findByStatut(statut);
    }

    // Marquer comme payée
    public Facture marquerPayee(Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture introuvable."));
        facture.setStatutFacture(StatutFacture.PAYE);
        return factureRepository.update(facture);
    }

    // Génération automatique du numéro : FAC-2025-001
    private String genererNumero() {
        String annee = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        long count = factureRepository.findAll().size() + 1;
        return String.format("FAC-%s-%03d", annee, count);
    }

    private void valider(Facture facture) {
        if (facture.getConsultation() == null)
            throw new IllegalArgumentException("La consultation est obligatoire.");
        if (facture.getMontant() <= 0)
            throw new IllegalArgumentException("Le montant doit être supérieur à 0.");
        if (facture.getModePaiement() == null)
            throw new IllegalArgumentException("Le mode de paiement est obligatoire.");
    }
}

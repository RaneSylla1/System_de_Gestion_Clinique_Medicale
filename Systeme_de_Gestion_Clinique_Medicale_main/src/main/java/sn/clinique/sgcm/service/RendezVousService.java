package sn.clinique.sgcm.service;

import sn.clinique.sgcm.enums.StatutRendezVous;
import sn.clinique.sgcm.model.Medecin;
import sn.clinique.sgcm.model.RendezVous;
import sn.clinique.sgcm.repository.RendezVousRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RendezVousService implements GenericService<RendezVous, Long> {

    private final RendezVousRepository rendezVousRepository = new RendezVousRepository();

    @Override
    public RendezVous save(RendezVous rdv) {
        valider(rdv);
        verifierConflitHoraire(rdv);
        return rendezVousRepository.save(rdv);
    }

    @Override
    public RendezVous update(RendezVous rdv) {
        valider(rdv);
        verifierConflitHoraire(rdv);
        return rendezVousRepository.update(rdv);
    }

    @Override
    public void delete(Long id) {
        rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable avec l'id : " + id));
        rendezVousRepository.delete(id);
    }

    @Override
    public Optional<RendezVous> findById(Long id) {
        return rendezVousRepository.findById(id);
    }

    @Override
    public List<RendezVous> findAll() {
        return rendezVousRepository.findAll();
    }

    // Rendez-vous du jour
    public List<RendezVous> findDuJour() {
        return rendezVousRepository.findByDate(LocalDate.now());
    }

    public List<RendezVous> findByDate(LocalDate date) {
        return rendezVousRepository.findByDate(date);
    }

    // Filtrer par médecin
    public List<RendezVous> findByMedecin(Medecin medecin) {
        return rendezVousRepository.findByMedecin(medecin);
    }

    // Annuler un rendez-vous
    public RendezVous annuler(Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable."));
        rdv.setStatutRendezVous(StatutRendezVous.ANNULE);
        return rendezVousRepository.update(rdv);
    }

    // Terminer un rendez-vous
    public RendezVous terminer(Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable."));
        rdv.setStatutRendezVous(StatutRendezVous.TERMINE);
        return rendezVousRepository.update(rdv);
    }

    //  Pour asssurer qu'il n y pas deux RDV au même heure pour un médecin
    private void verifierConflitHoraire(RendezVous rdv) {
        boolean conflit = rendezVousRepository.existsConflitHoraire(
                rdv.getMedecin(), rdv.getDate(), rdv.getId()
        );
        if (conflit) throw new IllegalArgumentException(
                "Le médecin a déjà un rendez-vous à cet heure."
        );
    }

    private void valider(RendezVous rdv) {
        if (rdv.getDate() == null)
            throw new IllegalArgumentException("La date est obligatoire.");
        if (rdv.getPatient() == null)
            throw new IllegalArgumentException("Le patient est obligatoire.");
        if (rdv.getMedecin() == null)
            throw new IllegalArgumentException("Le médecin est obligatoire.");
        if (rdv.getDate().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("La date ne peut pas être dans le passé.");
    }
}
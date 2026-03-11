package sn.clinique.sgcm.service;

import sn.clinique.sgcm.enums.StatutRendezVous;
import sn.clinique.sgcm.model.Consultation;
import sn.clinique.sgcm.model.Patient;
import sn.clinique.sgcm.repository.ConsultationRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ConsultationService implements GenericService<Consultation, Long> {

    private final ConsultationRepository consultationRepository = new ConsultationRepository();
    private final RendezVousService rendezVousService = new RendezVousService();

    @Override
    public Consultation save(Consultation consultation) {
        valider(consultation);
        consultation.setDateConsultation(LocalDate.now());
        //  Marquer le RDV comme TERMINÉ automatiquement
        rendezVousService.terminer(consultation.getRendezVous().getId());
        return consultationRepository.save(consultation);
    }

    @Override
    public Consultation update(Consultation consultation) {
        valider(consultation);
        return consultationRepository.update(consultation);
    }

    @Override
    public void delete(Long id) {
        consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable avec l'id : " + id));
        consultationRepository.delete(id);
    }

    @Override
    public Optional<Consultation> findById(Long id) {
        return consultationRepository.findById(id);
    }

    @Override
    public List<Consultation> findAll() {
        return consultationRepository.findAll();
    }

    public List<Consultation> findByPatient(Patient patient) {
        return consultationRepository.findByPatient(patient);
    }

    private void valider(Consultation consultation) {
        if (consultation.getRendezVous() == null)
            throw new IllegalArgumentException("Le rendez-vous est obligatoire.");
        if (consultation.getPatient() == null)
            throw new IllegalArgumentException("Le patient est obligatoire.");
        if (consultation.getMedecin() == null)
            throw new IllegalArgumentException("Le médecin est obligatoire.");
        if (consultation.getDiagnostic() == null || consultation.getDiagnostic().isBlank())
            throw new IllegalArgumentException("Le diagnostic est obligatoire.");
        //  Vérifier que le RDV n'est pas annulé
        if (consultation.getRendezVous().getStatutRendezVous() == StatutRendezVous.ANNULE)
            throw new IllegalArgumentException("Impossible de consulter un rendez-vous annulé.");
    }
}
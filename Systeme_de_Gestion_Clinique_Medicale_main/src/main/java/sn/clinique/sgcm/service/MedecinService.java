package sn.clinique.sgcm.service;

import sn.clinique.sgcm.model.Medecin;
import sn.clinique.sgcm.repository.MedecinRepository;
import java.util.List;
import java.util.Optional;

public class MedecinService implements GenericService<Medecin, Long> {

    private final MedecinRepository medecinRepository = new MedecinRepository();

    @Override
    public Medecin save(Medecin medecin) {
        valider(medecin);
        // Vérifier unicité matricule
        medecinRepository.findByMatricule(medecin.getMatricule())
                .ifPresent(m -> { throw new IllegalArgumentException("Matricule déjà utilisé."); });
        return medecinRepository.save(medecin);
    }

    @Override
    public Medecin update(Medecin medecin) {
        valider(medecin);
        return medecinRepository.update(medecin);
    }

    @Override
    public void delete(Long id) {
        medecinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médecin introuvable avec l'id : " + id));
        medecinRepository.delete(id);
    }

    @Override
    public Optional<Medecin> findById(Long id) {
        return medecinRepository.findById(id);
    }

    @Override
    public List<Medecin> findAll() {
        return medecinRepository.findAll();
    }

    public Optional<Medecin> findByMatricule(String matricule) {
        return medecinRepository.findByMatricule(matricule);
    }

    public List<Medecin> findBySpecialite(String specialite) {
        return medecinRepository.findBySpecialite(specialite);
    }

    private void valider(Medecin medecin) {
        if (medecin.getNom() == null || medecin.getNom().isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire.");
        if (medecin.getPrenom() == null || medecin.getPrenom().isBlank())
            throw new IllegalArgumentException("Le prénom est obligatoire.");
        if (medecin.getMatricule() == null || medecin.getMatricule().isBlank())
            throw new IllegalArgumentException("Le matricule est obligatoire.");
        if (medecin.getSpecialite() == null || medecin.getSpecialite().isBlank())
            throw new IllegalArgumentException("La spécialité est obligatoire.");
    }
}
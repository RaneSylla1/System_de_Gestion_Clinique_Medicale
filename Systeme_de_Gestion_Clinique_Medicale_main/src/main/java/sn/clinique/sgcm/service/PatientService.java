package sn.clinique.sgcm.service;

import sn.clinique.sgcm.model.Patient;
import sn.clinique.sgcm.repository.PatientRepository;
import java.util.List;
import java.util.Optional;

public class PatientService implements GenericService<Patient, Long> {

    private final PatientRepository patientRepository = new PatientRepository();

    @Override
    public Patient save(Patient patient) {
        valider(patient);
        return patientRepository.save(patient);
    }

    @Override
    public Patient update(Patient patient) {
        valider(patient);
        return patientRepository.update(patient);
    }

    @Override
    public void delete(Long id) {
        patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient introuvable avec l'id : " + id));
        patientRepository.delete(id);
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    @Override
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public List<Patient> rechercher(String keyword) {
        if (keyword == null || keyword.isBlank())
            return patientRepository.findAll();
        return patientRepository.findByNomOuPrenom(keyword);
    }

    public List<Patient> findByTelephone(String telephone) {
        return patientRepository.findByTelephone(telephone);
    }

    //  Validation des champs obligatoires
    private void valider(Patient patient) {
        if (patient.getNom() == null || patient.getNom().isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire.");
        if (patient.getPrenom() == null || patient.getPrenom().isBlank())
            throw new IllegalArgumentException("Le prénom est obligatoire.");
        if (patient.getTelephone() == null || patient.getTelephone().isBlank())
            throw new IllegalArgumentException("Le téléphone est obligatoire.");
        if (patient.getDateNaissance() == null)
            throw new IllegalArgumentException("La date de naissance est obligatoire.");
    }
}
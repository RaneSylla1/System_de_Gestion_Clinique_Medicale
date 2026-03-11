package sn.clinique.sgcm.service;

import sn.clinique.sgcm.model.Utilisateur;
import sn.clinique.sgcm.repository.UtilisateurRepository;
import sn.clinique.sgcm.util.security.PasswordUtil;
import sn.clinique.sgcm.util.security.SessionUtilisateur;
import java.util.List;
import java.util.Optional;

public class UtilisateurService implements GenericService<Utilisateur, Long> {

    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepository();

    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        valider(utilisateur);
        //Vérifier unicité username
        utilisateurRepository.findByUsername(utilisateur.getUsername())
                .ifPresent(u -> { throw new IllegalArgumentException("Username déjà utilisé."); });
        // Hasher le mot de passe avant sauvegarde
        utilisateur.setPassword(PasswordUtil.hash(utilisateur.getPassword()));
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public Utilisateur update(Utilisateur utilisateur) {
        valider(utilisateur);
        return utilisateurRepository.update(utilisateur);
    }

    @Override
    public void delete(Long id) {
        utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'id : " + id));
        utilisateurRepository.delete(id);
    }

    @Override
    public Optional<Utilisateur> findById(Long id) {
        return utilisateurRepository.findById(id);
    }

    @Override
    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    // Login : ouvre la session si identifiants corrects
    public boolean login(String username, String password) {
        Optional<Utilisateur> opt = utilisateurRepository.findByUsername(username);
        if (opt.isPresent() && PasswordUtil.verify(password, opt.get().getPassword())) {
            SessionUtilisateur.ouvrir(opt.get());
            return true;
        }
        return false;
    }

    // ====Logout ====
    public void logout() {
        SessionUtilisateur.fermer();
    }

    private void valider(Utilisateur utilisateur) {
        if (utilisateur.getUsername() == null || utilisateur.getUsername().isBlank())
            throw new IllegalArgumentException("Le username est obligatoire.");
        if (utilisateur.getPassword() == null || utilisateur.getPassword().isBlank())
            throw new IllegalArgumentException("Le mot de passe est obligatoire.");
        if (utilisateur.getRole() == null)
            throw new IllegalArgumentException("Le rôle est obligatoire.");
    }
}
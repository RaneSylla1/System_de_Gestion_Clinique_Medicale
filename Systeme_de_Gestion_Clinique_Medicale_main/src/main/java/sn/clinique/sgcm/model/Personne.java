package sn.clinique.sgcm.model;

import jakarta.persistence.*;
import lombok.*;
import sn.clinique.sgcm.enums.Sexe;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class Personne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    private Sexe sexe;

    private String email;

    private String telephone;

    private String adresse;
    //  Déplacé depuis Patient (tous les Personne ont une adresse)

    public String getNomComplet() {
        return prenom + " " + nom;
    }
}
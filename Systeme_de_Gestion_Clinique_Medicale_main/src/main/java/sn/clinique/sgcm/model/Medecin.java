package sn.clinique.sgcm.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medecins")
public class Medecin extends Utilisateur {

    @Column(nullable = false, unique = true)
    private String matricule;

    @Column(nullable = false)
    private String specialite;
}
package sn.clinique.sgcm.model;

import jakarta.persistence.*;
import lombok.*;
import sn.clinique.sgcm.enums.GroupeSanguin;
import sn.clinique.sgcm.enums.Rehsus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patients")
public class Patient extends Personne {

    // hérite attribut de Personne

    @Enumerated(EnumType.STRING)
    private GroupeSanguin groupeSanguin;

    @Enumerated(EnumType.STRING)
    private Rehsus rehsus;

    @Column(columnDefinition = "TEXT")
    private String antecedentsMedicaux;
}
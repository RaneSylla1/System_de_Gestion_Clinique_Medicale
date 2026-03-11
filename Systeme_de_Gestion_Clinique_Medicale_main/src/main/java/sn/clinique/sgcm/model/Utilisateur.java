package sn.clinique.sgcm.model;

import jakarta.persistence.*;
import lombok.*;
import sn.clinique.sgcm.enums.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "utilisateurs")
//   utilisation de  @Getter @Setter à la place de @Data

public class Utilisateur extends Personne {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean actif = true;
}
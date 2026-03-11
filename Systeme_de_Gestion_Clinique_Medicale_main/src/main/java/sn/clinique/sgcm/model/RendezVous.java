package sn.clinique.sgcm.model;

import jakarta.persistence.*;
import lombok.*;
import sn.clinique.sgcm.enums.StatutRendezVous;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rendez_vous")
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(columnDefinition = "TEXT")
    private String motif;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRendezVous statutRendezVous = StatutRendezVous.PROGRAMME;  //  valeur par défaut

    @OneToOne(mappedBy = "rendezVous")
    private Consultation consultation;  //  relation inverse
}
package sn.clinique.sgcm.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "consultations")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "rendez_vous_id")
    private RendezVous rendezVous;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    private double prix;
    private LocalDate dateConsultation;

    @Column(columnDefinition = "TEXT")
    private String diagnostic;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @Column(columnDefinition = "TEXT")
    private String prescription;

    //  Relation inverse avec Facture
    @OneToOne(mappedBy = "consultation", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Facture facture;
}
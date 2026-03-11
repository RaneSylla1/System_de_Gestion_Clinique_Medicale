package sn.clinique.sgcm.model;

import jakarta.persistence.*;
import lombok.*;
import sn.clinique.sgcm.enums.ModePaiement;
import sn.clinique.sgcm.enums.StatutFacture;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroFacture;

    //  mappedBy côté Consultation, JoinColumn ici
    @OneToOne
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;

    private double montant;
    private LocalDate dateFacture;

    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;

    @Enumerated(EnumType.STRING)
    private StatutFacture statutFacture = StatutFacture.NONPAYE;
}
package ma.droguerie.caisse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Un paiement reçu pour une facture. */
@Entity
@Table(name = "reglements")
public class Reglement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "facture_id")
    private Facture facture;

    @Column(name = "date_reglement", nullable = false)
    private LocalDateTime date;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ModePaiement mode;

    protected Reglement() {
        // pour JPA
    }

    Reglement(Facture facture, BigDecimal montant, ModePaiement mode) {
        this.facture = facture;
        this.montant = montant;
        this.mode = mode;
        this.date = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public LocalDateTime getDate() { return date; }
    public BigDecimal getMontant() { return montant; }
    public ModePaiement getMode() { return mode; }
}

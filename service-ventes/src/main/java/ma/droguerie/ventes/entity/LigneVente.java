package ma.droguerie.ventes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import ma.droguerie.ventes.exception.RegleMetierException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Une ligne du bon : un produit, une quantité et le prix au moment de la vente.
 * La désignation et le prix sont copiés : si le prix change plus tard dans
 * service-produits, le bon garde le prix auquel le client a acheté.
 */
@Entity
@Table(name = "lignes_vente")
public class LigneVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "bon_id")
    private BonDeVente bon;

    /** Référence logique vers service-produits. */
    @Column(nullable = false)
    private Long produitId;

    @Column(nullable = false, length = 30)
    private String reference;

    @Column(nullable = false, length = 120)
    private String designation;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantite;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire;

    protected LigneVente() {
        // pour JPA
    }

    public LigneVente(Long produitId, String reference, String designation,
                      BigDecimal quantite, BigDecimal prixUnitaire) {
        if (quantite == null || quantite.signum() <= 0) {
            throw new RegleMetierException("La quantité de " + designation + " doit être positive");
        }
        this.produitId = produitId;
        this.reference = reference;
        this.designation = designation;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    void rattacherAuBon(BonDeVente bon) {
        this.bon = bon;
    }

    @JsonProperty("sousTotal")
    public BigDecimal sousTotal() {
        return prixUnitaire.multiply(quantite).setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() { return id; }
    public Long getProduitId() { return produitId; }
    public String getReference() { return reference; }
    public String getDesignation() { return designation; }
    public BigDecimal getQuantite() { return quantite; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
}

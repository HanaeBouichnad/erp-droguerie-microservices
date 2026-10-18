package ma.droguerie.produits.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import ma.droguerie.produits.exception.RegleMetierException;

import java.math.BigDecimal;

@Entity
@Table(name = "produits")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** RG-PRO-01 : la référence est unique. */
    @Column(nullable = false, unique = true, length = 30)
    private String reference;

    @Column(nullable = false)
    private String designation;

    @Column(nullable = false, length = 50)
    private String categorie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Unite unite;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prixAchat;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prixVente;

    /** Quantité décimale possible pour les produits vendus au mètre ou au kilo. */
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantiteStock = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal seuilAlerte;

    /** Un produit n'est jamais supprimé (il figure dans d'anciennes ventes) : il est désactivé. */
    @Column(nullable = false)
    private boolean actif = true;

    /** Verrouillage optimiste : deux ventes simultanées ne peuvent pas écraser le stock l'une de l'autre. */
    @Version
    private Long version;

    protected Produit() {
        // requis par JPA
    }

    public Produit(String reference, String designation, String categorie, Unite unite,
                   BigDecimal prixAchat, BigDecimal prixVente, BigDecimal seuilAlerte) {
        this.reference = reference;
        this.designation = designation;
        this.categorie = categorie;
        this.unite = unite;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.seuilAlerte = seuilAlerte;
    }

    /** RG-PRO-04 : le produit est signalé quand son stock atteint ou passe sous le seuil d'alerte. */
    @JsonProperty("enRupture")
    public boolean estEnRupture() {
        return quantiteStock.compareTo(seuilAlerte) <= 0;
    }

    /** RG-PRO-03 : le stock ne peut jamais devenir négatif. */
    public void retirerStock(BigDecimal quantite) {
        verifierQuantite(quantite);
        if (quantiteStock.compareTo(quantite) < 0) {
            throw new RegleMetierException("Stock insuffisant pour " + designation
                    + " : demandé " + quantite.stripTrailingZeros().toPlainString()
                    + ", disponible " + quantiteStock.stripTrailingZeros().toPlainString());
        }
        quantiteStock = quantiteStock.subtract(quantite);
    }

    public void ajouterStock(BigDecimal quantite) {
        verifierQuantite(quantite);
        quantiteStock = quantiteStock.add(quantite);
    }

    /** RG-PRO-02 : quantité strictement positive, et entière pour un produit vendu à la pièce. */
    public void verifierQuantite(BigDecimal quantite) {
        if (quantite == null || quantite.signum() <= 0) {
            throw new RegleMetierException("La quantité doit être strictement positive");
        }
        if (!unite.accepteDecimales() && quantite.stripTrailingZeros().scale() > 0) {
            throw new RegleMetierException(designation + " se vend à la pièce : quantité entière attendue");
        }
    }

    public void modifier(String reference, String designation, String categorie, Unite unite,
                         BigDecimal prixAchat, BigDecimal prixVente, BigDecimal seuilAlerte) {
        this.reference = reference;
        this.designation = designation;
        this.categorie = categorie;
        this.unite = unite;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.seuilAlerte = seuilAlerte;
    }

    public void desactiver() {
        this.actif = false;
    }

    public Long getId() { return id; }
    public String getReference() { return reference; }
    public String getDesignation() { return designation; }
    public String getCategorie() { return categorie; }
    public Unite getUnite() { return unite; }
    public BigDecimal getPrixAchat() { return prixAchat; }
    public BigDecimal getPrixVente() { return prixVente; }
    public BigDecimal getQuantiteStock() { return quantiteStock; }
    public BigDecimal getSeuilAlerte() { return seuilAlerte; }
    public boolean isActif() { return actif; }
    public Long getVersion() { return version; }
}
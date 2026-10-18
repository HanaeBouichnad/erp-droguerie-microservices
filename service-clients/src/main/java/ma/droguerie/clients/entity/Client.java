package ma.droguerie.clients.entity;

import jakarta.persistence.*;
import ma.droguerie.clients.exception.RegleMetierException;

import java.math.BigDecimal;

/**
 * Client habitué de la droguerie.
 * Le plafond de crédit est le montant maximum que le client a le droit de devoir.
 * Un plafond à 0 signifie : pas de vente à crédit pour ce client.
 */
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nom;

    @Column(length = 60)
    private String prenom;

    @Column(nullable = false, unique = true, length = 20)
    private String telephone;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal plafondCredit;

    @Column(nullable = false)
    private boolean actif = true;

    protected Client() {
        // pour JPA
    }

    public Client(String nom, String prenom, String telephone, BigDecimal plafondCredit) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.plafondCredit = verifierPlafond(plafondCredit);
    }

    public void modifier(String nom, String prenom, String telephone, BigDecimal plafondCredit) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.plafondCredit = verifierPlafond(plafondCredit);
    }

    /** On ne supprime jamais un client : il peut encore avoir des dettes. */
    public void desactiver() {
        this.actif = false;
    }

    private static BigDecimal verifierPlafond(BigDecimal plafond) {
        if (plafond == null || plafond.signum() < 0) {
            throw new RegleMetierException("Le plafond de crédit ne peut pas être négatif");
        }
        return plafond;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getTelephone() { return telephone; }
    public BigDecimal getPlafondCredit() { return plafondCredit; }
    public boolean isActif() { return actif; }
}

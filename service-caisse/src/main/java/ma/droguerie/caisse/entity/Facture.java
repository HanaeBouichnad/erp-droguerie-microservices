package ma.droguerie.caisse.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import ma.droguerie.caisse.exception.RegleMetierException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Facture établie par la caissière quand elle encaisse un bon de vente.
 * Elle peut être réglée en plusieurs fois : c'est ainsi qu'on suit le crédit d'un client.
 */
@Entity
@Table(name = "factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @Column(name = "date_facture", nullable = false)
    private LocalDateTime date;

    /** Référence logique vers service-ventes : un bon ne donne lieu qu'à une seule facture. */
    @Column(nullable = false, unique = true)
    private Long bonDeVenteId;

    @Column(nullable = false, length = 20)
    private String numeroBon;

    /** Référence logique vers service-clients. Vide pour un client de passage. */
    private Long clientId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTTC;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private StatutFacture statut;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Reglement> reglements = new ArrayList<>();

    protected Facture() {
        // pour JPA
    }

    public Facture(String numero, Long bonDeVenteId, String numeroBon, Long clientId, BigDecimal montantTTC) {
        this.numero = numero;
        this.bonDeVenteId = bonDeVenteId;
        this.numeroBon = numeroBon;
        this.clientId = clientId;
        this.montantTTC = montantTTC;
        this.date = LocalDateTime.now();
        this.statut = StatutFacture.NON_PAYEE;
    }

    /**
     * Enregistre un paiement : celui fait à la caisse, ou un remboursement plus tard.
     * On ne peut pas payer plus que ce qui reste dû.
     */
    public Reglement enregistrerReglement(BigDecimal montant, ModePaiement mode) {
        if (montant == null || montant.signum() <= 0) {
            throw new RegleMetierException("Le montant du règlement doit être positif");
        }
        if (statut == StatutFacture.PAYEE) {
            throw new RegleMetierException("La facture " + numero + " est déjà entièrement payée");
        }
        if (montant.compareTo(resteAPayer()) > 0) {
            throw new RegleMetierException("Le montant " + montant + " DH dépasse le reste à payer de la facture "
                    + numero + " (" + resteAPayer() + " DH)");
        }
        Reglement reglement = new Reglement(this, montant, mode);
        reglements.add(reglement);
        mettreAJourStatut();
        return reglement;
    }

    @JsonProperty("montantPaye")
    public BigDecimal montantPaye() {
        return reglements.stream()
                .map(Reglement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Ce que le client doit encore : c'est sa dette pour cette facture. */
    @JsonProperty("resteAPayer")
    public BigDecimal resteAPayer() {
        return montantTTC.subtract(montantPaye());
    }

    private void mettreAJourStatut() {
        if (resteAPayer().signum() == 0) {
            statut = StatutFacture.PAYEE;
        } else if (montantPaye().signum() > 0) {
            statut = StatutFacture.PARTIELLEMENT_PAYEE;
        } else {
            statut = StatutFacture.NON_PAYEE;
        }
    }

    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public LocalDateTime getDate() { return date; }
    public Long getBonDeVenteId() { return bonDeVenteId; }
    public String getNumeroBon() { return numeroBon; }
    public Long getClientId() { return clientId; }
    public BigDecimal getMontantTTC() { return montantTTC; }
    public StatutFacture getStatut() { return statut; }
    public List<Reglement> getReglements() { return reglements; }
}

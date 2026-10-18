package ma.droguerie.ventes.entity;

import jakarta.persistence.*;
import ma.droguerie.ventes.exception.RegleMetierException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Bon de vente : la commande du client, préparée par le vendeur
 * et transmise à la caisse pour être encaissée.
 */
@Entity
@Table(name = "bons_de_vente")
public class BonDeVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @Column(name = "date_bon", nullable = false)
    private LocalDateTime date;

    @Column(nullable = false, length = 60)
    private String vendeur;

    /** Référence logique vers service-clients. Vide pour un client de passage. */
    private Long clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutBon statut;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "bon", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LigneVente> lignes = new ArrayList<>();

    protected BonDeVente() {
        // pour JPA
    }

    public BonDeVente(String numero, String vendeur, Long clientId) {
        this.numero = numero;
        this.vendeur = vendeur;
        this.clientId = clientId;
        this.date = LocalDateTime.now();
        this.statut = StatutBon.EN_ATTENTE;
    }

    public void ajouterLigne(LigneVente ligne) {
        ligne.rattacherAuBon(this);
        lignes.add(ligne);
        montantTotal = calculerTotal();
    }

    /** Le total du bon est la somme des sous-totaux de ses lignes. */
    public BigDecimal calculerTotal() {
        return lignes.stream()
                .map(LigneVente::sousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Seul un bon en attente peut être annulé : un bon encaissé est déjà payé. */
    public void annuler() {
        verifierEnAttente("annulé");
        statut = StatutBon.ANNULE;
    }

    /** Appelé quand la caissière a encaissé le bon. */
    public void marquerEncaisse() {
        verifierEnAttente("encaissé");
        statut = StatutBon.ENCAISSE;
    }

    private void verifierEnAttente(String action) {
        if (statut != StatutBon.EN_ATTENTE) {
            throw new RegleMetierException("Le bon " + numero + " ne peut pas être " + action
                    + " : son statut est " + statut);
        }
    }

    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public LocalDateTime getDate() { return date; }
    public String getVendeur() { return vendeur; }
    public Long getClientId() { return clientId; }
    public StatutBon getStatut() { return statut; }
    public BigDecimal getMontantTotal() { return montantTotal; }
    public List<LigneVente> getLignes() { return lignes; }
}

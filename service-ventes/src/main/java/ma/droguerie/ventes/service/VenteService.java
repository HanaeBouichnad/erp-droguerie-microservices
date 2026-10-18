package ma.droguerie.ventes.service;

import ma.droguerie.ventes.client.ClientClient;
import ma.droguerie.ventes.client.ClientDto;
import ma.droguerie.ventes.client.ProduitClient;
import ma.droguerie.ventes.client.ProduitDto;
import ma.droguerie.ventes.dto.BonDeVenteRequest;
import ma.droguerie.ventes.entity.BonDeVente;
import ma.droguerie.ventes.entity.LigneVente;
import ma.droguerie.ventes.entity.StatutBon;
import ma.droguerie.ventes.exception.RegleMetierException;
import ma.droguerie.ventes.exception.RessourceIntrouvableException;
import ma.droguerie.ventes.repository.BonDeVenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VenteService {

    private final BonDeVenteRepository repository;
    private final ProduitClient produitClient;
    private final ClientClient clientClient;

    public VenteService(BonDeVenteRepository repository, ProduitClient produitClient, ClientClient clientClient) {
        this.repository = repository;
        this.produitClient = produitClient;
        this.clientClient = clientClient;
    }

    @Transactional(readOnly = true)
    public List<BonDeVente> lister(StatutBon statut) {
        return statut == null
                ? repository.findAllByOrderByDateDesc()
                : repository.findByStatutOrderByDateDesc(statut);
    }

    @Transactional(readOnly = true)
    public BonDeVente trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Bon de vente " + id + " introuvable"));
    }

    /**
     * Le vendeur crée un bon de vente.
     * 1. Si un client est indiqué, on vérifie qu'il existe (service-clients).
     * 2. Pour chaque produit, on vérifie le stock (service-produits).
     * 3. On enregistre le bon EN_ATTENTE : le stock ne bouge pas encore,
     *    il sera retiré quand la caissière encaissera.
     */
    public BonDeVente creer(BonDeVenteRequest request) {
        if (request.clientId() != null) {
            ClientDto client = clientClient.trouver(request.clientId());
            if (Boolean.FALSE.equals(client.actif())) {
                throw new RegleMetierException("Le client " + client.nom() + " est désactivé");
            }
        }

        BonDeVente bon = new BonDeVente(genererNumero(), request.vendeur(), request.clientId());
        for (Map.Entry<Long, BigDecimal> entree : regrouperParProduit(request.lignes()).entrySet()) {
            ProduitDto produit = produitClient.trouver(entree.getKey());
            BigDecimal quantite = entree.getValue();
            verifierDisponibilite(produit, quantite);
            bon.ajouterLigne(new LigneVente(produit.id(), produit.reference(), produit.designation(),
                    quantite, produit.prixVente()));
        }
        return repository.save(bon);
    }

    public BonDeVente annuler(Long id) {
        BonDeVente bon = trouver(id);
        bon.annuler();
        return bon;
    }

    /**
     * Appelé par service-caisse une fois le paiement accepté.
     * On revérifie tout le stock avant de retirer quoi que ce soit,
     * pour ne pas retirer une partie des produits puis échouer sur un autre.
     */
    public BonDeVente encaisser(Long id) {
        BonDeVente bon = trouver(id);
        if (bon.getStatut() != StatutBon.EN_ATTENTE) {
            throw new RegleMetierException("Le bon " + bon.getNumero() + " n'est pas en attente : "
                    + bon.getStatut());
        }
        for (LigneVente ligne : bon.getLignes()) {
            verifierDisponibilite(produitClient.trouver(ligne.getProduitId()), ligne.getQuantite());
        }
        for (LigneVente ligne : bon.getLignes()) {
            produitClient.retirerStock(ligne.getProduitId(), ligne.getQuantite());
        }
        bon.marquerEncaisse();
        return bon;
    }

    private void verifierDisponibilite(ProduitDto produit, BigDecimal quantite) {
        if (Boolean.FALSE.equals(produit.actif())) {
            throw new RegleMetierException("Le produit " + produit.designation() + " n'est plus vendu");
        }
        if ("PIECE".equals(produit.unite()) && quantite.stripTrailingZeros().scale() > 0) {
            throw new RegleMetierException("La quantité de " + produit.designation()
                    + " doit être un nombre entier (vendu à la pièce)");
        }
        if (produit.quantiteStock().compareTo(quantite) < 0) {
            throw new RegleMetierException("Stock insuffisant pour " + produit.designation()
                    + " : disponible " + produit.quantiteStock().stripTrailingZeros().toPlainString()
                    + ", demandé " + quantite.stripTrailingZeros().toPlainString());
        }
    }

    /** Si le vendeur saisit deux fois le même produit, on additionne les quantités. */
    private Map<Long, BigDecimal> regrouperParProduit(List<BonDeVenteRequest.Ligne> lignes) {
        Map<Long, BigDecimal> quantites = new LinkedHashMap<>();
        for (BonDeVenteRequest.Ligne ligne : lignes) {
            quantites.merge(ligne.produitId(), ligne.quantite(), BigDecimal::add);
        }
        return quantites;
    }

    private String genererNumero() {
        return String.format("BV-%05d", repository.count() + 1);
    }
}

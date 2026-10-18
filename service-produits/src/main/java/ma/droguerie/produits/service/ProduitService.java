package ma.droguerie.produits.service;

import ma.droguerie.produits.dto.DisponibiliteResponse;
import ma.droguerie.produits.dto.ProduitRequest;
import ma.droguerie.produits.dto.ReceptionRequest;
import ma.droguerie.produits.entity.Produit;
import ma.droguerie.produits.exception.RegleMetierException;
import ma.droguerie.produits.exception.RessourceIntrouvableException;
import ma.droguerie.produits.repository.ProduitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProduitService {

    private static final Logger log = LoggerFactory.getLogger(ProduitService.class);

    private final ProduitRepository repository;

    public ProduitService(ProduitRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Produit> lister(String recherche, String categorie) {
        return repository.rechercher(nettoyer(recherche), nettoyer(categorie));
    }

    @Transactional(readOnly = true)
    public List<Produit> listerEnAlerte() {
        return repository.findEnAlerte();
    }

    @Transactional(readOnly = true)
    public Produit trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Produit " + id + " introuvable"));
    }

    public Produit creer(ProduitRequest req) {
        if (repository.existsByReference(req.reference())) {
            throw new RegleMetierException("La référence " + req.reference() + " existe déjà");
        }
        return repository.save(new Produit(req.reference(), req.designation(), req.categorie(), req.unite(),
                req.prixAchat(), req.prixVente(), req.seuilAlerte()));
    }

    public Produit modifier(Long id, ProduitRequest req) {
        Produit produit = trouver(id);
        if (!produit.getReference().equals(req.reference()) && repository.existsByReference(req.reference())) {
            throw new RegleMetierException("La référence " + req.reference() + " existe déjà");
        }
        produit.modifier(req.reference(), req.designation(), req.categorie(), req.unite(),
                req.prixAchat(), req.prixVente(), req.seuilAlerte());
        return produit;
    }

    public void desactiver(Long id) {
        trouver(id).desactiver();
    }

    /** Appelé par service-ventes pendant la saisie d'un bon de vente (RG-VEN-01). */
    @Transactional(readOnly = true)
    public DisponibiliteResponse verifierDisponibilite(Long id, BigDecimal quantite) {
        Produit produit = trouver(id);
        produit.verifierQuantite(quantite);
        boolean disponible = produit.isActif() && produit.getQuantiteStock().compareTo(quantite) >= 0;
        return new DisponibiliteResponse(produit.getId(), produit.getDesignation(), produit.getUnite(),
                produit.getPrixVente(), quantite, produit.getQuantiteStock(), disponible);
    }

    /** Appelé par service-ventes à l'encaissement d'un bon (RG-VEN-03). */
    public Produit retirerStock(Long id, BigDecimal quantite) {
        Produit produit = trouver(id);
        if (!produit.isActif()) {
            throw new RegleMetierException("Le produit " + produit.getDesignation() + " n'est plus vendu");
        }
        produit.retirerStock(quantite);
        return produit;
    }

    /** Une réception est appliquée en entier ou pas du tout (une seule transaction). */
    public List<Produit> enregistrerReception(ReceptionRequest req) {
        List<Produit> produits = req.lignes().stream()
                .map(ligne -> {
                    Produit produit = trouver(ligne.produitId());
                    produit.ajouterStock(ligne.quantite());
                    return produit;
                })
                .toList();
        log.info("Réception du fournisseur {} : {} produit(s) mis à jour", req.fournisseur(), produits.size());
        return produits;
    }

    private static String nettoyer(String valeur) {
        return valeur == null ? "" : valeur.trim();
    }
}
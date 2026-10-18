package ma.droguerie.produits.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ma.droguerie.produits.dto.DisponibiliteResponse;
import ma.droguerie.produits.dto.ProduitRequest;
import ma.droguerie.produits.entity.Produit;
import ma.droguerie.produits.service.ProduitService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/produits")
@Tag(name = "Produits", description = "Catalogue de la droguerie et gestion du stock")
public class ProduitController {

    private final ProduitService service;

    public ProduitController(ProduitService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Rechercher des produits par nom ou référence, et filtrer par catégorie")
    public List<Produit> lister(@RequestParam(required = false) String recherche,
                                @RequestParam(required = false) String categorie) {
        return service.lister(recherche, categorie);
    }

    @GetMapping("/alertes")
    @Operation(summary = "Produits dont le stock est sous le seuil d'alerte")
    public List<Produit> alertes() {
        return service.listerEnAlerte();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un produit")
    public Produit trouver(@PathVariable Long id) {
        return service.trouver(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un produit (stock initial à 0, à alimenter par une réception)")
    public Produit creer(@Valid @RequestBody ProduitRequest req) {
        return service.creer(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un produit (référence, prix, seuil...) sans toucher au stock")
    public Produit modifier(@PathVariable Long id, @Valid @RequestBody ProduitRequest req) {
        return service.modifier(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Désactiver un produit (il reste visible dans les anciennes ventes)")
    public void desactiver(@PathVariable Long id) {
        service.desactiver(id);
    }

    @GetMapping("/{id}/disponibilite")
    @Operation(summary = "Vérifier si une quantité est disponible (appelé par service-ventes)")
    public DisponibiliteResponse disponibilite(@PathVariable Long id, @RequestParam BigDecimal quantite) {
        return service.verifierDisponibilite(id, quantite);
    }

    @PutMapping("/{id}/retirer")
    @Operation(summary = "Retirer du stock à l'encaissement d'une vente (appelé par service-ventes)")
    public Produit retirer(@PathVariable Long id, @RequestParam BigDecimal quantite) {
        return service.retirerStock(id, quantite);
    }
}
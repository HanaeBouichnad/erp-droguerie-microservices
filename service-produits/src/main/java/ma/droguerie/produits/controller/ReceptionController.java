package ma.droguerie.produits.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ma.droguerie.produits.dto.ReceptionRequest;
import ma.droguerie.produits.entity.Produit;
import ma.droguerie.produits.service.ProduitService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/receptions")
@Tag(name = "Réceptions", description = "Entrées de marchandise livrée par les fournisseurs")
public class ReceptionController {

    private final ProduitService service;

    public ReceptionController(ProduitService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Enregistrer une livraison fournisseur : augmente le stock de chaque produit reçu")
    public List<Produit> enregistrer(@Valid @RequestBody ReceptionRequest req) {
        return service.enregistrerReception(req);
    }
}
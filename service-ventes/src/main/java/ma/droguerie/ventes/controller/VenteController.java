package ma.droguerie.ventes.controller;

import jakarta.validation.Valid;
import ma.droguerie.ventes.dto.BonDeVenteRequest;
import ma.droguerie.ventes.entity.BonDeVente;
import ma.droguerie.ventes.entity.StatutBon;
import ma.droguerie.ventes.service.VenteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventes")
public class VenteController {

    private final VenteService service;

    public VenteController(VenteService service) {
        this.service = service;
    }

    /** Liste des bons ; la caisse affiche ceux qui sont EN_ATTENTE. */
    @GetMapping
    public List<BonDeVente> lister(@RequestParam(required = false) StatutBon statut) {
        return service.lister(statut);
    }

    @GetMapping("/{id}")
    public BonDeVente trouver(@PathVariable Long id) {
        return service.trouver(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BonDeVente creer(@Valid @RequestBody BonDeVenteRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}/annuler")
    public BonDeVente annuler(@PathVariable Long id) {
        return service.annuler(id);
    }

    /** Appelé par service-caisse : retire le stock et passe le bon à ENCAISSE. */
    @PutMapping("/{id}/encaisser")
    public BonDeVente encaisser(@PathVariable Long id) {
        return service.encaisser(id);
    }
}

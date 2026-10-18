package ma.droguerie.caisse.controller;

import jakarta.validation.Valid;
import ma.droguerie.caisse.dto.DetteClientResponse;
import ma.droguerie.caisse.dto.EncaissementRequest;
import ma.droguerie.caisse.dto.ReglementRequest;
import ma.droguerie.caisse.entity.Facture;
import ma.droguerie.caisse.service.CaisseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/caisse")
public class CaisseController {

    private final CaisseService service;

    public CaisseController(CaisseService service) {
        this.service = service;
    }

    @PostMapping("/encaissements")
    @ResponseStatus(HttpStatus.CREATED)
    public Facture encaisser(@Valid @RequestBody EncaissementRequest request) {
        return service.encaisser(request);
    }

    @GetMapping("/factures")
    public List<Facture> lister() {
        return service.lister();
    }

    @GetMapping("/factures/{id}")
    public Facture trouver(@PathVariable Long id) {
        return service.trouver(id);
    }

    @PostMapping("/factures/{id}/reglements")
    public Facture enregistrerRemboursement(@PathVariable Long id, @Valid @RequestBody ReglementRequest request) {
        return service.enregistrerRemboursement(id, request);
    }

    @GetMapping("/clients/{clientId}/dette")
    public DetteClientResponse detteClient(@PathVariable Long clientId) {
        return service.detteClient(clientId);
    }
}

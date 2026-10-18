package ma.droguerie.caisse.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/** Appels vers service-ventes. */
@FeignClient(name = "service-ventes")
public interface VenteClient {

    @GetMapping("/api/ventes/{id}")
    BonDeVenteDto trouver(@PathVariable("id") Long id);

    @PutMapping("/api/ventes/{id}/encaisser")
    BonDeVenteDto encaisser(@PathVariable("id") Long id);
}

package ma.droguerie.ventes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * Appels vers service-produits.
 * On donne seulement le nom du service : Eureka fournit son adresse.
 */
@FeignClient(name = "service-produits")
public interface ProduitClient {

    @GetMapping("/api/produits/{id}")
    ProduitDto trouver(@PathVariable("id") Long id);

    @PutMapping("/api/produits/{id}/retirer")
    void retirerStock(@PathVariable("id") Long id, @RequestParam("quantite") BigDecimal quantite);
}

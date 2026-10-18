package ma.droguerie.ventes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Appels vers service-clients. */
@FeignClient(name = "service-clients")
public interface ClientClient {

    @GetMapping("/api/clients/{id}")
    ClientDto trouver(@PathVariable("id") Long id);
}

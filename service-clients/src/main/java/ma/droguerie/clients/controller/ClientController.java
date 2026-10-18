package ma.droguerie.clients.controller;

import jakarta.validation.Valid;
import ma.droguerie.clients.dto.ClientRequest;
import ma.droguerie.clients.entity.Client;
import ma.droguerie.clients.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @GetMapping
    public List<Client> lister(@RequestParam(required = false) String recherche) {
        return service.lister(recherche);
    }

    @GetMapping("/{id}")
    public Client trouver(@PathVariable Long id) {
        return service.trouver(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client creer(@Valid @RequestBody ClientRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    public Client modifier(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return service.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactiver(@PathVariable Long id) {
        service.desactiver(id);
    }
}

package ma.droguerie.clients.service;

import ma.droguerie.clients.dto.ClientRequest;
import ma.droguerie.clients.entity.Client;
import ma.droguerie.clients.exception.RegleMetierException;
import ma.droguerie.clients.exception.RessourceIntrouvableException;
import ma.droguerie.clients.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Client> lister(String recherche) {
        String nom = recherche == null ? "" : recherche.trim();
        return repository.findByActifTrueAndNomContainingIgnoreCaseOrderByNomAsc(nom);
    }

    @Transactional(readOnly = true)
    public Client trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Client " + id + " introuvable"));
    }

    public Client creer(ClientRequest request) {
        if (repository.existsByTelephone(request.telephone())) {
            throw new RegleMetierException("Un client avec le téléphone " + request.telephone() + " existe déjà");
        }
        Client client = new Client(request.nom(), request.prenom(), request.telephone(), request.plafondCredit());
        return repository.save(client);
    }

    public Client modifier(Long id, ClientRequest request) {
        Client client = trouver(id);
        if (repository.existsByTelephoneAndIdNot(request.telephone(), id)) {
            throw new RegleMetierException("Un autre client utilise déjà le téléphone " + request.telephone());
        }
        client.modifier(request.nom(), request.prenom(), request.telephone(), request.plafondCredit());
        return client;
    }

    public void desactiver(Long id) {
        trouver(id).desactiver();
    }
}

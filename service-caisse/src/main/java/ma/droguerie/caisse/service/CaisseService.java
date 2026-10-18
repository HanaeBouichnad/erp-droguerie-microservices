package ma.droguerie.caisse.service;

import ma.droguerie.caisse.client.BonDeVenteDto;
import ma.droguerie.caisse.client.ClientClient;
import ma.droguerie.caisse.client.ClientDto;
import ma.droguerie.caisse.client.VenteClient;
import ma.droguerie.caisse.dto.DetteClientResponse;
import ma.droguerie.caisse.dto.EncaissementRequest;
import ma.droguerie.caisse.dto.ReglementRequest;
import ma.droguerie.caisse.entity.Facture;
import ma.droguerie.caisse.entity.StatutFacture;
import ma.droguerie.caisse.exception.RegleMetierException;
import ma.droguerie.caisse.exception.RessourceIntrouvableException;
import ma.droguerie.caisse.repository.FactureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CaisseService {

    private final FactureRepository repository;
    private final VenteClient venteClient;
    private final ClientClient clientClient;

    public CaisseService(FactureRepository repository, VenteClient venteClient, ClientClient clientClient) {
        this.repository = repository;
        this.venteClient = venteClient;
        this.clientClient = clientClient;
    }

    /**
     * La caissière encaisse un bon de vente.
     * 1. Le bon doit être EN_ATTENTE.
     * 2. S'il reste une partie à payer (vente à crédit) : le client doit être enregistré
     *    et sa dette totale ne doit pas dépasser son plafond de crédit.
     * 3. service-ventes retire le stock et passe le bon à ENCAISSE.
     * 4. On crée la facture et on enregistre le paiement reçu.
     */
    public Facture encaisser(EncaissementRequest request) {
        if (repository.existsByBonDeVenteId(request.bonDeVenteId())) {
            throw new RegleMetierException("Ce bon de vente a déjà été encaissé");
        }
        BonDeVenteDto bon = venteClient.trouver(request.bonDeVenteId());
        if (!"EN_ATTENTE".equals(bon.statut())) {
            throw new RegleMetierException("Seul un bon en attente peut être encaissé (statut du bon "
                    + bon.numero() + " : " + bon.statut() + ")");
        }

        BigDecimal total = bon.montantTotal();
        if (request.montantPaye().compareTo(total) > 0) {
            throw new RegleMetierException("Le montant payé dépasse le total du bon (" + total
                    + " DH) : rendez la monnaie et saisissez le montant exact");
        }

        BigDecimal resteAPayer = total.subtract(request.montantPaye());
        if (resteAPayer.signum() > 0) {
            verifierCredit(bon.clientId(), resteAPayer);
        }

        venteClient.encaisser(bon.id());

        Facture facture = new Facture(genererNumero(), bon.id(), bon.numero(), bon.clientId(), total);
        if (request.montantPaye().signum() > 0) {
            facture.enregistrerReglement(request.montantPaye(), request.mode());
        }
        return repository.save(facture);
    }

    /** Un client revient payer une partie (ou la totalité) de ce qu'il doit sur une facture. */
    public Facture enregistrerRemboursement(Long factureId, ReglementRequest request) {
        Facture facture = trouver(factureId);
        facture.enregistrerReglement(request.montant(), request.mode());
        return facture;
    }

    @Transactional(readOnly = true)
    public DetteClientResponse detteClient(Long clientId) {
        List<Facture> factures = facturesNonPayees(clientId);
        return new DetteClientResponse(clientId, totalDu(factures), factures);
    }

    @Transactional(readOnly = true)
    public List<Facture> lister() {
        return repository.findAllByOrderByDateDesc();
    }

    @Transactional(readOnly = true)
    public Facture trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Facture " + id + " introuvable"));
    }

    private void verifierCredit(Long clientId, BigDecimal nouveauCredit) {
        if (clientId == null) {
            throw new RegleMetierException("Une vente à crédit n'est possible que pour un client enregistré");
        }
        ClientDto client = clientClient.trouver(clientId);
        BigDecimal detteActuelle = totalDu(facturesNonPayees(clientId));
        BigDecimal detteApresVente = detteActuelle.add(nouveauCredit);
        if (detteApresVente.compareTo(client.plafondCredit()) > 0) {
            throw new RegleMetierException("Plafond de crédit dépassé pour " + client.nom()
                    + " : dette actuelle " + detteActuelle + " DH + nouveau crédit " + nouveauCredit
                    + " DH = " + detteApresVente + " DH, plafond " + client.plafondCredit() + " DH");
        }
    }

    private List<Facture> facturesNonPayees(Long clientId) {
        return repository.findByClientIdAndStatutNotOrderByDateAsc(clientId, StatutFacture.PAYEE);
    }

    private BigDecimal totalDu(List<Facture> factures) {
        return factures.stream()
                .map(Facture::resteAPayer)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String genererNumero() {
        return String.format("FA-%05d", repository.count() + 1);
    }
}

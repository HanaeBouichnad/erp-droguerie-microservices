package ma.droguerie.caisse.repository;

import ma.droguerie.caisse.entity.Facture;
import ma.droguerie.caisse.entity.StatutFacture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FactureRepository extends JpaRepository<Facture, Long> {

    boolean existsByBonDeVenteId(Long bonDeVenteId);

    List<Facture> findAllByOrderByDateDesc();

    /** Les factures d'un client qui ne sont pas encore entièrement payées. */
    List<Facture> findByClientIdAndStatutNotOrderByDateAsc(Long clientId, StatutFacture statut);
}

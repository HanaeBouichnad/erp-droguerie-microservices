package ma.droguerie.ventes.repository;

import ma.droguerie.ventes.entity.BonDeVente;
import ma.droguerie.ventes.entity.StatutBon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BonDeVenteRepository extends JpaRepository<BonDeVente, Long> {

    List<BonDeVente> findAllByOrderByDateDesc();

    List<BonDeVente> findByStatutOrderByDateDesc(StatutBon statut);
}

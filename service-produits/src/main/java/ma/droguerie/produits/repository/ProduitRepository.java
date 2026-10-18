package ma.droguerie.produits.repository;

import ma.droguerie.produits.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    boolean existsByReference(String reference);

    /** Recherche par désignation ou référence, filtre optionnel par catégorie (chaîne vide = pas de filtre). */
    @Query("""
            select p from Produit p
            where p.actif = true
              and (lower(p.designation) like lower(concat('%', :recherche, '%'))
                   or lower(p.reference) like lower(concat('%', :recherche, '%')))
              and (:categorie = '' or lower(p.categorie) = lower(:categorie))
            order by p.designation
            """)
    List<Produit> rechercher(@Param("recherche") String recherche, @Param("categorie") String categorie);

    @Query("select p from Produit p where p.actif = true and p.quantiteStock <= p.seuilAlerte order by p.designation")
    List<Produit> findEnAlerte();
}
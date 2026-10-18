package ma.droguerie.produits.dto;

import ma.droguerie.produits.entity.Unite;

import java.math.BigDecimal;

/** Réponse envoyée à service-ventes pendant la création d'un bon de vente. */
public record DisponibiliteResponse(
        Long produitId,
        String designation,
        Unite unite,
        BigDecimal prixVente,
        BigDecimal quantiteDemandee,
        BigDecimal quantiteStock,
        boolean disponible
) {
}
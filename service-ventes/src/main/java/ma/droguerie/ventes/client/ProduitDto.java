package ma.droguerie.ventes.client;

import java.math.BigDecimal;

/** Les informations d'un produit dont service-ventes a besoin. */
public record ProduitDto(
        Long id,
        String reference,
        String designation,
        String unite,
        BigDecimal prixVente,
        BigDecimal quantiteStock,
        Boolean actif
) {
}

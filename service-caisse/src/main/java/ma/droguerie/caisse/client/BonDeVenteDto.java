package ma.droguerie.caisse.client;

import java.math.BigDecimal;

/** Les informations d'un bon de vente dont la caisse a besoin. */
public record BonDeVenteDto(
        Long id,
        String numero,
        Long clientId,
        String statut,
        BigDecimal montantTotal
) {
}

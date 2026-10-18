package ma.droguerie.caisse.client;

import java.math.BigDecimal;

/** Les informations d'un client dont la caisse a besoin. */
public record ClientDto(
        Long id,
        String nom,
        String prenom,
        BigDecimal plafondCredit
) {
}

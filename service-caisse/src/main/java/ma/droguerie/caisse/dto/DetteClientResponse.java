package ma.droguerie.caisse.dto;

import ma.droguerie.caisse.entity.Facture;

import java.math.BigDecimal;
import java.util.List;

/** Ce qu'un client doit à la droguerie, et le détail des factures non soldées. */
public record DetteClientResponse(
        Long clientId,
        BigDecimal dette,
        List<Facture> facturesNonPayees
) {
}

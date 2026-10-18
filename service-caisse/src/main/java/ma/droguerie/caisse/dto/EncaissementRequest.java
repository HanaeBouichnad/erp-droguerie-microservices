package ma.droguerie.caisse.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ma.droguerie.caisse.entity.ModePaiement;

import java.math.BigDecimal;

/**
 * Ce que la caissière saisit : le bon à encaisser, ce que le client paie maintenant
 * et comment. Si le montant payé est inférieur au total, le reste passe en crédit.
 */
public record EncaissementRequest(
        @NotNull(message = "Le bon de vente est obligatoire")
        Long bonDeVenteId,

        @NotNull(message = "Le montant payé est obligatoire (0 si tout est à crédit)")
        @PositiveOrZero(message = "Le montant payé ne peut pas être négatif")
        BigDecimal montantPaye,

        @NotNull(message = "Le mode de paiement est obligatoire")
        ModePaiement mode
) {
}

package ma.droguerie.caisse.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ma.droguerie.caisse.entity.ModePaiement;

import java.math.BigDecimal;

/** Un client revient payer une partie de ce qu'il doit. */
public record ReglementRequest(
        @NotNull(message = "Le montant est obligatoire")
        @Positive(message = "Le montant doit être positif")
        BigDecimal montant,

        @NotNull(message = "Le mode de paiement est obligatoire")
        ModePaiement mode
) {
}

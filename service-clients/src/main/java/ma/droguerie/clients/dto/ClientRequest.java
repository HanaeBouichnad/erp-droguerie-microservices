package ma.droguerie.clients.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/** Données envoyées pour créer ou modifier un client. */
public record ClientRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        String prenom,

        @NotBlank(message = "Le téléphone est obligatoire")
        @Pattern(regexp = "0[5-7][0-9]{8}", message = "Le téléphone doit être un numéro marocain, par exemple 0661234567")
        String telephone,

        @NotNull(message = "Le plafond de crédit est obligatoire (0 si pas de crédit)")
        @PositiveOrZero(message = "Le plafond de crédit ne peut pas être négatif")
        BigDecimal plafondCredit
) {
}

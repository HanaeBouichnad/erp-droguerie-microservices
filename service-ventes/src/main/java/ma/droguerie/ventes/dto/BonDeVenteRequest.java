package ma.droguerie.ventes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ce que le vendeur saisit : son nom, le client (facultatif) et les produits demandés.
 * Le prix n'est pas saisi : il est lu dans service-produits.
 */
public record BonDeVenteRequest(
        @NotBlank(message = "Le nom du vendeur est obligatoire")
        String vendeur,

        Long clientId,

        @NotEmpty(message = "Le bon doit contenir au moins un produit")
        List<@Valid Ligne> lignes
) {

    public record Ligne(
            @NotNull(message = "Le produit est obligatoire")
            Long produitId,

            @NotNull(message = "La quantité est obligatoire")
            @Positive(message = "La quantité doit être positive")
            BigDecimal quantite
    ) {
    }
}

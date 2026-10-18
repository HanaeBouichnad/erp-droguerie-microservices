package ma.droguerie.produits.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

/** Réception d'une livraison fournisseur (BF-PRO-03) : plusieurs produits en une seule opération. */
public record ReceptionRequest(
        @NotBlank(message = "Le fournisseur est obligatoire") String fournisseur,
        @NotEmpty(message = "Une réception contient au moins une ligne") @Valid List<Ligne> lignes
) {
    public record Ligne(
            @NotNull(message = "Le produit est obligatoire") Long produitId,
            @NotNull @DecimalMin(value = "0.001", message = "La quantité doit être positive") BigDecimal quantite
    ) {
    }
}
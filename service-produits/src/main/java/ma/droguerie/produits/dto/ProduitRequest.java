package ma.droguerie.produits.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ma.droguerie.produits.entity.Unite;

import java.math.BigDecimal;

/**
 * Données pour créer ou modifier un produit.
 * Le stock n'en fait pas partie : il ne change que par une réception, une vente ou un inventaire.
 */
public record ProduitRequest(
        @NotBlank(message = "La référence est obligatoire") String reference,
        @NotBlank(message = "La désignation est obligatoire") String designation,
        @NotBlank(message = "La catégorie est obligatoire") String categorie,
        @NotNull(message = "L'unité est obligatoire (PIECE, METRE ou KILOGRAMME)") Unite unite,
        @NotNull @DecimalMin(value = "0.00", message = "Le prix d'achat ne peut pas être négatif") BigDecimal prixAchat,
        @NotNull @DecimalMin(value = "0.01", message = "Le prix de vente doit être positif") BigDecimal prixVente,
        @NotNull @DecimalMin(value = "0", message = "Le seuil ne peut pas être négatif") BigDecimal seuilAlerte
) {
}
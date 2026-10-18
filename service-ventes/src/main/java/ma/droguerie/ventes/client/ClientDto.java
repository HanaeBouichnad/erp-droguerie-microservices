package ma.droguerie.ventes.client;

/** Les informations d'un client dont service-ventes a besoin. */
public record ClientDto(
        Long id,
        String nom,
        String prenom,
        Boolean actif
) {
}

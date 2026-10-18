package ma.droguerie.ventes.entity;

/**
 * EN_ATTENTE : le vendeur a préparé le bon, le client n'a pas encore payé.
 * ENCAISSE   : la caissière a encaissé, le stock a été retiré.
 * ANNULE     : le client a changé d'avis.
 */
public enum StatutBon {
    EN_ATTENTE,
    ENCAISSE,
    ANNULE
}

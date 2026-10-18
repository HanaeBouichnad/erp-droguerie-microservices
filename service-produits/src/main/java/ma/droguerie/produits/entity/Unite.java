package ma.droguerie.produits.entity;

/**
 * Unité de vente d'un produit (RG-PRO-02).
 * Exemple : une ampoule se vend à la pièce, un câble au mètre, des clous au kilogramme.
 */
public enum Unite {
    PIECE,
    METRE,
    KILOGRAMME;

    /** Seuls les produits vendus au mètre ou au kilo acceptent une quantité décimale (ex. 2,5 m). */
    public boolean accepteDecimales() {
        return this != PIECE;
    }
}
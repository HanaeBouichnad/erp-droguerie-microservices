package ma.droguerie.produits.exception;

/** Levée quand une règle de gestion du cahier des charges n'est pas respectée (réponse HTTP 409). */
public class RegleMetierException extends RuntimeException {

    public RegleMetierException(String message) {
        super(message);
    }
}
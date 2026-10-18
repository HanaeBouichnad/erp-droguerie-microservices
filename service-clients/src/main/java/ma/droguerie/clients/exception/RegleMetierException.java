package ma.droguerie.clients.exception;

/** Levée quand une règle de gestion n'est pas respectée (réponse 409). */
public class RegleMetierException extends RuntimeException {

    public RegleMetierException(String message) {
        super(message);
    }
}

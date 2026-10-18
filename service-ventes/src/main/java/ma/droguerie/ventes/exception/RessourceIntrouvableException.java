package ma.droguerie.ventes.exception;

/** Levée quand on cherche un élément qui n'existe pas (réponse 404). */
public class RessourceIntrouvableException extends RuntimeException {

    public RessourceIntrouvableException(String message) {
        super(message);
    }
}

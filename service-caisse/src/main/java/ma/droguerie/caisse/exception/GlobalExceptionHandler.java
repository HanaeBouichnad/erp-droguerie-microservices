package ma.droguerie.caisse.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/** Transforme les exceptions en réponses d'erreur claires (format RFC 7807). */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ProblemDetail introuvable(RessourceIntrouvableException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(RegleMetierException.class)
    public ProblemDetail regleMetier(RegleMetierException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validation(MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(erreur -> erreur.getField() + " : " + erreur.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }

    /**
     * Erreur renvoyée par un autre microservice appelé avec OpenFeign.
     * On relaie son message pour que l'utilisateur comprenne ce qui s'est passé.
     */
    @ExceptionHandler(FeignException.class)
    public ProblemDetail serviceDistant(FeignException e) {
        String detail = extraireDetail(e);
        if (e.status() == 404) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                    detail != null ? detail : "Élément introuvable dans un autre service");
        }
        if (e.status() == 400 || e.status() == 409) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                    detail != null ? detail : "Opération refusée par un autre service");
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "Un service n'est pas disponible pour le moment, réessayez plus tard");
    }

    private String extraireDetail(FeignException e) {
        try {
            JsonNode corps = objectMapper.readTree(e.contentUTF8());
            return corps.path("detail").asText(null);
        } catch (Exception ignore) {
            return null;
        }
    }
}

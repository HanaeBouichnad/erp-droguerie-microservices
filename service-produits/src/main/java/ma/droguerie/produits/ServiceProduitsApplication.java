package ma.droguerie.produits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservice Produits : catalogue d"e la droguerie, stock et réceptions fournisseurs.
 * Swagger : http://localhost:8081/swagger-ui.html
 */
@SpringBootApplication
public class ServiceProduitsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceProduitsApplication.class, args);
    }
}
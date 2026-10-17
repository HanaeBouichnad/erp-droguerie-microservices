package ma.droguerie.decouverte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Serveur de découverte (annuaire des microservices).
 * Chaque service (produits, clients, ventes, caisse) s'y enregistre au démarrage
 * sous un nom logique. Les autres services le retrouvent par ce nom, sans adresse IP en dur.
 * Console : http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer
public class ServiceDecouverteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDecouverteApplication.class, args);
    }
}
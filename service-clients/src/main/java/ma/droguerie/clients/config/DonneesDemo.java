package ma.droguerie.clients.config;

import ma.droguerie.clients.entity.Client;
import ma.droguerie.clients.repository.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/** Quelques clients habitués de la droguerie, chargés au démarrage. */
@Component
public class DonneesDemo implements CommandLineRunner {

    private final ClientRepository repository;

    public DonneesDemo(ClientRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new Client("Benali", "Ahmed", "0661234567", new BigDecimal("2000.00")),
                new Client("Alaoui", "Fatima", "0677889900", new BigDecimal("1000.00")),
                new Client("Tazi", "Youssef (plombier)", "0655443322", new BigDecimal("5000.00")),
                new Client("Idrissi", "Khadija", "0600112233", BigDecimal.ZERO)
        ));
    }
}

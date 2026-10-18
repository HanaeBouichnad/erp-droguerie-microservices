package ma.droguerie.ventes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceVentesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceVentesApplication.class, args);
    }
}

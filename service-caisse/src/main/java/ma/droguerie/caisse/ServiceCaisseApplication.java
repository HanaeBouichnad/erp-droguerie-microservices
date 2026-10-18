package ma.droguerie.caisse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceCaisseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCaisseApplication.class, args);
    }
}

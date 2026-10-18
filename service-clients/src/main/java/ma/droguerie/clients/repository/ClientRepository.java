package ma.droguerie.clients.repository;

import ma.droguerie.clients.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByTelephone(String telephone);

    boolean existsByTelephoneAndIdNot(String telephone, Long id);

    List<Client> findByActifTrueAndNomContainingIgnoreCaseOrderByNomAsc(String nom);
}

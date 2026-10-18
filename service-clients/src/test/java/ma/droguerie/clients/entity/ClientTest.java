package ma.droguerie.clients.entity;

import ma.droguerie.clients.exception.RegleMetierException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientTest {

    @Test
    void unClientEstActifALaCreation() {
        Client client = new Client("Benali", "Ahmed", "0661234567", new BigDecimal("2000"));

        assertThat(client.isActif()).isTrue();
        assertThat(client.getPlafondCredit()).isEqualByComparingTo("2000");
    }

    @Test
    void unPlafondNegatifEstRefuse() {
        assertThatThrownBy(() -> new Client("Benali", "Ahmed", "0661234567", new BigDecimal("-100")))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("négatif");
    }

    @Test
    void unPlafondAZeroSignifiePasDeCredit() {
        Client client = new Client("Idrissi", "Khadija", "0600112233", BigDecimal.ZERO);

        assertThat(client.getPlafondCredit()).isZero();
    }

    @Test
    void desactiverUnClientLeRendInactif() {
        Client client = new Client("Benali", "Ahmed", "0661234567", new BigDecimal("2000"));

        client.desactiver();

        assertThat(client.isActif()).isFalse();
    }
}

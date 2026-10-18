package ma.droguerie.caisse.entity;

import ma.droguerie.caisse.exception.RegleMetierException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FactureTest {

    private Facture factureDe800() {
        return new Facture("FA-00001", 1L, "BV-00001", 1L, new BigDecimal("800.00"));
    }

    @Test
    void uneNouvelleFactureEstNonPayee() {
        Facture facture = factureDe800();

        assertThat(facture.getStatut()).isEqualTo(StatutFacture.NON_PAYEE);
        assertThat(facture.resteAPayer()).isEqualByComparingTo("800.00");
    }

    @Test
    void unPaiementPartielLaisseUneDette() {
        Facture facture = factureDe800();

        facture.enregistrerReglement(new BigDecimal("300.00"), ModePaiement.ESPECES);

        assertThat(facture.getStatut()).isEqualTo(StatutFacture.PARTIELLEMENT_PAYEE);
        assertThat(facture.resteAPayer()).isEqualByComparingTo("500.00");
    }

    @Test
    void plusieursRemboursementsSoldentLaFacture() {
        Facture facture = factureDe800();

        facture.enregistrerReglement(new BigDecimal("300.00"), ModePaiement.ESPECES);
        facture.enregistrerReglement(new BigDecimal("200.00"), ModePaiement.ESPECES);
        facture.enregistrerReglement(new BigDecimal("300.00"), ModePaiement.CHEQUE);

        assertThat(facture.getStatut()).isEqualTo(StatutFacture.PAYEE);
        assertThat(facture.resteAPayer()).isEqualByComparingTo("0");
        assertThat(facture.getReglements()).hasSize(3);
    }

    @Test
    void onNePeutPasPayerPlusQueLeResteDu() {
        Facture facture = factureDe800();
        facture.enregistrerReglement(new BigDecimal("700.00"), ModePaiement.ESPECES);

        assertThatThrownBy(() -> facture.enregistrerReglement(new BigDecimal("200.00"), ModePaiement.ESPECES))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("dépasse le reste à payer");
    }

    @Test
    void unMontantNulEstRefuse() {
        assertThatThrownBy(() -> factureDe800().enregistrerReglement(BigDecimal.ZERO, ModePaiement.ESPECES))
                .isInstanceOf(RegleMetierException.class);
    }
}

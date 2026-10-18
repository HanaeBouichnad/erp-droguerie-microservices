package ma.droguerie.ventes.entity;

import ma.droguerie.ventes.exception.RegleMetierException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BonDeVenteTest {

    private BonDeVente bonAvecDeuxLignes() {
        BonDeVente bon = new BonDeVente("BV-00001", "Said", 1L);
        bon.ajouterLigne(new LigneVente(1L, "ECL-001", "Ampoule LED 9W", new BigDecimal("2"), new BigDecimal("15.00")));
        bon.ajouterLigne(new LigneVente(6L, "ELE-001", "Câble électrique 2.5mm²", new BigDecimal("5.5"), new BigDecimal("8.00")));
        return bon;
    }

    @Test
    void unNouveauBonEstEnAttente() {
        assertThat(bonAvecDeuxLignes().getStatut()).isEqualTo(StatutBon.EN_ATTENTE);
    }

    @Test
    void leTotalEstLaSommeDesLignes() {
        // 2 x 15.00 + 5.5 x 8.00 = 30.00 + 44.00
        assertThat(bonAvecDeuxLignes().getMontantTotal()).isEqualByComparingTo("74.00");
    }

    @Test
    void unBonEnAttentePeutEtreAnnule() {
        BonDeVente bon = bonAvecDeuxLignes();

        bon.annuler();

        assertThat(bon.getStatut()).isEqualTo(StatutBon.ANNULE);
    }

    @Test
    void unBonEncaisseNePeutPasEtreAnnule() {
        BonDeVente bon = bonAvecDeuxLignes();
        bon.marquerEncaisse();

        assertThatThrownBy(bon::annuler)
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("ENCAISSE");
    }

    @Test
    void unBonNePeutPasEtreEncaisseDeuxFois() {
        BonDeVente bon = bonAvecDeuxLignes();
        bon.marquerEncaisse();

        assertThatThrownBy(bon::marquerEncaisse)
                .isInstanceOf(RegleMetierException.class);
    }

    @Test
    void uneQuantiteNulleEstRefusee() {
        assertThatThrownBy(() -> new LigneVente(1L, "ECL-001", "Ampoule LED 9W", BigDecimal.ZERO, new BigDecimal("15.00")))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("positive");
    }
}

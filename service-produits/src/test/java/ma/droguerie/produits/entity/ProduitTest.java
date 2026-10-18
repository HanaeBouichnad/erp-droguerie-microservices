package ma.droguerie.produits.entity;

import ma.droguerie.produits.exception.RegleMetierException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Tests des règles de gestion du produit, sans base de données ni Spring. */
class ProduitTest {

    private Produit produit(Unite unite, String stock, String seuil) {
        Produit p = new Produit("REF-1", "Produit test", "Test", unite,
                new BigDecimal("10"), new BigDecimal("15"), new BigDecimal(seuil));
        p.ajouterStock(new BigDecimal(stock));
        return p;
    }

    @Test
    void retirerStock_diminueLaQuantite() {
        Produit p = produit(Unite.PIECE, "20", "5");
        p.retirerStock(new BigDecimal("8"));
        assertThat(p.getQuantiteStock()).isEqualByComparingTo("12");
    }

    @Test
    void retirerStock_refuseUnStockNegatif() {
        Produit p = produit(Unite.PIECE, "3", "1");
        assertThatThrownBy(() -> p.retirerStock(new BigDecimal("4")))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("Stock insuffisant");
    }

    @Test
    void produitAuMetre_accepteUneQuantiteDecimale() {
        Produit p = produit(Unite.METRE, "10", "2");
        p.retirerStock(new BigDecimal("2.5"));
        assertThat(p.getQuantiteStock()).isEqualByComparingTo("7.5");
    }

    @Test
    void produitALaPiece_refuseUneQuantiteDecimale() {
        Produit p = produit(Unite.PIECE, "10", "2");
        assertThatThrownBy(() -> p.retirerStock(new BigDecimal("1.5")))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("à la pièce");
    }

    @Test
    void quantiteNulleOuNegative_estRefusee() {
        Produit p = produit(Unite.METRE, "10", "2");
        assertThatThrownBy(() -> p.ajouterStock(BigDecimal.ZERO)).isInstanceOf(RegleMetierException.class);
    }

    @Test
    void produit_estEnRupture_auSeuilOuEnDessous() {
        assertThat(produit(Unite.PIECE, "5", "5").estEnRupture()).isTrue();
        assertThat(produit(Unite.PIECE, "6", "5").estEnRupture()).isFalse();
    }
}
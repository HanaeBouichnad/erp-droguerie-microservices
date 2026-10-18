package ma.droguerie.produits.config;

import ma.droguerie.produits.entity.Produit;
import ma.droguerie.produits.entity.Unite;
import ma.droguerie.produits.repository.ProduitRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

/** Catalogue de démonstration, inséré uniquement si la base est vide. Prix en dirhams (MAD). */
@Configuration
public class DonneesDemo {

    @Bean
    CommandLineRunner chargerProduitsDemo(ProduitRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.saveAll(List.of(
                    produit("ECL-001", "Ampoule LED E27 9W", "Éclairage", Unite.PIECE, "8.50", "15.00", "120", "20"),
                    produit("ECL-002", "Réglette LED 120 cm", "Éclairage", Unite.PIECE, "45.00", "79.00", "4", "5"),
                    produit("SAN-001", "Lavabo céramique 60 cm", "Sanitaire", Unite.PIECE, "320.00", "450.00", "8", "3"),
                    produit("SAN-002", "Robinet mitigeur lavabo", "Sanitaire", Unite.PIECE, "140.00", "220.00", "15", "5"),
                    produit("SAN-003", "Tuyau PVC diamètre 32", "Sanitaire", Unite.METRE, "9.00", "15.00", "60", "20"),
                    produit("ELE-001", "Câble électrique 2,5 mm²", "Électricité", Unite.METRE, "4.20", "7.00", "300", "50"),
                    produit("QUI-001", "Clous 50 mm", "Quincaillerie", Unite.KILOGRAMME, "18.00", "28.00", "25.5", "5"),
                    produit("QUI-002", "Vis à bois 4x40", "Quincaillerie", Unite.KILOGRAMME, "35.00", "55.00", "3.2", "5"),
                    produit("OUT-001", "Étau d'établi 100 mm", "Outillage", Unite.PIECE, "160.00", "240.00", "4", "2")
            ));
        };
    }

    private static Produit produit(String reference, String designation, String categorie, Unite unite,
                                   String prixAchat, String prixVente, String stock, String seuil) {
        Produit produit = new Produit(reference, designation, categorie, unite,
                new BigDecimal(prixAchat), new BigDecimal(prixVente), new BigDecimal(seuil));
        produit.ajouterStock(new BigDecimal(stock));
        return produit;
    }
}
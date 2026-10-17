# ERP Droguerie : gestion des ventes, du stock et du crédit clients

ERP en architecture microservices (Spring Boot / Spring Cloud) conçu pour un vrai magasin de droguerie qui fonctionne aujourd'hui entièrement sur papier.

## 🎯 Problème
Le magasin (une gérante, deux vendeurs, une caissière) note le stock, les factures et les dettes clients à la main :
stock faux, factures perdues, crédits oubliés, aucun chiffre pour piloter l'activité.

## 💡 Solution
- **Vendeur** : crée le bon de vente et voit le stock en temps réel
- **Caissière** : encaisse en totalité ou en partie (crédit), édite la facture
- **Gérante** : gère produits et prix, suit les dettes clients et les ruptures de stock

## 🛠️ Architecture prévue
| Service | Rôle |
|---|---|
| service-produits | Catalogue, stock, réceptions fournisseurs |
| service-clients | Clients et plafonds de crédit |
| service-ventes | Bons de vente |
| service-caisse | Encaissements, factures, dettes |
| service-decouverte | Eureka |
| gateway | Point d'entrée unique |

Java 17 · Spring Boot 3 · Spring Cloud (Eureka, OpenFeign, Gateway) · PostgreSQL · Docker · JUnit 5

## 📌 Démarche
Cycle en V pour l'analyse et la conception, puis Scrum (5 sprints suivis dans Jira).

## 🚧 Statut
Phase d'analyse : cahier des charges et conception UML en cours.

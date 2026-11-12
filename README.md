# gestion-commandes — TP CI/CD (Expose n°9)

API REST Spring Boot (Java 17, Maven) avec de la logique metier testee, deployee par un pipeline CI/CD.

[![CI/CD](https://github.com/<VOTRE-UTILISATEUR>/<VOTRE-DEPOT>/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/<VOTRE-UTILISATEUR>/<VOTRE-DEPOT>/actions/workflows/ci-cd.yml)

## Regles metier
- Remise : 5 % des 50 000 FCFA HT, 10 % des 100 000 FCFA HT
- TVA : 18 % apres remise
- Livraison : 2 000 FCFA, gratuite des 50 000 FCFA TTC
- Telephone : mobile senegalais (70, 75, 76, 77, 78), indicatif +221 facultatif
- Une commande verifie le stock, le decremente, et peut etre annulee (le stock est restitue)

## Commandes utiles
```bash
mvn test                 # tests unitaires + rapport de couverture
mvn verify               # tests + controle du seuil de couverture JaCoCo
mvn spring-boot:run      # lancer l'API sur http://localhost:8080
```

## Endpoints
| Methode | URL | Role |
|---|---|---|
| GET | /api/produits | liste du catalogue |
| POST | /api/commandes | creer une commande |
| GET | /api/commandes/{id} | lire une commande |
| POST | /api/commandes/{id}/annuler | annuler une commande |

## Pipeline
- GitHub Actions : `.github/workflows/ci-cd.yml` (build -> test -> deploy)
- Jenkins : `Jenkinsfile` (+ `jenkins/docker-compose.yml` pour lancer Jenkins en local)
- Bonus Docker : `docs/bonus/docker-publish.yml`

# FinPulse — Plateforme bancaire à microservices (Projet ADRIA)

Projet réalisé pour le **Test ADRIA ENSET 2027** — ENSET Mohammedia, 3e année cycle ingénieur.
Auteure : Soumia ESSADQUI · Encadrant : P. ELYOUSSEFI

FinPulse permet de consulter des comptes, d'enregistrer des transactions, d'effectuer des virements et de publier des événements métier via Kafka. Un assistant conversationnel (Spring AI) exploite les services par MCP. Une étude Python indépendante traite la détection de fraude.

> **Périmètre :** les parties G (clients web/mobile) et I (DevOps) du sujet sont exclues.

## Architecture

| Module | Port | Rôle |
|---|---|---|
| `gateway-service` | 8080 | Routes REST (`/api/accounts`, `/api/transactions`, `/api/chat`) |
| `account-service` | 8081 | Comptes, soldes, idempotence ; REST, GraphQL, MCP |
| `transaction-service` | 8082 | Transactions, appel Feign vers Account, publication Kafka différée |
| `assistant-service` | 8083 | Spring AI + outils MCP |
| `discovery-service` | 8761 | Eureka |
| `config-service` | 8888 | Config Server (profil `native`, dossier `config-repo`) |
| Keycloak | 8180 | JWT, realm `bank` |
| `fraud-detection/` | — | Script Python, indépendant des microservices |

Topic Kafka : `bank.transactions.created` (clé = `accountId`).

## Règles métier

- Montant positif, au centime, plafonné par `bank.transaction.max-amount` (1000).
- Compte existant, actif, devise identique à la transaction ; solde suffisant pour un débit.
- Virement : deux comptes différents et de même devise.
- Idempotence : un même UUID avec le même contenu ne redébite pas ; un contenu différent est rejeté.

## Prérequis

- JDK et Maven (versions indiquées dans les `pom.xml`)
- Kafka, Keycloak (realm `bank`)
- Python 3 et les dépendances de `fraud-detection/requirements.txt`
- Une clé/configuration LLM pour `assistant-service`

## Lancement

Démarrer dans cet ordre, chaque module depuis son dossier :

1. `config-service`
2. `discovery-service`
3. `account-service`, `transaction-service`
4. `assistant-service`
5. `gateway-service`

```bash
mvn spring-boot:run
```

### Étude de fraude

```bash
pip install -r fraud-detection/requirements.txt
python fraud-detection/train.py bank_transactions.csv
```

Le fichier `bank_transactions.csv` (colonne cible `is_fraud`) doit être fourni.

## État du projet

Les sources sont générées, mais **aucune exécution n'a encore été démontrée** (seules les analyses statiques des `pom.xml` et du script Python ont été faites).

Limites connues :

- le transport MCP n'ajoute pas encore le jeton JWT : l'assistant n'est pas fonctionnel de bout en bout ;
- les autorisations par rôle et par propriété de compte sont à ajouter ;
- les transactions `PENDING` n'ont pas de réconciliation automatique ;
- le solde est stocké en `double` (préférer `DECIMAL`).

## Tests à exécuter

Création/consultation de compte (REST et GraphQL), règles métier, virement de 250 (soldes 5000 et 1000 → 4750 et 1250), nouvelle tentative idempotente, panne et reprise, accès avec et sans jeton, événement Kafka et log du consumer, métriques de fraude sur le CSV réel.

## Documentation

Voir le rapport `Rapport_ADRIA_FinPulse_ameliore.docx` pour la conception détaillée, les diagrammes et le protocole de validation.
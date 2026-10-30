# Diagnostic du code historique

## Violations des principes SOLID

| # | Défaut | Emplacement | Principe SOLID violé |
|---|---|---|---|
| 1 | Une seule classe fait tout : inscription, ajout de logement, attribution, cycle de vie du bail, relances, rapport et notifications | `GestionResidence` | **S** – Responsabilité unique |
| 2 | Le bail stocke ses données et met aussi en forme sa propre quittance | `Bail.genererQuittance()` | **S** – Responsabilité unique |
| 3 | `Database` simule la persistance et écrit aussi des requêtes SQL sur la console | `Database` | **S** – Responsabilité unique |
| 4 | Ajouter un type de logement oblige à modifier plusieurs `if` / `switch` | `CalculateurLoyer.calculerLoyer`, `Residence.initialiser`, `GestionResidence.ajouterLogement`, `GestionResidence.genererRapport` | **O** – Ouvert/fermé |
| 5 | Ajouter un profil oblige à modifier la remise, la solvabilité, la caution, les frais, le préavis et la pénalité | `GestionResidence`, `CalculateurLoyer` | **O** – Ouvert/fermé |
| 6 | Ajouter une option oblige à modifier `Bail` (booléens, `listerOptions`) et `CalculateurLoyer` | `Bail`, `CalculateurLoyer` | **O** – Ouvert/fermé |
| 7 | Ajouter un canal de notification oblige à modifier `notifier` et `relancerImpayes` | `GestionResidence` | **O** – Ouvert/fermé |
| 8 | `Colocation` refuse `loger()` et `liberer()` : elle ne peut pas remplacer un `Logement`, d'où les `instanceof Colocation` | `Colocation`, `GestionResidence.attribuerLogement / sortir / annulerBail` | **L** – Substitution de Liskov |
| 9 | `Studio`, `T2` et `Colocation` lancent `UnsupportedOperationException`, `T1` renvoie `null` pour les mêmes méthodes, d'où le `catch` vide du rapport | `Studio`, `T1`, `T2`, `Colocation`, `GestionResidence.genererRapport` | **L** – Substitution de Liskov |
| 10 | L'interface impose à tous les logements les méthodes de colocation, de balcon et d'entretien | `ILogement` | **I** – Ségrégation des interfaces |
| 11 | `GestionResidence` va chercher elle-même ses dépendances (`Database.getInstance()`, `new CalculateurLoyer()`) | `GestionResidence` | **D** – Inversion des dépendances |
| 12 | Les services de notification sont créés avec `new` ou appelés en `static` au milieu du code métier | `GestionResidence.notifier`, `GestionResidence.relancerImpayes` | **D** – Inversion des dépendances |
| 13 | État global `static` modifiable dont dépend le code métier | `Database.etudiants/baux`, `Residence.logements`, `Etudiant.compteur`, `GestionResidence.compteurBail`, `SmsService.compteurSms`, `CourrierService.totalAffranchissement`, `*.journal` | **D** – Inversion des dépendances |

## Autres défauts

| Défaut | Emplacement | Traitement |
|---|---|---|
| Codes `String` pour les types, profils, canaux et statuts | partout | remplacés par des enums (palier 1) |
| Options sous forme de 5 booléens | `Bail`, `CalculateurLoyer` | remplacées par des décorateurs (palier 4) |
| Nombres magiques (14, 350, 1.15, 0.05…) | `CalculateurLoyer`, `GestionResidence` | remplacés par des constantes nommées (palier 1) |
| Création des logements dupliquée dans deux `switch` | `Residence.initialiser`, `GestionResidence.ajouterLogement` | fabrique (palier 2) |
| Création des services de notification dupliquée | `GestionResidence.notifier`, `relancerImpayes` | fabrique (palier 2) |
| Requêtes SQL construites par concaténation (risque d'injection) | `Database` | à supprimer avec le singleton (palier 5) |

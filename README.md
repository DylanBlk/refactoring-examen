# Campus Vert – Examen de refactoring (3 h)

La **Résidence Campus Vert** loge des étudiants dans des studios, des T1, des T2 et des colocations. Son
logiciel de gestion a été écrit à la va-vite pendant l'été par un stagiaire. Il fonctionne, mais tout est
mélangé : la moindre évolution (un nouveau type de logement, une nouvelle option, un nouveau profil
d'étudiant) oblige à modifier du code un peu partout.

Votre mission : **refactorer ce code sans changer son comportement**, en appliquant les principes **SOLID** et
les patrons **Strategy**, **Decorator** et **Factory**.

> Le sujet est volontairement long : il n'est pas attendu que vous alliez au bout. Avancez par paliers, dans
> l'ordre, et gardez un code qui compile et une sortie identique à chaque palier.

## Organisation du projet

```
refactoring-exam/
├── pom.xml                  POM parent (Java 25, JUnit 5)
├── residence-contrat/       Tests d'acceptation communs aux deux implémentations : NE PAS MODIFIER
├── residence-legacy/        Code historique : référence, NE PAS MODIFIER
└── residence-refactored/    Votre code (vide au départ)
```

Le code des deux implémentations est dans le package `fr.efrei.residence`.

### Les tests de contrat

Le comportement attendu est décrit **une seule fois**, dans `residence-contrat` :

- `SystemeResidence` : interface qui expose les cas d'usage avec des types neutres (`DemandeInscription`,
  `DemandeAttribution`, `EtatBail`, `Option`…) ;
- `ContratResidenceTest` : 101 scénarios (inscription, parc, calcul du loyer, attribution, cycle de vie du bail,
  quittances, notifications, relances, rapport).

Chaque implémentation branche son code sur ces tests au moyen d'un **adaptateur** (patron *Adapter*) :

| Module | Adaptateur | Test |
|---|---|---|
| `residence-legacy` | `LegacyAdapter` | `LegacyContratTest` : passe ✅ |
| `residence-refactored` | `NouveauSystemeAdapter` (à écrire) | `NouveauSystemeContratTest` (`@Disabled` au départ) |

Les tests ne connaissent aucune classe interne : vous êtes libres de remodeler entièrement le code, tant que
l'adaptateur peut traduire les appels. Regardez `LegacyAdapter` : tout ce qu'il doit « bricoler » (vider des
champs statiques, convertir des enums en `String`, éclater les options en booléens) est un symptôme à corriger.

### Commandes

```bash
mvn install
```

Lance tous les tests. Pour la démo du code historique :

```bash
java -cp residence-legacy/target/classes fr.efrei.residence.Main
```

## Le code historique

| Classe | Rôle |
|---|---|
| `GestionResidence` | inscription des étudiants, ajout de logements, attribution, bail (entrée, paiement, préavis, sortie, annulation), relances, rapport, notifications |
| `CalculateurLoyer` | calcul du loyer hors charges et des charges |
| `Residence` | parc de logements (liste statique) chargé depuis un catalogue texte |
| `ILogement`, `Logement` | contrat et base commune des logements |
| `Studio`, `T1`, `T2`, `Colocation` | les types de logement |
| `Etudiant`, `Bail`, `Paiement` | le modèle |
| `Database` | persistance (simulée en mémoire, singleton) |
| `EmailService`, `SmsService`, `CourrierService` | canaux de notification (simulés sur la console) |
| `Utils` | formatage des dates et des montants, arrondi, validation d'email |
| `Main` | scénario de démonstration : 10 inscriptions, 7 demandes de logement, une rentrée et deux mois de loyers |

### Déroulé d'un séjour

```
inscrireEtudiant ─▶ attribuerLogement ─▶ entrerDansLesLieux ─▶ payerLoyer (chaque mois)
                          │                     │
                          ▼                     ▼
                     annulerBail          donnerPreavis ─▶ sortir
```

## Règles métier

Ces règles **doivent être conservées à l'identique**.

### Étudiants

Chaque étudiant a un **profil** : `STANDARD`, `BOURSIER` (avec un échelon de 0 à 7), `ALTERNANT` ou
`INTERNATIONAL`, et un **canal de notification préféré** : `EMAIL`, `SMS` ou `COURRIER`.

L'inscription est refusée, avec un message, si :
- le nom est vide ;
- l'email est invalide (doit contenir `@` et `.`, pas en première position) ou déjà inscrit ;
- le profil est inconnu ;
- le profil est `BOURSIER` et l'échelon n'est pas entre 0 et 7 ;
- le profil est `INTERNATIONAL` et le téléphone ne commence pas par `+` ;
- le canal est `SMS` sans téléphone, ou `COURRIER` sans adresse.

### Logements

| Type | Particularités |
|---|---|
| `STUDIO` | 1 occupant, pas de balcon |
| `T1` | 1 occupant, pas de balcon |
| `T2` | 1 occupant, balcon éventuel (surface en m²) |
| `COLOCATION` | plusieurs places (minimum 2 à l'ajout), parties communes à nettoyer au départ d'un colocataire |

Un logement est **accessible PMR** s'il est au rez-de-chaussée (étage 0) ou dans le bâtiment A (numéro
commençant par `A`, seul bâtiment avec ascenseur).

Le parc initial est décrit dans `Residence.CATALOGUE` (`TYPE;NUMERO;SURFACE;ETAGE;MEUBLE;PARAM`, où `PARAM`
est le nombre de places d'une colocation ou la surface du balcon d'un T2). On peut aussi ajouter un logement
avec `ajouterLogement` ; un type inconnu est refusé.

### Calcul du loyer mensuel

Les étapes s'appliquent **dans cet ordre** :

1. **Loyer de base** selon le type :

   | Type | Loyer de base |
   |---|---|
   | STUDIO | 14 €/m², minimum 350 € |
   | T1 | 13 €/m² + 50 € |
   | T2 | 12 €/m² + 100 € |
   | COLOCATION | 11 €/m² divisé par le nombre de places (loyer par colocataire) |

2. **Meublé** : ×1,15.
3. **Rez-de-chaussée** (étage 0) : ×0,95.
4. **Options** choisies par l'étudiant :

   | Option | Effet |
   |---|---|
   | Ménage | ×1,08 (×1,04 en colocation) |
   | Parking | +45 € |
   | Laverie | +12 € |
   | Fibre | +20 € (incluse, donc 0 €, en colocation) |
   | Assurance habitation | +9 € |

5. **Remise selon le profil**, sur le montant obtenu :

   | Profil | Remise |
   |---|---|
   | STANDARD | aucune |
   | BOURSIER | 5 % par échelon, plafonnée à 30 % |
   | ALTERNANT | 10 % |
   | INTERNATIONAL | aucune |

6. Arrondi au centime : c'est le **loyer hors charges (HC)**.
7. **Charges** forfaitaires : 35 € (25 € en colocation), sans remise. Loyer **charges comprises (CC)** = HC + charges.

### Attribution d'un logement

On parcourt le parc dans l'ordre et on retient le **premier** logement qui :
- est du type demandé ;
- a une place libre ;
- est accessible PMR si l'étudiant est PMR ;
- respecte la **solvabilité** : ressources mensuelles ≥ 3 × loyer CC (2 × pour un `ALTERNANT`, aucune
  condition pour un `BOURSIER`, garanti par l'État). Chaque logement écarté pour ce motif est affiché.

Si aucun logement ne convient, la demande est refusée. Sinon un bail `BAIL-001`, `BAIL-002`… est signé :

| Profil | Dépôt de garantie | Frais de dossier | Préavis |
|---|---|---|---|
| STANDARD | 1 mois de loyer HC | 100 € | 30 jours |
| BOURSIER | aucun | aucun | 15 jours |
| ALTERNANT | 1 mois de loyer HC | 100 € | 15 jours |
| INTERNATIONAL | 2 mois de loyer HC | 150 € | 30 jours |

### Cycle de vie du bail

```
SIGNE ──entrer──▶ ACTIF ──préavis──▶ PREAVIS ──sortir──▶ TERMINE
  │
  └──annuler──▶ ANNULE   (frais de dossier conservés)
```

- **Entrée** : uniquement depuis `SIGNE`, pas avant la date de signature.
- **Paiement** : uniquement en `ACTIF` ou `PREAVIS`. Le loyer d'un mois (`AAAA-MM`) est dû le 5. Payé après
  le 5 : pénalité de 5 % du loyer CC, minimum 10 € (les `BOURSIER` en sont exonérés). La différence entre le dû
  et le payé s'ajoute au solde (un trop-perçu rend le solde négatif, c'est une avance).
- **Préavis** : uniquement depuis `ACTIF`. Date de fin = date du préavis + durée du préavis du profil.
- **Sortie** : uniquement depuis `PREAVIS`, pas avant la date de fin. Le dépôt restitué = dépôt − retenues
  pour dégâts − impayés (solde positif), minimum 0 ; ce qui ne peut pas être couvert reste dû. En colocation,
  les parties communes sont nettoyées.
- **Quittance** : `Bail.genererQuittance(mois)` produit le reçu texte d'un mois payé.

### Notifications

Chaque étape (bail signé, entrée, paiement, préavis, sortie, annulation) et chaque relance d'impayé envoie un
message sur le **canal préféré** de l'étudiant :

| Canal | Format |
|---|---|
| EMAIL | `[EMAIL] expéditeur -> destinataire \| sujet` puis le corps sur une seconde ligne |
| SMS | `[SMS] -> numéro : sujet - message`, tronqué à 160 caractères |
| COURRIER | `[COURRIER] nom, adresse \| Objet : sujet` puis le texte ; chaque courrier coûte un timbre à 1,29 € |

Un étudiant `INTERNATIONAL` dont le canal n'est pas l'email reçoit **en plus** un email dont le sujet est
préfixé par `[EN] `. Les relances d'impayés ont leurs propres textes (plus courts en SMS).

### Rapport

`afficherRapport()` affiche l'occupation par type, le taux d'occupation global, la surface totale des balcons,
les loyers CC et les dépôts des baux en cours, les impayés, le nombre de SMS envoyés et le coût des timbres.

---

## Travail demandé

### Règles

1. **On ne modifie jamais `residence-legacy`.**
2. Un palier = un commit, avec un message qui nomme le refactoring (ex. `Factory : création des logements`).
3. Avant chaque commit : `mvn install` passe, avec les 101 tests de contrat verts dans votre module.
4. Un palier à moitié fait mais vert vaut mieux qu'un palier complet qui casse des tests.
5. L'adaptateur ne doit contenir que de la traduction : aucune règle métier.

### Palier 0 – Mise en place (10 min)

1. Copier toutes les classes de `residence-legacy/src/main/java/fr/efrei/residence` dans
   `residence-refactored/src/main/java/fr/efrei/residence`.
2. Remplacer le contenu de `NouveauSystemeAdapter` par celui de `LegacyAdapter` (en gardant le nom de la classe).
3. Retirer `@Disabled` de `NouveauSystemeContratTest`.

**Terminé quand :** `mvn install` affiche deux fois `Tests run: 101, Failures: 0`. Faire un premier commit.

### Palier 1 – Diagnostic et nettoyage

- Rédiger dans `residence-refactored/DIAGNOSTIC.md` un tableau *défaut → emplacement → principe SOLID violé*,
  avec au moins une violation par principe.
- Remplacer les codes `String` (type de logement, profil, canal, statut, option) par des **enums** et les
  nombres magiques par des **constantes nommées**.

**Terminé quand :** plus aucun `equals("BOURSIER")`, `equals("COLOCATION")`, `equals("ACTIF")`…

### Palier 2 – Factory

- La création des logements est dupliquée (`Residence.initialiser` et `GestionResidence.ajouterLogement`) avec
  deux `switch` différents : la centraliser dans une **fabrique**.
- La création du bon service de notification selon le canal est dupliquée (`notifier` et `relancerImpayes`) :
  même traitement.

**Terminé quand :** plus aucun `new Studio`, `new T1`… ni `new SmsService()` en dehors des fabriques.

### Palier 3 – Strategy

Tout ce qui dépend du profil de l'étudiant est éparpillé : remise, solvabilité, dépôt de garantie, frais de
dossier, durée du préavis, pénalité de retard. Le regrouper dans une **stratégie par profil**. Le loyer de base
par type de logement est un second candidat.

**Terminé quand :** plus aucun `if` ni `switch` sur un profil ou un type de logement en dehors de l'endroit qui
choisit la stratégie.

### Palier 4 – Decorator

Le loyer se construit par couches successives (base, meublé, rez-de-chaussée, options). Chaque couche devient
un **décorateur** autour du loyer de base, et une classe assemble la chaîne à partir du logement et des options
choisies. Attention à l'ordre : le ménage est un pourcentage, il doit être appliqué avant les options à prix
fixe, et la remise s'applique sur le tout.

**Terminé quand :** ajouter une option = créer un décorateur et l'ajouter à l'assemblage, sans toucher au reste.
Les cinq booléens d'options disparaissent de `Bail` et de `CalculateurLoyer`. Chaque décorateur a son test
unitaire.

### Palier 5 – Responsabilité unique et inversion des dépendances (S, D)

- Découper `GestionResidence` : validation des inscriptions, attribution, cycle de vie du bail, notifications,
  rapport. `Bail` ne devrait pas mettre en forme sa propre quittance.
- Supprimer le singleton `Database`, les méthodes et champs `static` modifiables (`EmailService.journal`,
  `Residence.logements`, les compteurs…). Introduire des interfaces (`BailRepository`, `CanalNotification`…)
  et injecter les dépendances par constructeur. Seuls `Main` et l'adaptateur construisent le graphe d'objets : `reinitialiser()` revient alors à « construire un nouveau graphe ».

**Terminé quand :** plus aucun `static` modifiable, plus aucun `getInstance()`, plus aucun `new` d'un service
dans un autre service.

### Palier 6 – Logements : Liskov et ségrégation des interfaces (L, I)

`ILogement` impose à tous les logements les méthodes de colocation, de balcon et d'entretien, d'où des
`UnsupportedOperationException` (et un `T1` qui renvoie `null`). `Colocation` refuse `loger()` : elle ne peut
pas remplacer un `Logement`, d'où les `instanceof Colocation` dans `GestionResidence`, et le `catch` vide du
rapport.

**Terminé quand :** plus aucun `instanceof`, plus aucune `UnsupportedOperationException`, plus aucun `catch` vide.

### Palier 7 – Preuve par l'extension

Ajouter, en ne créant **que de nouvelles classes** (et en complétant l'assemblage / les fabriques) :

- un type de logement **`DUPLEX`** : 12,50 €/m² + 80 €, 1 occupant, pas de balcon ;
- une option **local vélo** : +6 €, appliquée après l'assurance ;
- un profil **`SPORTIF`** (sportif de haut niveau) : remise 15 %, solvabilité 2 × le loyer CC, dépôt d'1 mois
  de loyer HC, frais de dossier 50 €, préavis 15 jours, pénalités de retard normales ;
- un canal **`PUSH`** (application mobile) : `[PUSH] -> email : sujet`.

Le contrat ne connaît pas ces nouveautés : écrivez vos propres tests unitaires (dans
`residence-refactored/src/test`) pour chacune d'elles. Les 101 tests de contrat doivent rester verts. Indiquez
dans `DIAGNOSTIC.md` combien de fichiers existants vous avez dû modifier.

## Grille d'auto-évaluation

| Critère | ✔ |
|---|---|
| 101 tests de contrat verts | |
| L'adaptateur ne contient que de la traduction | |
| Aucun code `String` pour les types, profils, canaux, statuts et options | |
| Logements et canaux créés par des fabriques | |
| Règles par profil (et loyer de base par type) en Strategy | |
| Couches du loyer en Decorator, assemblées en un seul endroit | |
| `GestionResidence` découpée, chaque classe se décrit en une phrase sans « et » | |
| Aucun singleton, aucun `static` modifiable, dépendances injectées | |
| Aucun `instanceof`, aucune `UnsupportedOperationException`, aucun `catch` vide | |
| Extension `DUPLEX` / local vélo / `SPORTIF` / `PUSH` sans modifier les classes existantes (hors assemblage) | |

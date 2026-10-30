package fr.efrei.residence.contrat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.efrei.residence.contrat.Option.ASSURANCE;
import static fr.efrei.residence.contrat.Option.FIBRE;
import static fr.efrei.residence.contrat.Option.LAVERIE;
import static fr.efrei.residence.contrat.Option.MENAGE;
import static fr.efrei.residence.contrat.Option.PARKING;
import static fr.efrei.residence.contrat.Profil.ALTERNANT;
import static fr.efrei.residence.contrat.Profil.BOURSIER;
import static fr.efrei.residence.contrat.Profil.INTERNATIONAL;
import static fr.efrei.residence.contrat.Profil.STANDARD;
import static fr.efrei.residence.contrat.TypeLogement.COLOCATION;
import static fr.efrei.residence.contrat.TypeLogement.STUDIO;
import static fr.efrei.residence.contrat.TypeLogement.T1;
import static fr.efrei.residence.contrat.TypeLogement.T2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests d'acceptation de la Résidence Campus Vert.
 * <p>
 * Ils décrivent le comportement attendu, indépendamment de l'implémentation.
 * Pour les exécuter sur une implémentation, créer une sous-classe qui fournit son adaptateur :
 * <pre>
 * class MonImplementationTest extends ContratResidenceTest {
 *     protected SystemeResidence creerSysteme() { return new MonAdaptateur(); }
 * }
 * </pre>
 */
public abstract class ContratResidenceTest {

    private static final double CENTIME = 0.001;

    protected SystemeResidence systeme;
    private final Map<DemandeInscription, String> inscrits = new HashMap<>();

    protected abstract SystemeResidence creerSysteme();

    @BeforeEach
    void initialiser() {
        systeme = creerSysteme();
        systeme.reinitialiser();
    }

    // =====================================================================
    // Jeu de données
    // =====================================================================

    static final LocalDate SIGNATURE = LocalDate.of(2026, 8, 25);
    static final LocalDate RENTREE = LocalDate.of(2026, 9, 1);
    static final YearMonth SEPTEMBRE = YearMonth.of(2026, 9);
    static final YearMonth OCTOBRE = YearMonth.of(2026, 10);

    static final DemandeInscription ALICE = new DemandeInscription("Martin", "Alice", "alice.martin@mail.fr",
            "0601020304", null, STANDARD, 0, 3000, false, Canal.EMAIL);
    static final DemandeInscription BILAL = new DemandeInscription("Haddad", "Bilal", "bilal.haddad@mail.fr",
            "0611223344", null, BOURSIER, 5, 450, false, Canal.SMS);
    static final DemandeInscription CHLOE = new DemandeInscription("Durand", "Chloé", "chloe.durand@mail.fr",
            "0622334455", null, ALTERNANT, 0, 1500, true, Canal.EMAIL);
    static final DemandeInscription DIEGO = new DemandeInscription("Alvarez", "Diego", "diego.alvarez@mail.es",
            "+34612345678", null, INTERNATIONAL, 0, 4000, false, Canal.SMS);
    static final DemandeInscription EMMA = new DemandeInscription("Petit", "Emma", "emma.petit@mail.fr",
            null, "12 rue des Lilas, 35000 Rennes", STANDARD, 0, 3000, false, Canal.COURRIER);
    static final DemandeInscription HUGO = new DemandeInscription("Leroy", "Hugo", "hugo.leroy@mail.fr",
            "0644556677", null, BOURSIER, 7, 0, true, Canal.EMAIL);
    static final DemandeInscription LI = new DemandeInscription("Wang", "Li", "li.wang@mail.cn",
            "+8613800000000", null, INTERNATIONAL, 0, 4000, false, Canal.EMAIL);

    static DemandeInscription standard(String prenom, double ressources) {
        return new DemandeInscription("Test", prenom, prenom.toLowerCase() + "@mail.fr", "0600000000", null,
                STANDARD, 0, ressources, false, Canal.EMAIL);
    }

    static DemandeInscription profil(Profil profil, double ressources) {
        return new DemandeInscription("Test", "Profil", profil.name().toLowerCase() + "@mail.fr", "+33600000000",
                null, profil, 0, ressources, false, Canal.EMAIL);
    }

    static Set<Option> options(Option... options) {
        return options.length == 0 ? EnumSet.noneOf(Option.class) : EnumSet.of(options[0], options);
    }

    /** Inscrit l'étudiant au premier appel, puis renvoie toujours la même référence. */
    String inscrit(DemandeInscription demande) {
        return inscrits.computeIfAbsent(demande, d ->
                systeme.inscrire(d).orElseThrow(() -> new AssertionError("inscription refusée : " + d)));
    }

    boolean estAccepte(DemandeInscription demande, TypeLogement type, Option... options) {
        return systeme.attribuer(new DemandeAttribution(inscrit(demande), type, options(options), SIGNATURE)).isPresent();
    }

    String bail(DemandeInscription demande, TypeLogement type, Option... options) {
        DemandeAttribution d = new DemandeAttribution(inscrit(demande), type, options(options), SIGNATURE);
        return systeme.attribuer(d).orElseThrow(() -> new AssertionError("attribution refusée : " + d));
    }

    String bailActif(DemandeInscription demande, TypeLogement type, Option... options) {
        String id = bail(demande, type, options);
        assertTrue(systeme.entrer(id, RENTREE));
        return id;
    }

    EtatBail etat(String id) {
        return systeme.consulter(id).orElseThrow(() -> new AssertionError("bail introuvable : " + id));
    }

    EtatLogement logement(String numero) {
        return systeme.logement(numero).orElseThrow(() -> new AssertionError("logement introuvable : " + numero));
    }

    double devis(String numero, Profil profil, int echelon, Option... options) {
        return systeme.devisLoyer(new DemandeDevis(numero, profil, echelon, options(options))).orElseThrow();
    }

    List<String> objets(Canal canal) {
        return systeme.notifications().stream().filter(n -> n.canal() == canal).map(Notification::objet).toList();
    }

    // =====================================================================

    @Nested
    @DisplayName("Inscription des étudiants")
    class Inscription {

        @Test
        void un_etudiant_valide_est_inscrit() {
            assertTrue(systeme.inscrire(ALICE).isPresent());
        }

        @Test
        void chaque_etudiant_a_sa_propre_reference() {
            assertFalse(inscrit(ALICE).equals(inscrit(EMMA)));
        }

        @Test
        void un_boursier_a_un_echelon_entre_0_et_7() {
            assertTrue(systeme.inscrire(new DemandeInscription("A", "B", "a0@mail.fr", "06", null, BOURSIER, 0, 0, false, Canal.EMAIL)).isPresent());
            assertTrue(systeme.inscrire(new DemandeInscription("A", "B", "a7@mail.fr", "06", null, BOURSIER, 7, 0, false, Canal.EMAIL)).isPresent());
            assertTrue(systeme.inscrire(new DemandeInscription("A", "B", "a8@mail.fr", "06", null, BOURSIER, 8, 0, false, Canal.EMAIL)).isEmpty());
            assertTrue(systeme.inscrire(new DemandeInscription("A", "B", "am@mail.fr", "06", null, BOURSIER, -1, 0, false, Canal.EMAIL)).isEmpty());
        }

        @Test
        void le_nom_est_obligatoire() {
            assertTrue(systeme.inscrire(new DemandeInscription("  ", "B", "a@mail.fr", "06", null, STANDARD, 0, 0, false, Canal.EMAIL)).isEmpty());
            assertTrue(systeme.inscrire(new DemandeInscription(null, "B", "a@mail.fr", "06", null, STANDARD, 0, 0, false, Canal.EMAIL)).isEmpty());
        }

        @Test
        void l_email_doit_etre_valide() {
            for (String email : new String[]{"pas-un-email", "@mail.fr", "a@mailfr", null}) {
                assertTrue(systeme.inscrire(new DemandeInscription("A", "B", email, "06", null, STANDARD, 0, 0, false, Canal.EMAIL)).isEmpty(),
                        "email accepté : " + email);
            }
        }

        @Test
        void un_email_ne_peut_etre_inscrit_qu_une_fois() {
            inscrit(ALICE);
            assertTrue(systeme.inscrire(new DemandeInscription("Autre", "Alice", "alice.martin@mail.fr", "06", null,
                    STANDARD, 0, 0, false, Canal.EMAIL)).isEmpty());
        }

        @Test
        void un_international_doit_avoir_un_numero_international() {
            assertTrue(systeme.inscrire(new DemandeInscription("A", "B", "a@mail.fr", "0612345678", null, INTERNATIONAL, 0, 0, false, Canal.EMAIL)).isEmpty());
            assertTrue(systeme.inscrire(new DemandeInscription("A", "B", "b@mail.fr", null, null, INTERNATIONAL, 0, 0, false, Canal.EMAIL)).isEmpty());
            assertTrue(systeme.inscrire(LI).isPresent());
        }

        @Test
        void le_canal_sms_exige_un_telephone() {
            assertTrue(systeme.inscrire(new DemandeInscription("A", "B", "a@mail.fr", null, "adr", STANDARD, 0, 0, false, Canal.SMS)).isEmpty());
        }

        @Test
        void le_canal_courrier_exige_une_adresse() {
            assertTrue(systeme.inscrire(new DemandeInscription("A", "B", "a@mail.fr", "06", null, STANDARD, 0, 0, false, Canal.COURRIER)).isEmpty());
            assertTrue(systeme.inscrire(new DemandeInscription("A", "B", "b@mail.fr", "06", "", STANDARD, 0, 0, false, Canal.COURRIER)).isEmpty());
        }

        @Test
        void l_inscription_n_envoie_aucune_notification() {
            inscrit(ALICE);
            inscrit(BILAL);
            inscrit(EMMA);
            assertTrue(systeme.notifications().isEmpty());
        }
    }

    @Nested
    @DisplayName("Parc de logements")
    class Logements {

        @Test
        void le_parc_initial_vient_du_catalogue() {
            assertEquals(new EtatLogement("A001", STUDIO, 1, 0, true), logement("A001"));
            assertEquals(new EtatLogement("B105", T1, 1, 0, false), logement("B105"));
            assertEquals(new EtatLogement("A201", T2, 1, 0, true), logement("A201"));
            assertEquals(new EtatLogement("B301", COLOCATION, 4, 0, false), logement("B301"));
            assertEquals(new EtatLogement("C001", COLOCATION, 3, 0, true), logement("C001"));
        }

        @Test
        void accessible_pmr_au_rez_de_chaussee_ou_dans_le_batiment_a() {
            assertTrue(logement("A102").accessiblePMR());
            assertFalse(logement("B103").accessiblePMR());
            assertFalse(logement("B202").accessiblePMR());
            assertTrue(logement("C001").accessiblePMR());
        }

        @Test
        void un_logement_inconnu_est_introuvable() {
            assertTrue(systeme.logement("Z999").isEmpty());
        }

        @Test
        void on_peut_ajouter_un_logement() {
            assertTrue(systeme.ajouterLogement(new DemandeLogement(STUDIO, "C002", 16, 0, false, 0, 0)));
            assertEquals(new EtatLogement("C002", STUDIO, 1, 0, true), logement("C002"));
        }

        @Test
        void un_numero_deja_utilise_est_refuse() {
            assertFalse(systeme.ajouterLogement(new DemandeLogement(T1, "A001", 30, 0, false, 0, 0)));
            assertEquals(STUDIO, logement("A001").type());
        }

        @Test
        void une_colocation_a_au_moins_2_places() {
            assertTrue(systeme.ajouterLogement(new DemandeLogement(COLOCATION, "C010", 40, 1, false, 1, 0)));
            assertEquals(2, logement("C010").places());
        }

        @Test
        void le_balcon_d_un_t2_ajoute_compte_dans_le_rapport() {
            assertTrue(systeme.ajouterLogement(new DemandeLogement(T2, "C201", 50, 2, false, 0, 4)));
            assertTrue(systeme.rapport().contains("Balcons      : 10.0 m²"));
        }
    }

    @Nested
    @DisplayName("Calcul du loyer hors charges")
    class Loyer {

        @Test
        void studio_au_minimum_de_350_euros() {
            assertEquals(350.00, devis("B103", STANDARD, 0), CENTIME);
        }

        @Test
        void studio_meuble_majore_de_15_pourcent_apres_le_minimum() {
            assertEquals(402.50, devis("A102", STANDARD, 0), CENTIME);
        }

        @Test
        void studio_meuble_au_rez_de_chaussee_minore_de_5_pourcent() {
            assertEquals(397.67, devis("A001", STANDARD, 0), CENTIME);
        }

        @Test
        void t1() {
            assertEquals(440.00, devis("B105", STANDARD, 0), CENTIME);
            assertEquals(476.10, devis("A104", STANDARD, 0), CENTIME);
        }

        @Test
        void t2() {
            assertEquals(640.00, devis("A201", STANDARD, 0), CENTIME);
            assertEquals(777.40, devis("B202", STANDARD, 0), CENTIME);
        }

        @Test
        void colocation_loyer_par_place() {
            assertEquals(253.00, devis("B301", STANDARD, 0), CENTIME);
            assertEquals(252.37, devis("C001", STANDARD, 0), CENTIME);
        }

        @Test
        void menage_8_pourcent() {
            assertEquals(378.00, devis("B103", STANDARD, 0, MENAGE), CENTIME);
        }

        @Test
        void menage_4_pourcent_en_colocation() {
            assertEquals(263.12, devis("B301", STANDARD, 0, MENAGE), CENTIME);
        }

        @Test
        void parking_laverie_et_assurance_a_prix_fixe() {
            assertEquals(485.00, devis("B105", STANDARD, 0, PARKING), CENTIME);
            assertEquals(452.00, devis("B105", STANDARD, 0, LAVERIE), CENTIME);
            assertEquals(449.00, devis("B105", STANDARD, 0, ASSURANCE), CENTIME);
            assertEquals(506.00, devis("B105", STANDARD, 0, PARKING, LAVERIE, ASSURANCE), CENTIME);
        }

        @Test
        void fibre_payante_sauf_en_colocation() {
            assertEquals(460.00, devis("B105", STANDARD, 0, FIBRE), CENTIME);
            assertEquals(253.00, devis("B301", STANDARD, 0, FIBRE), CENTIME);
        }

        @Test
        void le_menage_s_applique_avant_les_options_a_prix_fixe() {
            assertEquals(520.20, devis("B105", STANDARD, 0, MENAGE, PARKING), CENTIME);
        }

        @Test
        void toutes_les_options() {
            assertEquals(561.20, devis("B105", STANDARD, 0, MENAGE, PARKING, LAVERIE, FIBRE, ASSURANCE), CENTIME);
        }

        @Test
        void boursier_5_pourcent_par_echelon() {
            assertEquals(440.00, devis("B105", BOURSIER, 0), CENTIME);
            assertEquals(374.00, devis("B105", BOURSIER, 3), CENTIME);
        }

        @Test
        void remise_boursier_plafonnee_a_30_pourcent() {
            assertEquals(308.00, devis("B105", BOURSIER, 6), CENTIME);
            assertEquals(308.00, devis("B105", BOURSIER, 7), CENTIME);
        }

        @Test
        void la_remise_porte_aussi_sur_les_options() {
            assertEquals(416.16, devis("B105", BOURSIER, 4, MENAGE, PARKING), CENTIME);
        }

        @Test
        void alternant_10_pourcent() {
            assertEquals(436.50, devis("B105", ALTERNANT, 0, PARKING), CENTIME);
        }

        @Test
        void international_sans_remise() {
            assertEquals(440.00, devis("B105", INTERNATIONAL, 0), CENTIME);
        }

        @Test
        void l_echelon_est_ignore_hors_boursier() {
            assertEquals(440.00, devis("B105", STANDARD, 5), CENTIME);
        }

        @Test
        void devis_sur_un_logement_inconnu() {
            assertTrue(systeme.devisLoyer(new DemandeDevis("Z999", STANDARD, 0, options())).isEmpty());
        }
    }

    @Nested
    @DisplayName("Attribution des logements")
    class Attribution {

        @Test
        void le_premier_logement_libre_du_type_demande_est_attribue() {
            assertEquals("A001", etat(bail(ALICE, STUDIO)).numeroLogement());
            assertEquals("A102", etat(bail(EMMA, STUDIO)).numeroLogement());
        }

        @Test
        void les_baux_sont_numerotes() {
            assertEquals("BAIL-001", bail(ALICE, STUDIO));
            assertEquals("BAIL-002", bail(EMMA, T2));
        }

        @Test
        void un_bail_signe_porte_le_loyer_et_les_charges() {
            EtatBail b = etat(bail(ALICE, STUDIO, PARKING, ASSURANCE));
            assertEquals(StatutBail.SIGNE, b.statut());
            assertEquals(451.67, b.loyerHorsCharges(), CENTIME);
            assertEquals(35.00, b.charges(), CENTIME);
            assertEquals(486.67, b.loyerChargesComprises(), CENTIME);
            assertEquals(0, b.solde(), CENTIME);
        }

        @Test
        void charges_reduites_en_colocation() {
            EtatBail b = etat(bail(BILAL, COLOCATION));
            assertEquals(189.75, b.loyerHorsCharges(), CENTIME);
            assertEquals(25.00, b.charges(), CENTIME);
            assertEquals(214.75, b.loyerChargesComprises(), CENTIME);
        }

        @Test
        void le_logement_attribue_est_occupe() {
            bail(ALICE, STUDIO);
            assertEquals(1, logement("A001").occupants());
        }

        @Test
        void une_colocation_se_remplit_avant_de_passer_a_la_suivante() {
            for (int i = 1; i <= 4; i++) {
                assertEquals("B301", etat(bail(standard("Coloc" + i, 3000), COLOCATION)).numeroLogement());
            }
            assertEquals(4, logement("B301").occupants());
            assertEquals("C001", etat(bail(standard("Coloc5", 3000), COLOCATION)).numeroLogement());
        }

        @Test
        void refus_quand_plus_aucun_logement_n_est_libre() {
            bail(standard("Un", 3000), T2);
            bail(standard("Deux", 3000), T2);
            assertFalse(estAccepte(standard("Trois", 3000), T2));
        }

        @Test
        void un_etudiant_pmr_n_obtient_qu_un_logement_accessible() {
            assertEquals("A104", etat(bail(ALICE, T1)).numeroLogement());
            assertFalse(estAccepte(CHLOE, T1)); // B105 n'est pas accessible
            assertEquals("B105", etat(bail(EMMA, T1)).numeroLogement());
        }

        @Test
        void les_ressources_doivent_couvrir_3_fois_le_loyer() {
            // A001 (432,67 € CC) et A102 (437,50 €) sont trop chers, B103 (385 €) passe
            assertEquals("B103", etat(bail(standard("Moyen", 1200), STUDIO)).numeroLogement());
            assertFalse(estAccepte(standard("Modeste", 1100), STUDIO));
        }

        @Test
        void un_alternant_doit_couvrir_2_fois_le_loyer() {
            assertEquals("A001", etat(bail(profil(ALTERNANT, 800), STUDIO)).numeroLogement());
        }

        @Test
        void un_alternant_sans_ressources_suffisantes_est_refuse() {
            assertFalse(estAccepte(profil(ALTERNANT, 600), STUDIO));
        }

        @Test
        void un_boursier_est_garanti_quelles_que_soient_ses_ressources() {
            assertEquals("A001", etat(bail(HUGO, STUDIO)).numeroLogement());
        }

        @Test
        void un_international_doit_couvrir_3_fois_le_loyer() {
            assertFalse(estAccepte(profil(INTERNATIONAL, 2000), T2));
            assertEquals("A201", etat(bail(DIEGO, T2)).numeroLogement());
        }

        @Test
        void les_options_comptent_dans_la_solvabilite() {
            // B103 avec parking : 430 € CC, il faut 1290 €
            assertFalse(estAccepte(standard("Juste", 1200), STUDIO, PARKING));
        }

        @Test
        void un_refus_n_envoie_aucune_notification() {
            estAccepte(standard("Modeste", 1100), STUDIO);
            assertTrue(systeme.notifications().isEmpty());
        }

        @Test
        void standard_un_mois_de_caution_et_100_euros_de_frais() {
            EtatBail b = etat(bail(ALICE, STUDIO));
            assertEquals(397.67, b.caution(), CENTIME);
            assertEquals(100.00, b.fraisDossier(), CENTIME);
        }

        @Test
        void boursier_ni_caution_ni_frais() {
            EtatBail b = etat(bail(BILAL, COLOCATION));
            assertEquals(0.00, b.caution(), CENTIME);
            assertEquals(0.00, b.fraisDossier(), CENTIME);
        }

        @Test
        void alternant_un_mois_de_caution_et_100_euros_de_frais() {
            EtatBail b = etat(bail(CHLOE, T1));
            assertEquals(428.49, b.loyerHorsCharges(), CENTIME);
            assertEquals(428.49, b.caution(), CENTIME);
            assertEquals(100.00, b.fraisDossier(), CENTIME);
        }

        @Test
        void international_deux_mois_de_caution_et_150_euros_de_frais() {
            EtatBail b = etat(bail(DIEGO, T2, PARKING));
            assertEquals(685.00, b.loyerHorsCharges(), CENTIME);
            assertEquals(1370.00, b.caution(), CENTIME);
            assertEquals(150.00, b.fraisDossier(), CENTIME);
        }
    }

    @Nested
    @DisplayName("Cycle de vie du bail")
    class CycleDeVie {

        @Test
        void entree_dans_les_lieux() {
            String id = bail(ALICE, STUDIO);
            assertTrue(systeme.entrer(id, RENTREE));
            assertEquals(StatutBail.ACTIF, etat(id).statut());
        }

        @Test
        void pas_d_entree_avant_la_signature() {
            String id = bail(ALICE, STUDIO);
            assertFalse(systeme.entrer(id, LocalDate.of(2026, 8, 20)));
            assertEquals(StatutBail.SIGNE, etat(id).statut());
            assertTrue(systeme.entrer(id, SIGNATURE));
        }

        @Test
        void on_n_entre_qu_une_fois() {
            String id = bailActif(ALICE, STUDIO);
            assertFalse(systeme.entrer(id, RENTREE));
        }

        @Test
        void bail_inconnu() {
            assertTrue(systeme.consulter("BAIL-999").isEmpty());
            assertFalse(systeme.entrer("BAIL-999", RENTREE));
            assertFalse(systeme.payer("BAIL-999", SEPTEMBRE, RENTREE, 100));
            assertFalse(systeme.donnerPreavis("BAIL-999", RENTREE));
            assertFalse(systeme.sortir("BAIL-999", RENTREE, 0));
            assertFalse(systeme.annuler("BAIL-999"));
        }

        @Test
        void pas_de_paiement_avant_l_entree() {
            String id = bail(ALICE, STUDIO);
            assertFalse(systeme.payer(id, SEPTEMBRE, RENTREE, 432.67));
        }

        @Test
        void paiement_a_l_heure_jusqu_au_5() {
            String id = bailActif(ALICE, STUDIO);
            assertTrue(systeme.payer(id, SEPTEMBRE, LocalDate.of(2026, 9, 5), 432.67));
            assertEquals(0, etat(id).solde(), CENTIME);
        }

        @Test
        void penalite_de_5_pourcent_apres_le_5() {
            String id = bailActif(ALICE, STUDIO);
            systeme.payer(id, SEPTEMBRE, LocalDate.of(2026, 9, 6), 432.67);
            assertEquals(21.63, etat(id).solde(), CENTIME);
        }

        @Test
        void penalite_minimum_de_10_euros() {
            systeme.ajouterLogement(new DemandeLogement(COLOCATION, "C010", 30, 1, false, 4, 0));
            bail(standard("Un", 3000), COLOCATION);
            bail(standard("Deux", 3000), COLOCATION);
            bail(standard("Trois", 3000), COLOCATION);
            bail(standard("Quatre", 3000), COLOCATION);
            bail(standard("Cinq", 3000), COLOCATION);
            bail(standard("Six", 3000), COLOCATION);
            bail(standard("Sept", 3000), COLOCATION);
            String id = bailActif(standard("Huit", 3000), COLOCATION);
            assertEquals("C010", etat(id).numeroLogement());
            assertEquals(107.50, etat(id).loyerChargesComprises(), CENTIME);
            systeme.payer(id, SEPTEMBRE, LocalDate.of(2026, 9, 10), 107.50);
            assertEquals(10.00, etat(id).solde(), CENTIME);
        }

        @Test
        void un_boursier_n_a_pas_de_penalite() {
            String id = bailActif(BILAL, COLOCATION);
            systeme.payer(id, SEPTEMBRE, LocalDate.of(2026, 9, 20), 214.75);
            assertEquals(0, etat(id).solde(), CENTIME);
        }

        @Test
        void les_paiements_partiels_s_accumulent() {
            String id = bailActif(ALICE, STUDIO);
            systeme.payer(id, SEPTEMBRE, LocalDate.of(2026, 9, 3), 400);
            systeme.payer(id, OCTOBRE, LocalDate.of(2026, 10, 3), 432.67);
            assertEquals(32.67, etat(id).solde(), CENTIME);
        }

        @Test
        void un_trop_percu_est_une_avance() {
            String id = bailActif(ALICE, STUDIO);
            systeme.payer(id, SEPTEMBRE, LocalDate.of(2026, 9, 3), 500);
            assertEquals(-67.33, etat(id).solde(), CENTIME);
        }

        @Test
        void preavis_de_30_jours_pour_un_standard() {
            String id = bailActif(ALICE, STUDIO);
            assertTrue(systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20)));
            assertEquals(StatutBail.PREAVIS, etat(id).statut());
            assertEquals(LocalDate.of(2026, 11, 19), etat(id).dateFin());
        }

        @Test
        void preavis_de_15_jours_pour_un_boursier_ou_un_alternant() {
            String b = bailActif(BILAL, COLOCATION);
            String c = bailActif(CHLOE, T1);
            systeme.donnerPreavis(b, LocalDate.of(2026, 10, 20));
            systeme.donnerPreavis(c, LocalDate.of(2026, 10, 20));
            assertEquals(LocalDate.of(2026, 11, 4), etat(b).dateFin());
            assertEquals(LocalDate.of(2026, 11, 4), etat(c).dateFin());
        }

        @Test
        void preavis_de_30_jours_pour_un_international() {
            String id = bailActif(DIEGO, T2);
            systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20));
            assertEquals(LocalDate.of(2026, 11, 19), etat(id).dateFin());
        }

        @Test
        void pas_de_preavis_sans_entree() {
            String id = bail(ALICE, STUDIO);
            assertFalse(systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20)));
            assertEquals(null, etat(id).dateFin());
        }

        @Test
        void on_paie_encore_pendant_le_preavis() {
            String id = bailActif(ALICE, STUDIO);
            systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20));
            assertTrue(systeme.payer(id, OCTOBRE, LocalDate.of(2026, 10, 21), 432.67));
        }

        @Test
        void pas_de_sortie_sans_preavis() {
            String id = bailActif(ALICE, STUDIO);
            assertFalse(systeme.sortir(id, LocalDate.of(2026, 12, 1), 0));
        }

        @Test
        void pas_de_sortie_avant_la_fin_du_preavis() {
            String id = bailActif(ALICE, STUDIO);
            systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20));
            assertFalse(systeme.sortir(id, LocalDate.of(2026, 11, 18), 0));
            assertTrue(systeme.sortir(id, LocalDate.of(2026, 11, 19), 0));
            assertEquals(StatutBail.TERMINE, etat(id).statut());
        }

        @Test
        void la_caution_est_rendue_moins_les_retenues_et_les_impayes() {
            String id = bailActif(ALICE, STUDIO);
            systeme.payer(id, SEPTEMBRE, LocalDate.of(2026, 9, 6), 432.67);
            systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20));
            systeme.sortir(id, LocalDate.of(2026, 11, 20), 50);
            EtatBail b = etat(id);
            assertEquals(326.04, b.cautionRestituee(), CENTIME);
            assertEquals(0, b.solde(), CENTIME);
        }

        @Test
        void une_avance_n_est_pas_deduite_de_la_caution() {
            String id = bailActif(ALICE, STUDIO);
            systeme.payer(id, SEPTEMBRE, LocalDate.of(2026, 9, 3), 500);
            systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20));
            systeme.sortir(id, LocalDate.of(2026, 11, 20), 0);
            assertEquals(397.67, etat(id).cautionRestituee(), CENTIME);
            assertEquals(0, etat(id).solde(), CENTIME);
        }

        @Test
        void ce_que_la_caution_ne_couvre_pas_reste_du() {
            String id = bailActif(BILAL, COLOCATION);
            systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20));
            systeme.sortir(id, LocalDate.of(2026, 11, 5), 40);
            assertEquals(0, etat(id).cautionRestituee(), CENTIME);
            assertEquals(40.00, etat(id).solde(), CENTIME);
        }

        @Test
        void la_sortie_libere_le_logement() {
            String id = bailActif(ALICE, STUDIO);
            systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20));
            systeme.sortir(id, LocalDate.of(2026, 11, 20), 0);
            assertEquals(0, logement("A001").occupants());
            assertEquals("A001", etat(bail(EMMA, STUDIO)).numeroLogement());
        }

        @Test
        void la_sortie_d_un_colocataire_libere_sa_place() {
            String id = bailActif(BILAL, COLOCATION);
            bail(ALICE, COLOCATION);
            systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20));
            systeme.sortir(id, LocalDate.of(2026, 11, 5), 0);
            assertEquals(1, logement("B301").occupants());
        }

        @Test
        void plus_de_paiement_apres_la_sortie() {
            String id = bailActif(ALICE, STUDIO);
            systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20));
            systeme.sortir(id, LocalDate.of(2026, 11, 20), 0);
            assertFalse(systeme.payer(id, OCTOBRE, LocalDate.of(2026, 11, 21), 100));
        }

        @Test
        void annulation_d_un_bail_signe() {
            String id = bail(ALICE, STUDIO);
            assertTrue(systeme.annuler(id));
            assertEquals(StatutBail.ANNULE, etat(id).statut());
            assertEquals(0, logement("A001").occupants());
        }

        @Test
        void annulation_en_colocation() {
            String id = bail(BILAL, COLOCATION);
            assertTrue(systeme.annuler(id));
            assertEquals(0, logement("B301").occupants());
        }

        @Test
        void pas_d_annulation_apres_l_entree() {
            String id = bailActif(ALICE, STUDIO);
            assertFalse(systeme.annuler(id));
            assertEquals(StatutBail.ACTIF, etat(id).statut());
        }
    }

    @Nested
    @DisplayName("Quittances")
    class Quittances {

        @Test
        void quittance_avec_penalite_et_reste_du() {
            String id = bailActif(ALICE, STUDIO, PARKING, ASSURANCE);
            systeme.payer(id, SEPTEMBRE, LocalDate.of(2026, 9, 10), 400);
            String attendu = """
                    ==========================================
                       RÉSIDENCE CAMPUS VERT - QUITTANCE
                    ==========================================
                    Bail      : BAIL-001
                    Locataire : Alice MARTIN (STANDARD)
                    Logement  : A001 - STUDIO 26.0 m² meublé
                    Période   : 2026-09
                    Options   : parking, assurance
                    ------------------------------------------
                    Loyer HC  : 451,67 €
                    Charges   : 35,00 €
                    Pénalité  : 24,33 €
                    Total dû  : 511,00 €
                    Payé le   : 10/09/2026 : 400,00 €
                    RESTE DÛ  : 111,00 €
                    ==========================================""";
            assertEquals(attendu, systeme.quittance(id, SEPTEMBRE).orElseThrow());
        }

        @Test
        void quittance_d_un_loyer_regle() {
            String id = bailActif(BILAL, COLOCATION);
            systeme.payer(id, SEPTEMBRE, LocalDate.of(2026, 9, 2), 214.75);
            String attendu = """
                    ==========================================
                       RÉSIDENCE CAMPUS VERT - QUITTANCE
                    ==========================================
                    Bail      : BAIL-001
                    Locataire : Bilal HADDAD (BOURSIER)
                    Logement  : B301 - COLOCATION 80.0 m² meublé
                    Période   : 2026-09
                    Options   : aucune
                    ------------------------------------------
                    Loyer HC  : 189,75 €
                    Charges   : 25,00 €
                    Total dû  : 214,75 €
                    Payé le   : 02/09/2026 : 214,75 €
                    ==========================================""";
            assertEquals(attendu, systeme.quittance(id, SEPTEMBRE).orElseThrow());
        }

        @Test
        void les_options_sont_listees_dans_un_ordre_fixe() {
            String id = bailActif(DIEGO, T2, ASSURANCE, FIBRE, PARKING, MENAGE, LAVERIE);
            systeme.payer(id, SEPTEMBRE, RENTREE, 1000);
            assertTrue(systeme.quittance(id, SEPTEMBRE).orElseThrow()
                    .contains("Options   : ménage, parking, laverie, fibre, assurance\n"));
        }

        @Test
        void quittance_d_un_mois_non_paye() {
            String id = bailActif(ALICE, STUDIO);
            assertEquals("Aucun paiement pour 2026-10", systeme.quittance(id, OCTOBRE).orElseThrow());
        }

        @Test
        void quittance_d_un_bail_inconnu() {
            assertTrue(systeme.quittance("BAIL-999", SEPTEMBRE).isEmpty());
        }
    }

    @Nested
    @DisplayName("Notifications")
    class Notifications {

        @Test
        void un_email_a_chaque_etape() {
            String id = bailActif(ALICE, STUDIO);
            systeme.payer(id, SEPTEMBRE, RENTREE, 432.67);
            systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20));
            systeme.sortir(id, LocalDate.of(2026, 11, 20), 0);
            assertEquals(List.of(
                    new Notification(Canal.EMAIL, "alice.martin@mail.fr", "Votre bail BAIL-001"),
                    new Notification(Canal.EMAIL, "alice.martin@mail.fr", "État des lieux d'entrée"),
                    new Notification(Canal.EMAIL, "alice.martin@mail.fr", "Quittance 2026-09"),
                    new Notification(Canal.EMAIL, "alice.martin@mail.fr", "Préavis de départ"),
                    new Notification(Canal.EMAIL, "alice.martin@mail.fr", "Sortie du logement A001")
            ), systeme.notifications());
        }

        @Test
        void paiement_incomplet() {
            String id = bailActif(ALICE, STUDIO);
            systeme.payer(id, SEPTEMBRE, LocalDate.of(2026, 9, 6), 432.67);
            assertEquals("Paiement incomplet 2026-09", objets(Canal.EMAIL).getLast());
        }

        @Test
        void annulation() {
            systeme.annuler(bail(ALICE, STUDIO));
            assertEquals("Annulation du bail BAIL-001", objets(Canal.EMAIL).getLast());
        }

        @Test
        void sms_avec_le_sujet_et_le_message() {
            bail(BILAL, COLOCATION);
            assertEquals(List.of(new Notification(Canal.SMS, "0611223344",
                            "Votre bail BAIL-001 - Bienvenue à la Résidence Campus Vert ! Logement B301, loyer 214,75 € charges comprises.")),
                    systeme.notifications());
        }

        @Test
        void sms_tronque_a_160_caracteres() {
            bailActif(BILAL, COLOCATION);
            String sms = objets(Canal.SMS).getLast();
            assertEquals(160, sms.length());
            assertTrue(sms.startsWith("État des lieux d'entrée - Votre état des lieux d'entrée du logement B301 est fait le 01/09/2026."));
            assertTrue(sms.endsWith("..."));
        }

        @Test
        void sms_de_sortie_avec_reste_du() {
            String id = bailActif(BILAL, COLOCATION);
            systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20));
            systeme.sortir(id, LocalDate.of(2026, 11, 5), 40);
            assertEquals("Sortie du logement B301 - Votre dépôt de garantie : 0,00 € vous sera restitué sous 30 jours. Il reste 40,00 € à régler.",
                    objets(Canal.SMS).getLast());
        }

        @Test
        void courrier_a_l_adresse_postale() {
            bail(EMMA, STUDIO);
            assertEquals(List.of(new Notification(Canal.COURRIER, "12 rue des Lilas, 35000 Rennes", "Votre bail BAIL-001")),
                    systeme.notifications());
        }

        @Test
        void un_international_recoit_aussi_un_email_en_anglais() {
            bail(DIEGO, T2);
            assertEquals(List.of(
                    new Notification(Canal.EMAIL, "diego.alvarez@mail.es", "[EN] Votre bail BAIL-001"),
                    new Notification(Canal.SMS, "+34612345678",
                            "Votre bail BAIL-001 - Bienvenue à la Résidence Campus Vert ! Logement A201, loyer 675,00 € charges comprises.")
            ), systeme.notifications());
        }

        @Test
        void un_international_par_email_ne_recoit_qu_un_email() {
            bail(LI, T2);
            assertEquals(List.of(new Notification(Canal.EMAIL, "li.wang@mail.cn", "Votre bail BAIL-001")),
                    systeme.notifications());
        }
    }

    @Nested
    @DisplayName("Relance des impayés")
    class Relances {

        @Test
        void seuls_les_baux_avec_un_solde_du_sont_relances() {
            String a = bailActif(ALICE, STUDIO);
            String b = bailActif(BILAL, COLOCATION);
            String e = bailActif(EMMA, STUDIO);
            String c = bailActif(CHLOE, T1);
            systeme.payer(a, SEPTEMBRE, LocalDate.of(2026, 9, 6), 432.67);
            systeme.payer(b, SEPTEMBRE, LocalDate.of(2026, 9, 2), 100);
            systeme.payer(e, SEPTEMBRE, LocalDate.of(2026, 9, 2), 400);
            systeme.payer(c, SEPTEMBRE, LocalDate.of(2026, 9, 2), 1000);
            int avant = systeme.notifications().size();

            systeme.relancerImpayes();

            List<Notification> relances = systeme.notifications();
            assertEquals(avant + 3, relances.size());
            assertEquals("RELANCE - loyer impayé", objets(Canal.EMAIL).getLast());
            assertEquals("Campus Vert : 114,75 € impayés sur BAIL-002", objets(Canal.SMS).getLast());
            assertEquals("Relance loyer impayé", objets(Canal.COURRIER).getLast());
        }

        @Test
        void un_bail_termine_avec_un_reste_du_est_relance() {
            String id = bailActif(BILAL, COLOCATION);
            systeme.donnerPreavis(id, LocalDate.of(2026, 10, 20));
            systeme.sortir(id, LocalDate.of(2026, 11, 5), 40);
            systeme.relancerImpayes();
            assertEquals("Campus Vert : 40,00 € impayés sur BAIL-001", objets(Canal.SMS).getLast());
        }

        @Test
        void aucune_relance_sans_impaye() {
            String id = bailActif(ALICE, STUDIO);
            systeme.payer(id, SEPTEMBRE, RENTREE, 432.67);
            int avant = systeme.notifications().size();
            systeme.relancerImpayes();
            assertEquals(avant, systeme.notifications().size());
        }
    }

    @Nested
    @DisplayName("Rapport")
    class Rapport {

        @Test
        void rapport_initial() {
            String attendu = """
                    =============== RAPPORT Résidence Campus Vert ===============
                    Studios      : 0/3
                    T1           : 0/2
                    T2           : 0/2
                    Colocation   : 0/7 places
                    Occupation   : 0 %
                    Balcons      : 6.0 m²
                    Loyers CC    : 0,00 € / mois
                    Cautions     : 0,00 €
                    Impayés      : 0,00 €
                    SMS envoyés  : 0
                    Timbres      : 0,00 €
                    ============================================================""";
            assertEquals(attendu, systeme.rapport());
        }

        @Test
        void rapport_en_cours_d_annee() {
            String a = bailActif(ALICE, STUDIO);
            bail(EMMA, STUDIO); // signé mais pas encore entrée : occupe le logement, pas compté dans les loyers
            String b = bailActif(BILAL, COLOCATION);
            systeme.payer(a, SEPTEMBRE, RENTREE, 432.67);
            systeme.payer(b, SEPTEMBRE, RENTREE, 100);
            String attendu = """
                    =============== RAPPORT Résidence Campus Vert ===============
                    Studios      : 2/3
                    T1           : 0/2
                    T2           : 0/2
                    Colocation   : 1/7 places
                    Occupation   : 21 %
                    Balcons      : 6.0 m²
                    Loyers CC    : 647,42 € / mois
                    Cautions     : 397,67 €
                    Impayés      : 114,75 €
                    SMS envoyés  : 3
                    Timbres      : 1,29 €
                    ============================================================""";
            assertEquals(attendu, systeme.rapport());
        }
    }
}

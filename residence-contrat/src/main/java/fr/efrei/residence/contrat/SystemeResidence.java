package fr.efrei.residence.contrat;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Point d'entrée unique utilisé par les tests de contrat.
 * <p>
 * Chaque implémentation (ancien code, nouveau code) fournit un adaptateur qui traduit ces appels
 * vers son propre modèle. Les tests ne dépendent donc d'aucune classe interne d'une implémentation.
 */
public interface SystemeResidence {

    /** Remet le système à l'état initial : parc du catalogue, aucun étudiant, aucun bail, aucune notification. */
    void reinitialiser();

    /** @return la référence de l'étudiant, ou vide si l'inscription est refusée */
    Optional<String> inscrire(DemandeInscription demande);

    /** @return {@code false} si le logement est refusé */
    boolean ajouterLogement(DemandeLogement demande);

    Optional<EtatLogement> logement(String numero);

    /** @return le loyer hors charges, ou vide si le logement est inconnu */
    OptionalDouble devisLoyer(DemandeDevis demande);

    /** @return l'identifiant du bail signé, ou vide si aucun logement ne convient */
    Optional<String> attribuer(DemandeAttribution demande);

    Optional<EtatBail> consulter(String idBail);

    boolean entrer(String idBail, LocalDate date);

    boolean payer(String idBail, YearMonth mois, LocalDate date, double montant);

    boolean donnerPreavis(String idBail, LocalDate date);

    boolean sortir(String idBail, LocalDate date, double retenuesDegats);

    boolean annuler(String idBail);

    /** @return le texte de la quittance, ou vide si le bail est inconnu */
    Optional<String> quittance(String idBail, YearMonth mois);

    void relancerImpayes();

    String rapport();

    /** Toutes les notifications envoyées depuis la dernière réinitialisation, dans l'ordre d'envoi par canal. */
    List<Notification> notifications();
}

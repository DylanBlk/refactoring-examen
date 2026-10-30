package fr.efrei.residence;

/**
 * Un moyen d'envoyer un message à un étudiant (email, SMS, courrier…).
 */
public interface CanalNotification {

    /** Envoie une notification classique (bail signé, entrée, paiement…). */
    void notifier(Etudiant e, String sujet, String message);

    /** Envoie une relance d'impayé, avec le texte propre à ce canal. */
    void relancer(Etudiant e, Bail b);
}

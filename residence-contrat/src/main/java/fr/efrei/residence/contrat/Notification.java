package fr.efrei.residence.contrat;

/**
 * @param destinataire adresse email, numéro de téléphone ou adresse postale
 * @param objet        sujet de l'email ou du courrier, texte complet du SMS
 */
public record Notification(Canal canal, String destinataire, String objet) {
}

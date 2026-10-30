package fr.efrei.residence.contrat;

/**
 * @param places     nombre de places (1 sauf en colocation)
 * @param occupants  nombre d'étudiants actuellement logés
 */
public record EtatLogement(String numero, TypeLogement type, int places, int occupants, boolean accessiblePMR) {
}

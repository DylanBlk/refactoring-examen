package fr.efrei.residence.contrat;

/**
 * @param places         nombre de places, utilisé seulement pour une colocation
 * @param surfaceBalcon  en m², utilisée seulement pour un T2
 */
public record DemandeLogement(TypeLogement type, String numero, double surface, int etage, boolean meuble,
                              int places, int surfaceBalcon) {
}

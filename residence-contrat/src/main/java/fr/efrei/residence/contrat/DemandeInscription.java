package fr.efrei.residence.contrat;

/**
 * @param echelonBourse          de 0 à 7, ignoré si le profil n'est pas {@link Profil#BOURSIER}
 * @param ressourcesMensuelles   en euros
 * @param pmr                    personne à mobilité réduite
 */
public record DemandeInscription(String nom, String prenom, String email, String telephone, String adresse,
                                 Profil profil, int echelonBourse, double ressourcesMensuelles, boolean pmr,
                                 Canal canalPrefere) {
}

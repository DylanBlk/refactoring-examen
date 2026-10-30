package fr.efrei.residence.contrat;

import java.time.LocalDate;

/**
 * Vue d'un bail telle que l'observent les tests.
 *
 * @param dateFin           fin du bail après un préavis, {@code null} sinon
 * @param solde             reste dû par l'étudiant (négatif = avance)
 * @param cautionRestituee  dépôt de garantie rendu à la sortie
 */
public record EtatBail(String id, String numeroLogement, StatutBail statut, double loyerHorsCharges,
                       double charges, double loyerChargesComprises, double caution, double fraisDossier,
                       LocalDate dateFin, double solde, double cautionRestituee) {
}

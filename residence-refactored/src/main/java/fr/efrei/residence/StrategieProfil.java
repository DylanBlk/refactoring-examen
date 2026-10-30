package fr.efrei.residence;

public interface StrategieProfil {
    int COEFFICIENT_SOLVABILITE_STANDARD = 3;
    double FRAIS_DOSSIER_STANDARD = 100;
    int PREAVIS_NORMAL = 30;
    int PREAVIS_COURT = 15;
    double TAUX_PENALITE = 0.05;
    double PENALITE_MINIMUM = 10;
    double appliquerRemise(double montant, int echelon);
    default boolean estSolvable(double ressources, double loyerCC) { return ressources >= loyerCC * COEFFICIENT_SOLVABILITE_STANDARD; }
    default double calculerCaution(double loyerHC) { return loyerHC; }
    default double fraisDossier() { return FRAIS_DOSSIER_STANDARD; }
    default int dureePreavis() { return PREAVIS_NORMAL; }
    default double penaliteRetard(double loyerCC) { return Utils.arrondir(Math.max(loyerCC * TAUX_PENALITE, PENALITE_MINIMUM)); }
    default String erreurInscription(int echelon, String telephone) { return null; }
    default boolean emailSupplementaire() { return false; }
}

package fr.efrei.residence;

public class CalculateurLoyer {

    // loyer de base selon le type
    private static final double PRIX_M2_STUDIO = 14;
    private static final double LOYER_MIN_STUDIO = 350;
    private static final double PRIX_M2_T1 = 13;
    private static final double FORFAIT_T1 = 50;
    private static final double PRIX_M2_T2 = 12;
    private static final double FORFAIT_T2 = 100;
    private static final double PRIX_M2_COLOCATION = 11;

    // caractéristiques du logement
    private static final double COEF_MEUBLE = 1.15;
    private static final int ETAGE_REZ_DE_CHAUSSEE = 0;
    private static final double COEF_REZ_DE_CHAUSSEE = 0.95;

    // options
    private static final double COEF_MENAGE = 1.08;
    private static final double COEF_MENAGE_COLOCATION = 1.04;
    private static final double PRIX_PARKING = 45;
    private static final double PRIX_LAVERIE = 12;
    private static final double PRIX_FIBRE = 20;
    private static final double PRIX_ASSURANCE = 9;

    // remises selon le profil
    private static final double REMISE_PAR_ECHELON = 0.05;
    private static final double REMISE_MAX_BOURSIER = 0.30;
    private static final double COEF_ALTERNANT = 0.90;

    // charges forfaitaires
    private static final double CHARGES = 35;
    private static final double CHARGES_COLOCATION = 25;

    /**
     * Loyer mensuel hors charges, options comprises, remise déduite.
     */
    public double calculerLoyer(Logement l, Profil profil, int echelon, boolean parking, boolean menage,
                                boolean laverie, boolean fibre, boolean assurance) {
        // loyer de base selon le type
        double loyer;
        if (l.getType() == TypeLogement.STUDIO) {
            loyer = l.getSurface() * PRIX_M2_STUDIO;
            if (loyer < LOYER_MIN_STUDIO) loyer = LOYER_MIN_STUDIO;
        } else if (l.getType() == TypeLogement.T1) {
            loyer = l.getSurface() * PRIX_M2_T1 + FORFAIT_T1;
        } else if (l.getType() == TypeLogement.T2) {
            loyer = l.getSurface() * PRIX_M2_T2 + FORFAIT_T2;
        } else if (l.getType() == TypeLogement.COLOCATION) {
            loyer = l.getSurface() * PRIX_M2_COLOCATION / l.getNombrePlaces();
        } else {
            throw new IllegalArgumentException("Type de logement inconnu : " + l.getType());
        }

        if (l.isMeuble()) loyer = loyer * COEF_MEUBLE;
        if (l.getEtage() == ETAGE_REZ_DE_CHAUSSEE) loyer = loyer * COEF_REZ_DE_CHAUSSEE;

        // options
        if (menage) {
            if (l.getType() == TypeLogement.COLOCATION) {
                loyer = loyer * COEF_MENAGE_COLOCATION; // parties communes déjà nettoyées
            } else {
                loyer = loyer * COEF_MENAGE;
            }
        }
        if (parking) loyer = loyer + PRIX_PARKING;
        if (laverie) loyer = loyer + PRIX_LAVERIE;
        if (fibre && l.getType() != TypeLogement.COLOCATION) loyer = loyer + PRIX_FIBRE; // fibre incluse en coloc
        if (assurance) loyer = loyer + PRIX_ASSURANCE;

        // remise selon le profil
        if (profil == Profil.BOURSIER) {
            double taux = echelon * REMISE_PAR_ECHELON;
            if (taux > REMISE_MAX_BOURSIER) taux = REMISE_MAX_BOURSIER;
            loyer = loyer * (1 - taux);
        } else if (profil == Profil.ALTERNANT) {
            loyer = loyer * COEF_ALTERNANT;
        }

        return Utils.arrondir(loyer);
    }

    public double calculerCharges(Logement l) {
        if (l.getType() == TypeLogement.COLOCATION) return CHARGES_COLOCATION;
        return CHARGES;
    }
}

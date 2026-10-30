package fr.efrei.residence;

public class CalculateurLoyer {
    private static final double COEF_MEUBLE = 1.15;
    private static final int ETAGE_REZ_DE_CHAUSSEE = 0;
    private static final double COEF_REZ_DE_CHAUSSEE = 0.95;
    private static final double PRIX_PARKING = 45;
    private static final double PRIX_LAVERIE = 12;
    private static final double PRIX_ASSURANCE = 9;

    /**
     * Loyer mensuel hors charges, options comprises, remise déduite.
     */
    public double calculerLoyer(Logement l, Profil profil, int echelon, boolean parking, boolean menage,
                                boolean laverie, boolean fibre, boolean assurance) {
        StrategieLogement tarif = l.getType().strategie();
        double loyer = tarif.loyerBase(l);
        if (l.isMeuble()) loyer = loyer * COEF_MEUBLE;
        if (l.getEtage() == ETAGE_REZ_DE_CHAUSSEE) loyer = loyer * COEF_REZ_DE_CHAUSSEE;
        if (menage) loyer = loyer * tarif.coefficientMenage();
        if (parking) loyer = loyer + PRIX_PARKING;
        if (laverie) loyer = loyer + PRIX_LAVERIE;
        if (fibre) loyer = loyer + tarif.prixFibre();
        if (assurance) loyer = loyer + PRIX_ASSURANCE;
        return Utils.arrondir(profil.strategie().appliquerRemise(loyer, echelon));
    }

    public double calculerCharges(Logement l) { return l.getType().strategie().charges(); }
}

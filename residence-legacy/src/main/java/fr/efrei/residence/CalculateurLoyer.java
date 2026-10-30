package fr.efrei.residence;

public class CalculateurLoyer {

    /**
     * Loyer mensuel hors charges, options comprises, remise déduite.
     */
    public double calculerLoyer(Logement l, String profil, int echelon, boolean parking, boolean menage,
                                boolean laverie, boolean fibre, boolean assurance) {
        // loyer de base selon le type
        double loyer;
        if (l.getType().equals("STUDIO")) {
            loyer = l.getSurface() * 14;
            if (loyer < 350) loyer = 350;
        } else if (l.getType().equals("T1")) {
            loyer = l.getSurface() * 13 + 50;
        } else if (l.getType().equals("T2")) {
            loyer = l.getSurface() * 12 + 100;
        } else if (l.getType().equals("COLOCATION")) {
            loyer = l.getSurface() * 11 / l.getNombrePlaces();
        } else {
            throw new IllegalArgumentException("Type de logement inconnu : " + l.getType());
        }

        if (l.isMeuble()) loyer = loyer * 1.15;
        if (l.getEtage() == 0) loyer = loyer * 0.95;

        // options
        if (menage) {
            if (l.getType().equals("COLOCATION")) {
                loyer = loyer * 1.04; // parties communes déjà nettoyées
            } else {
                loyer = loyer * 1.08;
            }
        }
        if (parking) loyer = loyer + 45;
        if (laverie) loyer = loyer + 12;
        if (fibre && !l.getType().equals("COLOCATION")) loyer = loyer + 20; // fibre incluse en coloc
        if (assurance) loyer = loyer + 9;

        // remise selon le profil
        if (profil.equals("BOURSIER")) {
            double taux = echelon * 0.05;
            if (taux > 0.30) taux = 0.30;
            loyer = loyer * (1 - taux);
        } else if (profil.equals("ALTERNANT")) {
            loyer = loyer * 0.90;
        }

        return Math.round(loyer * 100.0) / 100.0;
    }

    public double calculerCharges(Logement l) {
        if (l.getType().equals("COLOCATION")) return 25;
        return 35;
    }
}

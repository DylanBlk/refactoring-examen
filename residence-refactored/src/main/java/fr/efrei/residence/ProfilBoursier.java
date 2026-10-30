package fr.efrei.residence;

public class ProfilBoursier implements StrategieProfil {
    private static final int ECHELON_MIN = 0;
    private static final int ECHELON_MAX = 7;
    private static final double REMISE_PAR_ECHELON = 0.05;
    private static final double REMISE_MAX = 0.30;
    @Override
    public double appliquerRemise(double montant, int echelon) {
        return montant * (1 - Math.min(echelon * REMISE_PAR_ECHELON, REMISE_MAX));
    }
    @Override
    public boolean estSolvable(double ressources, double loyerCC) { return true; }
    @Override
    public double calculerCaution(double loyerHC) { return 0; }
    @Override
    public double fraisDossier() { return 0; }
    @Override
    public int dureePreavis() { return PREAVIS_COURT; }
    @Override
    public double penaliteRetard(double loyerCC) { return 0; }
    @Override
    public String erreurInscription(int echelon, String telephone) {
        return echelon < ECHELON_MIN || echelon > ECHELON_MAX
                ? "Inscription refus\u00e9e : \u00e9chelon de bourse invalide" : null;
    }
}

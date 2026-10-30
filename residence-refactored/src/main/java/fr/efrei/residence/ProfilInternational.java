package fr.efrei.residence;

public class ProfilInternational implements StrategieProfil {
    private static final int MOIS_CAUTION = 2;
    private static final double FRAIS_DOSSIER = 150;
    @Override
    public double appliquerRemise(double montant, int echelon) { return montant; }
    @Override
    public double calculerCaution(double loyerHC) { return loyerHC * MOIS_CAUTION; }
    @Override
    public double fraisDossier() { return FRAIS_DOSSIER; }
    @Override
    public boolean emailSupplementaire() { return true; }
    @Override
    public String erreurInscription(int echelon, String telephone) {
        return telephone == null || !telephone.startsWith("+")
                ? "Inscription refus\u00e9e : num\u00e9ro international (+XX) obligatoire" : null;
    }
}

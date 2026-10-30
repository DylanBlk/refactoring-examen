package fr.efrei.residence;

public class ProfilAlternant implements StrategieProfil {
    private static final double COEFFICIENT_REMISE = 0.90;
    private static final int COEFFICIENT_SOLVABILITE = 2;
    @Override
    public double appliquerRemise(double montant, int echelon) { return montant * COEFFICIENT_REMISE; }
    @Override
    public boolean estSolvable(double ressources, double loyerCC) {
        return ressources >= loyerCC * COEFFICIENT_SOLVABILITE;
    }
    @Override
    public int dureePreavis() { return PREAVIS_COURT; }
}

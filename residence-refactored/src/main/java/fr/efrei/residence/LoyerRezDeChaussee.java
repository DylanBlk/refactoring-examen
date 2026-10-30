package fr.efrei.residence;

public class LoyerRezDeChaussee extends DecorateurLoyer {
    private static final double COEFFICIENT = 0.95;

    public LoyerRezDeChaussee(Loyer loyer) {
        super(loyer);
    }

    @Override
    public double montant() {
        return loyer.montant() * COEFFICIENT;
    }
}

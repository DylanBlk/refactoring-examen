package fr.efrei.residence;

public class LoyerBase implements Loyer {
    private final double montant;

    public LoyerBase(double montant) {
        this.montant = montant;
    }

    @Override
    public double montant() {
        return montant;
    }
}

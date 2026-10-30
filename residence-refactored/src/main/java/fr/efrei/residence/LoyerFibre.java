package fr.efrei.residence;

public class LoyerFibre extends DecorateurLoyer {
    private final double prix;

    public LoyerFibre(Loyer loyer, double prix) {
        super(loyer);
        this.prix = prix;
    }

    @Override
    public double montant() {
        return loyer.montant() + prix;
    }
}

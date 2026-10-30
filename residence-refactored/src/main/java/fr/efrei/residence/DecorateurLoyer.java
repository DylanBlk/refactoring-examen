package fr.efrei.residence;

public abstract class DecorateurLoyer implements Loyer {
    protected final Loyer loyer;

    public DecorateurLoyer(Loyer loyer) {
        this.loyer = loyer;
    }
}

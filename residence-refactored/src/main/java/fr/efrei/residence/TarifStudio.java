package fr.efrei.residence;

public class TarifStudio implements StrategieLogement {
    private static final double PRIX_M2 = 14;
    private static final double LOYER_MINIMUM = 350;
    @Override
    public double loyerBase(Logement logement) { return Math.max(logement.getSurface() * PRIX_M2, LOYER_MINIMUM); }
}

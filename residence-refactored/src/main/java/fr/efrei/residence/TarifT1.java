package fr.efrei.residence;

public class TarifT1 implements StrategieLogement {
    private static final double PRIX_M2 = 13;
    private static final double FORFAIT = 50;
    @Override
    public double loyerBase(Logement logement) { return logement.getSurface() * PRIX_M2 + FORFAIT; }
}

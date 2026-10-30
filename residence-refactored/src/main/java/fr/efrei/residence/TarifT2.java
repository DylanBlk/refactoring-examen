package fr.efrei.residence;

public class TarifT2 implements StrategieLogement {
    private static final double PRIX_M2 = 12;
    private static final double FORFAIT = 100;
    @Override
    public double loyerBase(Logement logement) { return logement.getSurface() * PRIX_M2 + FORFAIT; }
}

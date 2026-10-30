package fr.efrei.residence;

public class TarifColocation implements StrategieLogement {
    private static final double PRIX_M2 = 11;
    private static final double COEFFICIENT_MENAGE_COLOCATION = 1.04;
    private static final double CHARGES_COLOCATION = 25;
    @Override
    public double loyerBase(Logement logement) {
        return logement.getSurface() * PRIX_M2 / logement.getNombrePlaces();
    }
    @Override
    public double coefficientMenage() { return COEFFICIENT_MENAGE_COLOCATION; }
    @Override
    public double prixFibre() { return 0; }
    @Override
    public double charges() { return CHARGES_COLOCATION; }
    @Override
    public int capacite(Logement logement) { return logement.getNombrePlaces(); }
    @Override
    public int occupation(Logement logement) { return logement.getColocataires().size(); }
}

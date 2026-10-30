package fr.efrei.residence;

public enum TypeLogement {
    STUDIO(new TarifStudio()), T1(new TarifT1()), T2(new TarifT2()), COLOCATION(new TarifColocation());

    private final StrategieLogement strategie;
    TypeLogement(StrategieLogement strategie) { this.strategie = strategie; }
    public StrategieLogement strategie() { return strategie; }
}

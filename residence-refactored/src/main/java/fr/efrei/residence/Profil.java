package fr.efrei.residence;

public enum Profil {
    STANDARD(new ProfilStandard()), BOURSIER(new ProfilBoursier()),
    ALTERNANT(new ProfilAlternant()), INTERNATIONAL(new ProfilInternational());

    private final StrategieProfil strategie;
    Profil(StrategieProfil strategie) { this.strategie = strategie; }
    public StrategieProfil strategie() { return strategie; }
}

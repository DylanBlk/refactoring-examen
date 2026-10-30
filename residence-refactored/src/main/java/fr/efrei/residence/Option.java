package fr.efrei.residence;

public enum Option {
    MENAGE("m\u00e9nage"), PARKING("parking"), LAVERIE("laverie"), FIBRE("fibre"), ASSURANCE("assurance");

    private final String libelle;

    Option(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}

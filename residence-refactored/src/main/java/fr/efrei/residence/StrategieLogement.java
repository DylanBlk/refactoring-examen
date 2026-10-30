package fr.efrei.residence;

public interface StrategieLogement {
    double COEFFICIENT_MENAGE = 1.08;
    double PRIX_FIBRE = 20;
    double CHARGES = 35;
    double loyerBase(Logement logement);
    default double coefficientMenage() { return COEFFICIENT_MENAGE; }
    default double prixFibre() { return PRIX_FIBRE; }
    default double charges() { return CHARGES; }
    default int capacite(Logement logement) { return 1; }
    default int occupation(Logement logement) { return logement.estLibre() ? 0 : 1; }
}

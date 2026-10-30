package fr.efrei.residence;

import java.util.Set;

public class AssemblageLoyer {
    private static final int REZ_DE_CHAUSSEE = 0;

    public Loyer assembler(Logement logement, Set<Option> options) {
        StrategieLogement tarif = logement.getType().strategie();
        Loyer loyer = new LoyerBase(tarif.loyerBase(logement));

        if (logement.isMeuble()) {
            loyer = new LoyerMeuble(loyer);
        }
        if (logement.getEtage() == REZ_DE_CHAUSSEE) {
            loyer = new LoyerRezDeChaussee(loyer);
        }
        if (options.contains(Option.MENAGE)) {
            loyer = new LoyerMenage(loyer, tarif.coefficientMenage());
        }
        if (options.contains(Option.PARKING)) {
            loyer = new LoyerParking(loyer);
        }
        if (options.contains(Option.LAVERIE)) {
            loyer = new LoyerLaverie(loyer);
        }
        if (options.contains(Option.FIBRE)) {
            loyer = new LoyerFibre(loyer, tarif.prixFibre());
        }
        if (options.contains(Option.ASSURANCE)) {
            loyer = new LoyerAssurance(loyer);
        }
        return loyer;
    }
}

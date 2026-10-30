package fr.efrei.residence;

import java.util.Set;

public class CalculateurLoyer {
    private final AssemblageLoyer assemblage;

    public CalculateurLoyer(AssemblageLoyer assemblage) {
        this.assemblage = assemblage;
    }

    /**
     * Loyer mensuel hors charges, options comprises, remise déduite.
     */
    public double calculerLoyer(Logement logement, Profil profil, int echelon, Set<Option> options) {
        Loyer loyer = assemblage.assembler(logement, options);
        double montant = profil.strategie().appliquerRemise(loyer.montant(), echelon);
        return Utils.arrondir(montant);
    }

    public double calculerCharges(Logement logement) {
        return logement.getType().strategie().charges();
    }
}

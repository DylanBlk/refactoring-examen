package fr.efrei.residence;

import fr.efrei.residence.contrat.ContratResidenceTest;
import fr.efrei.residence.contrat.SystemeResidence;

class LegacyContratTest extends ContratResidenceTest {

    @Override
    protected SystemeResidence creerSysteme() {
        return new LegacyAdapter();
    }
}

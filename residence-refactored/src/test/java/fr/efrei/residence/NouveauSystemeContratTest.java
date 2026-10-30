package fr.efrei.residence;

import fr.efrei.residence.contrat.ContratResidenceTest;
import fr.efrei.residence.contrat.SystemeResidence;
import org.junit.jupiter.api.Disabled;

@Disabled("Retirer cette annotation dès que NouveauSystemeAdapter est branché sur le nouveau code")
class NouveauSystemeContratTest extends ContratResidenceTest {

    @Override
    protected SystemeResidence creerSysteme() {
        return new NouveauSystemeAdapter();
    }
}

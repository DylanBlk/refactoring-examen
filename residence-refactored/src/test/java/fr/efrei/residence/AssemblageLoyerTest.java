package fr.efrei.residence;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AssemblageLoyerTest {
    private static final double PRECISION = 0.000001;

    @Test
    void assembleLesCaracteristiquesAvantLeMenageEtLesOptionsFixes() {
        Logement logement = LogementFactory.creer(TypeLogement.T1, "TEST", 30, 0, true, 0);
        Loyer loyer = new AssemblageLoyer().assembler(logement, EnumSet.allOf(Option.class));
        assertEquals(605.156, loyer.montant(), PRECISION);
    }

    @Test
    void appliqueLaRemiseSurToutesLesCouchesPuisArrondit() {
        Logement logement = LogementFactory.creer(TypeLogement.T1, "TEST", 30, 0, true, 0);
        double montant = new CalculateurLoyer().calculerLoyer(logement, Profil.ALTERNANT, 0,
                EnumSet.allOf(Option.class));
        assertEquals(544.64, montant, PRECISION);
    }

    @Test
    void utiliseLeMenageReduitEtLaFibreIncluseEnColocation() {
        Logement logement = LogementFactory.creer(TypeLogement.COLOCATION, "TEST", 60, 1, false, 3);
        Loyer loyer = new AssemblageLoyer().assembler(logement, EnumSet.of(Option.MENAGE, Option.FIBRE));
        assertEquals(228.8, loyer.montant(), PRECISION);
    }

    @Test
    void sansOptionLeLoyerDeBaseEstConserve() {
        Logement logement = LogementFactory.creer(TypeLogement.STUDIO, "TEST", 20, 1, false, 0);
        Loyer loyer = new AssemblageLoyer().assembler(logement, EnumSet.noneOf(Option.class));
        assertEquals(350, loyer.montant(), PRECISION);
    }

    @Test
    void leBailConserveSesOptionsEtLeurOrdreDAffichage() {
        Bail bail = new Bail("TEST", null, null, LocalDate.of(2026, 9, 1));
        Set<Option> options = EnumSet.of(Option.ASSURANCE, Option.PARKING, Option.MENAGE);
        bail.setOptions(options);
        options.clear();
        assertEquals("m\u00e9nage, parking, assurance", bail.listerOptions());
        bail.setOptions(EnumSet.noneOf(Option.class));
        assertEquals("aucune", bail.listerOptions());
    }
}

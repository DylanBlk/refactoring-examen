package fr.efrei.residence;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DecorateurLoyerTest {
    private static final double PRECISION = 0.000001;

    @Test
    void meubleAjouteQuinzePourCent() {
        assertEquals(115, new LoyerMeuble(new LoyerBase(100)).montant(), PRECISION);
    }

    @Test
    void rezDeChausseeRetireCinqPourCent() {
        assertEquals(95, new LoyerRezDeChaussee(new LoyerBase(100)).montant(), PRECISION);
    }

    @Test
    void menageAjouteHuitPourCent() {
        assertEquals(108, new LoyerMenage(new LoyerBase(100), 1.08).montant(), PRECISION);
    }

    @Test
    void menageEnColocationAjouteQuatrePourCent() {
        assertEquals(104, new LoyerMenage(new LoyerBase(100), 1.04).montant(), PRECISION);
    }

    @Test
    void parkingAjouteQuaranteCinqEuros() {
        assertEquals(145, new LoyerParking(new LoyerBase(100)).montant(), PRECISION);
    }

    @Test
    void laverieAjouteDouzeEuros() {
        assertEquals(112, new LoyerLaverie(new LoyerBase(100)).montant(), PRECISION);
    }

    @Test
    void fibreAjouteVingtEuros() {
        assertEquals(120, new LoyerFibre(new LoyerBase(100), 20).montant(), PRECISION);
    }

    @Test
    void fibreIncluseNeChangePasLeLoyer() {
        assertEquals(100, new LoyerFibre(new LoyerBase(100), 0).montant(), PRECISION);
    }

    @Test
    void assuranceAjouteNeufEuros() {
        assertEquals(109, new LoyerAssurance(new LoyerBase(100)).montant(), PRECISION);
    }
}

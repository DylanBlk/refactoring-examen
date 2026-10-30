package fr.efrei.residence;

import fr.efrei.residence.contrat.DemandeAttribution;
import fr.efrei.residence.contrat.DemandeDevis;
import fr.efrei.residence.contrat.DemandeInscription;
import fr.efrei.residence.contrat.DemandeLogement;
import fr.efrei.residence.contrat.EtatBail;
import fr.efrei.residence.contrat.EtatLogement;
import fr.efrei.residence.contrat.Notification;
import fr.efrei.residence.contrat.SystemeResidence;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Branche votre nouveau code sur les tests de contrat. Cette classe ne doit contenir que de la traduction.
 */
public class NouveauSystemeAdapter implements SystemeResidence {

    @Override
    public void reinitialiser() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Optional<String> inscrire(DemandeInscription demande) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean ajouterLogement(DemandeLogement demande) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Optional<EtatLogement> logement(String numero) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public OptionalDouble devisLoyer(DemandeDevis demande) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Optional<String> attribuer(DemandeAttribution demande) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Optional<EtatBail> consulter(String idBail) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean entrer(String idBail, LocalDate date) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean payer(String idBail, YearMonth mois, LocalDate date, double montant) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean donnerPreavis(String idBail, LocalDate date) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean sortir(String idBail, LocalDate date, double retenuesDegats) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean annuler(String idBail) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Optional<String> quittance(String idBail, YearMonth mois) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void relancerImpayes() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public String rapport() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public List<Notification> notifications() {
        throw new UnsupportedOperationException("TODO");
    }
}

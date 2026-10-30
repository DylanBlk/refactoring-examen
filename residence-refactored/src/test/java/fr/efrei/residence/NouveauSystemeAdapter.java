package fr.efrei.residence;

import fr.efrei.residence.contrat.Canal;
import fr.efrei.residence.contrat.DemandeAttribution;
import fr.efrei.residence.contrat.DemandeDevis;
import fr.efrei.residence.contrat.DemandeInscription;
import fr.efrei.residence.contrat.DemandeLogement;
import fr.efrei.residence.contrat.EtatBail;
import fr.efrei.residence.contrat.EtatLogement;
import fr.efrei.residence.contrat.Notification;
import fr.efrei.residence.contrat.Option;
import fr.efrei.residence.contrat.StatutBail;
import fr.efrei.residence.contrat.SystemeResidence;
import fr.efrei.residence.contrat.TypeLogement;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.EnumSet;

/**
 * Branche le code historique sur les tests de contrat.
 * <p>
 * Tout le « bricolage » nécessaire pour tester l'ancien code (état statique à vider,
 * conversion des codes {@code String}, booléens d'options…) est confiné ici.
 */
public class NouveauSystemeAdapter implements SystemeResidence {

    private GestionResidence gestion;

    @Override
    public void reinitialiser() {
        Database.getInstance();
        Database.etudiants.clear();
        Database.baux.clear();
        EmailService.journal.clear();
        SmsService.journal.clear();
        CourrierService.journal.clear();
        SmsService.compteurSms = 0;
        CourrierService.totalAffranchissement = 0;
        Etudiant.compteur = 0;
        GestionResidence.compteurBail = 0;
        Residence.initialiser();
        gestion = new GestionResidence();
    }

    @Override
    public Optional<String> inscrire(DemandeInscription d) {
        Etudiant e = gestion.inscrireEtudiant(d.nom(), d.prenom(), d.email(), d.telephone(), d.adresse(),
                fr.efrei.residence.Profil.valueOf(d.profil().name()), d.echelonBourse(), d.ressourcesMensuelles(), d.pmr(),
                fr.efrei.residence.Canal.valueOf(d.canalPrefere().name()));
        return Optional.ofNullable(e).map(Etudiant::getId);
    }

    @Override
    public boolean ajouterLogement(DemandeLogement d) {
        int param = d.type() == TypeLogement.COLOCATION ? d.places() : d.surfaceBalcon();
        return gestion.ajouterLogement(fr.efrei.residence.TypeLogement.valueOf(d.type().name()), d.numero(), d.surface(), d.etage(), d.meuble(), param) != null;
    }

    @Override
    public Optional<EtatLogement> logement(String numero) {
        Logement l = Residence.trouver(numero);
        if (l == null) {
            return Optional.empty();
        }
        int occupants;
        if (l instanceof Colocation) {
            occupants = l.getColocataires().size();
        } else {
            occupants = l.estLibre() ? 0 : 1;
        }
        return Optional.of(new EtatLogement(l.getNumero(), TypeLogement.valueOf(l.getType().name()), l.getNombrePlaces(),
                occupants, l.estAccessiblePMR()));
    }

    @Override
    public OptionalDouble devisLoyer(DemandeDevis d) {
        Logement l = Residence.trouver(d.numeroLogement());
        if (l == null) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(new CalculateurLoyer().calculerLoyer(l,
                fr.efrei.residence.Profil.valueOf(d.profil().name()), d.echelonBourse(), convertirOptions(d.options())));
    }

    @Override
    public Optional<String> attribuer(DemandeAttribution d) {
        Etudiant e = etudiant(d.refEtudiant());
        if (e == null) {
            return Optional.empty();
        }
        Bail b = gestion.attribuerLogement(e, fr.efrei.residence.TypeLogement.valueOf(d.type().name()),
                convertirOptions(d.options()), d.dateSignature());
        return Optional.ofNullable(b).map(Bail::getId);
    }

    @Override
    public Optional<EtatBail> consulter(String idBail) {
        Bail b = Database.baux.get(idBail);
        if (b == null) {
            return Optional.empty();
        }
        return Optional.of(new EtatBail(b.getId(), b.getLogement().getNumero(), StatutBail.valueOf(b.getStatut().name()),
                b.getLoyerHorsCharges(), b.getCharges(), b.getLoyerChargesComprises(), b.getCaution(),
                b.getFraisDossier(), b.getDateFin(), b.soldeDu, b.cautionRestituee));
    }

    @Override
    public boolean entrer(String idBail, LocalDate date) {
        return gestion.entrerDansLesLieux(idBail, date);
    }

    @Override
    public boolean payer(String idBail, YearMonth mois, LocalDate date, double montant) {
        return gestion.payerLoyer(idBail, mois.toString(), date, montant);
    }

    @Override
    public boolean donnerPreavis(String idBail, LocalDate date) {
        return gestion.donnerPreavis(idBail, date);
    }

    @Override
    public boolean sortir(String idBail, LocalDate date, double retenuesDegats) {
        return gestion.sortir(idBail, date, retenuesDegats);
    }

    @Override
    public boolean annuler(String idBail) {
        return gestion.annulerBail(idBail);
    }

    @Override
    public Optional<String> quittance(String idBail, YearMonth mois) {
        return Optional.ofNullable(Database.baux.get(idBail)).map(b -> b.genererQuittance(mois.toString()));
    }

    @Override
    public void relancerImpayes() {
        gestion.relancerImpayes();
    }

    @Override
    public String rapport() {
        return gestion.genererRapport();
    }

    @Override
    public List<Notification> notifications() {
        List<Notification> notifications = new ArrayList<>();
        for (String[] email : EmailService.journal) {
            notifications.add(new Notification(Canal.EMAIL, email[0], email[1]));
        }
        for (String[] sms : SmsService.journal) {
            notifications.add(new Notification(Canal.SMS, sms[0], sms[1]));
        }
        for (String[] courrier : CourrierService.journal) {
            notifications.add(new Notification(Canal.COURRIER, courrier[0], courrier[1]));
        }
        return notifications;
    }

    private Set<fr.efrei.residence.Option> convertirOptions(Set<Option> options) {
        Set<fr.efrei.residence.Option> resultat = EnumSet.noneOf(fr.efrei.residence.Option.class);
        for (Option option : options) {
            resultat.add(fr.efrei.residence.Option.valueOf(option.name()));
        }
        return resultat;
    }

    private static Etudiant etudiant(String ref) {
        for (Etudiant e : Database.etudiants.values()) {
            if (e.getId().equals(ref)) return e;
        }
        return null;
    }
}

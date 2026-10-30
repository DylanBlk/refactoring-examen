package fr.efrei.residence;

import java.time.LocalDate;
import java.util.Set;

public class GestionResidence {
    private final InscriptionService inscriptions;
    private final AttributionService attributions;
    private final CycleBailService cycle;
    private final NotificationService notifications;
    private final RapportService rapports;
    private final LogementRepository logements;

    public GestionResidence(InscriptionService inscriptions, AttributionService attributions,
                            CycleBailService cycle, NotificationService notifications,
                            RapportService rapports, LogementRepository logements) {
        this.inscriptions = inscriptions;
        this.attributions = attributions;
        this.cycle = cycle;
        this.notifications = notifications;
        this.rapports = rapports;
        this.logements = logements;
    }

    public Etudiant inscrireEtudiant(String nom, String prenom, String email, String telephone, String adresse,
                                     Profil profil, int echelonBourse, double ressources, boolean pmr,
                                     Canal canalPrefere) {
        return inscriptions.inscrireEtudiant(nom, prenom, email, telephone, adresse, profil,
                echelonBourse, ressources, pmr, canalPrefere);
    }

    public Logement ajouterLogement(TypeLogement type, String numero, double surface, int etage, boolean meuble, int param) {
        return logements.ajouterLogement(type, numero, surface, etage, meuble, param);
    }

    public Bail attribuerLogement(Etudiant etudiant, TypeLogement type, Set<Option> options, LocalDate signature) {
        return attributions.attribuerLogement(etudiant, type, options, signature);
    }

    public boolean entrerDansLesLieux(String idBail, LocalDate date) {
        return cycle.entrerDansLesLieux(idBail, date);
    }

    public boolean payerLoyer(String idBail, String mois, LocalDate date, double montant) {
        return cycle.payerLoyer(idBail, mois, date, montant);
    }

    public boolean donnerPreavis(String idBail, LocalDate date) {
        return cycle.donnerPreavis(idBail, date);
    }

    public boolean sortir(String idBail, LocalDate date, double retenues) {
        return cycle.sortir(idBail, date, retenues);
    }

    public boolean annulerBail(String idBail) {
        return cycle.annulerBail(idBail);
    }

    public void relancerImpayes() {
        notifications.relancerImpayes();
    }

    public String genererRapport() {
        return rapports.genererRapport();
    }

    public void afficherRapport() {
        System.out.println();
        System.out.println(genererRapport());
    }
}

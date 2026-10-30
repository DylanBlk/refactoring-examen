package fr.efrei.residence;

import java.time.LocalDate;
import java.util.Set;

public class AttributionService {
    private final BailRepository db;
    private final LogementRepository logements;
    private final CalculateurLoyer calculateur;
    private final NotificationService notifications;
    private int compteurBail = 0;

    public AttributionService(BailRepository db, LogementRepository logements,
                              CalculateurLoyer calculateur, NotificationService notifications) {
        this.db = db;
        this.logements = logements;
        this.calculateur = calculateur;
        this.notifications = notifications;
    }

    public Bail attribuerLogement(Etudiant e, TypeLogement typeSouhaite, Set<Option> options,
                                  LocalDate dateSignature) {
        System.out.println("--- Attribution d'un " + typeSouhaite + " à " + e.getNomComplet());
        Logement choisi = null;
        double loyer = 0;
        double charges = 0;
        for (Logement l : logements.tousLesLogements().stream()
                .filter(logement -> logement.getType() == typeSouhaite).toList()) {
            if (!l.estLibre()) continue;
            if (e.isPmr() && !l.estAccessiblePMR()) continue;

            double loyerCandidat = calculateur.calculerLoyer(l, e.getProfil(), e.getEchelonBourse(), options);
            double chargesCandidat = calculateur.calculerCharges(l);
            double total = loyerCandidat + chargesCandidat;

            // solvabilité
            boolean solvable = e.getProfil().strategie().estSolvable(e.getRessourcesMensuelles(), total);
            if (!solvable) {
                System.out.println("  " + l.getNumero() + " : ressources insuffisantes (" + Utils.euros(total) + "/mois)");
                continue;
            }
            choisi = l;
            loyer = loyerCandidat;
            charges = chargesCandidat;
            break;
        }
        if (choisi == null) {
            System.out.println("  Aucun logement disponible pour " + e.getNomComplet());
            return null;
        }

        if (choisi instanceof Colocation) {
            choisi.ajouterColocataire(e);
        } else {
            choisi.loger(e);
        }

        compteurBail++;
        String id = "BAIL-" + String.format("%03d", compteurBail);
        Bail b = new Bail(id, e, choisi, dateSignature);
        b.setOptions(options);
        b.setLoyerHorsCharges(loyer);
        b.setCharges(charges);

        StrategieProfil strategie = e.getProfil().strategie();
        b.setCaution(Utils.arrondir(strategie.calculerCaution(loyer)));
        b.setFraisDossier(strategie.fraisDossier());

        db.sauvegarderBail(b);
        System.out.println("  Bail " + id + " signé : " + choisi.getNumero() + ", " + Utils.euros(b.getLoyerChargesComprises())
                + " CC/mois, caution " + Utils.euros(b.getCaution()) + ", frais de dossier " + Utils.euros(b.getFraisDossier()));
        notifications.notifier(e, "Votre bail " + id,
                "Bienvenue à la " + Residence.NOM + " ! Logement " + choisi.getNumero() + ", loyer "
                        + Utils.euros(b.getLoyerChargesComprises()) + " charges comprises.");
        return b;
    }
}

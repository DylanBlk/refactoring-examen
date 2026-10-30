package fr.efrei.residence;

public class NotificationService {
    private static final String PREFIXE_ANGLAIS = "[EN] ";
    private final NotificationFactory canaux;
    private final BailRepository db;

    public NotificationService(NotificationFactory canaux, BailRepository db) {
        this.canaux = canaux;
        this.db = db;
    }

    public void notifier(Etudiant e, String sujet, String message) {
        Canal canal = e.getCanalPrefere();
        canaux.obtenir(canal).notifier(e, sujet, message);
        // les internationaux reçoivent toujours un email en plus (demande de l'administration)
        if (e.getProfil().strategie().emailSupplementaire() && canal != Canal.EMAIL) {
            canaux.obtenir(Canal.EMAIL).notifier(e, PREFIXE_ANGLAIS + sujet, message);
        }
    }

    public void relancerImpayes() {
        System.out.println("--- Relance des impayés");
        for (Bail b : db.tousLesBaux()) {
            if (b.soldeDu <= 0) continue;
            if (b.getStatut() == StatutBail.ANNULE) continue;
            Etudiant e = b.getEtudiant();
            canaux.obtenir(e.getCanalPrefere()).relancer(e, b);
        }
    }
}

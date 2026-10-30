package fr.efrei.residence;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        Residence.initialiser();
        GestionResidence gestion = new GestionResidence();

        gestion.ajouterLogement("STUDIO", "C002", 16, 0, false, 0);
        gestion.ajouterLogement("DUPLEX", "C003", 60, 1, true, 0);

        System.out.println();
        Etudiant alice = gestion.inscrireEtudiant("Martin", "Alice", "alice.martin@mail.fr", "0601020304", null,
                "STANDARD", 0, 1500, false, "EMAIL");
        Etudiant bilal = gestion.inscrireEtudiant("Haddad", "Bilal", "bilal.haddad@mail.fr", "0611223344", null,
                "BOURSIER", 5, 450, false, "SMS");
        Etudiant chloe = gestion.inscrireEtudiant("Durand", "Chloé", "chloe.durand@mail.fr", "0622334455", null,
                "ALTERNANT", 0, 1100, true, "EMAIL");
        Etudiant diego = gestion.inscrireEtudiant("Alvarez", "Diego", "diego.alvarez@mail.es", "+34612345678", null,
                "INTERNATIONAL", 0, 3000, false, "SMS");
        Etudiant emma = gestion.inscrireEtudiant("Petit", "Emma", "emma.petit@mail.fr", null,
                "12 rue des Lilas, 35000 Rennes", "STANDARD", 0, 1300, false, "COURRIER");
        Etudiant gaelle = gestion.inscrireEtudiant("Morel", "Gaëlle", "gaelle.morel@mail.fr", "0633445566", null,
                "STANDARD", 0, 1000, false, "EMAIL");
        Etudiant hugo = gestion.inscrireEtudiant("Leroy", "Hugo", "hugo.leroy@mail.fr", "0644556677", null,
                "BOURSIER", 7, 300, true, "EMAIL");
        gestion.inscrireEtudiant("Faure", "Farid", "farid.faure-at-mail.fr", "0655667788", null,
                "STANDARD", 0, 1200, false, "EMAIL");
        gestion.inscrireEtudiant("Wang", "Li", "li.wang@mail.cn", "0612121212", null,
                "INTERNATIONAL", 0, 2000, false, "EMAIL");
        gestion.inscrireEtudiant("Doublon", "Alice", "alice.martin@mail.fr", "0601020304", null,
                "STANDARD", 0, 1500, false, "EMAIL");

        System.out.println();
        LocalDate signature = LocalDate.of(2026, 8, 25);
        Bail bA = gestion.attribuerLogement(alice, "STUDIO", true, false, false, false, true, signature);
        Bail bB = gestion.attribuerLogement(bilal, "COLOCATION", false, true, true, true, false, signature);
        Bail bC = gestion.attribuerLogement(chloe, "T1", false, false, true, true, true, signature);
        Bail bD = gestion.attribuerLogement(diego, "T2", true, true, false, true, true, signature);
        Bail bE = gestion.attribuerLogement(emma, "COLOCATION", false, false, false, true, false, signature);
        Bail bG = gestion.attribuerLogement(gaelle, "STUDIO", false, true, false, true, false, signature);
        Bail bH = gestion.attribuerLogement(hugo, "COLOCATION", false, true, false, false, true, signature);

        System.out.println();
        LocalDate rentree = LocalDate.of(2026, 9, 1);
        gestion.entrerDansLesLieux(bA.getId(), rentree);
        gestion.entrerDansLesLieux(bB.getId(), rentree);
        gestion.entrerDansLesLieux(bC.getId(), rentree);
        gestion.entrerDansLesLieux(bD.getId(), rentree);
        gestion.entrerDansLesLieux(bE.getId(), LocalDate.of(2026, 8, 20));
        gestion.entrerDansLesLieux(bE.getId(), rentree);
        if (bH != null) gestion.annulerBail(bH.getId());
        if (bG != null) gestion.annulerBail(bG.getId());

        System.out.println();
        gestion.payerLoyer(bA.getId(), "2026-09", LocalDate.of(2026, 9, 3), bA.getLoyerChargesComprises());
        gestion.payerLoyer(bB.getId(), "2026-09", LocalDate.of(2026, 9, 12), bB.getLoyerChargesComprises());
        gestion.payerLoyer(bC.getId(), "2026-09", LocalDate.of(2026, 9, 5), bC.getLoyerChargesComprises());
        gestion.payerLoyer(bD.getId(), "2026-09", LocalDate.of(2026, 9, 9), bD.getLoyerChargesComprises());
        gestion.payerLoyer(bE.getId(), "2026-09", LocalDate.of(2026, 9, 2), 200);
        gestion.payerLoyer(bA.getId(), "2026-10", LocalDate.of(2026, 10, 15), 300);
        gestion.payerLoyer(bC.getId(), "2026-10", LocalDate.of(2026, 10, 1), 1000);

        System.out.println();
        System.out.println(bD.genererQuittance("2026-09"));
        System.out.println(bA.genererQuittance("2026-10"));

        System.out.println();
        gestion.relancerImpayes();

        System.out.println();
        gestion.donnerPreavis(bB.getId(), LocalDate.of(2026, 10, 20));
        gestion.donnerPreavis(bD.getId(), LocalDate.of(2026, 10, 20));
        gestion.donnerPreavis(bE.getId(), LocalDate.of(2026, 10, 20));
        gestion.sortir(bD.getId(), LocalDate.of(2026, 11, 5), 0);
        gestion.sortir(bB.getId(), LocalDate.of(2026, 11, 5), 40);
        gestion.sortir(bD.getId(), LocalDate.of(2026, 11, 20), 120);
        gestion.sortir(bE.getId(), LocalDate.of(2026, 11, 20), 30);

        gestion.afficherRapport();
    }
}

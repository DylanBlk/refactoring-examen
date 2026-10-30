package fr.efrei.residence;

import java.time.LocalDate;

public class Paiement {

    public String mois; // format AAAA-MM
    public LocalDate date;
    public double montant;
    public double penalite;
    public double du;

    public Paiement(String mois, LocalDate date, double montant, double penalite, double du) {
        this.mois = mois;
        this.date = date;
        this.montant = montant;
        this.penalite = penalite;
        this.du = du;
    }
}

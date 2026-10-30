package fr.efrei.residence;

import java.util.List;
import java.util.ArrayList;

public class GenerateurQuittance {
    public String listerOptions(Bail bail) {
        List<String> libelles = new ArrayList<>();
        for (Option option : Option.values()) {
            if (bail.getOptions().contains(option)) {
                libelles.add(option.getLibelle());
            }
        }
        if (libelles.isEmpty()) return "aucune";
        return String.join(", ", libelles);
    }

    public String generer(Bail bail, String mois) {
        Paiement p = null;
        for (Paiement x : bail.paiements) {
            if (x.mois.equals(mois)) p = x;
        }
        if (p == null) {
            return "Aucun paiement pour " + mois;
        }
        String q = "";
        q += "==========================================\n";
        q += "   RÉSIDENCE CAMPUS VERT - QUITTANCE\n";
        q += "==========================================\n";
        q += "Bail      : " + bail.getId() + "\n";
        q += "Locataire : " + bail.getEtudiant().getNomComplet() + " (" + bail.getEtudiant().getProfil() + ")\n";
        q += "Logement  : " + bail.getLogement().getNumero() + " - " + bail.getLogement().getType() + " " + bail.getLogement().getSurface() + " m²";
        if (bail.getLogement().isMeuble()) q += " meublé";
        q += "\n";
        q += "Période   : " + mois + "\n";
        q += "Options   : " + listerOptions(bail) + "\n";
        q += "------------------------------------------\n";
        q += "Loyer HC  : " + Utils.euros(bail.getLoyerHorsCharges()) + "\n";
        q += "Charges   : " + Utils.euros(bail.getCharges()) + "\n";
        if (p.penalite > 0) {
            q += "Pénalité  : " + Utils.euros(p.penalite) + "\n";
        }
        q += "Total dû  : " + Utils.euros(p.du) + "\n";
        q += "Payé le   : " + Utils.date(p.date) + " : " + Utils.euros(p.montant) + "\n";
        if (p.montant < p.du) {
            q += "RESTE DÛ  : " + Utils.euros(Utils.arrondir(p.du - p.montant)) + "\n";
        }
        q += "==========================================";
        return q;
    }
}

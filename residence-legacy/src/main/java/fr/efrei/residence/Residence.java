package fr.efrei.residence;

import java.util.ArrayList;
import java.util.List;

/**
 * Le parc de logements de la résidence.
 */
public class Residence {

    public static final String NOM = "Résidence Campus Vert";

    // TYPE;NUMERO;SURFACE;ETAGE;MEUBLE;PARAM (PARAM = nb de places en coloc, surface du balcon en T2)
    public static final String[] CATALOGUE = {
            "STUDIO;A001;26;0;true;0",
            "STUDIO;A102;22;1;true;0",
            "STUDIO;B103;20;1;false;0",
            "T1;A104;28;1;true;0",
            "T1;B105;30;1;false;0",
            "T2;A201;45;2;false;6",
            "T2;B202;48;2;true;0",
            "COLOCATION;B301;80;3;true;4",
            "COLOCATION;C001;63;0;true;3",
    };

    public static List<Logement> logements = new ArrayList<Logement>();

    public static void initialiser() {
        logements.clear();
        for (String ligne : CATALOGUE) {
            String[] t = ligne.split(";");
            String type = t[0];
            String numero = t[1];
            double surface = Double.parseDouble(t[2]);
            int etage = Integer.parseInt(t[3]);
            boolean meuble = Boolean.parseBoolean(t[4]);
            int param = Integer.parseInt(t[5]);
            Logement l;
            switch (type) {
                case "STUDIO":
                    l = new Studio(numero, surface, etage, meuble);
                    break;
                case "T1":
                    l = new T1(numero, surface, etage, meuble);
                    break;
                case "T2":
                    l = new T2(numero, surface, etage, meuble, param);
                    break;
                case "COLOCATION":
                    l = new Colocation(numero, surface, etage, meuble, param);
                    break;
                default:
                    System.out.println("Type inconnu ignoré : " + type);
                    continue;
            }
            logements.add(l);
        }
    }

    public static Logement trouver(String numero) {
        for (Logement l : logements) {
            if (l.getNumero().equals(numero)) return l;
        }
        return null;
    }
}

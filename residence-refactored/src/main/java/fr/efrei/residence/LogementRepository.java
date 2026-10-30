package fr.efrei.residence;

import java.util.List;

public interface LogementRepository {
    Logement trouver(String numero);
    List<Logement> tousLesLogements();
    Logement ajouterLogement(TypeLogement type, String numero, double surface, int etage, boolean meuble, int param);
}

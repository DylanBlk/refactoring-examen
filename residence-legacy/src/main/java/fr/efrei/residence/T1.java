package fr.efrei.residence;

import java.util.List;

public class T1 extends Logement {

    public T1(String numero, double surface, int etage, boolean meuble) {
        super(numero, "T1", surface, etage, meuble);
    }

    public void ajouterColocataire(Etudiant e) {
        throw new UnsupportedOperationException("Pas de colocation dans un T1");
    }

    public void retirerColocataire(Etudiant e) {
        throw new UnsupportedOperationException("Pas de colocation dans un T1");
    }

    public List<Etudiant> getColocataires() {
        return null;
    }

    public int getNombrePlaces() {
        return 1;
    }

    public double getSurfaceBalcon() {
        return 0; // pas de balcon non plus mais ça évite les exceptions
    }

    public void nettoyerPartiesCommunes() {
        // rien à faire
    }
}

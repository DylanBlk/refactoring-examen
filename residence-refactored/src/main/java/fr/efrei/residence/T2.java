package fr.efrei.residence;

import java.util.List;

public class T2 extends Logement {

    private double surfaceBalcon;

    public T2(String numero, double surface, int etage, boolean meuble, double surfaceBalcon) {
        super(numero, "T2", surface, etage, meuble);
        this.surfaceBalcon = surfaceBalcon;
    }

    public void ajouterColocataire(Etudiant e) {
        throw new UnsupportedOperationException("Pas de colocation dans un T2");
    }

    public void retirerColocataire(Etudiant e) {
        throw new UnsupportedOperationException("Pas de colocation dans un T2");
    }

    public List<Etudiant> getColocataires() {
        throw new UnsupportedOperationException("Pas de colocation dans un T2");
    }

    public int getNombrePlaces() {
        return 1;
    }

    public double getSurfaceBalcon() {
        return surfaceBalcon;
    }

    public void nettoyerPartiesCommunes() {
        throw new UnsupportedOperationException("Pas de parties communes dans un T2");
    }
}

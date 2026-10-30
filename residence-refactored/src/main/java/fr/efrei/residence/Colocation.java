package fr.efrei.residence;

import java.util.ArrayList;
import java.util.List;

public class Colocation extends Logement {

    private int places;
    private List<Etudiant> colocataires = new ArrayList<Etudiant>();

    public Colocation(String numero, double surface, int etage, boolean meuble, int places) {
        super(numero, "COLOCATION", surface, etage, meuble);
        this.places = places;
    }

    @Override
    public void loger(Etudiant e) {
        throw new UnsupportedOperationException("Utiliser ajouterColocataire() pour une colocation");
    }

    @Override
    public void liberer(Etudiant e) {
        throw new UnsupportedOperationException("Utiliser retirerColocataire() pour une colocation");
    }

    @Override
    public boolean estLibre() {
        return colocataires.size() < places;
    }

    @Override
    public Etudiant getOccupant() {
        if (colocataires.isEmpty()) return null;
        return colocataires.get(0);
    }

    public void ajouterColocataire(Etudiant e) {
        if (colocataires.size() >= places) {
            throw new IllegalStateException("Colocation " + numero + " complète");
        }
        colocataires.add(e);
    }

    public void retirerColocataire(Etudiant e) {
        colocataires.remove(e);
    }

    public List<Etudiant> getColocataires() {
        return colocataires;
    }

    public int getNombrePlaces() {
        return places;
    }

    public double getSurfaceBalcon() {
        throw new UnsupportedOperationException("Les colocations n'ont pas de balcon");
    }

    public void nettoyerPartiesCommunes() {
        System.out.println("[ENTRETIEN] Nettoyage des parties communes de la colocation " + numero);
    }
}

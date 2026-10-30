package fr.efrei.residence;

public class ProfilStandard implements StrategieProfil {
    @Override
    public double appliquerRemise(double montant, int echelon) { return montant; }
}

package fr.efrei.residence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Accès à la base de données (simulée en mémoire pour l'instant).
 */
public class Database {

    private static Database instance;

    public static Map<String, Etudiant> etudiants = new HashMap<String, Etudiant>();
    public static Map<String, Bail> baux = new HashMap<String, Bail>();

    private Database() {
        System.out.println("[DB] Connexion à jdbc:postgresql://db.campusvert.local:5432/residence");
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void executer(String sql) {
        System.out.println("[SQL] " + sql);
    }

    public void sauvegarderEtudiant(Etudiant e) {
        executer("INSERT INTO etudiant (id, nom, email, profil) VALUES ('" + e.getId() + "', '" + e.getNom() + "', '" + e.getEmail() + "', '" + e.getProfil() + "')");
        etudiants.put(e.getEmail(), e);
    }

    public Etudiant trouverEtudiantParEmail(String email) {
        executer("SELECT * FROM etudiant WHERE email = '" + email + "'");
        return etudiants.get(email);
    }

    public void sauvegarderBail(Bail b) {
        executer("INSERT INTO bail (id, etudiant, logement, statut, loyer) VALUES ('" + b.getId() + "', '" + b.getEtudiant().getId() + "', '" + b.getLogement().getNumero() + "', '" + b.getStatut() + "', " + b.getLoyerChargesComprises() + ")");
        baux.put(b.getId(), b);
    }

    public void mettreAJourBail(Bail b) {
        executer("UPDATE bail SET statut = '" + b.getStatut() + "', solde = " + b.soldeDu + " WHERE id = '" + b.getId() + "'");
    }

    public Bail trouverBail(String id) {
        executer("SELECT * FROM bail WHERE id = '" + id + "'");
        return baux.get(id);
    }

    public List<Bail> tousLesBaux() {
        executer("SELECT * FROM bail");
        List<Bail> liste = new ArrayList<Bail>(baux.values());
        liste.sort((a, b) -> a.getId().compareTo(b.getId()));
        return liste;
    }
}

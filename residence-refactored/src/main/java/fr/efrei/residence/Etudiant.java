package fr.efrei.residence;

public class Etudiant {

    public static int compteur = 0;

    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private Profil profil;
    private int echelonBourse; // 0 à 7, seulement pour les boursiers
    private double ressourcesMensuelles;
    private boolean pmr;
    private Canal canalPrefere;

    public Etudiant(String nom, String prenom, String email, String telephone, String adresse, Profil profil,
                    int echelonBourse, double ressourcesMensuelles, boolean pmr, Canal canalPrefere) {
        compteur++;
        this.id = "ETU-" + compteur;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.profil = profil;
        this.echelonBourse = echelonBourse;
        this.ressourcesMensuelles = ressourcesMensuelles;
        this.pmr = pmr;
        this.canalPrefere = canalPrefere;
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getNomComplet() { return prenom + " " + nom.toUpperCase(); }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }
    public String getAdresse() { return adresse; }
    public Profil getProfil() { return profil; }
    public void setProfil(Profil profil) { this.profil = profil; }
    public int getEchelonBourse() { return echelonBourse; }
    public double getRessourcesMensuelles() { return ressourcesMensuelles; }
    public boolean isPmr() { return pmr; }
    public Canal getCanalPrefere() { return canalPrefere; }
    public void setCanalPrefere(Canal canalPrefere) { this.canalPrefere = canalPrefere; }
}

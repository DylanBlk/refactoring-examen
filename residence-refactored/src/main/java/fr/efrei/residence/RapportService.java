package fr.efrei.residence;

import java.util.List;
import java.util.Map;
import java.util.EnumMap;

public class RapportService {
    private final LogementRepository logements;
    private final BailRepository db;
    private final CanalNotification sms;
    private final CanalNotification courrier;

    public RapportService(LogementRepository logements, BailRepository db,
                          CanalNotification sms, CanalNotification courrier) {
        this.logements = logements;
        this.db = db;
        this.sms = sms;
        this.courrier = courrier;
    }

    public String genererRapport() {
        String r = "";
        r += "=============== RAPPORT " + Residence.NOM + " ===============" + "\n";
        Map<TypeLogement, Integer> capacites = new EnumMap<>(TypeLogement.class);
        Map<TypeLogement, Integer> occupations = new EnumMap<>(TypeLogement.class);
        double surfaceBalcons = 0;
        for (Logement l : logements.tousLesLogements()) {
            StrategieLogement strategie = l.getType().strategie();
            capacites.put(l.getType(), capacites.getOrDefault(l.getType(), 0) + strategie.capacite(l));
            occupations.put(l.getType(), occupations.getOrDefault(l.getType(), 0) + strategie.occupation(l));
            try {
                surfaceBalcons += l.getSurfaceBalcon();
            } catch (UnsupportedOperationException ex) {
            }
        }
        int nbStudios = capacites.getOrDefault(TypeLogement.STUDIO, 0);
        int nbStudiosOccupes = occupations.getOrDefault(TypeLogement.STUDIO, 0);
        int nbT1 = capacites.getOrDefault(TypeLogement.T1, 0);
        int nbT1Occupes = occupations.getOrDefault(TypeLogement.T1, 0);
        int nbT2 = capacites.getOrDefault(TypeLogement.T2, 0);
        int nbT2Occupes = occupations.getOrDefault(TypeLogement.T2, 0);
        int nbPlacesColoc = capacites.getOrDefault(TypeLogement.COLOCATION, 0);
        int nbPlacesColocOccupees = occupations.getOrDefault(TypeLogement.COLOCATION, 0);
        r += "Studios      : " + nbStudiosOccupes + "/" + nbStudios + "\n";
        r += "T1           : " + nbT1Occupes + "/" + nbT1 + "\n";
        r += "T2           : " + nbT2Occupes + "/" + nbT2 + "\n";
        r += "Colocation   : " + nbPlacesColocOccupees + "/" + nbPlacesColoc + " places" + "\n";
        int total = nbStudios + nbT1 + nbT2 + nbPlacesColoc;
        int occupes = nbStudiosOccupes + nbT1Occupes + nbT2Occupes + nbPlacesColocOccupees;
        r += "Occupation   : " + Math.round(occupes * 100.0 / total) + " %" + "\n";
        r += "Balcons      : " + surfaceBalcons + " m²" + "\n";

        double loyersMensuels = 0;
        double impayes = 0;
        double cautions = 0;
        List<Bail> baux = db.tousLesBaux();
        for (Bail b : baux) {
            if (b.getStatut() == StatutBail.ACTIF || b.getStatut() == StatutBail.PREAVIS) {
                loyersMensuels += b.getLoyerChargesComprises();
                cautions += b.getCaution();
            }
            if (b.soldeDu > 0) impayes += b.soldeDu;
        }
        r += "Loyers CC    : " + Utils.euros(Utils.arrondir(loyersMensuels)) + " / mois" + "\n";
        r += "Cautions     : " + Utils.euros(Utils.arrondir(cautions)) + "\n";
        r += "Impayés      : " + Utils.euros(Utils.arrondir(impayes)) + "\n";
        r += "SMS envoyés  : " + sms.nombreEnvois() + "\n";
        r += "Timbres      : " + Utils.euros(Utils.arrondir(courrier.coutAffranchissement())) + "\n";
        r += "============================================================";
        return r;
    }
}

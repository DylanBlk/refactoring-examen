package fr.efrei.residence;

/**
 * Fabrique : le seul endroit qui sait créer un logement à partir de son type.
 */
public class LogementFactory {

    private static final int PLACES_MIN_COLOCATION = 2;

    /**
     * @param param nombre de places pour une colocation, surface du balcon pour un T2 (ignoré sinon)
     * @return le logement créé, ou null si le type est inconnu
     */
    public static Logement creer(TypeLogement type, String numero, double surface, int etage, boolean meuble,
                                 int param) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case STUDIO:
                return new Studio(numero, surface, etage, meuble);
            case T1:
                return new T1(numero, surface, etage, meuble);
            case T2:
                return new T2(numero, surface, etage, meuble, param);
            case COLOCATION:
                int places = Math.max(param, PLACES_MIN_COLOCATION);
                return new Colocation(numero, surface, etage, meuble, places);
            default:
                return null;
        }
    }
}

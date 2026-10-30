package fr.efrei.residence;

/**
 * Fabrique : le seul endroit qui sait créer un logement à partir de son type.
 */
public class LogementFactory {

    private static final int PLACES_MIN_COLOCATION = 2;

    @FunctionalInterface
    private interface ConstructeurLogement {
        Logement creer(String numero, double surface, int etage, boolean meuble, int param);
    }

    private static final java.util.Map<TypeLogement, ConstructeurLogement> CONSTRUCTEURS = java.util.Map.of(
            TypeLogement.STUDIO, (numero, surface, etage, meuble, param) -> new Studio(numero, surface, etage, meuble),
            TypeLogement.T1, (numero, surface, etage, meuble, param) -> new T1(numero, surface, etage, meuble),
            TypeLogement.T2, (numero, surface, etage, meuble, param) -> new T2(numero, surface, etage, meuble, param),
            TypeLogement.COLOCATION, (numero, surface, etage, meuble, param) ->
                    new Colocation(numero, surface, etage, meuble, Math.max(param, PLACES_MIN_COLOCATION)));

    /**
     * @param param nombre de places pour une colocation, surface du balcon pour un T2 (ignoré sinon)
     * @return le logement créé, ou null si le type est inconnu
     */
    public static Logement creer(TypeLogement type, String numero, double surface, int etage, boolean meuble,
                                 int param) {
        if (type == null) return null;
        ConstructeurLogement constructeur = CONSTRUCTEURS.get(type);
        return constructeur == null ? null : constructeur.creer(numero, surface, etage, meuble, param);
    }
}

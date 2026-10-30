package fr.efrei.residence;

/**
 * Fabrique : le seul endroit qui sait quel service utiliser pour un canal donné.
 */
public class NotificationFactory {

    public static CanalNotification creer(Canal canal) {
        switch (canal) {
            case EMAIL:
                return new EmailService();
            case SMS:
                return new SmsService();
            case COURRIER:
                return new CourrierService();
            default:
                throw new IllegalArgumentException("Canal inconnu " + canal);
        }
    }
}

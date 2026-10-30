package fr.efrei.residence;

public class NotificationFactory {
    private final CanalNotification email;
    private final CanalNotification sms;
    private final CanalNotification courrier;

    public NotificationFactory(CanalNotification email, CanalNotification sms, CanalNotification courrier) {
        this.email = email;
        this.sms = sms;
        this.courrier = courrier;
    }

    public CanalNotification obtenir(Canal canal) {
        switch (canal) {
            case EMAIL: return email;
            case SMS: return sms;
            case COURRIER: return courrier;
            default: throw new IllegalArgumentException("Canal inconnu " + canal);
        }
    }
}

package fr.efrei.residence;

import java.util.ArrayList;
import java.util.List;

public class SmsService implements CanalNotification {

    private int compteurSms = 0;
    private final List<String[]> journal = new ArrayList<String[]>();

    public void envoyerSms(String numero, String message) {
        if (numero == null || numero.length() < 10) {
            System.out.println("[SMS] numéro invalide : " + numero);
            return;
        }
        if (message.length() > 160) {
            message = message.substring(0, 157) + "...";
        }
        compteurSms++;
        journal.add(new String[]{numero, message});
        System.out.println("[SMS] -> " + numero + " : " + message);
    }

    @Override
    public void notifier(Etudiant e, String sujet, String message) {
        envoyerSms(e.getTelephone(), sujet + " - " + message);
    }

    @Override
    public void relancer(Etudiant e, Bail b) {
        envoyerSms(e.getTelephone(), "Campus Vert : " + Utils.euros(b.soldeDu) + " impayés sur " + b.getId());
    }
    @Override
    public List<String[]> historique() {
        return List.copyOf(journal);
    }

    @Override
    public int nombreEnvois() {
        return compteurSms;
    }

}

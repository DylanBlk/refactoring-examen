package fr.efrei.residence;

import java.util.ArrayList;
import java.util.List;

public class SmsService {

    public static int compteurSms = 0;
    public static List<String[]> journal = new ArrayList<String[]>();

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
}

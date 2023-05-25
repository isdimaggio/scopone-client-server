package Server;

import Commons.Carta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Distribuzione {

    static ArrayList<Carta> creaMazzoServer() throws Exception {
        ArrayList<Carta> arrayList = new ArrayList<>();

        // per tutti i semi
        for (char seme: Carta.LISTA_SEMI) {
            // per tutti i valori
            for(int i = 1; i < 11; i++){
                arrayList.add(new Carta(seme, i));
            }
        }

        Collections.shuffle(arrayList); // mischia il mazzo
        return arrayList;
    }

    static void distribuisciDaServerAClient(
            ArrayList<Carta> mazzoServer,
            Utente client
    ){
        for(int i = 0; i < 10; i++){
            Random rand = new Random();
            int index = rand.nextInt(mazzoServer.size());
            client.aggiungiAMazzo(mazzoServer.get(index));
            mazzoServer.remove(index);
        }
    }

    static String mazzoToString(ArrayList<Carta> mazzo){
        StringBuilder stringa = new StringBuilder();
        for (Carta carta:
             mazzo) {
            stringa.append(carta.toString());
        }
        return stringa.toString();
    }

    static void ritiraCarteClient(
            ArrayList<Carta> mazzoServer,
            ArrayList<Utente> listaClient
    ){
        for (Utente utente: listaClient) {
            utente.setMazzoRichiesto(false);
            mazzoServer.addAll(utente.getMazzoUtente());
            mazzoServer.addAll(utente.getMazzoVinte());
            utente.getMazzoUtente().clear();
        }
        Collections.shuffle(mazzoServer);
    }

    static void rimuoviCartaDaMazzo(
            ArrayList<Carta> mazzoServer,
            Carta carta
    ){
        for(int i = 0; i < mazzoServer.size(); i++){
            Carta sele = mazzoServer.get(i);
            if(sele.getValore() == carta.getValore() && sele.getSeme() == carta.getSeme()){
                mazzoServer.remove(i);
                return;
            }
        }
    }

}

package Server;

import java.util.Objects;

import static Server.Main.listaUtenti;

public class Partita {

    public static Utente autenticazione(String id) throws Exception{
        for(Utente u : listaUtenti){
            if(Objects.equals(id, u.getId())){
                return u;
            }
        }
        throw new Exception("Utente non trovato");
    }

    public static String getTurno(){
        for(Utente u : listaUtenti){
            if(u.isMioTurno()){
                return u.getId();
            }
        }
        // se nessuno ha la flag turno parte il primo
        listaUtenti.get(0).setMioTurno(true);
        return listaUtenti.get(0).getId();
    }

    public static boolean partitaIniziata() {
        boolean b = true;
        for(Utente user : listaUtenti){
            if(user.isMazzoRichiesto()){
                b = false;
                break;
            }
        }
        return b;
    }

    public static void avanzaTurno(){
        for(int i = 0; i < listaUtenti.size(); i++){
            if(listaUtenti.get(i).isMioTurno()){
                listaUtenti.get(i).setMioTurno(false);
                if((i+1) < listaUtenti.size()){
                    listaUtenti.get(i+1).setMioTurno(true);
                }else{
                    // arrivato a ultima pos in lista riparti da zero
                    listaUtenti.get(0).setMioTurno(true);
                }
                return;
            }
        }
    }

    public static boolean partitaFinita(){
        // la partita è finita quando tutti e quattro i mazzi dei client sono vuoti
        boolean finita = true;
        for (Utente utente: listaUtenti) {
            if (!utente.getMazzoUtente().isEmpty()) {
                finita = false;
                break;
            }
        }
        return finita;
    }

    public static int mazzoVincitePiuGrosso(){
        int vinte = 0;
        for (Utente utente: listaUtenti) {
            if(utente.getMazzoVinte().size() > vinte){
               vinte = utente.getMazzoVinte().size();
            }
        }
        return vinte;
    }
}

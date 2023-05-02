package Server;

import java.util.Objects;

import static Server.Main.listaUtenti;

public class Utility {

    public static UtenteLoggato autenticazione(String id) throws Exception{
        for(UtenteLoggato u : listaUtenti){
            if(Objects.equals(id, u.getId())){
                return u;
            }
        }
        throw new Exception("Utente non trovato");
    }

    public static String getTurno(){
        for(UtenteLoggato u : listaUtenti){
            if(u.isMioTurno()){
                return u.getId();
            }
        }
        // se nessuno ha la flag turno parte il primo
        listaUtenti.get(0).setMioTurno(true);
        return listaUtenti.get(0).getId();
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
}

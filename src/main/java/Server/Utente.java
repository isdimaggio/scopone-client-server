package Server;

import Commons.Carta;

import java.util.ArrayList;
import java.util.UUID;

public class Utente {

    private final String id;
    private final String username;
    private boolean mazzoRichiesto;
    private ArrayList<Carta> mazzoUtente = new ArrayList<>();
    private ArrayList<Carta> mazzoVinte = new ArrayList<>();
    private boolean mioTurno;

    public Utente(String username) {
        // generazione id random utente
        this.id = UUID.randomUUID()                     // genera uuid v4
                .toString()                             // trasforma in stringa
                .replace("-", "")   // rimuove i trattini
                .trim()                                 // elimina tutti i caratteri speciali
                .substring(0, 9);                       // prende i primi 10 caratteri

        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setMazzoRichiesto(){
        mazzoRichiesto = true;
    }

    public void setMazzoRichiesto(boolean bool){
        mazzoRichiesto = bool;
    }

    public boolean isMazzoRichiesto(){
        return mazzoRichiesto;
    }
    public ArrayList<Carta> getMazzoUtente() {
        return mazzoUtente;
    }
    public ArrayList<Carta> getMazzoVinte() {
        return mazzoVinte;
    }
    public void aggiungiAMazzo(Carta carta){
        mazzoUtente.add(carta);
    }
    public void aggiungiAMazzoVinte(Carta carta){
        mazzoVinte.add(carta);
    }

    public boolean isMioTurno() {
        return mioTurno;
    }

    public void setMioTurno(boolean mioTurno) {
        this.mioTurno = mioTurno;
    }
}

package Server;

import java.util.UUID;

public class UtenteLoggato {

    private String id;
    private String username;
    private boolean mazzoRichiesto;
    private boolean mioTurno;

    public UtenteLoggato(String username) {
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMazzoRichiesto(){
        mazzoRichiesto = true;
    }

    public boolean isMazzoRichiesto(){
        return mazzoRichiesto;
    }

    public boolean isMioTurno() {
        return mioTurno;
    }

    public void setMioTurno(boolean mioTurno) {
        this.mioTurno = mioTurno;
    }
}

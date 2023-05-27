package ClientLib;

import Commons.Carta;

import java.util.ArrayList;

public class ClientScopone {

    private final String address;
    private final int port;
    private final String username;

    private final String clientId;
    private ArrayList<Carta> mazzo;
    private boolean loggedIn;
    private boolean toDispose;

    public ClientScopone (
            String address,
            int port,
            String username
    ) throws ScoponeException {
        RispostaServer res;
        try{
            res = SocketManager.richiesta("AC" + username, address, port);
        }catch (Exception e){
            throw new ScoponeException(e.getMessage(), ScoponeException.ERR_SOCKET);
        }
        switch (res.getStatusCode()){
            case "OK" -> {
                this.address = address;
                this.port = port;
                this.username = username;

                this.clientId = res.getParameter();
                this.loggedIn = true;
            }
            case "EU" -> throw new ScoponeException("Nome utente gia in uso", ScoponeException.ERR_USER_ALREADY_EXISTS);
            case "MX" -> throw new ScoponeException("Server pieno", ScoponeException.ERR_SERVER_FULL);
            default -> throw new ScoponeException("Errore generico", ScoponeException.ERR_SERVER_GENERIC);
        }
    }

    public void disconnect() throws ScoponeException {
        if(!loggedIn){
            throw new ScoponeException("Non connesso ad ancora nessun server", ScoponeException.ERR_INSTANCE_INVALID);
        }
        try {
            SocketManager.richiesta("QU" + clientId, address, port);
        }catch (Exception e){
            throw new ScoponeException(e.getMessage(), ScoponeException.ERR_SOCKET);
        }
        this.loggedIn = false;
        this.toDispose = true;
    }

    public void remoteReset() {
        try {
            SocketManager.richiesta("remoteReset", address, port);
        } catch (Exception ignored) {}
    }

    public void requestMazzo() throws ScoponeException {
        if(toDispose){
            throw new ScoponeException("Partita terminata", ScoponeException.ERR_GAME_FINISHED);
        }
        RispostaServer res;
        try{
            res = SocketManager.richiesta("DS" + clientId, address, port);
        }catch (Exception e){
            throw new ScoponeException(e.getMessage(), ScoponeException.ERR_SOCKET);
        }
        switch (res.getStatusCode()){
            case "CR" -> {
                // deserialize mazzo (lo fa la classe carta, che lo serializza anche)
                try{
                    this.mazzo = Carta.deserializeMazzo(res.getParameter());
                }catch (Exception e){
                    throw new ScoponeException("Risposta server malformata", ScoponeException.ERR_BAD_RESPONSE);
                }
            }
            case "WA" -> throw new ScoponeException(
                    "Numero giocatori non raggiunto", ScoponeException.ERR_LOBBY_NOT_FULL);
            default -> throw new ScoponeException("Errore generico", ScoponeException.ERR_SERVER_GENERIC);
        }
    }

    public ArrayList<Carta> requestTavolo() throws ScoponeException {
        if(toDispose){
            throw new ScoponeException("Partita terminata", ScoponeException.ERR_GAME_FINISHED);
        }
        RispostaServer res;
        try{
            res = SocketManager.richiesta("ST" + clientId, address, port);
        }catch (Exception e){
            throw new ScoponeException(e.getMessage(), ScoponeException.ERR_SOCKET);
        }
        switch (res.getStatusCode()){
            case "CR" -> {
                // deserialize mazzo (lo fa la classe carta, che lo serializza anche)
                try{
                    return Carta.deserializeMazzo(res.getParameter());
                }catch (Exception e){
                    throw new ScoponeException("Risposta server malformata", ScoponeException.ERR_BAD_RESPONSE);
                }
            }
            case "PI" -> {
                toDispose = true;
                throw new ScoponeException("Partita terminata", ScoponeException.ERR_GAME_FINISHED);
            }
            default -> throw new ScoponeException("Errore generico", ScoponeException.ERR_SERVER_GENERIC);
        }
    }

    public boolean controllaTurno() throws ScoponeException{
        if(toDispose){
            throw new ScoponeException("Partita terminata", ScoponeException.ERR_GAME_FINISHED);
        }
        RispostaServer res;
        try{
            res = SocketManager.richiesta("TU" + clientId, address, port);
        }catch (Exception e){
            throw new ScoponeException(e.getMessage(), ScoponeException.ERR_SOCKET);
        }
        switch (res.getStatusCode()){
            case "TX" -> {
                return true;
            }
            case "TN" -> {
                return false;
            }
            case "PI" -> {
                toDispose = true;
                throw new ScoponeException("Partita terminata", ScoponeException.ERR_GAME_FINISHED);
            }
            default -> throw new ScoponeException("Errore generico", ScoponeException.ERR_SERVER_GENERIC);
        }
    }

    public void autoPlay() throws ScoponeException{
        if(toDispose){
            throw new ScoponeException("Partita terminata", ScoponeException.ERR_GAME_FINISHED);
        }

        // TODO: fare autoplay per bene con un algoritmo sensato
        // per il momento prende la prima carta del mazzo
        Carta carta = mazzo.get(0);
        giocaCarta(carta);
    }

    public void giocaCarta(Carta cartaSelezionata) throws ScoponeException{
        if(toDispose){
            throw new ScoponeException("Partita terminata", ScoponeException.ERR_GAME_FINISHED);
        }
        if(!mazzo.contains(cartaSelezionata)){
            throw new ScoponeException("Carta non nel mazzo", ScoponeException.ERR_CARD_NOT_IN_DECK);
        }
        RispostaServer res;
        try{
            res = SocketManager.richiesta(
                    "GC" + clientId + "," + cartaSelezionata.toString(), address, port);
        }catch (Exception e){
            throw new ScoponeException(e.getMessage(), ScoponeException.ERR_SOCKET);
        }
        switch (res.getStatusCode()){
            case "AF" -> {
                // carta giocata, rimuovi da mazzo locale
                mazzo.remove(cartaSelezionata);
            }
            case "PI" -> {
                toDispose = true;
                throw new ScoponeException("Partita terminata", ScoponeException.ERR_GAME_FINISHED);
            }
            default -> throw new ScoponeException("Errore generico", ScoponeException.ERR_SERVER_GENERIC);
        }
    }

    public StatoPartita isPartitaVinta() throws ScoponeException{
        if(toDispose){
            throw new ScoponeException("Partita terminata", ScoponeException.ERR_GAME_FINISHED);
        }

        RispostaServer res;
        try{
            res = SocketManager.richiesta("VP" + clientId, address, port);
        }catch (Exception e){
            throw new ScoponeException(e.getMessage(), ScoponeException.ERR_SOCKET);
        }
        switch (res.getStatusCode()){
            case "PV" -> {
                return new StatoPartita(
                        StatoPartita.GAME_WON,
                        Integer.parseInt(res.getParameter())
                );
            }
            case "PP" -> {
                return new StatoPartita(
                        StatoPartita.GAME_LOST,
                        Integer.parseInt(res.getParameter())
                );
            }
            case "IC" -> {
                return new StatoPartita(StatoPartita.GAME_PLAYING);
            }
            case "PI" -> {
                toDispose = true;
                throw new ScoponeException("Partita terminata", ScoponeException.ERR_GAME_FINISHED);
            }
            default -> throw new ScoponeException("Errore generico", ScoponeException.ERR_SERVER_GENERIC);
        }
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getClientId() {
        return clientId;
    }

    public ArrayList<Carta> getMazzo() {
        return mazzo;
    }
}

package Server;

import Commons.Carta;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

import static Server.Partita.*;

public class Main {

    static ServerSocket serverSocket;
    static ArrayList<Utente> listaUtenti = new ArrayList<>();
    static ArrayList<Carta> mazzoServer = new ArrayList<>();

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(777);
        } catch (Exception e) {
            System.err.println("Impossibile aprire il socket: " + e.getMessage());
            return;
        }

        String comando = ""; // genera la mappa delle carte
        try {
            mazzoServer = Distribuzione.creaMazzoServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // ascolta per le connessioni in ingresso con un loop infinito
        while (!Objects.equals(comando, "terminaServer")) {

            try {
                Socket client = serverSocket.accept();
                System.out.println("ACK: " + client.getRemoteSocketAddress().toString());
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                // Lettura richiesta dal client
                comando = in.readLine();
                if (Objects.equals(comando, "terminaServer")) continue;

                // risposta
                String risposta = elaborazione(comando);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                System.out.println("REQ: " + comando + " \nRES: " + risposta);

                out.write(risposta);
                out.flush();
                client.close();
                in.close();
                out.close();

            } catch (Exception e) {
                System.err.println("EXCP: " + e.getMessage());
            }

        }

    }

    private static String elaborazione(String richiesta) {
        if (Objects.equals(richiesta, "remoteReset")) {
            listaUtenti.clear();
            try {
                mazzoServer = Distribuzione.creaMazzoServer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("RESET REMOTO INIZIATO");
            return "RESET";
        }
        int comando;
        String parametro;
        try {
            RichiestaClient richiestaClient = new RichiestaClient(richiesta);
            comando = richiestaClient.getCommand();
            parametro = richiestaClient.getParameter();
        } catch (Exception e) {
            System.out.println("EXCP: " + e.getMessage());
            return "ER";
        }
        switch (comando) {
            case RichiestaClient.LOGIN_COMMAND -> {
                // se il server è vuoto rigenera il mazzo
                // bugfix terribile per server che hanno tanta latenza e raccolgono male
                if(listaUtenti.size() == 0){
                    try{
                        mazzoServer.clear();
                        mazzoServer = Distribuzione.creaMazzoServer();
                    }catch (Exception ignored) {}
                }

                // controlla se il server non è pieno!
                if (listaUtenti.size() > 3) {
                    System.out.println("Server pieno, rifiutando");
                    return "MX";
                }

                // controlla che lo username non esista gia
                for (Utente utente : listaUtenti) {
                    if (Objects.equals(utente.getUsername(), parametro)) {
                        System.out.println("Username in uso");
                        return "EU";
                    }
                }

                // va bene
                Utente nuovoUtente = new Utente(parametro);
                listaUtenti.add(nuovoUtente);
                return "OK" + nuovoUtente.getId();
            }
            case RichiestaClient.DISTRIBUZ_COMMAND -> {
                // autentica il client
                Utente utente;
                try {
                    utente = autenticazione(parametro);
                } catch (Exception e) {
                    System.out.println("Auth fallita");
                    return "ER"; // implementare err specifico
                }

                // no carte se non QUATTRO utenti
                if (listaUtenti.size() != 4) {
                    System.out.println("Lobby non piena");
                    return "WA";
                }

                // vedi se il client ha già richiesto
                if (utente.isMazzoRichiesto()) {
                    System.out.println("Quel client ha gia richiesto il mazzo");
                    return "ER"; // implementare err specifico
                }

                // prendi 10 carte a caso dal mazzo del server e consegnale al client
                utente.setMazzoRichiesto();
                Distribuzione.distribuisciDaServerAClient(mazzoServer, utente);
                return "CR" + Distribuzione.mazzoToString(utente.getMazzoUtente());

            }
            case RichiestaClient.MOVE_REQUEST_COMMAND -> {
                // vedi se ci sono abbastanza giocatori, altrimenti interrompi la partita
                if (listaUtenti.size() != 4) {
                    System.out.println("Partita interrotta, non abbastanza utenti");
                    return "PI";
                }

                // autentica il client
                Utente utente;
                try {
                    utente = autenticazione(parametro);
                } catch (Exception e) {
                    System.out.println("Auth fallita");
                    return "ER"; // implementare err specifico
                }

                // vedi se è il turno del client
                if (Objects.equals(utente.getId(), getTurno())) {
                    System.out.println("È il turno per il client richiedente");
                    return "TX";
                } else {
                    System.out.println("Non è il turno per il client richiedente");
                    return "TN";
                }

            }
            case RichiestaClient.QUIT_COMMAND -> {
                // autentica il client
                Utente utente;
                try {
                    utente = autenticazione(parametro);
                } catch (Exception e) {
                    System.out.println("Auth fallita");
                    return "ER"; // implementare err specifico
                }

                // autenticazione ok esci dalla partita
                Distribuzione.ritiraCarteClient(mazzoServer, listaUtenti);
                listaUtenti.remove(utente);
                return "PI";
            }
            case RichiestaClient.MOVE_PLAY_COMMAND -> {
                // vedi se ci sono abbastanza giocatori, altrimenti interrompi la partita
                if (listaUtenti.size() != 4) {
                    System.out.println("Partita interrotta, non abbastanza utenti");
                    return "PI";
                }

                // split dei parametri
                String[] parametri = parametro.split(",");
                if (parametri.length != 2) return "ER";

                // autentica il client
                Utente utente;
                try {
                    utente = autenticazione(parametri[0]);
                } catch (Exception e) {
                    System.out.println("Auth fallita");
                    return "ER"; // implementare err specifico
                }

                // vedi se è il turno del client
                if (!Objects.equals(utente.getId(), getTurno())) {
                    System.out.println("Non è il turno per il client richiedente");
                    return "TI";
                }

                // controlla se la carta che il client vuole giocare è effettivamente nel suo mazzo
                // crea oggetto carta a partire dai parametri
                Carta cartaDaGiocare;
                try {
                    cartaDaGiocare = new Carta(parametri[1]);
                } catch (Exception e) {
                    System.out.println("Carta fornita non valida");
                    return "ER";
                }

                // la carta esiste, rimuovila dal mazzo utente
                Distribuzione.rimuoviCartaDaMazzo(utente.getMazzoUtente(), cartaDaGiocare);

                if(cartaDaGiocare.getValore() == 1){
                    // prendi tutte le carte
                    utente.aggiungiAMazzoVinte(cartaDaGiocare);
                    for (Carta cartaSuTavolo : mazzoServer) {
                        utente.aggiungiAMazzoVinte(cartaSuTavolo);
                    }
                    mazzoServer.clear();
                    avanzaTurno();
                    return "AF";
                }else{
                    for (Carta cartaSuTavolo : mazzoServer) {
                        if (cartaSuTavolo.getValore() == cartaDaGiocare.getValore()) {
                            // esiste, inserisci giocata (e server) nel mazzo vinte
                            utente.aggiungiAMazzoVinte(cartaDaGiocare);
                            utente.aggiungiAMazzoVinte(cartaSuTavolo);

                            // rimuovi la carta vinta dal mazzo del server
                            Distribuzione.rimuoviCartaDaMazzo(mazzoServer, cartaSuTavolo);

                            avanzaTurno();
                            return "AF";
                        }
                    }
                }


                // non c'è sul tavolo, aggiungila al mazzo server
                mazzoServer.add(cartaDaGiocare);
                avanzaTurno();
                return "AF";
            }
            case RichiestaClient.GET_STATE_COMMAND -> {
                // vedi se ci sono abbastanza giocatori, altrimenti interrompi la partita
                if (listaUtenti.size() != 4) {
                    System.out.println("Partita interrotta, non abbastanza utenti");
                    return "PI";
                }

                // autentica il client
                try {
                    autenticazione(parametro);
                } catch (Exception e) {
                    System.out.println("Auth fallita");
                    return "ER"; // implementare err specifico
                }

                StringBuilder listaCarte = new StringBuilder("CR");

                // bugfix orribile (se la partita non è ancora iniziato forza tavolo vuoto
                // per risolvere problemi di client che renderizzano male

                boolean iniziata = false;

                for(Utente user : listaUtenti){
                    iniziata = user.isMazzoRichiesto();
                }

                if(iniziata){
                    for (Carta cartaSuTavolo : mazzoServer) {
                        listaCarte.append(cartaSuTavolo.toString());
                    }
                }

                return listaCarte.toString();
            }
            case RichiestaClient.GET_WIN_COMMAND -> {
                // vedi se ci sono abbastanza giocatori, altrimenti interrompi la partita
                if (listaUtenti.size() != 4) {
                    System.out.println("Partita interrotta, non abbastanza utenti");
                    return "PI";
                }

                // autentica il client
                Utente utente;
                try {
                    utente = autenticazione(parametro);
                } catch (Exception e) {
                    System.out.println("Auth fallita");
                    return "ER"; // implementare err specifico
                }

                if (!partitaFinita()) return "IC";
                int conteggioUtente = utente.getMazzoVinte().size();

                // la partita è finita, vedi il vincitore
                if (mazzoVincitePiuGrosso() == conteggioUtente) {
                    System.out.println("Partita vinta da " + utente.getUsername() + "con carte: " + conteggioUtente);
                    return "PV" + conteggioUtente;
                } else {
                    return "PP" + conteggioUtente;
                }
            }
            default -> {
                return "ER";
            }

        }
    }

}

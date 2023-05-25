package TestSuite;

import ClientLib.ClientScopone;
import ClientLib.ScoponeException;

import java.util.Scanner;

public class Main {

    static ClientScopone c1;
    static ClientScopone c2;
    static ClientScopone c3;
    static ClientScopone c4;

    static String address;
    static int port;

    static int passes = 0;
    static int fails = 0;

    public static void testResult(
            boolean pass,
            String testName
    ){
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if(pass){
            builder.append("\u001B[42m\u001B[30m").append("PASS");
            passes++;
        }else{
            builder.append("\u001B[41m\u001B[30m").append("FAIL");
            fails++;
        }
        builder.append("\u001B[0m").append("] ").append(testName);
        System.out.println(builder);
    }

    public static void main(String[] args) {
        System.out.println("**********************");
        System.out.println("* SCOPONE TEST SUITE *");
        System.out.println("**********************");
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Indirizzo del server di scopa: ");
        address = scanner.next();
        System.out.print("Porta del server: ");
        port = scanner.nextInt();
        System.out.println();

        runTests();

        /*
         * TERMINAZIONE TEST SUITE
         * Conta i test totali, i pass, i fail e resetta il server remoto allo stato iniziale
         * */
        try{
            c1.remoteReset();
        }catch (Exception ignored) {};
        System.out.println();
        System.out.println("-------------------------");
        System.out.println("TEST PASSATI: " + passes);
        System.out.println("TEST FALLITI: " + fails);
        System.out.println("TEST TOTALI: " + (passes + fails));
    }

    public static void runTests(){
        /*
         * TEST No 1 *******************************
         * Connetti i primi tre client,
         * PASS se la libreria non genera eccezioni
         * */
        try {
            c1 = new ClientScopone(address, port, "ts1client");
            c2 = new ClientScopone(address, port, "ts2client");
            c3 = new ClientScopone(address, port, "ts3client");
            testResult(true, "Connessione primi 3 client validi");
        } catch (ScoponeException e) {
            testResult(false, "Connessione primi 3 client validi [ERR " + e.getMessage() + "]");
            System.err.println("Il fail di questo test indica un non funzionamento totale del server.");
            return;
        }

        /*
         * TEST No 2 *******************************
         * Duplica uno username, PASS se la libreria
         * genera eccezione per utente duplicato
         * */
        try {
            c4 = new ClientScopone(address, port, "ts3client");
            testResult(false, "Duplicazione username");
        } catch (ScoponeException e) {
            testResult(
                    e.getErrorCode() == ScoponeException.ERR_USER_ALREADY_EXISTS,
                    "Duplicazione username"
            );
        }

        /*
         * TEST No 3 *******************************
         * Tenta distribuzione delle carte con un
         * numero di client non sufficiente, PASS
         * se la libreria genera eccezione opportuna.
         * */
        try {
            c3.requestMazzo();
            testResult(false, "Blocco distribuzione a lobby vuota");
        } catch (ScoponeException e) {
            testResult(
                    e.getErrorCode() == ScoponeException.ERR_LOBBY_NOT_FULL,
                    "Blocco distribuzione a lobby vuota"
            );
        }

        /*
         * TEST No 4 *******************************
         * Login pulito quarto utente, PASS se la
         * libreria non genera eccezioni
         * */
        try {
            c4 = new ClientScopone(address, port, "ts4client");
            testResult(true, "Login pulito quarto utente");
        } catch (ScoponeException e) {
            testResult(false, "Login pulito quarto utente [ERR " + e.getMessage() + "]");
            System.err.println("Il fail di questo test indica un non funzionamento totale del server.");
            return;
        }

        /*
         * TEST No 5 *******************************
         * Login utente di troppo, PASS se la libreria
         * genera eccezione di server pieno
         * */
        try {
            new ClientScopone(address, port, "ts5client");
            testResult(false, "Login utente di troppo");
        } catch (ScoponeException e) {
            testResult(
                    e.getErrorCode() == ScoponeException.ERR_SERVER_FULL,
                    "Login utente di troppo"
            );
        }

        /*
         * TEST No 6 *******************************
         * Distribuzione a tutti e quattro gli utenti,
         * PASS se la libreria non genera eccezioni
         * */
        try {
            c1.requestMazzo();
            c2.requestMazzo();
            c3.requestMazzo();
            c4.requestMazzo();
            testResult(true, "Distribuzione dei quattro mazzi");
        } catch (ScoponeException e) {
            testResult(false, "Distribuzione dei quattro mazzi [ERR " + e.getMessage() + "]");
        }

        /*
         * TEST No 7 *******************************
         * Controlla che dopo la distribuzione il
         * tavolo sia vuoto, se non lo è FAIL
         * */
        try {
            testResult(c1.requestTavolo().size() == 0, "Tavolo vuoto dopo distribuzione");
        } catch (ScoponeException e) {
            testResult(false, "Tavolo vuoto dopo distribuzione [ERR " + e.getMessage() + "]");
        }

        /*
         * TEST No 8 *******************************
         * Verifica che il primo client abbia
         * effettivamente il diritto di giocare per
         * primo altrimenti FAIL.
         * */
        try {
            testResult(c1.controllaTurno(), "Primo turno a primo client joinato");
        } catch (ScoponeException e) {
            testResult(false, "Primo turno a primo client joinato [ERR " + e.getMessage() + "]");
        }

        /*
         * TEST No 9 *******************************
         * Gioca una carta da client1 e verifica sia
         * effettivamente sul tavolo, FAIL se non
         * presente
         * */
        try {
            c1.autoPlay();
            testResult(c1.requestTavolo().size() == 1, "Spostamento carta su tavolo");
        } catch (ScoponeException e) {
            testResult(false, "Spostamento carta su tavolo [ERR " + e.getMessage() + "]");
        }

        /*
         * TEST No 10 *******************************
         * Effettua un giro completo e vedi se la
         * turnazione segue il percorso
         * 1 -> 2 -> 3 -> 4 -> 1
         * */
        try {
            if(c2.controllaTurno()){
                c2.autoPlay();
                if(c3.controllaTurno()){
                    c3.autoPlay();
                    if(c4.controllaTurno()){
                        c4.autoPlay();
                        testResult(c1.controllaTurno(), "Turnazione giro completo");
                    }else{
                        testResult(false, "Turnazione giro completo [F4]");
                    }
                }else{
                    testResult(false, "Turnazione giro completo [F3]");
                }
            }else{
                testResult(false, "Turnazione giro completo [F2]");
            }
        } catch (ScoponeException e) {
            testResult(false, "Turnazione giro completo [ERR " + e.getMessage() + "]");
        }

        /*
         * TEST No 11 *******************************
         * Esci con un client dalla partita e controlla
         * se la partita viene interrotta per tutti.
         * */
        boolean test11pass = true;
        try {
            c1.disconnect();
        } catch (ScoponeException e) {
            test11pass = false;
        }
        try {
            c2.requestTavolo();
        } catch (ScoponeException e) {
            if(e.getErrorCode() != ScoponeException.ERR_GAME_FINISHED) test11pass = false;
        }
        try {
            c3.requestTavolo();
        } catch (ScoponeException e) {
            if(e.getErrorCode() != ScoponeException.ERR_GAME_FINISHED) test11pass = false;
        }
        try {
            c4.requestTavolo();
        } catch (ScoponeException e) {
            if(e.getErrorCode() != ScoponeException.ERR_GAME_FINISHED) test11pass = false;
        }
        try{
            // una volta che si apprende che la partita è terminata è compito del client
            // terminare la connessione per liberare il server ed essere liberati dalla
            // lista degli utenti
            c2.disconnect();
            c3.disconnect();
            c4.disconnect();
        } catch (ScoponeException e) {
            test11pass = false;
        }
        testResult(test11pass, "Quit partita iniziativa di un solo client");

        /*
         * TEST No 12 *******************************
         * Ricollega tutti quanti i client e verifica
         * che il tavolo contenga di nuovo tutte e
         * 40 le carte.
         * */
        try{
            c1 = new ClientScopone(address, port, "ts1client");
            c2 = new ClientScopone(address, port, "ts2client");
            c3 = new ClientScopone(address, port, "ts3client");
            c4 = new ClientScopone(address, port, "ts4client");
            testResult(c1.requestTavolo().size() == 40, "Riconnessione e conteggio carte restituite");
        } catch (ScoponeException e) {
            testResult(false, "Riconnessione e conteggio carte restituite [ERR " + e.getMessage() + "]");
        }

        // TODO: a partita ricominciata testare se le carte vengono vinte, se l'asso vince tutto e poi alla fine chi
        // vince la partita, sviluppare anche un autoplayer fatto per bene
    }
}

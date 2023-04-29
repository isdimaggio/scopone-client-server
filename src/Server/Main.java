package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {

    static ServerSocket serverSocket;

    static List<UtenteLoggato> listaUtenti = new ArrayList<>();
    static String[] mappaCarte;
    static int mappeUsate = 0;

    public static void main(String[] args) {
         try {
             serverSocket = new ServerSocket(777);
         }catch (Exception e){
             System.err.println("Impossibile aprire il socket: " + e.getMessage());
             return;
         }

         String comando = "";

         // genera la mappa delle carte
         mappaCarte = Distribuzione.creaMappaCarte();

         // ascolta per le connessioni in ingresso con un loop infinito
         while(!Objects.equals(comando, "terminaServer")){

             try{
                 Socket client = serverSocket.accept();
                 System.out.println("ACK: " + client.getRemoteSocketAddress().toString());
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                 // Lettura richiesta dal client
                 comando = in.readLine();
                 if(Objects.equals(comando, "terminaServer")) continue;

                 // risposta
                 String risposta = elaborazione(comando);
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                 System.out.println("REQ: " + comando + " \nRES: " + risposta);

                 out.write(risposta);
                 out.flush();
                 client.close();
                 in.close();
                 out.close();

             } catch (IOException e) {
                 throw new RuntimeException(e);
             }

         }

    }

    private static String elaborazione(String richiesta){
        int comando;
        String parametro;
        try{
            RichiestaClient richiestaClient = new RichiestaClient(richiesta);
            comando = richiestaClient.getCommand();
            parametro = richiestaClient.getParameter();
        }catch (Exception e){
            System.out.println("EXCP: " + e.getMessage());
            return "ER";
        }
        switch (comando) {
            case RichiestaClient.LOGIN_COMMAND -> {
                // controlla se il server non è pieno!
                if(listaUtenti.size() > 3){
                    System.out.println("Server pieno, rifiutando");
                    return "MX";
                }

                // controlla che lo username non esista gia
                for (UtenteLoggato utente : listaUtenti) {
                    if(Objects.equals(utente.getUsername(), parametro)){
                        System.out.println("Username in uso");
                        return "EU";
                    }
                }

                // va bene
                UtenteLoggato nuovoUtente = new UtenteLoggato(parametro);
                listaUtenti.add(nuovoUtente);
                return "OK" + nuovoUtente.getId();
            }
            case RichiestaClient.DISTRIBUZ_COMMAND -> {
                // autentica il client
                if(!autenticazione(parametro)){
                    System.out.println("Auth fallita");
                    return "ER"; // implementare err specifico
                }

                // no carte se non 4 utenti
                if(listaUtenti.size() != 4) {
                    System.out.println("Lobby non piena");
                    return "WA";
                }

                //distribuisci la carta
                if(mappeUsate > 3){
                    System.out.println("Tutte le mappe usate");
                    return "ER"; // implementare err specifico
                }

                mappeUsate++;
                return mappaCarte[mappeUsate-1];

            }
            default -> {
                return "ER";
            }
        }
    }

    public static boolean autenticazione(String id) {
        for(UtenteLoggato u : listaUtenti){
            if(Objects.equals(id, u.getId())){
                return true;
            }
        }
        return false;
    }

}

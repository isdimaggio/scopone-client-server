package TestSuite;

import java.io.*;
import java.net.*;
public class ConnessioneAServer
{
    Socket client;
    String indirizzo;
    int porta;
    BufferedReader in;
    BufferedWriter out;
    public ConnessioneAServer(String indirizzo, int porta)
    {
        this.indirizzo=indirizzo;
        this.porta=porta;
    }
    public String risposta(String richiesta)
    {
        String s="";
        try {
// Apro la connessione con il server
            client = new Socket(indirizzo, porta);
            in = new BufferedReader(new
                    InputStreamReader(client.getInputStream()));
            out = new BufferedWriter(new
                    OutputStreamWriter(client.getOutputStream()));
// Invio richiesta da parte del client
            out.write(richiesta);
            out.newLine();
            out.flush();
            // Acquisizione risposta da parte del server
            s = in.readLine();
            // Chiusura connesioni
            client.close();
            in.close();
            out.close();
        }
        catch (Exception e) {
            System.out.println("COMUNICAZIONE FALLITA!\nErrore: " +e.getMessage());
                    s="ERR"; }
        return s; }
}

package ClientLib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketManager {
    static Socket client;
    static BufferedReader in;
    static BufferedWriter out;

    public static RispostaServer richiesta(
            String richiesta,
            String indirizzo,
            int porta
    ) throws Exception {
        String s;

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

        return new RispostaServer(s);
    }
}

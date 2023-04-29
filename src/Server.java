
import java.net.*;
import java.io.*;
import java.util.Collections;
import java.util.ArrayList;

public class Server
{
    // Variabili globali del server necessarie ad elaborare la risposta
    static int cont=0;
    public static void main(String args[])
    {
        ServerSocket ss;
        try{
            ss = new ServerSocket(55555);
            while(true)
            {
                try{
                    Socket client = ss.accept();
                    System.out.println("Accettata connessione da" + client.getRemoteSocketAddress().toString());
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                    // Lettura richiesta dal client
                    String str=in.readLine();
                    String risposta = elaborazione(str);

                    //public class CardDistribution {
                        //public static void main(String[] args) {
                            // creazione lista delle carte napoletane
                            ArrayList<String> cards = new ArrayList<String>();

                            String[] semi = {"C", "S", "B", "D"};
                            String[] valori = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

                            for (String seme : semi) {
                                for (String valore : valori) {
                                    cards.add( seme + valore );
                                }
                            }

                            // mischia la lista di carte
                            Collections.shuffle(cards);

                            // distribuzione delle carte
                            ArrayList<String>[] hands = new ArrayList[4];

                            for (int i = 0; i < 4; i++) {
                                hands[i] = new ArrayList<String>();
                            }

                            for (int i = 0; i < 40; i++) {
                                hands[i % 4].add(cards.get(i));
                            }

                            String[] handsString = new String[4];
                            for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 10; j++) {
                                    handsString[i] += "C" + hands[j];
                                }
                            }





                    //trasmissione risposta del server
                    out.write(risposta);
                    out.flush();
                    // chiusura connessione
                    client.close();
                    in.close();
                    out.close();
                }
                catch(Exception e) {
                    System.out.println("COMUNICAZIONE FALLITA!\nErrore: " + e.getMessage());
                }
            }
        }
        catch(Exception e) {
            System.out.println("APERTURA ServerSocket FALLITA!\nErrore: " +
                    e.getMessage());
        } }
    static private String elaborazione(String richiesta)
    {
        if (richiesta.equals("DN"))
        {
            String s;
            cont++;
            s="NU"+cont;
            // FINE ELABORAZIONE
            return s; }
        else
        return "ER";
    }
}
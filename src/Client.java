import java.io.*;

public class Client
{
    public static void main(String args[]) throws Exception
    {
        InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader tastiera = new BufferedReader(in);

        System.out.println("Inserisci indirizzo");
        String indirizzo=tastiera.readLine();
        System.out.println("Inserisci porta");
        int porta=Integer.parseInt(tastiera.readLine());
        ConnessioneAServer conn=new ConnessioneAServer(indirizzo,porta);
        String richiesta;
        do
        {
            System.out.println("Inserisci richiesta (.. per terminare)");
            richiesta=tastiera.readLine();
            if (!richiesta.equals(".."))
            {
                String a=conn.risposta(richiesta);
                System.out.println("Risposta server: "+a);
                System.out.println("\n");
            }
        }
        while(!richiesta.equals(".."));

    }

}
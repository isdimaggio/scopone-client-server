package Commons;

import java.util.ArrayList;

public class Carta {

    // COSTANTI VALORI SEMI
    public static final char SEME_COPPA = 'C';
    public static final char SEME_SPADA = 'S';
    public static final char SEME_DENARO = 'D';
    public static final char SEME_BASTONE = 'B';
    public static final char[] LISTA_SEMI = {'C', 'S', 'B', 'D'};

    private final char seme;
    private final int valore;

    // costruttore carta da seme e valore
    public Carta(char s, int v) throws Exception {

        if (s != SEME_BASTONE && s != SEME_DENARO && s != SEME_SPADA && s != SEME_COPPA) {
            throw new Exception("Seme non valido");
        }

        if (v < 1 || v > 10) {
            throw new Exception("Valore non valido (" + v + ")");
        }

        this.seme = s;
        this.valore = v;

    }

    // costruttore carta da stringa
    public Carta(String raw) throws Exception {
        this(
                rawToChar(raw),
                rawToInt(raw)
        );
    }

    // restituisce il primo carattere (0) della stringa
    private static char rawToChar(String raw) {
        return raw.charAt(0);
    }

    // restituisce il secondo carattere (1) della stringa
    private static int rawToInt(String raw) {
        int v = Integer.parseInt(
                String.valueOf(
                        raw.charAt(1)));

        if (v == 0) v = 10;

        return v;
    }

    // scompone la stringa Mazzo restituendo Arraylist composto da oggetti carta
    public static ArrayList<Carta> deserializeMazzo(String mazzoS) throws Exception {
        ArrayList<Carta> mazzo = new ArrayList<>();

        if (mazzoS.length() == 0) {
            return mazzo;
        }

        for (int i = 0; i < mazzoS.length(); i += 2) {
            if (i + 1 < mazzoS.length()) {
                char seme = mazzoS.charAt(i);
                char valore = mazzoS.charAt(i + 1);
                int valok = Integer.parseInt(String.valueOf(valore));

                mazzo.add(new Carta(
                        seme,
                        valok == 0 ? 10 : valok // condizione ? valore_se_vera : valore_se_falsa
                ));
            }
        }
        return mazzo;
    }

    public char getSeme() {
        return seme;
    }

    public int getValore() {
        return valore;
    }

    @Override
    public String toString() {
        return String.valueOf(seme) + (valore != 10 ? valore : 0);
    }
}

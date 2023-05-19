package Commons;

public class Carta {

    // COSTANTI VALORI SEMI
    public static final char SEME_COPPA = 'C';
    public static final char SEME_SPADA = 'S';
    public static final char SEME_DENARO = 'D';
    public static final char SEME_BASTONE = 'B';
    public static final char[] LISTA_SEMI = {'C', 'S', 'B', 'D'};

    private final char seme;
    private final int valore;

    public Carta(char s, int v) throws Exception {

        if(s != SEME_BASTONE && s != SEME_DENARO && s != SEME_SPADA && s != SEME_COPPA){
            throw new Exception("Seme non valido");
        }

        if(v < 1 || v > 10){
            throw new Exception("Valore non valido");
        }

        this.seme = s;
        this.valore = v;

    }

    public Carta(String raw) throws Exception {
        this(
                rawToChar(raw),
                rawToInt(raw)
        );
    }

    public char getSeme() {
        return seme;
    }

    public int getValore() {
        return valore;
    }

    @Override
    public String toString(){
        return String.valueOf(seme) + (valore != 10 ? valore : 0);
    }

    private static char rawToChar(String raw){
        return raw.charAt(0);
    }

    private static int rawToInt(String raw){
        int v = Integer.parseInt(
                String.valueOf(
                        raw.charAt(1)));

        if (v == 0) v = 10;

        return v;
    }
}

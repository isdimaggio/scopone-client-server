package ClientLib;

public class StatoPartita {

    public static final int GAME_PLAYING = 0;
    public static final int GAME_WON = 1;
    public static final int GAME_LOST = -1;

    int statoPartita;
    int numeroCarte;

    public StatoPartita(int statoPartita) {
        this.statoPartita = statoPartita;
        this.numeroCarte = 0;
    }

    public StatoPartita(int statoPartita, int numeroCarte) {
        this.statoPartita = statoPartita;
        this.numeroCarte = numeroCarte;
    }

    public int getStatoPartita() {
        return statoPartita;
    }

    public int getNumeroCarte() {
        return numeroCarte;
    }
}

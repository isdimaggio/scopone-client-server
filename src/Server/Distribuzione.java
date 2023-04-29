package Server;

import java.util.ArrayList;
import java.util.Collections;

public class Distribuzione {

    public static String[] creaMappaCarte() {
        ArrayList<String> cards = new ArrayList<>();
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
            handsString[i] = "";
            for (int j = 0; j < 10; j++) {
                handsString[i] += "CR"+hands[i].get(j);
            }
        }

        return handsString;
    }

}

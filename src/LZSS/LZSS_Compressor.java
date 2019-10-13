package LZSS;

import Triplet.Triplet;

import java.util.ArrayList;
import java.lang.String;

public class LZSS_Compressor {

    private Triplet<Integer, Integer, Character> dades;
    private ArrayList<Triplet<Integer, Integer, Character>> myarray = new ArrayList<>();
    private ArrayList<Character> searchBuffer = new ArrayList<>();

    public LZSS_Compressor() {
    }

    public void Compress (String item) {

        int size = item.length();
        char c;

        for (int i = 0; i < size; ++i) {

            c = item.charAt(i);
            int despl = 0;
            int offset = 0;
            searchBuffer.add(c);

            for (int j = 0; j < searchBuffer.size() - 1; ++j) { //-1 perque no es trobi a ell mateix (s'ha inserit en l'última posició)

                if (searchBuffer.get(j) == c) {

                    offset = j;
                    despl++;
                    j++;
                    int recent = 1;

                    for (; j < searchBuffer.size() - recent && (i+1) < item.length(); j++, i++) { //recent augmenta cada cop que s'insereix un nou caràcter a searcBuffer per evitar que es trobin coincidencies amb ells mateixos
                        if (item.charAt(i + 1) == searchBuffer.get(j)) {
                            despl++;
                            searchBuffer.add(item.charAt(i + 1));
                            recent++;
                        } else break;
                    }
                    break;
                }

            }

            if (despl != 0) {
                dades = new Triplet(offset, despl, '0'); //'0' = null
                myarray.add(dades);
            } else {
                dades = new Triplet(0, 0, c);
                myarray.add(dades);
            }
        }


        for (int h = 0; h < myarray.size(); h++) {
            System.out.println(myarray.get(h).getFirst().toString() + ", "  + myarray.get(h).getSecond().toString() + ", " + myarray.get(h).getThird().toString());

        }

    }
}
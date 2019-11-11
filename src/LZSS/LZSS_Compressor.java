package LZSS;

import LZSS.Pair;
import Triplet.Triplet;
import java.util.ArrayList;
import java.lang.String;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Queue;


public class LZSS_Compressor {

    private Triplet<Integer, Integer, Character> dades;
    //private Pair<Integer, Integer>() Intent;
    private Queue<Pair<Integer,Integer>> coincidence = new LinkedList<>();
    private Queue<Character> charQ  = new LinkedList<>();
    


    private ArrayList<Triplet<Integer, Integer, Character>> myarray = new ArrayList<>();
    private ArrayList<Character> searchBuffer = new ArrayList<>();


    public LZSS_Compressor() {
    }
/*
    public ArrayList<Triplet<Integer, Integer, Character>> getArray() {
        return myarray;
    }

    public void Compress (String item) {

        int size = item.length();
        BitSet bits = new BitSet(size); // 1 si coincidencia, 0 si no

        char c;

        for (int i = 0; i < size; ++i) {

            c = item.charAt(i);
            int despl = 0;
            int offset = 0;
            searchBuffer.add(c);

            for (int j = 0; j < searchBuffer.size() - 1; ++j) { //-1 perque no es trobi a ell mateix (s'ha inserit en l'última posició)

                if (searchBuffer.get(j) == c) {
                    //System.out.println("searchjaja");
                    offset = j;
                    despl++;
                    j++;
                    int recent = 1;

                    for (; j < searchBuffer.size() - recent && (i+1) < item.length(); j++, i++) { //recent augmenta cada cop que s'insereix un nou caràcter a searcBuffer per evitar que es trobin coincidencies amb ells mateixos
                        bits.set(i, false);
                        if (item.charAt(i + 1) == searchBuffer.get(j)) {
                            bits.set(i, true);
                            //System.out.println("dos");
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
                coincidence.add(new Pair<Integer, Integer>(offset, despl) );

            } else {
                dades = new Triplet(0, 0, c);
                myarray.add(dades);
                charQ.add(c);
            }
        }


        for (int h = 0; h < myarray.size(); h++) {
            System.out.println(myarray.get(h).getFirst().toString() + ", "  + myarray.get(h).getSecond().toString() + ", " + myarray.get(h).getThird().toString());
            if (bits.get(h) == false ) {
                char removed = charQ.remove();
                System.out.println(removed);
            }
            else {
                Pair <Integer, Integer>  aux = coincidence.remove();
                System.out.println(aux.getFirst() + ", " + aux.getSecond() );
                h += (aux.getSecond() - 1);
            }

        }

    }
*/
    public void compress(String item) {



        int window = item.length(); //preguntar com posar un valor de int pes petit de 16bits en un char

        BitSet bits = new BitSet(4096);


        short offset = 0;
        short despl = -3;
        boolean coinc;

        for (int i = 0; i < window; ++i) {

            int nous = 0;
            coinc = false;
            despl = 0;
            char c = item.charAt(i);
            int size = searchBuffer.size();

            for (short j = 0; j < searchBuffer.size(); ++j) {

                if (searchBuffer.size() >= 3 && searchBuffer.get(j) == item.charAt(i) && searchBuffer.get(j+1) == item.charAt(i+1) && searchBuffer.get(j+2) == item.charAt(i+2)) { //hem trobat una coincidencia de almenys llargada 3
                    despl = -2;
                    nous++;
                    searchBuffer.add(item.charAt(i));
                    coinc = true;
                    offset = j;
                    j++;
                    for (; j < searchBuffer.size() - nous && (i+1) < item.length(); j++, i++) {

                        if (item.charAt(i+1) == searchBuffer.get(j)) {
                            despl++;
                            nous++;
                            searchBuffer.add(item.charAt(i+1));
                        }
                        else break;
                    }
                    break;
                }
            }

            if (coinc == true) { //hem trobat coincidencia
                //posar offset en els 12 primers bits i despl en els 4 ultims dun char (16bits)
                bits.set( (i-(despl+2)), true);

                //System.out.println(despl);
                short of = (short) (offset*16);
                System.out.println(of);
                short desp = (short) (15 & despl); // es el desplaçament real -2 (coincidencies a partir de 3)
                System.out.println(desp);

                char aux = (char) (of | desp);
                System.out.print((short)aux);
                charQ.add(aux);
            }
            else {
                searchBuffer.add(item.charAt(i));
                bits.set(i, false);
                charQ.add(item.charAt(i));
                System.out.println(item.charAt(i));
            }
        }
/*
        String result = "";

        for (int i = 0; i < item.length(); ++i) {
            if (bits.get(i) == false) {
                char caux = charQ.remove();
                //System.out.print(caux);
                result += caux;
            }
            else {

                char aux = charQ.remove();
                short os = (short) (65520&aux);
                short dpl = (short) (15&aux);

                //i = i + (int) dpl;

                //for (int j = (i - (int) os); j <= (j + (int) dpl); ++j) {
                for (int j = 0; j < 2; ++j) {
                    //System.out.print(result.charAt(j));
                    result = result + result.charAt(j);
                }

            }
        }

        for (int i = 0; i < result.length(); ++i) {
            System.out.print(result.charAt(i));

        }

*/
    }
}
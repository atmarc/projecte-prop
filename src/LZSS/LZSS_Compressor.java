package LZSS;

import FileManager.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.lang.String;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Queue;


public class LZSS_Compressor {


    private Queue<Character> charQ  = new LinkedList<>();


    private boolean Coincidence(int i, int j, int window, String item, ArrayList<Character> searchBuffer) {
        return i <= (window - 3) && searchBuffer.size() >= 3 &&
                searchBuffer.get(j) == item.charAt(i) && searchBuffer.get(j + 1) == item.charAt(i + 1) &&
                searchBuffer.get(j + 2) == item.charAt(i + 2);
    }

    private BitSet SubBitSet(BitSet s, int i, int j) {

        BitSet aux = new BitSet(8);
        int h = 0;

        for (; i < j; ++i, h++) {
            aux.set(h, s.get(i));
        }

        return aux;
    }

    private String BitSetToString(ArrayList<Boolean> s) {
        String aux = "";
        System.out.println(s.size());
        for (int i = 0; i < s.size(); ++i) {
            if (!s.get(i)) {
                aux += "0";
            }
            else aux += "1";
        }
        return aux;
    }

    private static String stringBinToStringChar(String s) {
        String retorn = "";
        for (int j = 0; j + 16 <= s.length(); j += 16) {
            char b = 0;
            String aux = s.substring(j, j + 16);
            for (int i = 0; i < 16; ++i) {
                if (aux.charAt(i) == '1') {
                    b = (char) (b | (1 << 15-i));
                }
            }
            retorn = retorn + b;
        }
        return retorn;
    }
    public LZSS_Compressor() {
    }
    public void compress(String superItem) {

        boolean end = false;
        int beggining = 0;
        String item;
        String result = "";

        while (!end) {
            ArrayList<Character> searchBuffer = new ArrayList<>(0);

            if (superItem.length() > (beggining + 4095) ) {
                item = superItem.substring(beggining, beggining + 4095);
                beggining = beggining + 4095;
            }
            else {
                item = superItem.substring(beggining ,superItem.length());
                end = true;
            }

            int window = item.length(); //preguntar com posar un valor de int pes petit de 16bits en un char

            //BitSet bits = new BitSet(item.length());
            ArrayList<Boolean> bits = new ArrayList<Boolean>();

            short offset = 0;
            short despl = -3;
            boolean coinc;
            int sizeQ;

            for (int i = 0; i < (window); ++i) {

                int nous = 0;
                coinc = false;
                despl = 0;
                char c = item.charAt(i);
                int size = searchBuffer.size();
                int principi = 0;

                for (short j = 0; j <= searchBuffer.size() - 3; ++j) {

                    if (Coincidence(i,j,window,item,searchBuffer)) { //hem trobat una coincidencia de almenys llargada 3
                        principi = i;
                        despl = -2;
                        nous++;
                        searchBuffer.add(item.charAt(i));
                        coinc = true;
                        bits.add(true);
                        offset = j;
                        j++;
                        i++;
                        for (; j < searchBuffer.size() - nous && (i) < item.length(); j++,i++) {

                            if (item.charAt(i) == searchBuffer.get(j)) {
                                despl++;
                                nous++;
                                searchBuffer.add(item.charAt(i));
                                bits.add(false);

                            }
                            else {
                                i--;
                                break;
                            }
                            if (despl == 15) break;
                        }

                        break;
                    }
                }

                //System.out.println(i);
                if (coinc) { //hem trobat coincidencia
                    //posar offset en els 12 primers bits i despl en els 4 ultims dun char (16bits)
                    //bits.set( (i-(despl+2)), true);
                    //if (principi == 93) System.out.println("hello");
                    short of = (short) (offset<<4);

                    short desp = (short) (0x000F & despl); // es el desplaçament real -2 (coincidencies a partir de 3)

                    char aux = (char) (of | desp); //12 bits de offset i 4 de desplaçament
                    charQ.add(aux);
                    sizeQ = charQ.size();
                }
                else {
                    searchBuffer.add(item.charAt(i));
                    bits.add(false);
                    charQ.add(item.charAt(i));
                    sizeQ = charQ.size();
                }
            }



            String hola = BitSetToString(bits);
            //System.out.println(hola);

            int modhola = hola.length() % 16;

            if (modhola != 0) {
                String holaux = "";
                for (int i = 0; i < (16 - modhola); ++i) {
                    if (i != (16 - modhola -1) ) {
                        holaux += "0";
                    }
                    else {
                        holaux += "1";
                    }
                }
                hola = holaux + hola;
            }
            else {
                hola = "0000000000000001" + hola;
            }

            String jaja = stringBinToStringChar(hola);
            //System.out.println(jaja);
            System.out.println(jaja);

            /*String result = "";
            for (; charQ.size() != 0;) {
                result += charQ.remove();
            }
            result += "-1";
            for (int i = 0; i < item.length(); ++i) {
                if (!bits.get(i)) {
                    result += "0";
                }
                else result += "1";
            }*/


            result = result + jaja;
            result = result + (char) 0xFFFF;
            for (; charQ.size() != 0;) {
                result += charQ.remove();
            }
            result = result + (char) 0xFFFF;
            //System.out.println(result);

        }

        try {
            FileManager.createFile(result, "testing_files/Compressed.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void compress2(String item) {
        int window = item.length(); //preguntar com posar un valor de int pes petit de 16bits en un char

        BitSet bits = new BitSet(item.length());

        short offset = 0;
        short despl = -3;
        boolean coinc;
        int sizeQ;

        for (int i = 0; i < (window); ++i) {

            int nous = 0;
            coinc = false;
            despl = 0;
            char c = item.charAt(i);
            int size = searchBuffer.size();
            int principi = 0;

            for (short j = 0; j <= searchBuffer.size() - 3; ++j) {

                if (Coincidence(i,j,window,item)) { //hem trobat una coincidencia de almenys llargada 3
                    principi = i;
                    despl = -2;
                    nous++;
                    searchBuffer.add(item.charAt(i));
                    coinc = true;
                    offset = j;
                    j++;
                    i++;
                    for (; j < searchBuffer.size() - nous && (i) < item.length(); j++,i++) {

                        if (item.charAt(i) == searchBuffer.get(j)) {
                            despl++;
                            nous++;
                            searchBuffer.add(item.charAt(i));

                        }
                        else {
                            i--;
                            break;
                        }
                        if (despl == 15) break;
                    }

                    break;
                }
            }

            //System.out.println(i);
            if (coinc) { //hem trobat coincidencia
                //posar offset en els 12 primers bits i despl en els 4 ultims dun char (16bits)
                //bits.set( (i-(despl+2)), true);
                //if (principi == 93) System.out.println("hello");
                bits.set(principi, true);
                short of = (short) (offset<<4);

                short desp = (short) (0x000F & despl); // es el desplaçament real -2 (coincidencies a partir de 3)

                char aux = (char) (of | desp); //12 bits de offset i 4 de desplaçament
                charQ.add(aux);
                sizeQ = charQ.size();
            }
            else {
                searchBuffer.add(item.charAt(i));
                bits.set(i, false);
                charQ.add(item.charAt(i));
                sizeQ = charQ.size();
            }
        }



        String hola = BitSetToString(bits);
        //System.out.println(hola);

        int modhola = hola.length() % 16;

        if (modhola != 0) {
            for (int i = 0; i < (16 - modhola); ++i) {
                if (i != (16 - modhola -1) ) {
                    hola = "0" + hola;
                }
                else {
                    hola = "1" + hola;
                }
            }
        }
        else {
            hola = "0000000000000001" + hola;
        }

        String jaja = stringBinToStringChar(hola);
        //System.out.println(jaja);
        System.out.println(jaja);

        String result = "";
        for (; charQ.size() != 0;) {
            result += charQ.remove();
        }
        result += "-1";
        for (int i = 0; i < item.length(); ++i) {
            if (!bits.get(i)) {
                result += "0";
            }
            else result += "1";
        }

        /*String result = "";
        result = result + jaja;
        result = result + (char) 0xFFFF;
        for (; charQ.size() != 0;) {
            result += charQ.remove();
        }
        result = result + (char) 0xFFFF;


        try {
            FileManager.createFile(result, "testing_files/Compressed.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
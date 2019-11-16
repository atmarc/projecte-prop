package LZSS;

import FileManager.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.lang.String;
import java.util.LinkedList;
import java.util.Queue;

public class LZSS_Decompressor {


    private ArrayList<Character> searchBuffer = new ArrayList<>();

    public LZSS_Decompressor() {
    }

    private String BitSetToString(ArrayList<Boolean> s) {
        String aux = "";
        //System.out.println(s.size());
        for (int i = 0; i < s.size(); ++i) {
            if (!s.get(i)) {
                aux += "0";
            }
            else aux += "1";
        }
        return aux;
    }

    private Queue<Character> PrintQueue(Queue<Character> Q) {
        String aux = "";
        Queue<Character> charQ2  = new LinkedList<>();
        for(;!Q.isEmpty();) {
            charQ2.add(Q.peek());
            aux += Q.remove();
        }
        //System.out.println(aux);
        return charQ2;
    }



    public void Decompress(String item) throws IOException {
        int nextsegment = 0;
        String resultat = "";
        boolean end = false;
        String reslocal;

        while (!end) {
            Queue<Character> charQ  = new LinkedList<>();
            ArrayList<Boolean> BitSet = new ArrayList<Boolean>();

            boolean endBits = false;
            int poscharQ = 0;
            boolean start = false; //Els primers bits son per quadrar-los en multuiples de 8
            reslocal = "";

            for (int i = nextsegment; !endBits; i++) {
                char c = item.charAt(i);
                if (start) {
                    if (c == 0xFFFF) {
                        poscharQ = i + 1;
                        endBits = true;
                    } else {

                        for (int j = 0; j < 16; ++j) {
                            char aux = (char) ((c >> 15 - j) & 0x0001);

                            if (aux == 0) BitSet.add(false);
                                else BitSet.add(true);
                        }
                    }
                } else {
                    if (c == 0x0001) {
                        start = true;
                    } else {
                        boolean prefix = true;
                        for (int h = 0; h < 16; ++h) {
                            char aux = (char) ((c >> 15 - h) & 0x0001);
                            if (prefix) {
                                if (aux == 1) prefix = false;
                            } else {
                                if (aux == 0) BitSet.add(false);
                                else BitSet.add(true);
                            }
                        }
                        start = true;
                    }
                }
            }


            for (int i = poscharQ; i < item.length(); i++) {
                if (item.charAt(i) == 0xFFFF) { //Vol dir que s'ha acabat aquest segment
                    nextsegment = i + 1;
                    if (nextsegment >= item.length()) end = true;
                    break;
                } else charQ.add(item.charAt(i));
            }

            //charQ = PrintQueue(charQ);


            for (int i = 0; i < BitSet.size(); ++i) {
                if (!BitSet.get(i)) {
                    //System.out.print(charQ.peek());
                    reslocal += (char) charQ.remove();

                } else {

                    char aux = charQ.remove();
                    short os = (short) (aux & 0xFFF0);
                    os = (short) (os >> 4);
                    os = (short) (os & 0x0FFF);
                    short dpls = (short) (aux & 0x000F);
                    dpls = (short) (2 + dpls);
                    short point = (short) (os);

                    for (short j = 0; j <= dpls; ++j) {
                        //System.out.print(reslocal.charAt(point + j));
                        reslocal = reslocal + reslocal.charAt(point + j);
                    }
                    i += dpls;

                }
            }

            resultat = resultat + reslocal;
        }

        try {
            FileManager.createFile(resultat, "testing_files/result.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
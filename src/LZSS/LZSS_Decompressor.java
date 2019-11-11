package LZSS;

import Triplet.Triplet;

import java.util.ArrayList;
import java.lang.String;

public class LZSS_Decompressor {

    private Triplet<Integer, Integer, Character> dades;
    private ArrayList<Triplet<Integer, Integer, Character>> myarray = new ArrayList<>();
    private ArrayList<Character> searchBuffer = new ArrayList<>();
    private String result = "";

    public LZSS_Decompressor() {
    }

    /*public void Decompress(ArrayList<Triplet<Integer, Integer, Character>> x) {
        String result = "";
        int size = x.size();

        for (int i = 0; i < size; ++i) {
            if (x.get(i).getFirst() == 0) {
                result += x.get(i).getThird();
            }
            else {
                int offset = x.get(i).getFirst();
                int despl = x.get(i).getSecond();
                int pos = result.length() - offset;

                for (int h = 0; h < despl; ++h) {
                    result += result.charAt(pos);
                    pos++;
                }
            }
        }

        for (int j = 0; j < result.length(); ++j) {
            System.out.print(result.charAt(j));
        }
    }*/
/*
    public void Decompress (item) {
        //Read bitset, coincidence and charQ

        for (int i; i < bits.size(); ++i) {

            if (bits.get(i) == false) {
                result += charQ.remove();
            }
            else {
                Pair <Integer, Integer> aux = coincidence.remove();
                int h = i;
                for (int j = 0; j < aux.getSecond(); ++j) {

                    result = result + result.charAt(h - aux.getFirst());
                    h++;
                }
                i += aux.getSecond();
            }
        }
    }
*/}

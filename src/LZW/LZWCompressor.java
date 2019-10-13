package LZW;

import java.util.ArrayList;
import java.util.HashMap;

public class LZWCompressor {

    static private HashMap<String, Integer> basicDictionary() {
        HashMap <String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; ++i) {
            dictionary.put(String.valueOf((char)i), i);
        }
        return dictionary;
    }

    public static ArrayList<Integer> compress(String data) {
        ArrayList<Integer> outList = new ArrayList<>();
        HashMap<String, Integer> localDictionary = basicDictionary();
        StringBuilder pattern = new StringBuilder();

        for (int i = 0; i < data.length(); i++) {

            char c = data.charAt(i);
            String s = pattern.toString();

            if (!localDictionary.containsKey(s + c)) {
                outList.add(localDictionary.get(s));
                int temp = localDictionary.size();
                localDictionary.put(s + c, temp);
                pattern = new StringBuilder();
                pattern.append(c);
            }
            else pattern.append(c);
        }
        outList.add(localDictionary.get(pattern.toString()));

        return outList;
    }

}
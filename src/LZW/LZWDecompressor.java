package LZW;


import java.util.ArrayList;

public class LZWDecompressor {

    static private ArrayList<String> basicDictionary() {
        ArrayList <String> dictionary = new ArrayList<>();
        for (int i = 0; i < 256; ++i) {
            dictionary.add(String.valueOf((char)i));
        }
        return dictionary;
    }

    // Unused
    static private ArrayList<Integer> getIndexes (String S) {
        String[] localArray = S.split(" ");
        ArrayList<Integer> retVal = new ArrayList<>();
        for (String s : localArray) {
            if (!s.isEmpty()) retVal.add(Integer.parseInt(s));
        }
        return retVal;
    }

    public static String decompress(ArrayList<Integer> data) {
        StringBuilder outString = new StringBuilder();
        ArrayList<String> localDictionary = basicDictionary();
        String pattern = localDictionary.get(data.get(0));
        outString.append(localDictionary.get(data.get(0)));

        for (int i = 1; i < data.size(); ++i) {
            int index = data.get(i);
            if (index < localDictionary.size()) {
                String out = localDictionary.get(index);
                outString.append(out);
                localDictionary.add(pattern + out.charAt(0));
                pattern = out;
            }
            else {
                localDictionary.add(pattern + pattern.charAt(0));
                outString.append(pattern + pattern.charAt(0));
            }
        }

        return outString.toString();
    }
}
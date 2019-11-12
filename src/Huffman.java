import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;

public class Huffman {


    private class Node {
        int data;
        String c;
        Node left;
        Node right;

        public Node() {
            this.left = null;
            this.right = null;
        }

        private Node (int data, String c) {
            this.c = c;
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }

    private class NodeComparator implements Comparator<Node> {
        public int compare(Node x, Node y)
        {

            return x.data - y.data;
        }
    }

    private HashMap<String, String> dictionary = new HashMap<>();

    public String encode(String s) {
        String file [] = s.split(",");

        LinkedHashMap<String, Integer> lhm = calculateFreq(file);
        PriorityQueue<Node> pQ = new PriorityQueue<>(lhm.size(), new NodeComparator());

        lhm.forEach((c, freq) -> {
            Node node = new Node(freq, c);
            pQ.add(node);
        });

        Node root = new Node();

        while(pQ.size() > 1) {

            Node x = pQ.peek();
            pQ.poll();

            Node y = pQ.peek();
            pQ.poll();

            Node f = new Node(x.data + y.data, "999999");
            f.left = x;
            f.right = y;
            root = f;
            pQ.add(f);
        }

        makeDict(root, "");

        // Posem un 1 per saber que comença el diccionari i 2 al final per dir que s'acaba
        String comprimit = "1" + addDictionary() + "011111111111111110";

        for (int i = 0; i < file.length; ++i) {
            comprimit += dictionary.get(file[i]);
        }

        // Posem 0 al principi perque sigui múltiple de 8
        if (comprimit.length()%8 != 0) {
            int offset = 8 - comprimit.length()%8;
            for (int i = 0; i < offset; ++i) comprimit = "0" + comprimit;
        }

        if (comprimit.length()%8 != 0) System.out.println("Segueix sense ser multiple");

        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            String c = entry.getKey();
            String code = entry.getValue();
            System.out.println(c + ": " + code);
        }

        return comprimit;
    }


    // Perque no s'hagi de copiar cada vegada al fer isSeparador
    private String fileGlobal = "";
    private boolean isSeparador (int index) {
        if (fileGlobal.charAt(index) != '0') return false;
        for (int i = 1; i <= 16; ++i) {
            if (fileGlobal.charAt(index + i) != '1') return false;
        }
        if (fileGlobal.charAt(index + 17) != '0') return false;
        return  true;
    }

    public int[] decode(String file) {
        LinkedHashMap<String, String> dictionary = new LinkedHashMap<>();
        int index = 0;
        fileGlobal = file;
        // Llegim els 0 que el fan múltiple de 8
        System.out.println(file.length());
        while (file.charAt(index) != '1') ++index;
        // Saltem el primer 1 que indica el principi de la
        ++index;
        while (!isSeparador(index) && !isSeparador(index + 18)) {
            String key = "";
            while (!isSeparador(index)) {
                key += file.charAt(index);
                ++index;
            }
            // Com que hem trobat un separador, el saltem
            index += 18;
            String code = "";
            while (!isSeparador(index)) {
                code += file.charAt(index);
                ++index;
            }
            // Com que hem trobat un separador, el saltem
            index += 18;

            if (key.charAt(0) == '1' && key.length() == 16) {
                int aux = valueOf(key, 2);
                key = "" + (aux - 65536);
            } else {
                int aux = valueOf(key, 2);
                key = "" + (aux);
            }

            dictionary.put(key, code);
        }

        //retorn +=  '|' + character + '|' + separador + '|' + code + '|' + separador;
        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            String c = entry.getKey();
            String code = entry.getValue();
            System.out.println(c + ": " + code);
        }
        return null;
    }

    public static String charToBin(char c) {
        String s = Integer.toBinaryString(c);
        for (int i = s.length(); i < 16; ++i) s = '0' + s;
        return s;
    }

    private String addDictionary() {
        String retorn = "";
        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            String c = entry.getKey();
            String code = entry.getValue();
            // Posem separadors de 32 bits (2 caràcters)
            String separador = "011111111111111110";
            String character = Integer.toBinaryString(parseInt(c));
            if (character.length() > 16) character = character.substring(character.length() - 16);

            //retorn +=  '|' + character + '|' + separador + '|' + code + '|' + separador;
            retorn += character + separador + code + separador;
        }
        return retorn;
    }

    private void makeDict(Node root, String s) {
        if (root.left == null && root.right == null && root.c != "999999") {
            dictionary.put(root.c, s);
            //System.out.println(root.c + ":" + s);
            return;
        }
        makeDict(root.left, s + "0");
        makeDict(root.right, s + "1");
    }

    private LinkedHashMap<String, Integer> calculateFreq(String[] file) {

        LinkedHashMap<String, Integer> dic = new LinkedHashMap<>();
        for (int i = 0; i < file.length; ++i) {

            String key = file[i];

            if (dic.containsKey(key)) {
                dic.replace(key, dic.get(key) + 1);
            } else {
                dic.put(key, 1);
            }
        }
        return dic;
    }
}

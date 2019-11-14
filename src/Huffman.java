import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;

public class Huffman {


    private class Node {
        int data;
        int c;
        Node left;
        Node right;

        public Node() {
            this.left = null;
            this.right = null;
        }

        private Node (int data, int c) {
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

    private HashMap<Integer, String> dictionary = new HashMap<>();

    public void encode(int[] file, LinkedList<Integer> bits) {

        LinkedHashMap<Integer, Integer> lhm = calculateFreq(file);
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

            Node f = new Node(x.data + y.data, 999999);
            f.left = x;
            f.right = y;
            root = f;
            pQ.add(f);
        }

        makeDict(root, "");

        // Posem un 1 per saber que comença el diccionari i 2 al final per dir que s'acaba
        bits.add(1);
        addDictionary(bits);
        addSeparador(bits);
        //String comprimit = "1" + addDictionary() + "011111111111111110";

        for (int i = 0; i < file.length; ++i) {
            for (int j = 0; j < dictionary.get(file[i]).length(); ++j) {
                if (dictionary.get(file[i]).charAt(j) == '1')
                    bits.add(1);
                else bits.add(0);
            }
        }

        // Posem 0 al principi perque sigui múltiple de 8
        if (bits.size() % 8 != 0) {
            int offset = 8 - bits.size() % 8;
            for (int i = 0; i < offset; ++i) bits.addFirst(0);
        }

        if (bits.size() % 8 != 0) System.out.println("Segueix sense ser multiple");
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

    public ArrayList<Integer> decode(String file) {
        LinkedHashMap<String, String> dictionary = new LinkedHashMap<>();
        int index = 0;
        fileGlobal = file;
        // Llegim els 0 que el fan múltiple de 8
        while (file.charAt(index) != '1') ++index;
        // Saltem el primer 1 que indica el principi de la
        ++index;
        // Llegim diccionari
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

            dictionary.put(code, key);
        }

        index += 18;
        String sub = file.substring(index);
        ArrayList<Integer> valors = new ArrayList<>();
        while (index < file.length()) {
            String code = "";
            while (!dictionary.containsKey(code)) {
                code += file.charAt(index);
                ++index;
            }
            valors.add(parseInt(dictionary.get(code)));
        }
        return valors;
    }

    public static String charToBin(char c) {
        String s = Integer.toBinaryString(c);
        for (int i = s.length(); i < 16; ++i) s = '0' + s;
        return s;
    }

    private void addSeparador(LinkedList<Integer> bits) {
        bits.add(0);
        for (int i = 0; i < 16; ++i) bits.add(1);
        bits.add(0);
    }

    private void addDictionary(LinkedList<Integer> bits) {

        for (Map.Entry<Integer, String> entry : dictionary.entrySet()) {
            int c = entry.getKey();
            String code = entry.getValue();
            // Posem separadors de 32 bits (2 caràcters)
            String character = Integer.toBinaryString(c);
            if (character.length() > 16) character = character.substring(character.length() - 16);

            //retorn +=  '|' + character + '|' + separador + '|' + code + '|' + separador;
            for (int i = 0; i < character.length(); ++i) {
                if (character.charAt(i) == '1') bits.add(1);
                else bits.add(0);
            }
            addSeparador(bits);
            for (int i = 0; i < code.length(); ++i) {
                if (code.charAt(i) == '1') bits.add(1);
                else bits.add(0);
            }
            addSeparador(bits);
        }
    }

    private void makeDict(Node root, String s) {
        if (root.left == null && root.right == null && root.c != 999999) {
            dictionary.put(root.c, s);
            //System.out.println(root.c + ":" + s);
            return;
        }
        makeDict(root.left, s + "1");
        makeDict(root.right, s + "0");
    }

    private LinkedHashMap<Integer, Integer> calculateFreq(int[] file) {

        LinkedHashMap<Integer, Integer> dic = new LinkedHashMap<>();
        for (int i = 0; i < file.length; ++i) {

            int key = file[i];

            if (dic.containsKey(key)) {
                dic.replace(key, dic.get(key) + 1);
            } else {
                dic.put(key, 1);
            }
        }
        return dic;
    }
}

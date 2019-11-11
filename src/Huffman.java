import java.util.*;

import static java.lang.Integer.parseInt;

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

    public String encode(String s) {
        String file [] = s.split(",");

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

        String comprimit = addDictionary();

        for (int i = 0; i < file.length; ++i) {
            comprimit += dictionary.get(parseInt(file[i]));
        }
        return comprimit;
    }

    public int[] decode(String file) {
        return null;
    }

    private static String charToBin(char c) {
        String s = Integer.toBinaryString(c);
        for (int i = s.length(); i < 8; ++i) s = '0' + s;
        return s;
    }

    private String addDictionary() {
        String retorn = "";
        for (Map.Entry<Integer, String> entry : dictionary.entrySet()) {
            int freq = entry.getKey();
            char f1 = (char)((char)(freq)/256);
            char f2 = (char)((char)(freq)%256);
            String c = entry.getValue();
            char clau = (char) parseInt(c);
            char separador = 0xFF;

            retorn += Integer.toBinaryString(clau) + charToBin(separador) + Integer.toBinaryString(f1)
                    + Integer.toBinaryString(f2) + charToBin(separador);
        }
        return retorn;
    }

    public void makeDict(Node root, String s) {
        if (root.left == null && root.right == null && root.c != 999999) {
            dictionary.put(root.c, s);
            //System.out.println(root.c + ":" + s);
            return;
        }
        makeDict(root.left, s + "0");
        makeDict(root.right, s + "1");
    }

    private LinkedHashMap<Integer, Integer> calculateFreq(String[] file) {

        LinkedHashMap<Integer, Integer> dic = new LinkedHashMap<>();
        for (int i = 0; i < file.length; ++i) {

            int key = 0;
            try {
                key = parseInt(file[i]);
            } catch (NumberFormatException e) {
                System.out.println("Numero:");
                System.out.println(file[i]);
                e.printStackTrace();
            }

            if (dic.containsKey(key)) {
                dic.replace(key, dic.get(key) + 1);
            } else {
                dic.put(key, 1);
            }
        }
        return dic;
    }
}

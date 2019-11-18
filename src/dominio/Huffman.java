package dominio;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;

/*!
 *  \brief     Clase auxiliar para la implementacion del algortimo JPEG.
 *  \details
 *  \author    Marc Amorós
 */
public class Huffman {

    /**
     * Classe privada para crear el árbol de Huffman
     */
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

    /**
     * Classe para poder comparar dos nodos y poder ordenar-los el la queue.
     */
    private class NodeComparator implements Comparator<Node> {
        public int compare(Node x, Node y) {
            return x.data - y.data;
        }
    }

    /**
     * Diccionario de enteros (clave) y su clave de Huffman en binario en un String.
     */
    private HashMap<Integer, String> dictionary = new HashMap<>();

    /**
     * Funcion para comprimir usando Huffman el array file.
     * @param file Array de enteros que vamos a comprimir usando Huffman.
     * @param bits Cadena de bits representados por enteros que representa el array de enteros comprimido con Huffman.
     */
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

    /**
     * Funcion que nos dice si el la posición index del array de enteros bits empieza un separador de forma 011111111111111110.
     * @param bits Cadena de bits representados en un array de enteros.
     * @param index Posición que queremos comprobar.
     * @return Valor booleano que nos dice si hay un separador o no.
     */
    private boolean isSeparador (int[] bits, int index) {
        if (bits[index] != 0) return false;
        for (int i = 1; i <= 16; ++i) {
            if (bits[index + i] != 1) return false;
        }
        if (bits[index + 17] != 0) return false;
        return true;
    }

    /**
     * Descomprime una cadena de bits comprimidos con Huffman en un arrayList de enteros.
     * @param file Cadena de bits representados en un array de enteros.
     * @param valors Valores enteros que conseguimos al descomprimir la cadena de bits con Huffman.
     */
    public void decode(int[] file, ArrayList<Integer> valors) {
        LinkedHashMap<String, String> dictionary = new LinkedHashMap<>();
        int index = 0;
        // Llegim els 0 que el fan múltiple de 8
        while (file[index] != 1) ++index;
        // Saltem el primer 1 que indica el principi de la
        ++index;
        // Llegim diccionari
        while (!isSeparador(file, index) && !isSeparador(file, index + 18)) {
            String key = "";
            while (!isSeparador(file, index)) {
                key += file[index];
                ++index;
            }
            // Com que hem trobat un separador, el saltem
            index += 18;
            String code = "";
            while (!isSeparador(file, index)) {
                code += file[index];
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

        while (index < file.length) {
            String code = "";
            while (!dictionary.containsKey(code)) {
                code += file[index];
                ++index;
            }
            valors.add(parseInt(dictionary.get(code)));
        }
    }

    /**
     * Funcion que añade un separador a la cadena de bits.
     * @param bits Cadena de bits a la que le añadiremos el separador.
     */
    private void addSeparador(LinkedList<Integer> bits) {
        bits.add(0);
        for (int i = 0; i < 16; ++i) bits.add(1);
        bits.add(0);
    }

    /**
     * Funcion que añade el diccionario de Huffman utilizado para comprimir al principio de una cadena de bits.
     * @param bits Cadena de bits representada en una LinkedList de enteros.
     */
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

    /**
     * Funcion que recorre recursivamente el arbol para crear los codigos de Huffman i añadirlos al diccionario.
     * @param root Raíz del arbol de Huffman que hemos creado.
     * @param s String donde se va añadiendo 0s o 1s al ir recorriendo el arbol y que termina conteniendo el codigo
     *          Huffman de cada hoja cuando llegas a ella.
     */
    private void makeDict(Node root, String s) {
        if (root.left == null && root.right == null && root.c != 999999) {
            dictionary.put(root.c, s);
            //System.out.println(root.c + ":" + s);
            return;
        }
        makeDict(root.left, s + "1");
        makeDict(root.right, s + "0");
    }

    /**
     * Función que recorre el array de enteros y calcula la frequéncia con la que aparecen los enteros.
     * @param file Cadena de enteros que se recorre y de la que se calculan las frequéncias.
     * @return Diccionario con cada entero y la frequéncia con la que aparece en el array.
     */
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

package SearchTree;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchTreeRoot {

    private HashMap<Byte,Node> Tree;


    // LZ78 acceleration
    private SearchTree previous_tree;
    private int last_index;

    public SearchTreeRoot() {
        Tree = new HashMap<Byte, Node>();
    }

    // Este codigo no vale para nada, necesito informacion de los otros algoritmos para saber hacia donde escribir.
    public int find_insert_word(ArrayList<Byte> word, int next_index) {

        Node aux;

        aux = Tree.putIfAbsent(word.get(0), new Node(next_index, new SearchTree()));


        if (aux == null) return 0;
        if (word.size() > 1) return aux.sons.find(word, 1, next_index);
        return 0;
    }

    public int find_LZ78(byte B, int next_index) {

        /*
        * Este find es especial para el algoritmo LZ78. Evita busquedas recurrentes al mismo conjunto de palabras.
        * Si antes he buscado ABCD, en vez de buscar ABCDE, buscare a partir del ultimo arbol visitado el byte E.
        *
        * */
        ArrayList<Byte> word = new ArrayList<Byte>(1);
        word.add(B);
        return previous_tree.find(word, next_index, 0);
    }

}

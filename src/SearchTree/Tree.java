package SearchTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tree{
    enum type {
        LZ78,
        LZW,
        LZSS
    };

    private List<Node> sons;

    // LZ78 acceleration
    private Tree previous_tree;
    private int last_index;

    public Tree() {
        sons = new ArrayList<Node>();
    }

    // Constructors
    public Tree(type Alg) {
        // Creadora comun. Cada uno en su funcion void puede inicializar el arbol a su gusto.
        switch (Alg) {
            case LZW:
                LZW_Tree();
                break;
            case LZSS:
                LZSS_Tree();
                break;
            case LZ78:
                LZ78_Tree();
        }
    }

    public void LZ78_Tree() {


    }

    public void LZSS_Tree() {


    }

    public void LZW_Tree() {


    }

    // Consultants

    private Node getSon(byte id) {
        for (Node son : sons) {
            if (son.areYou(id)) return son;
        }
        Node son = new Node(id, last_index, new Tree());
        sons.add(son);
        return son;
    }

    public int find(byte[] word, int digit) {
        Node aux = getSon(word[digit]);
        if (digit == word.length - 1) {
            return aux.getData();
        }
        if (aux.getData() != -1) return aux.getSons().find(word, digit + 1);
        return -1;
    }

}

package SearchTree;

import java.util.ArrayList;
import java.util.List;

public class Tree{

    private List<Node> sons;
    private Node previous_node;
    private int last_index;

    // Constructors
    public Tree() {
        previous_node = null;
        sons = new ArrayList<Node>();
    }

    public int getLast_index() {
        return last_index;
    }

    public void putSon(byte b, int data) {

        // Pre: El byte b NO esta presente en el arbol.
        // Post: Se ha insertado un nuevo nodo hijo a la raiz con id = b y data = data;

        sons.add(new Node(b, data, new Tree()));
    }


    private Node getNode(byte id) {
        for (Node son : sons) {
            if (son.areYou(id)) return son;
        }
        Node son = new Node(id, last_index, new Tree());
        sons.add(son);
        return son;
    }

    public int find(byte[] word, int digit) {
        Node aux = getNode(word[digit]);
        if (digit == word.length - 1) {
            previous_node = aux;
            return aux.getData();
        }
        if (aux.getData() != -1) return aux.getSons().find(word, digit + 1);
        return -1;
    }


    public int findNextByte(byte b) {

        // Pre: Previamente se ha buscado un byte 'a'.
        // Post: Retorna la 'data' del nodo con identificador = b, el cual es hijo del nodo 'a' (previamente buscado).
        //       Si este no estaba presente en el arbol, este queda insertado con el ultimo indice.
        if (previous_node == null) previous_node = getNode(b);

        previous_node = previous_node.getSons().getNode(b);
        return previous_node.getData();
    }



}




















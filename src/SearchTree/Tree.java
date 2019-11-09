package SearchTree;

import java.util.ArrayList;
import java.util.List;

public class Tree{

    private List<Node> sons;
    private static Node previous_node;
    private static int last_index;


    // Constructors
    public Tree() {
        sons = new ArrayList<>();
    }
    public Tree(int last_index) {
        sons = new ArrayList<>();
        Tree.last_index = last_index;
    }

    public int progressive_find(byte B, boolean restart) {

        Node aux;
        if (!restart) aux = findNextByte(B);
        else aux = putNodeIfAbsent(B);

        int data = aux.getData();
        if (data == last_index) ++last_index;   // El nodo no exisitia, y ha sido insertado
        else previous_node = aux;               // El nodo  estaba presente y debemos seguir buscando
        return data;
    }
    private Node putNodeIfAbsent(byte id) {
        for (Node son : sons) {
            if (son.areYou(id)) return son;
        }
        Node son = new Node(id, last_index, new Tree());
        sons.add(son);
        return son;
    }
    private Node findNextByte(byte B) {

        // Pre: Previamente se ha buscado un byte 'a'.
        // Post: Retorna la 'data' del nodo con identificador = b, el cual es hijo del nodo 'a' (previamente buscado).
        //       Si este no estaba presente en el arbol, este queda insertado con el ultimo indice.

        previous_node = previous_node.getSons().putNodeIfAbsent(B);
        return previous_node;
    }



}




















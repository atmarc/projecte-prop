package SearchTree;

import java.util.ArrayList;
import java.util.List;

public class Tree{

    private List<Node> sons;
    private static Node previous_node;
    private static int last_index;


    // Constructors
    private Tree() {
        sons = new ArrayList<>();
    }
    public Tree(int last_index) {
        sons = new ArrayList<>();
        Tree.last_index = last_index;
    }

    public int find(byte[] word, int digit) {
        Node aux = getNode(word[digit]);
        if (aux == null) return -1; // no encontrado
        if (digit == word.length - 1) // caso base
            return aux.getIndex();

        return aux.getSons().find(word, digit + 1);
    }

    public void insert(byte[] word, int digit, int index) {
        Node aux = getNode(word[digit]);
        if (aux == null) {
            if (digit == word.length - 1) {
                aux = new Node(word[digit], index, new Tree());
                sons.add(aux);
            }
            else {
                aux = new Node(word[digit], -1, new Tree());
                sons.add(aux);
                aux.getSons().insert(word, digit + 1, index);
            }
        }
        else if (digit != word.length - 1) {
            aux.getSons().insert(word, digit + 1, index);
        }
        else throw new IllegalArgumentException("La palabra ya estaba insertada con index = " + aux.getIndex());
    }


    public void reset() {
        previous_node = null;
        sons = new ArrayList<>();
    }

    public Node getNode(byte B) {
        for (Node son : sons) {
            if (son.areYou(B)) return son;
        }
        return null;
    }


    /**
     * @param B Byte identificador del nodo que se quiere buscar.
     * @param restart Indica si se quiere realizar la busqueda desde la raiz o si se desea continuar la busqueda desde donde se dejo la anterior.
     * @return Retorna el "index" del nodo buscado (con identificador B). Si este no existia, el nodo ha sido creado, e insertado con "index" = last_index;
     */
    public int progressive_find(byte B, boolean restart) {

        Node aux;
        if (!restart) aux = findNextByte(B);
        else aux = putNodeIfAbsent(B);

        int data = aux.getIndex();
        if (data == last_index) ++last_index;   // El nodo no exisitia, y ha sido insertado
        else previous_node = aux;               // El nodo  estaba presente y debemos seguir buscando
        return data;
    }

    private Node putNodeIfAbsent(byte id) {
        Node son = getNode(id);
        if (son == null) {
            son = new Node(id, last_index, new Tree());
            sons.add(son);
        }
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




















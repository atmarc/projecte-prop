import java.util.ArrayList;
import java.util.List;

public class Tree {

    private List<Node> sons;
    private static Node last_visited_node;
    private static int next_index;


    /**
     * Inicializa el arbol con una lista de hijos vacia sin cambiar ninguna variable estatica (last_visited_node, next_index).
     */
    private Tree() {
        sons = new ArrayList<>();
    }

    /**
     * Inicializa el arbol con una lista de hijos vacia cambiando la variable estatica next_index.
     * @param next_index Indice que se quiere establecer como next_index en todos los arboles.
     */
    public Tree(int next_index) {
        sons = new ArrayList<>();
        Tree.next_index = next_index;
    }

    /**
     * Busca recursivamente una palabra (cadena de bytes) en el arbol de busqueda.
     * @param word Cadena de bytes a buscar.
     * @return Retorna el "index" del nodo buscado o -1 si este no existia.
     */
    public int find(byte[] word) {
        return find(word, 0);
    }

    /**
     * Funcion recursiva sobre la que se relizan las busquedas comprobando byte a byte.
     * @param word Cadena de bytes a buscar.
     * @param digit Indice sobre la cadena word que se inspecciona en cada llamada.
     * @return Retorna, al final de la recursion, el "index" del nodo buscado o -1 si este no existia.
     */
    private int find(byte[] word, int digit) {
        Node aux = getNode(word[digit]);
        if (aux == null) return -1; // no encontrado
        if (digit == word.length - 1) // caso base
            return aux.getIndex();

        return aux.getSons().find(word, digit + 1);
    }


    /**
     * Inserta una palabra (cadena de bytes) en el arbol de busqueda.
     * 
     *  Precondicion: La palabra word no esta presente en el arbol de busqueda.
     *  Postcondicion: La palabra word ha sido insertada con index = index.
     * 
     * @param word Cadena de bytes a insertar.
     * @param index Index que se quiere guardar como dato asociado a la palabra word.
     */
    public void insert(byte[] word, int index) {
        insert(word, 0, index);
    }


    /**
     * Inserta recursivamente (byte a byte) una palabra (cadena de bytes) en el arbol de busqueda.
     * 
     * @param word Cadena de bytes a insertar.
     * @param digit Indice sobre la palabra word que indica el byte que se debe insertar en esta llamada.
     * @param index Index que se quiere guardar como dato asociado a la palabra word.
     */
    private void insert(byte[] word, int digit, int index) {
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

    /**
     * Restaura el arbol de busqueda a su estado inicial, vacio.
     */
    public void clear() {
        last_visited_node = null;
        sons = new ArrayList<>();
    }

    /**
     * Funcion que busca y retorna el nodo con id = B si existe. Retorna null en caso contrario.
     * 
     * @param B Byte identificador (id) del nodo que se quiere buscar.
     * @return Retorna el nodo con id = B si existe, o null en caso contrario.
     */
    public Node getNode(byte B) {
        for (Node son : sons) {
            if (son.areYou(B)) return son;
        }
        return null;
    }
    
    /**
     * El funcionamiento varia segun el valor de restart:
     * - Si restart == true: La busqueda se lleva a cabo sobre los hijos de la raiz del arbol.
     * - Si restart == false: La busqueda se lleva a cabo sobre los hijos del ultimo nodo visitado.
     *
     * Se busca un nodo con id = B.
     * - Si este existe se retorna su contenido index.
     * - Si este no existe, se crea e inserta un nodo con id = B, index = next_index y una lista de hijos vacia; y se retorna index.
     *
     * @param B Byte identificador (id) del nodo que se quiere buscar.
     * @param restart Indica si se debe realizar la busqueda sobre los hijos de la raiz del arbol (true) o obre los hijos del ultimo nodo visitado (false).
     * @return Retorna el "index" del nodo buscado. Si este no existia, index = next_index.
     */
    public int progressive_find(byte B, boolean restart) {

        Node aux;
        if (!restart) aux = findNextByte(B);
        else aux = putNodeIfAbsent(B);

        int index = aux.getIndex();
        if (index == next_index) ++next_index;   // El nodo no exisitia, y ha sido insertado
        else last_visited_node = aux;               // El nodo  estaba presente y debemos seguir buscando
        return index;
    }

    /**
     * Busca el nodo con id = B.
     *  Si existe: retorna el nodo.
     *  Si no existe: crea un nodo con id = B, index = next_index y un arbol vacio; y lo inserta en la lista de hijos
     * 
     * @param B Byte identificador (id) del nodo que se quiere buscar/insertar.
     * @return Retorna el nodo con id = B de la lista de hijos.
     */
    private Node putNodeIfAbsent(byte B) {
        Node son = getNode(B);
        if (son == null) {
            son = new Node(B, next_index, new Tree());
            sons.add(son);
        }
        return son;
    }


    /**
     * Funcion auxiliar de la funcion "progressive_find".
     *  Se busca un nodo con id = B entre los hijos de last_visited_node.
     *  - Si este existe se retorna su contenido index.
     *  - Si este no existe, se crea e inserta un nodo con id = B, index = next_index y una lista de hijos vacia; y se retorna index.
     *  
     *  Precondicion: Haber realizado una busqueda almenos una vez, para que last_visited_node haga referencia a algun nodo.
     *  Postcondicion: El Nodo buscado pasa a ser el ultimo nodo visitado (last_visited_node).
     *  
     * @param B Byte identificador (id) del nodo que se quiere buscar.
     * @return Nodo buscado entre los hijos de last_visited_node con id = B.
     */
    private Node findNextByte(byte B) {
        
        last_visited_node = last_visited_node.getSons().putNodeIfAbsent(B);
        return last_visited_node;
    }
}




















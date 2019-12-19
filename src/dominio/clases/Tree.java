package dominio.clases;
import java.util.ArrayList;
import java.util.List;

/*!
 *  \brief     Arbol de busqueda utilizado para agilizar la compresion en los algoritmos LZ-78 y LZ-W.
 *  \details   Cada arbol contiene su informacion propia (tratamiento a nivel de nodo) + la informacion de sus hijos (tratamiento a nivel de arbol).
 *  \author    Edgar Perez
 */
public class Tree {

    // DATOS NIVEL NODO
    private byte id;                        ///< Byte identificador del nodo del arbol.
    private int index;                      ///< Indice referente a los diccionarios de los algoritmos de compresion guardado en el nodo del arbol.

    // DATOS NIVEL ARBOL
    private List<Tree> sons;                ///< Lista de arboles hijos.
    private static Tree last_visited_node;  ///< Referencia al ultimo nodo/arbol visitado (utilizado para hacer busquedas byte a byte sin tener que recorrer el arbol completo).
    private static int next_index;          ///< Siguiente indice que se debe asignar a un nuevo nodo/arbol (Dado que los algoritmos asignan indices de forma creciente y continua, con esta variable se mantiene en todo momento constancia de cual es el siguiente que se debe asignar.).


    // CONSTRUCTORAS

    /**
     * Constructora que inicializa el arbol con una lista de hijos vacia sin cambiar ninguna variable estatica (last_visited_node, next_index).
     */
    private Tree() {
        sons = new ArrayList<>();
        index = -1;
    }

    /**
     * Constructora que inicializa el arbol con una lista de hijos vacia cambiando la variable estatica next_index.
     * @param next_index Indice que se quiere establecer como next_index en todos los arboles.
     */
    public Tree(int next_index) {
        index = -1;
        sons = new ArrayList<>();
        Tree.next_index = next_index;
    }

    /**
     * Constructora que inicializa el arbol con una lista de hijos vacia y asignando los valores de nodo/arbol.
     * @param id Byte identificador del nodo del arbol.
     * @param index Indice referente a los diccionarios de los algoritmos de compresion guardado en el nodo del arbol.
     */
    public Tree(byte id, int index) {
        this.index = index;
        this.id = id;
        sons = new ArrayList<>();
    }

    /**
     * Constructora que inicializa el arbol con una lista de hijos vacia y asignando los valores de nodo/arbol y ademas cambia la variable estatica next_index.
     * @param id Byte identificador del nodo del arbol.
     * @param index Indice referente a los diccionarios de los algoritmos de compresion guardado en el nodo del arbol.
     * @param next_index Indice que se quiere establecer como next_index en todos los arboles.
     */
    public Tree(byte id, int index, int next_index) {
        this.index = index;
        this.id = id;
        sons = new ArrayList<>();
        Tree.next_index = next_index;
    }


    // CONSULTORAS

    /**
     * Getter de la variable id.
     * @return Retorna el valor del identificador del nodo/arbol: id.
     */
    public byte getId() {
        return id;
    }

    /**
     * Setter de la variable id.
     * @param id Valor que se quiere asignar a la variable id.
     */
    public void setId(byte id) {
        this.id = id;
    }

    /**
     * Funcion que retorna si el nodo/arbol tiene como identificador el parametro id y ademas contiene un indice valido (esta "presente").
     * @param id Byte que deseas comprobar si es su identificador o no.
     * @return True si el parametro id se corresponde con su identificador y ademas su indice es valido (index >= 0). False en caso contrario.
     */
    public boolean areYou(byte id) {
        return (this.index > -1 && this.id == id);
    }

    /**
     * Getter de la variable index.
     * @return Retorna el valor del indice que guarda el nodo/arbol.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Getter de la variable sons (Arbol que contiene a sus hijos).
     * @return Retorna el la lista de hijos del nodo/arbol.
     */
    public List<Tree> getSons() {
        return sons;
    }

    /**
     * Funcion que busca y retorna el arbol con id = B si existe. Retorna null en caso contrario.
     *
     * @param B Byte identificador (id) del arbol que se quiere buscar.
     * @return Retorna el arbol con id = B si existe, o null en caso contrario.
     */
    private Tree getTree(byte B) {
        for (Tree son : sons) {
            if (son.areYou(B)) return son;
        }
        return null;
    }


    // MODIFICADORAS

    /**
     * Setter de la variable index.
     * @param index Valor que se quiere asignar a la variable index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Setter de la variable sons.
     * @param sons Lista de hijos que se quiere asignar a la variable sons.
     */
    public void setSons(List<Tree> sons) {
        this.sons = sons;
    }

    /**
     * Restaura el arbol de busqueda a su estado inicial, vacio.
     */
    public void clear() {
        last_visited_node = null;
        sons = new ArrayList<>();
    }


    // BUSQUEDA / INSERCION

    /**
     * Busca recursivamente una palabra (cadena de bytes) en el arbol de busqueda.
     * @param word Cadena de bytes a buscar.
     * @return Retorna el "index" del nodo/arbol buscado o -1 si este no existia.
     */
    public int find(byte[] word) {
        return find(word, 0);
    }

    /**
     * Funcion recursiva sobre la que se relizan las busquedas comprobando byte a byte.
     * @param word Cadena de bytes a buscar.
     * @param digit Indice sobre la cadena word que se inspecciona en cada llamada.
     * @return Retorna, al final de la recursion, el "index" del nodo/arbol buscado o -1 si este no existia.
     */
    private int find(byte[] word, int digit) {
        Tree aux = getTree(word[digit]);
        if (aux == null) return -1; // no encontrado
        if (digit == word.length - 1) // caso base
            return aux.getIndex();

        return aux.find(word, digit + 1);
    }

    /**
     * El funcionamiento varia segun el valor de restart:
     * - <b>Si restart == true:</b> La busqueda se lleva a cabo sobre los hijos de la raiz del arbol.
     * - <b>Si restart == false:</b> La busqueda se lleva a cabo sobre los hijos del ultimo nodo/arbol visitado.
     *
     * Se busca un nodo/arbol con id = B.
     * - Si este existe se retorna su contenido index.
     * - Si este no existe, se crea e inserta un nodo/arbol con id = B, index = next_index y una lista de hijos vacia; y se retorna index.
     *
     * @param B Byte identificador (id) del nodo/arbol que se quiere buscar.
     * @param restart Indica si se debe realizar la busqueda sobre los hijos de la raiz del arbol (true) o obre los hijos del ultimo nodo/arbol visitado (false).
     * @return Retorna el "index" del nodo/arbol buscado. Si este no existia, index = next_index.
     */
    public int progressive_find(byte B, boolean restart) {

        Tree aux;
        if (!restart) aux = findNextByte(B);
        else aux = putTreeIfAbsent(B);

        int index = aux.getIndex();
        if (index == next_index) ++next_index;      // El nodo no exisitia, y ha sido insertado
        else last_visited_node = aux;               // El nodo  estaba presente y debemos seguir buscando
        return index;
    }

    /**
     * Funcion auxiliar de la funcion "progressive_find".
     *  Se busca un nodo/arbol con id = B entre los hijos de last_visited_node.
     *  - Si este existe se retorna su contenido index.
     *  - Si este no existe, se crea e inserta un nodo/arbol con id = B, index = next_index y una lista de hijos vacia; y se retorna index.
     *  
     *  Precondicion: Haber realizado una busqueda almenos una vez, para que last_visited_node haga referencia a algun nodo/arbol.
     *  Postcondicion: El Nodo/arbol buscado pasa a ser el ultimo nodo/arbol visitado (last_visited_node).
     *  
     * @param B Byte identificador (id) del nodo/arbol que se quiere buscar.
     * @return Nodo/arbol buscado entre los hijos de last_visited_node con id = B.
     */
    private Tree findNextByte(byte B) {
        
        last_visited_node = last_visited_node.putTreeIfAbsent(B);
        return last_visited_node;
    }

    /**
     * Busca el nodo/arbol con id = B.
     *  Si existe: retorna el nodo/arbol.
     *  Si no existe: crea un nodo/arbol con id = B, index = next_index y un arbol vacio; y lo inserta en la lista de hijos
     *
     * @param B Byte identificador (id) del nodo/arbol que se quiere buscar/insertar.
     * @return Retorna el nodo/arbol con id = B de la lista de hijos.
     */
    private Tree putTreeIfAbsent(byte B) {
        Tree son = getTree(B);
        if (son == null) {
            son = new Tree(B, next_index);
            sons.add(son);
        }
        return son;
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
        Tree aux = getTree(word[digit]);
        if (aux == null) {
            if (digit == word.length - 1) {
                aux = new Tree(word[digit], index);
                sons.add(aux);
            }
            else {
                aux = new Tree(word[digit], -1);
                sons.add(aux);
                aux.insert(word, digit + 1, index);
            }
        }
        else if (digit != word.length - 1) {
            aux.insert(word, digit + 1, index);
        }
        else throw new IllegalArgumentException("La palabra ya estaba insertada con index = " + aux.getIndex());
    }
}




















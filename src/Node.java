/*!
 *  \brief     Clase contenedora de datos y sub arboles para la clase Tree.
 *  \details
 *  \author    Edagr Perez
 */
public class Node {
    private byte id;
    private int index;
    private Tree sons;

    /**
     * Constructora de la clase nodo. Crea un nodo con id = id, index = index y sons = sons.
     * @param id Valor que se quiere asignar a la variable id.
     * @param index Valor que se quiere asignar a la variable index.
     * @param sons Arbol de hijos que se quiere asignar a la variable sons.
     */
    public Node(byte id, int index, Tree sons) {
        this.id = id;
        this.index = index;
        this.sons = sons;
    }

    /**
     * Getter de la variable id.
     * @return Retorna el valor del identificador del nodo: id.
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
     * Funcion que retorna si el nodo tiene como identficador el parametro id y ademas contiene un indice valido (esta "presente").
     * @param id Byte que deseas comprobar si es su identificador o no.
     * @return True si el parametro id se corresponde con su identificador y ademas su indice es valido (index >= 0). False en caso contrario.
     */
    public boolean areYou(byte id) {
        return (this.index > -1 && this.id == id);
    }

    /**
     * Getter de la variable index.
     * @return Retorna el valor del indice que guarda el nodo: index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Setter de la variable index.
     * @param index Valor que se quiere asignar a la variable index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Getter de la variable sons (Arbol que contiene a sus hijos).
     * @return Retorna el arbol de hijos del nodo: sons.
     */
    public Tree getSons() {
        return sons;
    }

    /**
     * Setter de la variable sons.
     * @param sons Arbol de hijos que se quiere asignar a la variable sons.
     */
    public void setSons(Tree sons) {
        this.sons = sons;
    }

}

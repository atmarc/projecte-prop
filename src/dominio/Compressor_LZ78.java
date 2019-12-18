package dominio;
import java.util.ArrayList;
/*!
 *  \brief     Extension de la clase Compressor mediante el algoritmo LZ-78.
 *  \details
 * Para comprimir mediante el algoritmo LZ78 vamos generando un diccionario de pares (vease la clase Pair) guardado en la
 * variable comp_file. Esta implementacion se ha llevado a cabo con un arbol de busqueda que se va generando durante la
 * compresion (vease la clase Tree). Desde la funcion compress(), vamos haciendo lecturas byte a byte del archivo comprimido.
 * Para cada byte leido se realiza la compresion del mismo. Dependiendo de la situacion del compresor y la disposicion de los
 * bytes del fichero original, el algoritmo puede presentar 2 estados diferentes:
 *
 *  - <b>Estado de repeticion:</b>
 * A cada byte leido, si la cadena formada por los anteriormente leidos (y no insertados) con el actual esta presente en
 * el arbol de busqueda, se solicita otro byte mas para aumentar la cadena considerada.
 *
 *  - <b>Estado de nueva aparicion:</b>
 * Repetido el estado anterior suficientes veces, eventualmente llegaremos a una situacion en la que la cadena de bytes
 * considerada no esta presente en el arbol (no habia sido vista antes). Cuando sucede esto, se crea un nuevo par, formado
 * por el indice que referencia a la entrada en el diccionario que representa la cadena inspeccionada en la anterior iteracion
 * y un offset igual al ultimo byte leido. Este par es introducido en el diccionario, y la cadena equivalente se moestrara
 * presente en el arbol de busqueda.
 *
 * La variacion entre los dos estados la controlamos con el retorno booleano de la funcion compress. Mientras retorna false,
 * signifca que estan habiendo repeticiones. En el momento que la cadena inspeccionada no esta presente, realiza las inserciones
 * necesarias y retorna true, indicando que se debe resetear la busqueda en el arbol, de nuevo a la raiz, ya que mientras
 * hay repeticiones, va profundizando en el arbol (en mas detalle a continuacion).
 *
 * <b>Coste de las busquedas:</b>
 * Respecto al coste de las busquedas, el arbol utilizado (Tree) permite profundizar byte a byte en el, sin tener que
 * realizar una busqueda de una cadena completa cada vez. Debido al duncionamiento de este algoritmo, si estamos buscando
 * una cadena de N bytes, es porque previamente hemos buscado la cadena correspondiente a los primeros N-1 bytes y el
 * resultado de la busqueda ha sido positivo, asi que no tendria sentido volver a recorrer los N-1 niveles del arbol para
 * consultar si el siguiente byte esta presente. Por lo tanto, cada busqueda es constante, O(255).
 *
 *
 *  \author    Edgar Perez
 */
public class Compressor_LZ78 extends Compressor {

    public ArrayList<Pair> getComp_file() {
        return comp_file;
    }

    private ArrayList<Pair> comp_file;                  ///< Archivo sobre el que se escribe la compresion actual
    private int next_index;                             ///< write

    /*!
     *  \brief      Clase auxiliar para la implementacion del diccionario del compresor mediante el algortimo LZ-78.
     *  \details    Dado que la compresion se basa en pares de indice y valor, esta clase representa cada entrada del diccionario generado.
     *  \author     Edgar Perez
     */
    public static class Pair {

        public int index;   ///< Indice referente a su antecesor (es igual a 0 en caso de no tener antecesor).
        public byte offset; ///< Byte que concatenado con los bytes de todos sus antecesores, forma la cadena de bytes representada por una entrada de este tipo.

        /**
         * Constructora de la clase Pair dado el contenido de sus variables.
         * @pre -
         * @post Se ha creado un objeto de tipo Pair con index = i y offset = b.
         * @param i Indice que se desea asignar a la variable index.
         * @param b Byte que se desea asignar a la variable offset.
         */
        public Pair(int i, byte b) {
            index = i;
            offset = b;
        }

    }

    /**
     * Constructora de la clase compresor de tipo LZ78.
     * @pre -
     * @post Se ha creado un objeto Compresor_LZ78, con un comp_file nuevo con la primera entrada incializada a 0.
     */
    public Compressor_LZ78() {
        comp_file = new ArrayList<>();
        comp_file.add(new Pair(0, (byte) 0x00));
    }

    /**
     * Constructora de la clase compresor de tipo LZ78 con un parametro de entrada para incializar next_index.
     * @pre -
     * @post Se ha creado un objeto Compresor_LZ78, con un comp_file nuevo con la primera entrada incializada a 0 y con next_index = next_i.
     * @param next_i Valor con el que se inicializara la variable next_index del compresor.
     */
    public Compressor_LZ78(int next_i) {
        comp_file = new ArrayList<>();
        comp_file.add(new Pair(0, (byte) 0x00));
        next_index = next_i;
    }

    /**
     * @pre -
     * @post Se retorna el valor de la extension personal de los archivos comprimidos del compresor.
     * @return String que contiene la extension en formato ".algoritmo".
     */
    public String getExtension() {
        return ".lz78";
    }

    /**
     * Funcion encargada de controlar la compresion. Se comunica con la controladora del compresor, desde la que obtiene
     * la informacion para comprimir y va haciendo llamadas a la funcion compress(byte, Tree, boolean) para que comprima
     * cada byte leido. Una vez todo leido y comprimido, verifica que el algoritmo no haya quedado en estado de repeticion.
     * En caso de ser asi, anade un byte = 0 al final para que la ultima entrada quede registrada. Finalmente, llama a la
     * funcion write_compressed_file(), sobre la que delegara la escritura del archivo comprimido.
     *
     * @pre La controladora de compresion (Compression_Controller) existe.
     * @post El fichero inputFile de la controladora ha sido comprimido mediante el algoritmo LZ-78 y ha sido escrito en
     * el fichero outputFile de la controladora.
     */
    public void compress() {

        int B;
        Tree tree = new Tree(1);
        next_index = 0;
        boolean new_searching = true;

        while ((B = controller.readByte()) > 0)
            new_searching = compress((byte) (B & 0xFF), tree, new_searching);

        controller.closeReader();

        if (!new_searching) compress((byte) 0x00, tree, false);

        write_compressed_file();
    }

    /**
     *  @pre Si top_search = false, previamente ha habido llamadas a esta funcion, con el mismo tree y top_search = true;
     *  @post En cualquier caso, el arbol recuerda el nodo referente a este Byte como ultima visita. El comportamiento varia segun el valor del parametro top_search:
     *      - <b>top_search = true</b>-> El byte B esta insertado en el primer nivel del arbol (o bien ya lo estaba, o se ha
     *      realizado en esta instancia con indice = comp_file.size()-1).
     *
     *      - <b>top_search = false</b> -> El byte B esta insertado en el nivel iesimo del arbol, donde i = numero de bytes de
     *      la cadena en consideracion y por lo tanto tambien i = numero llamadas previas con top_search = false desde la
     *      ultima llamada con top_search = true. O bien ya estaba insertado, o se ha realizado en esta llamada con indice = comp_file.size()-1).
     *
     *
     * @param B Byte que se quiere comprimir.
     * @param tree Arbol de motes conocidos sobre el que se realizan las inserciones y busquedas.
     * @param top_search Indica si se desea reiniciar la busqueda desde arriba del arbol.
     *
     * @return Retorna el estado en el que se encuentra la compresion:
     *  - <b>true</b>  -> Se ha anadido un mote nuevo. La siguiente entrada empezara a buscar desde arriba en el arbol.
     *  - <b>false</b> -> El mote buscado existe. Se solicita otro byte para continuar buscando en la actual altura del arbol.
     */
    public boolean compress(byte B, Tree tree, boolean top_search) {

        int index = tree.progressive_find(B, top_search);

        if (index == comp_file.size()) {
            comp_file.add(new Pair(next_index, B));
            next_index = 0;
            return true;
        }

        next_index = index;
        return false;
    }

    /**
     * Escribe en el fichero de salida mediante la controladora del compresor el diccionario comprimido. Primero guarda
     * el tamano del diccionario a escribir en un entero (4 bytes) y despues, desde el principio del diccionario hasta el
     * final va guardando todos los pares indice-byte. Los indices, se van guardando progrsivamente con un numero de bytes
     * superior, conforme es necesario.
     *
     * Dado que todo indice siempre sera menor que la posicion que ocupa el par en el diccionario, podemos guiarnos por las
     * posiciones de las entradas en el diccionario. En este caso se diferencias 4 fases, donde cada fase utiliza un byte mas
     * que la anterior para guardar los indices, y logicamente, siempre un solo byte para guardar los offsets ya que estos
     * son bytes por definicion.
     *
     * Todas las escrituras son llevadas a cabo mediante la controladora del compresor, a la cual unicamente le envia la
     * cadena de bytes que debe escribir.
     *
     * @pre Comp_file no esta vacio.
     * @post Se ha escrito el tamano de y el contenido de comp_file a traves de la controladora del compresor.
     */
    private void write_compressed_file() {

        int i = 1;
        byte[] buffer = new byte[4];
        int index = comp_file.size();

        buffer[0] = ((byte) ((index & 0xFF000000) >> 24));
        buffer[1] = ((byte) ((index & 0x00FF0000) >> 16));
        buffer[2] = ((byte) ((index & 0x0000FF00) >> 8));
        buffer[3] = ((byte) (index & 0x000000FF));

        controller.writeBytes(buffer);

        buffer = new byte[2];
        for (; i < 128 && i < comp_file.size(); i++) { // 1 + 1 Byte
            buffer[0] = (byte) (comp_file.get(i).index & 0xFF);
            buffer[1] = comp_file.get(i).offset;
            controller.writeBytes(buffer);
        }
        buffer = new byte[3];
        for (; i < 32768 && i < comp_file.size(); i++) {    // 2 + 1 Byte
            index = comp_file.get(i).index;
            buffer[0] = ((byte) ((index & 0x0000FF00) >> 8));
            buffer[1] = ((byte) (index & 0x000000FF));
            buffer[2] = comp_file.get(i).offset;
            controller.writeBytes(buffer);
        }
        buffer = new byte[4];
        for (; i < 8388608 && i < comp_file.size(); i++) { // 3 + 1 Byte
            index = comp_file.get(i).index;
            buffer[0] = ((byte) ((index & 0x00FF0000) >> 16));
            buffer[1] = ((byte) ((index & 0x0000FF00) >> 8));
            buffer[2] = ((byte) (index & 0x000000FF));
            buffer[3] = comp_file.get(i).offset;
            controller.writeBytes(buffer);
        }
        buffer = new byte[5];
        for (; i < comp_file.size(); i++) {                 // 4 + 1 Byte
            index = comp_file.get(i).index;
            buffer[0] = ((byte) ((index & 0xFF000000) >> 24));
            buffer[1] = ((byte) ((index & 0x00FF0000) >> 16));
            buffer[2] = ((byte) ((index & 0x0000FF00) >> 8));
            buffer[3] = ((byte) (index & 0x000000FF));
            buffer[4] = comp_file.get(i).offset;
            controller.writeBytes(buffer);
        }
    }

}

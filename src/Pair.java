/*!
 *  \brief     Clase auxiliar para la implementacion del diccionario del compresor mediante el algortimo LZ-78.
 *  \details
 *  \author    Edgar Perez
 */
public class Pair {

    public int index;
    public byte offset;

    public Pair(int i, byte b) {
        index = i;
        offset = b;
    }

}

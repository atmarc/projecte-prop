package dominio.clases;
import java.math.BigInteger;
import java.util.ArrayList;

/*!
 *  \brief     Extension de la clase Decompressor mediante el algoritmo LZ-78.
 *  \details   Mediante la controladora, lee un archivo comprimido mediante el algoritmo LZ78 y lo descomprime, escribiendo el resultado en un archivo de salida también a través de la controladora. Para cada entrada recibida, de par índice-byte, es añadida al diccionario para futuras referencias a la misma a la vez que toda cadena descomprimida es añadida al fichero destino por orden de aparición.
 *  \author    Edgar Perez
 */
public class Decompressor_LZ78 extends Decompressor {

    public ArrayList<byte[]> getDictionary() {
        return dictionary;
    }
    public void setDictionary(ArrayList<byte[]> dictionary) {
        this.dictionary = dictionary;
    }
    ArrayList<byte[]> dictionary; ///< Archivo sobre el que se escribe la descompresión actual.

    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    int length; ///< Numero de entradas que contendra dictionary.

    public Decompressor_LZ78() {}

    /**
     * Función encargada de controlar la descompresión. A medida que va obteniendo datos, va decidiendo que cantidades leer en funcion de lo leido hasta el momento y va trantando las entradas recibidas, incluyendolas en el diccionario y escribiendolas directamente en el fichero de salida a traves de la controladora.
     * @pre El controlador del descompresor existe.
     * @post El archivo de entrada se ha descomprimido y ha sido escrito a traves de la controladora.
     */
    public void decompress() {

        byte[] singleByte = new byte[1], index = new byte[4];
        controller.readNBytes(index);

        int i = 1;
        length = new BigInteger(index).intValue();

        dictionary = new ArrayList<>(length);
        dictionary.add(null); // para empezar desde la posicion 1

        // 1 + 1 Byte
        index = new byte[1];
        for (; i < 128 && controller.readNBytes(index) >= 0 && controller.readNBytes(singleByte) >= 0; i++)
            controller.writeBytes(decompress(index, singleByte[0]));
        // 2 + 1 Byte
        index = new byte[2];
        for (; i < 32768 && controller.readNBytes(index) >= 0 && controller.readNBytes(singleByte) >= 0; i++)
            controller.writeBytes(decompress(index, singleByte[0]));
        // 3 + 1 Byte
        index = new byte[3];
        for (; i < 8388608 && controller.readNBytes(index) >= 0 && controller.readNBytes(singleByte) >= 0; i++)
            controller.writeBytes(decompress(index, singleByte[0]));
        // 4 + 1 Byte
        index = new byte[4];
        for (; controller.readNBytes(index) >= 0 && controller.readNBytes(singleByte) >= 0; i++)
            controller.writeBytes(decompress(index, singleByte[0]));

    }

    /**
     * Descomprime el par de entrada índice-byte que entra por parámetro.
     *
     * @pre El diccionario esta inicializado.
     * @post Retorna la cadena de bytes referente al par de entrada que ha recibido como parámetro. Además de haber incluido en el diccionario de la clase la referencia a la cadena retornada.
     *
     * @param indexB Indice de la entrada leida.
     * @param offset Byte offset de la entrada leida.
     * @return Retorna la cadena de bytes referente al par de entrada que ha recibido como parametro.
     */
    public byte[] decompress(byte[] indexB, byte offset) {

        int index = new BigInteger(indexB).intValue();

        byte[] word;

        if (index == 0) {
            word = new byte[1];
            word[0] = offset;
        }
        else if (dictionary.size() == length - 1 && offset == (byte) 0){
            word = dictionary.get(index);
        }
        else {
            byte[] prefix = dictionary.get(index);
            word = new byte[prefix.length + 1];
            System.arraycopy(prefix, 0, word, 0, prefix.length);
            word[prefix.length] = offset;
        }

        dictionary.add(word);
        return word;
    }

    public String getExtension() {
        return "_decompressed.txt";
    }

}

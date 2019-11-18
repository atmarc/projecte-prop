package dominio;
import java.util.ArrayList;

/**
 *  @brief     Extension de la clase Decompressor mediante el algoritmo LZ-W.
 *  @details   La clase que implementa la decompression de un fichero mediante el
 * 			   algoritmo LZW.
 *  @author    Andrei Mihalache
 */
public class Decompressor_LZW extends Decompressor {
    private static final int BYTE_SIZE = 8; ///< dimension de un byte
    private ArrayList<String> dictionary; ///< el diccionario de los patrones
    private int codewordSize; ///< la longitud en bits para escribir la codificación

    /**
     * @brief Crea un objecto decompressor con el diccionario básico.
     */
    public Decompressor_LZW() {
        inicializar();
    }

    /**
     * @brief Inicializa el diccionario del descompresor
     * @details Inserta todos los caracteres de ASCII extendido en el
     * diccionario del compressor. Esto forma el diccionario basico.
     * Ademas, establece la longitud de los codigos a leer a 16 bits.
     */
    private void inicializar() {
        dictionary = new ArrayList<>();
        for (char i = 0; i < 256; ++i) dictionary.add(String.valueOf(i));
        codewordSize =  16;
    }

    /**
     * @brief Retorna la extension de los ficheros descomprimidos
     * con este algoritmo. Se anade el sufijo "_decompressed" para
     * no sobrescribir el fichero original comprimido.
     * @return La extencion del fichero descomprimido
     */
    public String getExtension() {
        return "_decompressed.txt";
    }

    /**
     * @brief Descomprime un fichero codificado con el algoritmo LZW
     * @details La funcion lee todos los codigos escritos en el fichero
     * comprimido usando la controladora. Si el código leído está en el
     * diccionario entonces se escribe en la salida la cadena de caracteres
     * correspondiente al código leído y se añade al diccionario la cadena
     * resultante a la concatenación de la palabra correspondiente al penúltimo
     * código descomprimido y el primer carácter del la palabra actual, en otro
     * caso se añade al diccionario y se escribe en la salida la palabra
     * correspondiente a la concatenación de la palabra del penúltimo código
     * descomprimido y el primer carácter de la misma palabra.
     */
    public void decompress() {
        inicializar();
        byte[] codeword = new byte[codewordSize/BYTE_SIZE];
        controller.readNBytes(codeword);
        int index = getNextIndex(codeword);
        String pattern = dictionary.get(index);
        for (int i = 0; i < pattern.length(); ++i) controller.writeByte((byte)pattern.charAt(i));
        while (controller.readNBytes(codeword) != -1) {
            index = getNextIndex(codeword);
            String out = "";
            if (index < dictionary.size()) {
                out = dictionary.get(index);
            }
            else out = pattern + pattern.charAt(0);
            dictionary.add(pattern + out.charAt(0));
            for (int i = 0; i < out.length(); ++i) controller.writeByte((byte)out.charAt(i));
            pattern = out;
            if (dictionary.size() >= (1 << codewordSize)-1) {
                codewordSize += BYTE_SIZE;
                codeword = new byte[codewordSize/BYTE_SIZE];
            }
        }
    }

    /**
     * @brief Convierte un byte array en un int. Cada elemento del array representa un digito en la base 256.
     * @param codeword el byte array a convertir
     * @return un numero entero que representa el resultado de la conversion
     */
    private int getNextIndex(byte[] codeword) {
        int index = 0;
        for (byte b : codeword) {
            index = (index << BYTE_SIZE) | (b & 0xFF);
        }
        return index;
    }

}

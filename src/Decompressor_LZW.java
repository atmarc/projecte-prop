import java.io.*;
import java.util.ArrayList;

/*!
 *  \brief     Extension de la clase Decompressor mediante el algoritmo LZ-W.
 *  \details
 *  \author    Andrei Mihalache
 */
public class Decompressor_LZW extends Decompressor {
    private static final int BYTE_SIZE = 8;
    private ArrayList<String> dictionary;
    private int codewordSize;    // la longitud en bits para escribir la codificación

    public Decompressor_LZW() {
        inicializar();
    }

    /**
     * Inicializa el diccionario para la descompresion
     */
    private void inicializar() {
        dictionary = new ArrayList<>();
        for (char i = 0; i < 256; ++i) dictionary.add(String.valueOf(i));
        codewordSize =  16;
    }


    /**
     * @return La extencion del fichero descomprimido
     */
    protected String getExtension() {
        return "_decompressed.txt";
    }

    /**
     * Descomprime un fichero codificado con el algoritmo LZW
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
     * Convierte un byte array en un int. Cada elemento del array representa un digito en la base 256.
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

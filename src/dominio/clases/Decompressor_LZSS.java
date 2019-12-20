package dominio.clases;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.ArrayList;

/*!
 *  \brief      Extensión de la clase Decompressor mediante el algoritmo LZSS
 *  \details    Clase que realiza la decompresión de un texto comprimido mediante el algoritmo LZSS. La lectura del fichero comprimido
 *              consiste en leer byte a byte el texto comprimido y separarlo en 2 partes: la primera son los bytes
 *              que contienen los bits que representan coincidencia (1) o no coincidencia (0), y la segunda son los bytes
 *              que representan o bien un carácter no codificado o bien una coincidencia codificada, en que dos bytes
 *              consecutivos se tienen que interpretar como un short en que los 12 primeros bits representan un offset y los
 *              4 ultimos como desplazamiento. Sabremos cuando acaba la primera parte y empieza la segunda mediante un separador
 *              acordado con el compresor. El comportamiento una vez hayamos descodificado los bits, será guardar un subarray
 *              del texto comprimido a partir de donde acaban los bits hasta el final de este, y iterar para todos los bits.
 *              Si encontramos un 0, copiaremos en un array result (del mismo tamaño que el de bits) el byte correspondiente,
 *              ya que significará que ese byte se guardó descodificado. Si encontramos un 1, tendremos que descodificar
 *              los dos bytes siguientes y copiar los bytes que coinciden de result al final de este.
 *  \author    Nicolas Camerlynck
 */
public class Decompressor_LZSS extends Decompressor {
    /**
     * @pre
     * @post
     * Retorna la extension de los ficheros descomprimidos con esta clase
     * @return la extension de los ficheros descomprimidos
     */
    public String getExtension() {
        return "_decompressed.txt";
    }

    /**
     * Descomprime un fichero codificado con el algoritmo LZSS
     */
    public void decompress() {
        byte item[] = new byte[0];
        try {
            item = controller.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Boolean> bits = new ArrayList<>();

        byte first = item[0];
        int begin = -1;
        if (first != 0x01) {
            for (int i = 7; i >= 0; --i) {
                byte aux = (byte) (first >> i);
                aux = (byte) (aux & 0x01);

                if (aux == 0x01) {
                    begin = i;
                    break;
                }
            }

            for (int j = begin - 1; j >= 0; --j) {
                byte aux = (byte) (first >> j);
                aux = (byte) (aux & 0x01);

                if (aux == 0x00) {
                    bits.add(false);
                }
                else bits.add(true);
            }
        }
        int i = 1;
        for (; item[i] != -1 || item[i+1] != -1; ++i) {

            byte element = item[i];

            for (int j = 7; j >= 0; --j) {
                byte aux = (byte) (element >> j);
                aux = (byte) (aux & 0x01);

                if (aux == 0x00) {
                    bits.add(false);
                }
                else bits.add(true);
            }
        }
        i  += 2;

        byte[] chrs = Arrays.copyOfRange(item, i, item.length);

        int size = bits.size();

        byte[] result = new byte[size];
        int itchrs = 0;

        for (int j = 0; j < size; ++j) {
            if (!bits.get(j)) {
                result[j] = chrs[itchrs];
                itchrs++;
            }
            else {
                short a = mergeBytes(chrs[itchrs], chrs[itchrs + 1]);
                itchrs += 2;

                short offset = (short) (a >> 4);
                offset = (short) (offset & 0x0FFF);

                short d = (short) (a & 0x000F);

                int desp = d + 0;
                int point = j;

                for(int k = 0; k < desp; k++) {
                    byte b = result[point - offset + k];
                    result[j] = b;
                    j++;
                }
                j--;
            }
        }
        controller.writeBytes(result);
    }

    /**
     * Función que concatena dos bytes y retorna el short correspondiente, siendo el primero la parte alta del short y el
     * segundo la parte baja.
     * @param high
     * @param low
     * @return Un short en que los primeros 8 bits coinciden con el parámetro high y los ultimos 8 corresponden con el parámetro low
     */
    public short mergeBytes(byte high, byte low) {
        short h = (short) high;
        h = (short) (h << 8);
        h = (short) (h & 0xFF00);

        short l = (short) low;
        l = (short) (l & 0x00FF);

        short a = (short) (h | l);
        return a;
    }

}

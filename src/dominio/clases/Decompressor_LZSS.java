package dominio.clases;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.ArrayList;

/*!
 *  \brief     Clase que realiza la decompresión de un texto comprimido mediante el algoritmo LZSS. El comportamiento
 *             consiste en leer byte a byte el texto comprimido y separarlo en 3 partes: la primera son los bytes
 *             que contienen los bits que representan coincidencia (1) o no coincidencia (0). La segunda parte son
 *             los bytes que corresponden a los carácteres con los que no hemos encontrado coincidencia. Y la tercera
 *             son los carácteres codificados con 12 bits de offset y 4 de desplazamiento. Para diferenciar las partes
 *             usamos como separador dos bytes con valor FF. Una vez hayamos recogido las partes y las hayamos puesto
 *             en colas, iteraremos por cada elemento de la cola de booleanos (que corresponde a la primera parte).
 *             Si en la iteración actual encontramos un valor false (0), miraremos el elemento top de la cola de no
 *             coincidencias y la escribiremos en un array de resultado. Si encontramos un valor true (1), miraremos el
 *             elemento top de la cola de coincidencias y descodificaremos el elemento, cogiendo los 12 bits de mas peso
 *             como offset y los 4 de menos peso como desplazamiento, y buscaremos los carácteres correspondientes en el
 *             array de resultado.
 *  \details
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
                char a = convert(chrs[itchrs], chrs[itchrs + 1]);
                itchrs += 2;

                char offset = (char) (a >> 4);
                offset = (char) (offset & 0x0FFF);

                char d = (char) (a & 0x000F);

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

    public char convert(byte high, byte low) {
        char h = (char) high;
        h = (char) (h << 8);
        h = (char) (h & 0xFF00);

        char l = (char) low;
        l = (char) (l & 0x00FF);

        char a = (char) (h | l);
        return a;
    }

    public byte[] readAllBytes() {
        byte[] b = new byte[0];
        try {
            b = Files.readAllBytes(Paths.get("testing_files/nicompressed.lzss"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    public void writeBytes(byte[] bytes) {
        Path p = Paths.get("testing_files/nico1.txt");
        try {
            Files.write(p, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

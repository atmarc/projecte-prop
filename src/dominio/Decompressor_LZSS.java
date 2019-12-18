package dominio;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
        //writeBytes(item);
        /*System.out.println();
        System.out.print("Array comprimit: ");
        printByteArray(item);
        System.out.println();

         */

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

        /*System.out.print("Bits al decompress: ");
        printBitArray(arrListBoolToArray(bits));
        System.out.println();*/

        byte[] chrs = Arrays.copyOfRange(item, i, item.length);
/*
        System.out.print("Codificat al decompress: ");
        printByteArray(chrs);

 */

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
        /*
        System.out.println();

        System.out.print("Final: ");
        printByteArray(result);
        System.out.println();
        System.out.println();

         */

        writeBytes(result);

        //boolean[] bits = bitQtoBitSet(bitQ);
        /*printBitArray(bits);


        byte[] chars = Arrays.copyOfRange(item, i, item.length);
        byte[] resultat = new byte[bits.length];
        int l = 0;

        //escrivim el resultat
        for (int h = 0; h < bits.length; ++h) {
            if (bits[h]) {
                byte high = chars[l];
                byte low = chars[l+1];
                char a =  decrypt(high, low);

                char offset = (char) (a >> 4);
                offset = (char) (offset & 0x0FFF);
                char d = (char) (a & 0x000F);
                int desp = d + 0;
                for(int j = 0; j < desp; j++) {
                    byte b = resultat[offset + j];
                    resultat[h] = b;
                }
                h += desp;
                l++;
            }
            else {
                resultat[h] = chars[l];
            }
            l++;
        }

        System.out.println(resultat);

        //writeBytes(resultat);*/
    }

    /**
     * Pasa de array list a array
     * @pre a es un arrayList de bytes no vacío
     * @post El array aux de retorno contiene los mismos elementos que el arrayList a.
     * @param a ArrayList de Bytes
     * @return Un Array de Bytes con los mismos elementos que la Arraylist, para usar el método writeBytes
     */
    private byte[] arrListToArray (ArrayList<Byte> a) {

        int size = a.size();
        byte[] aux = new byte[size];

        for (int i = 0; i < size; ++i) {
            aux[i] = a.get(i);
        }

        return aux;
    }

    private boolean[] arrListBoolToArray (ArrayList<Boolean> a) {

        int size = a.size();
        boolean[] aux = new boolean[size];

        for (int i = 0; i < size; ++i) {
            aux[i] = a.get(i);
        }

        return aux;
    }

    public boolean[] bitQtoBitSet (Queue<Boolean> a) {

        boolean[] bits = new boolean[a.size()];
        int i = 0;
        boolean aux;
        while (!a.isEmpty()) {
            aux = a.remove();
            if (aux) {
                bits[i] = true;
            }
            else {
                bits[i] = false;
            }
            i++;
        }

        return bits;
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

    public void printBitArray (boolean[] a) {

        int j = a.length;

        for (int i = 0; i < j; ++i) {
            if(a[i]) {
                System.out.print("1");
            }
            else System.out.print("0");
        }

    }

    public void printByteArray (byte[] a) {

        int j = a.length;

        for (int i = 0; i < j; ++i) {
            System.out.print(a[i]);
            System.out.print(" ");
        }

    }

}

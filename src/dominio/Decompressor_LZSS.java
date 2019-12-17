package dominio;
import java.io.IOException;
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
    public void decompress() throws Exception {
        byte item[] = controller.readAllBytes();

        Queue<Byte> noCoincQ = new LinkedList<>();
        Queue<Character> coincQ = new LinkedList<>();
        Queue<Boolean> bitQ = new LinkedList<>();

        //primer llegim els bits
        //bits d'aliniament
        boolean start = false;
        boolean endstart = false;
        int i = 7;
        while (!start) {
            if (item[0] == 0x01) {
                start = true;
                endstart = true;
            }
            else {
                byte aux = (byte) (item[0] >> i);
                aux = (byte) (aux & 0x01);
                if (aux == 1) start = true;
                i--;
            }
        }
        while(i >= 0 && !endstart) {
            byte aux = (byte) (item[0] >> i);
            aux = (byte) (aux & 0x01);
            if (aux == 1) bitQ.add(true);
            else bitQ.add(false);
            i--;
        }


        boolean endbBits = false;
        i = 1;
        while(!endbBits) {
            if (item[i] == -1 && item[i+1] == -1) {
                endbBits = true;
                i++;
            }
            else {
                byte aux = item[i];

                for (int j = 0; j < 8; j++) {
                    byte bit = (byte) (aux >> (7-j));
                    bit = (byte) (bit & 0x01);
                    if (bit == 1) bitQ.add(true);
                    else bitQ.add(false);
                }
            }
            i++;
        }
        boolean endNoCoinc = false;

        while(!endNoCoinc) {
            if (item[i] == -1 && item[i+1] == -1) {
                endNoCoinc = true;
                i++;
            }
            else {
                byte aux = item[i];
                noCoincQ.add(aux);
            }
            i++;
        }


        while(i < item.length) {
            char a = (char) item[i];
            a = (char) (a << 8);
            a = (char) (a & 0xFF00);
            char b = (char) item[i+1];
            b = (char) (b & 0x00FF);
            a = (char) (a | b);
            coincQ.add(a);
            i += 2;
        }

        ArrayList<Byte> result = new ArrayList<>();

        /*byte a1[] = byteQtoByteArray(noCoincQ);
        byte b1[] = charQtoByteArray(coincQ);
        byte c1[] = boolQtoByteArray(bitQ);

        System.out.println(a1);
        System.out.println(b1);
        System.out.println(c1);*/

        //int i = 0;

        while (!bitQ.isEmpty()) {

            boolean aux = bitQ.remove();
            if (!aux) {
                result.add(noCoincQ.remove());

            }
            else {
                char a = coincQ.remove();
                char offset = (char) (a >> 4);
                offset = (char) (offset & 0x0FFF);
                char d = (char) (a & 0x000F);
                int desp = d + 3;
                for(int j = 0; j < desp; j++) {
                    byte b = result.get(result.size() - offset);
                    result.add(b);
                }
                for(int j = 0; j < desp - 1; j++) {

                    bitQ.remove();
                }

            }
        }

        byte[] res = arrListToArray(result);
        controller.writeBytes(res);
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

}

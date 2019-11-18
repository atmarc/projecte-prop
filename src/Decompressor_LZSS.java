import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*!
 *  \brief     Extension de la clase Decompressor mediante el algoritmo LZ-SS.
 *  \details
 *  \author    Nicolas Camerlynck
 */
public class Decompressor_LZSS extends Decompressor {

    /**
     * @return La extension del fichero descomprimido
     */
    protected String getExtension() {
        return "_decompressed.txt";
    }

    /**
     * Descomprime un fichero codificado con el algoritmo LZSS
     */
    protected void decompress() {
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

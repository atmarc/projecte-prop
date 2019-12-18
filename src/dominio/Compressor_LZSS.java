package dominio;
import java.util.*;

/*!
 *  \brief     Clase que realiza la compresión del texto. El comportamiento consiste en acceder iterativamente a cada
 *             carácter del texto original. Para cada carácter, mirará en un search Buffer (que sera una copia de los
 *             carácteres encontrados hasta el momento) si encuentra coincidencias con el carácter actual y, como
 *             mínimo, con los 2 carácteres siguientes. También añadiremos a cada iteración el carácter al search Buffer
 *             para mantenerlo actualizado. Si encontramos coincidencia guardaremos el offset hasta la posición en que hay
 *             la coincidencia mas larga (con el numero de carácteres seguidos máximo) y el desplazamiento (el numero de
 *             carácteres). En caso que no encontremos coincidencia, guararemos el carácter. Paralelamente guardaremos
 *             en una cola de booleanos un valor true si hay coincidencia y un valor false si no la hay. Para acabar,
 *             escribiremos la cola de booleanos en forma de bits agrupados en bytes, seguido de los elementos que no
 *             coinciden y las coincidencias codificadas en chars (los 12 bits de mas peso offset y los 4 de menos peso
 *             de desplazamiento).
 *  \details
 *  \author    Nicolas Camerlynck
 */
public class Compressor_LZSS extends Compressor {


    private HashMap<Byte, ArrayList<Integer>> searchB2 = new HashMap<>();
    private ArrayList<Pair<Integer,Byte>> act = new ArrayList<>();

    /**
     * @pre
     * @post
     * Retorna la extension de los ficheros comprimidos con esta clase
     * @return la extension de los ficheros comprimidos
     */
    public String getExtension() {
        return ".lzss";
    }

    /**
     * Comprime un fichero mediante el algoritmo LZSS
     */
    public void compress () {

        byte[] itemb = new byte[0];
        try {
            itemb = controller.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Byte> result = new ArrayList<>();

        boolean[] bits = new boolean[itemb.length];
        for (int i = 0; i < itemb.length; i++) {
            short chr = -1;

            if (searchB2.containsKey(itemb[i])) { //hi ha una entrada amb la lletra que avaluem
                chr = coin4(itemb, i);
            }
            else { //no hi ha entrada, l'afegim i posem al set la posicio en la q estem
                ArrayList<Integer> a = new ArrayList<>();
                a.add(i);
                searchB2.put(itemb[i], a);
                act.add(new Pair(i, itemb[i]));
                //4501 n

            }

            if (chr == -1) { //hem afegit una entrada nova
                result.add(itemb[i]);
            }
            else if (chr == 0x0000) {

                result.add(itemb[i]);
                searchB2.get(itemb[i]).add(i);

                act.add(new Pair(i, itemb[i]));
            }
            else {
                //mirem la posicio de l'offset per posar el bit a true

                short aux1 = (short) (chr >> 4);
                aux1 = (short) (aux1 & 0x0FFF);
                bits[i] = true;

                byte aux2 = (byte) (chr >> 8);
                result.add(aux2);

                result.add( (byte) (chr));

                short despl = (short) (chr & 0x000F);
                int desp = (int) despl;

                for (int g = 0; g < desp; ++g) {
                    searchB2.get(itemb[i + g]).add(i + g);

                    act.add(new Pair(i+g, itemb[i+g]));
                }

                i = i + desp - 1;

            }

            actualitzar(i);
        }
        byte[] separador = new byte[2];
        separador[0] = -1;
        separador[1] = -1;

        byte[] aaux = mergeArrays(BitsetToByteArray(bits), separador);
        controller.writeBytes(mergeArrays(aaux, arrayListToArray(result)));
    }

    public short coin4 (byte[] itemb, int p) {
        byte first = itemb[p];
        ArrayList<Integer> poss = searchB2.get(first);
        int maxdesp = 0;
        int offset = -1;
        int auxP = p;

        for (int i : poss) {
            int desp = 1;
            while (p + desp < itemb.length && desp < 15) {
                if (itemb[i+desp] == itemb[p + desp]) {
                    desp++;
                }
                else break;
            }
            if (desp > maxdesp) {
                maxdesp = desp;
                offset = i;
            }
        }
        return encrypt(maxdesp, offset, p);
    }

    public Short encrypt(int maxdesp, int maxpos, int p) {
        if (maxdesp < 3) return 0x0000; // TODO: s'afegeix dos cops al search B
        else {

            short os = (short) (p - maxpos);
            os = (short) (os << 4);
            os = (short) (os & 0xFFF0);

            short d = (short) maxdesp;
            d = (short) (d & 0x000F);

            short res = (short) (os | d);

            return res;
        }
    }

    public byte[] BitsetToByteArray(boolean[] a) {
        int auxj = 0;

        //System.out.println(a.length);
        int mida = (a.length / 8) + 1;
        //System.out.println(mida);
        int modA = a.length % 8;
        byte b[] = new byte[mida];
        byte c = 0;
        int i = 0;
        if (modA == 0) {
            b[i] = 1;
        }
        else {
            int nbits = 8 - modA;
            c = 1;

            for(int j=0; j<modA; ++j) {
                boolean aux = a[j];
                c  = (byte) (c << 1);
                if (aux) {
                    c = (byte) (c + 1);
                }
            }
            b[i] = c;
        }
        auxj = modA;
        i++;
        c = 0;
        //System.out.println(a.length);
        //System.out.println(auxj);
        while (auxj != a.length) {
            for (int h=0; h < 8; ++h) {
                boolean aux = a[auxj];
                c  = (byte) (c << 1);
                if (aux) {
                    c = (byte) (c + 1);
                }
                auxj++;
            }
            b[i] = c;
            i++;
        }

        return b;
    }

    public byte[] arrayListToArray (ArrayList<Byte> a) {
        byte[] res = new byte[a.size()];

        for (int i = 0; i < a.size(); ++i) {
            res[i] = a.get(i);
        }

        return res;
    }

    public byte[] mergeArrays(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        byte[] c = new byte[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public void actualitzar(int pos) {
        while (act.size() > 4095) {
            Pair<Integer, Byte> aux = act.get(0);
            int bor = aux.getL();
            byte Bybor = aux.getR();
            act.remove(0);

            if (searchB2.get(Bybor).size() > 1) {
                searchB2.get(Bybor).remove(Integer.valueOf(bor));
            }

            else {
                searchB2.remove(Bybor);
            }
        }
    }

}

package dominio.clases;
import java.util.*;

/*!
 *  \brief      Extensión de la clase Compressor mediante el algoritmo LZSS
 *  \details    Clase que realiza la compresión del texto. El comportamiento consiste en acceder iterativamente a cada
 *              byte del texto original. Para cada byte, mirará si en las últimas 4095 posiciones (o las posiciones que llevemos
 *              si aún no hemos llegado a 4095) hay alguna aparición previa de ese byte. Si es el caso, mirará si por los
 *              siguientes bytes también hay coincidencia en las siguientes posiciones a la aparición del primer byte. Si
 *              encuentra 3 o más coincidencias seguidas, codifica un short en que los 12 primeros bits representan el offset
 *              (el desplazamiento des de la posición que estamos iterando hasta la primera letra de la coincidencia) máximo
 *              encontrado, y en que los últimos 4 bits representan el desplazamiento (el numero de bytes que coinciden). En
 *              caso que no encontremos coincidencia o esta sea menor que 3, guardaremos el byte original. Paralelamente,
 *              guardaremos en un array de booleanos un valor false si no hemos encontrado coincidencia, y un valor true en
 *              la primera posición de la coincidencia si si que la hemos encontrado. Para guardar las 4095 posiciones anteriores
 *              usamos un hashmap en que el key es el byte que hemos encontrado, y el value es un arraylist con todas las posiciones
 *              en que aparece ese byte dentro de la ventana (ultimas 4095 posiciones). Para mantener este diccionario actualizado
 *              insertamos los bytes que vamos encontrando al iterar por los bytes del texto original, y su posición respectiva en el
 *              arraylist, pero también lo insertamos en un arraylist de pair <posición, byte>. Al final de cada iteración
 *              miraremos si el tamaño de este último arraylsit es mayor que 4095, y si es el caso, borraremos los primeros elementos
 *              del arraylist y las posiciones del Hashmap correspondientes hasta que el tamaño sea 4095. De esta forma el tamaño
 *              del conjunto de posiciones guardadas en el searchBuffer será siempre 4095 como máximo.
 *              Para acabar guardamos los bits agrupados en bytes y los bytes codificados (o no, dependiendo de si se ha encontrado
 *              coincidencia), en un array result, con un separador en medio para diferenciarlos y escribimos este array en el
 *              fichero comprimido.
 *  \author    Nicolas Camerlynck
 */
public class Compressor_LZSS extends Compressor {


    private HashMap<Byte, ArrayList<Integer>> searchBuffer = new HashMap<>();
    private ArrayList<Pair> act = new ArrayList<>();

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

            if (searchBuffer.containsKey(itemb[i])) { //hi ha una entrada amb la lletra que avaluem
                chr = coincidence(itemb, i);
            }
            else { //no hi ha entrada, l'afegim i posem al set la posicio en la q estem
                ArrayList<Integer> a = new ArrayList<>();
                a.add(i);
                searchBuffer.put(itemb[i], a);
                Pair aux = new Pair(i, itemb[i]);
                act.add(aux);
                //4501 n

            }

            if (chr == -1) { //hem afegit una entrada nova
                result.add(itemb[i]);
            }
            else if (chr == 0x0000) {

                result.add(itemb[i]);
                searchBuffer.get(itemb[i]).add(i);

                act.add(new Pair(i, itemb[i]));
            }
            else {
                //mirem la posicio de l'offset per posar el bit a true
                bits[i] = true;

                byte aux2 = (byte) (chr >> 8);
                result.add(aux2);

                result.add( (byte) (chr));

                short despl = (short) (chr & 0x000F);
                int desp = (int) despl;

                for (int g = 0; g < desp; ++g) {
                    searchBuffer.get(itemb[i + g]).add(i + g);

                    act.add(new Pair(i+g, itemb[i+g]));
                }

                i = i + desp - 1;

            }

            actualitzar();
        }
        byte[] separador = new byte[2];
        separador[0] = -1;
        separador[1] = -1;

        byte[] aaux = mergeArrays(groupBits(bits), separador);
        controller.writeBytes(mergeArrays(aaux, arrayListToArray(result)));
    }

    /**
     * Esta función busca la máxima coincidencia en las 4095 posiciones anteriores a la posición p. Lo hace consultando
     * primero en el diccionario y buscando las posiciones en que aparece la primera letra en la ventana. Después mirará
     * si las letras que siguen a estas posiciones coinciden con las letras que siguen a la posición p. Guardará la
     * posición de la primera letra y el desplazamiento únicamente si es el mayor que ha encontrado, y cuando haya
     * iterado por todas las posiciones de la primera letra, codificará un short mediante la función codify y lo retornará.
     * @param itemb item de bytes que contiene la información que debemos comprimir.
     * @param p posición del itemb a partir de la que debemos empezar a buscar coincidencias.
     * @return un short codificado con los 12 primeros bits representando el offset y los ultimos 4 el desplazamiento.
     */
    public short coincidence(byte[] itemb, int p) {
        byte first = itemb[p];
        ArrayList<Integer> poss = searchBuffer.get(first);
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
        return codify(maxdesp, offset, p);
    }

    /**
     * Función que codifica un short. Con los 12 primeros bits representa el offset relativo a la posición p (por lo tanto
     * 4095 como máximo, ya que 2^12 es 2096) y con los 4 últimos representa el desplazamiento (15 como máximo).
     * @param maxdesp el desplazamiento de la coincidencia que se debe codificar.
     * @param maxpos la posición relativa al principio de la primera letra de la coincidencia.
     * @param p posición de la primera letra que hemos evaluado.
     * @return un short en que los 12 primeros bits representan el offset (la posicion relativa des de p) y los ultimos
     *         4 bits representan el numero de carácteres seguidos con los que hemos encontrado coincidencia.
     */
    public short codify(int maxdesp, int maxpos, int p) {
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

    /**
     * Esta función agrupa el array de valores booleanos que le pasamos en bytes, con un 1 para true y un 0 para false. Para
     * que los bits se puedan guardar en bytes tienen que ser multiplos de 8 (8bits = 1byte), así que al principio del primer byte
     * pondremos una serie de "dummy bits" para que los bits sean múltiplos de 8. Los "dummy bits" consisten en ceros y un uno para
     * indicar el final de estos y el principio de los bits de verdad. Si el conjunto de bits que nos pasan ya es múltiplo de 8,
     * el primer byte será un 1 (siete ceros y un uno).
     * @param a array de booleanos que representan los bits.
     * @return un array de bytes en que el primero contendrá los bits necesarios de relleno para que el conjunto de
     *         bits sea múltiplo de 8 (y se pueda agrupar en bytes). Y dónde el resto de bytes serán los bits.
     *         agrupados en bytes
     */
    public byte[] groupBits(boolean[] a) {
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

    /**
     * Función que convierte una arrayList a array manteniendo el mismo contenido.
     * @param a arraylist de bytes
     * @return un array de bytes con los mismos elementos que a
     */
    public byte[] arrayListToArray (ArrayList<Byte> a) {
        byte[] res = new byte[a.size()];

        for (int i = 0; i < a.size(); ++i) {
            res[i] = a.get(i);
        }

        return res;
    }

    /**
     * Función que fusiona dos arrays concatenándolos.
     * @param a array de bytes.
     * @param b array de bytes.
     * @return un array que representa la concatenación de los arrays a y b.
     */
    public byte[] mergeArrays(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        byte[] c = new byte[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    /**
     * Función que mantiene actualizado el diccionario searchBuffer. Lo hace evaluando el tamaño del arraylist act,
     * que guarda por orden de inserción las posiciones y carácteres añadidos al searchBuffer. Mientras el tamaño de
     * act sea mayor que 4094 (máximo tamaño del searchBuffer), borraremos el primer elemento de act, y buscaremos ese
     * mismo elemento en el searchBuffer para eliminarlo también.
     */
    public void actualitzar() {
        while (act.size() > 4094) {
            Pair aux = act.get(0);
            int bor = aux.getL();
            byte Bybor = aux.getR();
            act.remove(0);

            if (searchBuffer.get(Bybor).size() > 1) {
                searchBuffer.get(Bybor).remove(Integer.valueOf(bor));
            }

            else {
                searchBuffer.remove(Bybor);
            }
        }
    }

}

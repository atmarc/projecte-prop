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
        byte[] itemb = controller.readAllBytes();
        HashMap<Integer, Byte> item = byteArrayToHashMap(itemb);
        int itemSize = item.size();

        Queue<Byte> noCoincQ = new LinkedList<>();
        Queue<Character> coincQ = new LinkedList<>();
        Queue<Boolean> bitQ = new LinkedList<>();
        HashMap<Integer, Byte> searchB = new HashMap<>();
        HashMap<Byte, Set<Integer>> searchB2 = new HashMap<>();


        for (int i = 0; i < itemSize; i++) {

            boolean coinc = false;
            boolean small = false;
            if (i < 4) small = true;

            //searchB.put(i, item.get(i));
            if (searchB2.containsKey(item.get(i))) {

            }
            else {
                //TODO: UNCOMMENT
                /*Set<Integer> a = new Set<Integer>;
                a.add(i);
                searchB2.put(item.get(i), a);*/


            } //idjewoi
            /*getOut.add(i);
            if (getOut.size() >= 4096) {
                int elim = getOut.peek();
                getOut.remove();
                searchB.remove(elim);
            }*/
            int desplmaxcoinc = 0;
            int maxcoincpointer = 0;

            int posSearch = min(4095, searchB.size());

            /*for (int j = 1; j < posSearch && !small; ++j) {
                //Bucle que mirarà les 4095 posicions anteriors al searchbuffer buscant coincidencies d'almenys 3 nums seguits
                if (i - j < 0 || (i - j) <= (i-4095)) break;

                if (coincidence(i,j,item, searchB)) {
                    //si trobem una coincidencia d'almenys 3, mirem de quant es la coincidencia
                    coinc = true;
                    boolean endcoinc = false;
                    int despl = 0;
                    for (int h = i - j, k = i; !endcoinc; ++h, ++k) {
                        if (despl == 18) break;
                        if (h < searchB.size() && k < itemSize && searchB.get(h) == item.get(k)) {
                            despl++;
                        }
                        else endcoinc = true;
                    }
                    //Hem de buscar la coincidencia maxima, per tant mirem si el despl trobat es el mes gran
                    if (despl == 18) {
                        desplmaxcoinc = despl;
                        maxcoincpointer = j;
                        break;
                    }
                    if (despl > desplmaxcoinc) {
                        desplmaxcoinc = despl;
                        maxcoincpointer = j;
                    }
                }
            }*/

            if (!coinc) { //no tenim coincidencia
                bitQ.add(false);
                //byte aux = charToByte(item.get(i));
                noCoincQ.add(item.get(i));
            }

            else {
                //hem trobat una coincidencia
                //hem de posar a true el primer bit, i la resta de la coincidencia a false
                bitQ.add(true);
                for (int l = 1; l < desplmaxcoinc; ++l) {
                    bitQ.add(false);
                    i++; //ja no cal que visitem els (desp-1) elements seguents, ja que els hem trobat a la coincidencia
                    searchB.put(i, item.get(i)); //afegim els elements "coincidits" al searchBufferç
                    /*getOut.add(i);
                    if (getOut.size() >= 4096) {
                        int elim = getOut.peek();
                        getOut.remove();
                        searchB.remove(elim);
                    }*/
                }

                //transformacions per posar 12bits de offset i 4bits de despl
                char offset = (char) (maxcoincpointer); //agafa els 16 bits mes petits
                offset = (char) (offset << 4);
                offset = (char) (offset & 0xFFF0); //tenim el offset als primers 12 bits

                char dsp = (char) (desplmaxcoinc - 3);
                dsp = (char) (dsp & 0x000F); //tenim el desp als ultims 4 bits

                char codificat = (char) (offset | dsp);

                coincQ.add(codificat);
            }

        }

        //codifiquem

        byte aux[] = new byte[2];
        aux[0] = (byte) 0xFF;
        aux[1] = (byte) 0xFF;

        byte a[] = byteQtoByteArray(noCoincQ);
        byte b[] = charQtoByteArray(coincQ);
        byte c[] = boolQtoByteArray(bitQ);

        int index = 0;
        byte []arrFinal = new byte[a.length + b.length + c.length + 4];
        while (index < c.length) {
            arrFinal[index] = c[index];
            index++;
        }
        arrFinal[index] = aux[0];
        index++;
        arrFinal[index] = aux[1];
        index++;
        int h = 0;
        while (h < a.length) {
            arrFinal[index] = a[h];
            index++;
            h++;
        }
        arrFinal[index] = aux[0];
        index++;
        arrFinal[index] = aux[1];
        index++;
        h = 0;
        while (h < b.length) {
            arrFinal[index] = b[h];
            index++;
            h++;
        }

        controller.writeBytes(arrFinal);

    }

    /**
     * @pre a y b son dos enteros
     * @post La variable de retorno contiene el valor del elemento mas pequeño entre a y b
     * @param a
     * @param b
     * @return El elemento mas pequeño entre a y b
     */
    private int min(int a, int b) {
        if (a < b) return a;
        else return b;
    }

    /**
     * @pre a es una cola de bytes
     * @post El Array de retorno contiene todos los elementos del parámetro a, con orden ascendente de inserción en la cola
     * @param a Cola de Bytes que tenemos que pasar a array
     * @return Un Array de a.size() elementos con los elementos de la cola a;
     */
    private byte[] byteQtoByteArray (Queue<Byte> a) {
        int size = a.size();
        byte b[] = new byte[size];
        for (int i = 0; i < size; ++i) {
            b[i] = a.remove();
        }
        return b;
    }

    /**
     * @pre a es una cola de carácteres
     * @post El Array de retorno contiene todos los elementos del parámetro a, con orden ascendente de inserción en la cola
     * @param a Cola de Chars que tenemos que pasar a array
     * @return Un Array de a.size() elementos con los elementos de la cola a;
     */
    private byte[] charQtoByteArray (Queue<Character> a) {
        int size = a.size();
        byte b[] = new byte[size*2];

        for (int i = 0; i < size*2; i+=2) {
            char aux = a.remove();
            byte c = (byte) (aux>>8);
            byte d = (byte) (aux);
            b[i] = c;
            b[i+1] = d;
        }
        return b;
    }

    /**
     * Pasa de una cola de booleanos a un array de bytes con bits a 1 para valores true, y bits a 0 para false
     * @pre a es una cola de valores booleanos no vacía
     * @post b contiene (size cola / 8) + 1 bytes, y en los bits de mas peso del primer byte se han añadido bits a 0,
     *        para que el numero de bits sea multiplo de 8 y se pueda representar en bytes. Después de hacer que el
     *        numero de bits sea multiplo de 8, se añade un bit a 1 para indicar que a partir de ahí empiezan los
     *        valores de verdad, con 0 para representar a false y 1 para representar a true.
     * @param a Cola de booleans que tenemos que pasar a array
     * @return Un Array de bytes de tamaño (a.size() / 8) + 1, donde cada bit de estos bytes representa un valor true o
     *         false de la cola a. Al principio se añaden ceros y un 1 para cuadrar que el total de bits sea múltiplo de 8
     */
    private byte[] boolQtoByteArray (Queue<Boolean> a) {
        int mida = (a.size() / 8) + 1;
        int modA = a.size() % 8;
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
                boolean aux = a.remove();
                c  = (byte) (c << 1);
                if (aux) {
                    c = (byte) (c + 1);
                }
            }
            b[i] = c;
        }
        i++;
        c = 0;
        while (!a.isEmpty()) {
            for (int h=0; h < 8; ++h) {
                boolean aux = a.remove();
                c  = (byte) (c << 1);
                if (aux) {
                    c = (byte) (c + 1);
                }
            }
            b[i] = c;
            i++;
        }

        return b;
    }

    /**
     * @pre i y (i-j) son posiciones de item y searchB, respectivamente.
     * @post retorna true si se han encontrado coincidencias en los carácteres de las tres posiciones consecutivas de
     *        item y searchB, a partir de la posición i de item y (i-j) de searchB.
     * @param i posicion inicial a la que accederemos de item.
     * @param j posicion inicial a la que accederemos de searchB.
     * @param item Hashmap que contiene el texto a comprimir.
     * @param searchB Search Buffer.
     * @return true si encontramos una coincidencia de tres carácteres seguidos con posicion inicial i y j en Item y searchB, respectivamente
     *         false en caso opuesto
     */
    private boolean coincidence(int i, int j, HashMap<Integer,Byte> item, HashMap<Integer, Byte> searchB) {

        if ( i+2 < item.size() && (i-j+2) < searchB.size() &&
                item.get(i) == searchB.get( (i-j) ) &&
                item.get(i+1) == searchB.get((i-j)+1) &&
                item.get(i+2) == searchB.get((i-j)+2)) {
            return true;
        }
        return false;
    }

    /**
     * Pasa de un array de bytes a un hash map con las posiciones en las key y los bytes en el value.
     * @pre a es un array de bytes no vacío.
     * @post el contenido del Hashmap aux que se retorna es el mismo que el de a, con las posiciones como key y los
     *       bytes como value.
     * @param a Hashmap que contiene el texto a comprimir.
     * @return Un Hashmap<Int,Char> con en la key la posicion de los elementos que habia en a y en el value el byte
     *         que había.
     */
    private HashMap<Integer, Byte> byteArrayToHashMap(byte[] a) {
        HashMap<Integer,Byte> aux = new HashMap<>();
        for (int i = 0; i < a.length; ++i) {
            aux.put(i,a[i]);
        }

        return aux;
    }

    /**
     *
     * @param i
     * @param j
     * @param item
     * @param searchB2
     * @return
     */
    private short coincidence2(int i, int j, HashMap<Integer,Byte> item, HashMap<Byte, Set<Integer>> searchB2) {

        int aux = item.get(i);
        int desp = 0;
        int maxdesp = 0;
        int offs;
        int maxoffs = 0;
        if (searchB2.containsKey(aux)) {
            Iterator it = (Iterator) searchB2.get(aux);
            while (it.hasNext()) {
                desp = 1;
                boolean end = false;
                int element = (int) it.next(); // TODO: CHECK
                offs = element;
                for (int h = i+1; !end; ++h) {
                    if (searchB2.get(item.get(h)).contains(element + 1)) {
                        desp++;
                        element++;
                        if (desp == 15) break;
                    }
                    else end = true;
                }
                if (desp > maxdesp) {
                    maxdesp = desp;
                    maxoffs = offs;
                }
            }
        }
        else return 0x0000;

        if (maxdesp < 3) return 0x0000;
        else {

            maxoffs = i - maxoffs;
            short os = (short) maxoffs;
            os = (short) (os << 4);
            os = (short) (os & 0xFFF0);

            short d = (short) maxdesp;
            d = (short) (d & 0x000F);

            short res = (short) (os | d);

            return res;
        }
    }

    /**
     * Borra un elemento del Hashmap
     * @param key
     * @param value
     * @param searchB
     */
    private void deleteValue(byte key, int value, HashMap<Byte, Set<Integer>> searchB) {

    }
}

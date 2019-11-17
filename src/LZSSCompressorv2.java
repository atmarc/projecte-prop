import java.lang.reflect.Array;
import java.sql.Struct;
import java.util.LinkedList;
import java.util.Queue;

public class LZSSCompressorv2 extends Compressor {

    public String getExtension() {
        return ".lzss";
    }

    public void compress () {
        String item = readFileString();
        int itemSize = item.length();
        StringBuilder searchBuffer = new StringBuilder();
        Queue<Byte> noCoincQ = new LinkedList<>();
        Queue<Character> coincQ = new LinkedList<>();
        Queue<Boolean> bitQ = new LinkedList<>();

        for (int i = 0; i < itemSize; i++) {

            boolean coinc = false;
            boolean small = false;
            if (i < 4) small = true;

            searchBuffer.append(item.charAt(i)); //afegim l'element al searchBuffer
            int desplmaxcoinc = 0;
            int maxcoincpointer = 0;

            int posSearch = min(4096, searchBuffer.length());

            for (int j = 1; j < posSearch && !small; ++j) {
                //Bucle que mirarà les 4095 posicions anteriors al searchbuffer buscant coincidencies d'almenys 3 nums seguits
                //dummy variables TODO: esborrarles
                if (i - j < 0) break;

                if (coincidence(i,j,item, searchBuffer.toString())) {
                    //si trobem una coincidencia d'almenys 3, mirem de quant es la coincidencia
                    coinc = true;
                    boolean endcoinc = false;
                    int despl = 0;
                    for (int h = i - j, k = i; !endcoinc; ++h, ++k) {
                        if (despl == 15) break;
                        if (h < searchBuffer.length() && k < itemSize && searchBuffer.charAt(h) == item.charAt(k)) {
                            despl++;
                        }
                        else endcoinc = true;
                    }
                    //Hem de buscar la coincidencia maxima, per tant mirem si el despl trobat es el mes gran
                    if (despl == 15) {
                        desplmaxcoinc = despl;
                        maxcoincpointer = i-j;
                        break;
                    }
                    if (despl > desplmaxcoinc) {
                        desplmaxcoinc = despl;
                        maxcoincpointer = i-j;
                    }
                }
            }

            if (!coinc) { //no tenim coincidencia
                bitQ.add(false);
                byte aux = charToByte(item.charAt(i));
                noCoincQ.add(aux);
            }

            else {
                //hem trobat una coincidencia
                //hem de posar a true el primer bit, i la resta de la coincidencia a false
                bitQ.add(true);
                for (int l = 1; l < desplmaxcoinc; ++l) {
                    bitQ.add(false);
                    i++; //ja no cal que visitem els (desp-1) elements seguents, ja que els hem trobat a la coincidencia
                    searchBuffer.append(item.charAt(i)); //afegim els elements "coincidits" al searchBuffer
                }

                //transformacions per posar 12bits de offset i 4bits de despl
                char offset = (char) (maxcoincpointer); //agafa els 16 bits mes petits
                offset = (char) (offset << 4);
                offset = (char) (offset & 0xFFF0); //tenim el offset als primers 12 bits

                char dsp = (char) (desplmaxcoinc);
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

        writeBytes(arrFinal);

    }

    private byte charToByte(char a) {
        char aux = (char) (a & 0x00FF);
        byte ret = (byte) aux;
        return ret;
    }

    private int min(int a, int b) {
        if (a < b) return a;
        else return b;
    }

    private byte[] byteQtoByteArray (Queue<Byte> a) {
        int size = a.size();
        byte b[] = new byte[size];
        for (int i = 0; i < size; ++i) {
            b[i] = a.remove();
        }
        return b;
    }

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

    private boolean coincidence(int i, int j, String item, String searchBuffer) {

        if ( i+2 < item.length() && (i-j+2) < searchBuffer.length() &&
                item.charAt(i) == searchBuffer.charAt(i-j) &&
                item.charAt(i+1) == searchBuffer.charAt((i-j)+1) &&
                item.charAt(i+2) == searchBuffer.charAt((i-j)+2)) {
            return true;
        }
        return false;
    }
}

import java.sql.Struct;
import java.util.LinkedList;
import java.util.Queue;

public class LZSSCompressorv2 extends Compressor {

    public void Compress (String item) {

        int itemsize = item.length();
        StringBuilder searchBuffer = new StringBuilder();
        Queue<Byte> noCoincQ = new LinkedList<>();
        Queue<Character> coincQ = new LinkedList<>();
        Queue<Boolean> bitQ = new LinkedList<>();

        for (int i = 0; i < itemsize; i++) {

            boolean coinc = false;

            if (i < 4) break;

            searchBuffer.append(item.charAt(i)); //afegim l'element al searchBuffer
            int desplmaxcoinc = 0;
            int maxcoincpointer = 0;

            int posSearch = min(4096, searchBuffer.length());

            for (int j = 1; j < posSearch; ++j) { //Bucle que mirarà les 4095 posicions anteriors al searchbuffer buscant coincidencies d'almenys 3 nums seguits

                if (i - j <= 0) break;
                desplmaxcoinc = 0;
                maxcoincpointer = 0;

                if (coincidence(i,j,item, searchBuffer.toString())) {
                    //si trobem una coincidencia d'almenys 3, mirem de quant es la coincidencia
                    coinc = true;
                    boolean endcoinc = false;
                    int despl = 0;
                    for (int h = j, k = i; !endcoinc; ++h, ++k) {
                        if (despl == 15) break;
                        if (searchBuffer.charAt(h) == item.charAt(k)) {
                            despl++;
                        }
                        else endcoinc = true;
                    }
                    //Hem de buscar la coincidencia maxima, per tant mirem si el despl trobat es el mes gran
                    if (despl == 15) {
                        desplmaxcoinc = despl;
                        maxcoincpointer = j;
                        break;
                    }
                    if (despl > desplmaxcoinc) {
                        desplmaxcoinc = despl;
                        maxcoincpointer = j;
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

    private Pair checkBiggestCoincidence()

    private boolean coincidence(int i, int j, String item, String searchBuffer) {

        if (item.charAt(i) == searchBuffer.charAt(i-j) && item.charAt(i+1) == searchBuffer.charAt(i-j+1) && item.charAt(i+2) == searchBuffer.charAt(i-j+2)) {

        }

    }
}

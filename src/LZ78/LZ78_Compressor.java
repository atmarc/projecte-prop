package LZ78;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class LZ78_Compressor {

    private ArrayList<Pair> comp_file;
    private HashMap<String, Integer> Mapa;

    public LZ78_Compressor() {

    }

    private int look_for(String word) {
        /*
            Mapa de prueba temporal, hasta que hagamos el arbol.
            Cuando no lo encuentra, guarda esa palabra con el ultimo indice utilizado en el arbol de patrones. Se puede
            Post-Condicion:
             - Si la palabra buscada existe; retorna el indice donde esta situada la palabra en el comp_file.
             - Si la palabra buscada no existe, retorna -1, y esta es anadida al arbol con indice = comp_file.size()
         */
        if (!Mapa.containsKey(word)) {
            Mapa.put(word, comp_file.size());
            return -1; // no estaba, y ha sido anadido
        }
        return Mapa.get(word);
    }

    public void compress (String regular_item){

        //System.out.println("Texto original:" + regular_item);

        Mapa = new HashMap<String, Integer>();

        int size = regular_item.length();
        comp_file = new ArrayList<Pair>();
        comp_file.add(new Pair(-1, (byte) 0));

        int it = 0;
        int index;
        int previous_index = 0;

        StringBuilder word = new StringBuilder();
        while (it < size) {
            word.append(regular_item.charAt(it));
            index = look_for(word.toString());
            if (index < 0) {
                comp_file.add(new Pair(previous_index, (byte) regular_item.charAt(it)));
                previous_index = 0;
                word = new StringBuilder();
            }
            else previous_index = index;

            ++it;
        }

        System.out.println("Texto comprimido:\n");

        if (false) comp_file.forEach(pair -> {
            System.out.print(pair.index);
            System.out.print("\t");
            System.out.printf("%x" ,pair.offset);
            System.out.print("\n");
        });

        System.out.println("Tamano de la salida = " + comp_file.size() + "\n");

        prepare_writing(calculoBaseIndice());


    }

    private void prepare_writing(int bytes) {


        if (bytes == 1) {



        } else if (bytes == 2) {


        } else {



        }
    }

    private int calculoBaseIndice() {
        int s = comp_file.size();
        if (s < 128) return 1;
        if (s < 32767) return 2;
        return 4;
    }

}

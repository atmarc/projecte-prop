package JPEG;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

import Triplet.Triplet;

public class JpegAlgorithm {


    public static void compress(String s) {

        //Filtro per linies
        String l[] = s.split("\r\n");
        ArrayList <String> file = new ArrayList<>();
        for (int i = 0; i < l.length;++i) {
            String aux[] = l[i].split(" ");
            for (int j = 0; j < aux.length; ++j) {
                if (!aux[j].equals("")) {
                    file.add(aux[j]);
                }
            }
        }

        int width = parseInt(file.get(1));
        int height = parseInt(file.get(2));
        int max_val_color = parseInt(file.get(3));

        ArrayList <Triplet> Pixels = new ArrayList<>();

        for (int i = 4; i < file.size(); i += 3) {
            int element1 = parseInt(file.get(i));
            int element2 = parseInt(file.get(i + 1));
            int element3 = parseInt(file.get(i + 2));
            Triplet <Integer, Integer, Integer> aux = new Triplet<>(element1, element2, element3);
            Pixels.add(aux);
        }

        System.out.println(Pixels.get(1).getFirstElement());
        System.out.println(Pixels.get(1).getSecondElement());
        System.out.println(Pixels.get(1).getThirdElement());

    }
}

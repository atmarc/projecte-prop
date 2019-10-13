package JPEG;

import java.util.ArrayList;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

import Triplet.Triplet;

public class JpegAlgorithm {


    public static void compress(String s) {
        // Filtro per linies
        String l[] = s.split("\n");
        ArrayList <String> file = new ArrayList<>();
        for (int i = 0; i < l.length;++i) {
            String aux[] = l[i].split(" ");
            for (int j = 0; j < aux.length; ++j) {
                if (!aux[j].equals("")) {
                    file.add(aux[j]);
                }
            }
        }
        // Valors de config
        int WIDTH = parseInt(file.get(1));
        int HEIGHT = parseInt(file.get(2));
        float MAX_VAL_COLOR = parseInt(file.get(3));

        ArrayList <Triplet> Row = new ArrayList<>();
        ArrayList < ArrayList <Triplet> > Pixels = new ArrayList<>();

        // Llegim els pixels i ja els passem a YCbCr
        for (int i = 4; i < file.size(); i += 3) {
            float element1 = parseFloat(file.get(i)) / MAX_VAL_COLOR;
            float element2 = parseFloat(file.get(i + 1)) / MAX_VAL_COLOR;
            float element3 = parseFloat(file.get(i + 2)) / MAX_VAL_COLOR;
            Triplet <Float, Float, Float> aux = new Triplet<>(element1, element2, element3);
            Row.add(RGBtoYCbCr(aux));
            if (Row.size() % WIDTH == 0) { // Si s'ha acabat la linia l'afegim a la matriu
                Pixels.add(Row);
                Row = new ArrayList<>();
            }
        }


    }

    /*
    RGB --> YCbCr
        Y = 16 +  65.481 R + 128.553 G + 24.966 B
        Cb = 128 - 37.797 R - 74.203 G + 112 B
        Cr = 128 + 112 R - 93.786 G - 18.214 B
    */
    private static Triplet<Integer, Integer, Integer> RGBtoYCbCr (Triplet<Float, Float, Float> pixel) {
        int Y = (int) (65.481 * pixel.getFirst() + 128.553 * pixel.getSecond()
                + 24.966 * pixel.getThird() + 16);
        int Cb = (int) (-37.797 * pixel.getFirst() - 74.203 * pixel.getSecond()
                + 112 * pixel.getThird() + 128);
        int Cr = (int) (112 * pixel.getFirst() - 93.786 * pixel.getSecond()
                - 18.214 * pixel.getThird() + 128);

        return new Triplet<>(Y, Cb, Cr);
    }

    private static void  printPixels(ArrayList < ArrayList <Triplet> > Pixels) {
        for (int i = 0; i < Pixels.size(); ++i) {
            for (int j = 0; j < Pixels.get(0).size(); ++j) {
                Pixels.get(i).get(j).print();
            }
            System.out.println('\n');
        }
    }
}

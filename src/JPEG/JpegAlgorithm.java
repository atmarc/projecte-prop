package JPEG;

import java.util.ArrayList;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

import Triplet.Triplet;

public class JpegAlgorithm {


    public static void compress(String s) {

        int compressFactor = 2;

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

        Triplet<Integer, Integer, Integer> Pixels [][] = new Triplet[HEIGHT][WIDTH];

        // Llegim els pixels i ja els passem a YCbCr
        int f = 0;
        int c = 0;
        for (int i = 4; i < file.size(); i += 3) {
            float R = parseFloat(file.get(i));
            float G = parseFloat(file.get(i + 1));
            float B = parseFloat(file.get(i + 2));

            Pixels[f][c] = RGBtoYCbCr(R, G, B);
            ++c;

            if (c == WIDTH) { // Si s'ha acabat la linia
                c = 0;
                ++f;
            }
        }

        // Downsampling

        // Block splitting

        // Tenim en compte si el nombre de pixels és múltiple de 8
        int nBlocksX = (WIDTH % 8 == 0) ? WIDTH/8 : WIDTH/8 + 1;
        int nBlocksY = (HEIGHT % 8 == 0) ? HEIGHT/8 : HEIGHT/8 + 1;

        int BlocksArrayY [][] = new int[nBlocksY][nBlocksX];

        // int BlocksMat [][][][] = new int [nBlocksY][nBlocksX][8][8];

        int numOfBlocks = nBlocksX * nBlocksY;

        for (int x = 0; x < numOfBlocks; ++x) {
            int BlockYChannel [][] = new int [8][8];

            int incrementX = (x * 8 <= WIDTH) ? x * 8 : 0;
            int incrementY = x / nBlocksX;
            incrementY *= 8;

            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    BlockYChannel[i][j] = Pixels[i + incrementY][j + incrementX].getFirst();
                }
            }

        }

        printPixels(Pixels);
    }

    /*
    RGB --> YCbCr
        Y = 0.299 R + 0.587 G + 0.114 B
        Cb = - 0.1687 R - 0.3313 G + 0.5 B + 128
        Cr = 0.5 R - 0.4187 G - 0.0813 B + 128
    */
    private static Triplet<Integer, Integer, Integer> RGBtoYCbCr (float R, float G, float B) {
        int Y = (int) (0.299 * R + 0.587 * G + 0.114 * B);
        int Cb = (int) (-0.1687 * R - 0.3313 * G + 0.5 * B + 128);
        int Cr = (int) (0.5 * R - 0.4187 * G - 0.0813 * B + 128);
        return new Triplet<>(Y, Cb, Cr);
    }

    private static void printPixels(Triplet<Integer, Integer, Integer> Pixels [][]) {
        for (int i = 0; i < Pixels.length; ++i) {
            for (int j = 0; j < Pixels[0].length; ++j) {
                Pixels[i][j].print();
            }
            System.out.println('\n');
        }
    }
}

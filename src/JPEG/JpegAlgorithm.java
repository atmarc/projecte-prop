package JPEG;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

import Triplet.Triplet;

public class JpegAlgorithm {

    public static void compressP6 (ArrayList<Byte> s) throws IOException {
        File file;
        byte[] fileB = Files.readAllBytes(Paths.get("testing_files/boxes_1.ppm"));
        int c;
        for (int i = 0; i < s.size() && i < fileB.length; ++i) {
            c = s.get(i).intValue();
            if (c < 0) c += 256;
            System.out.println(c.toString());
        }
    }

    public static void compress(String s) {

        Triplet<Integer, Integer, Float> headers = readHeaders(s);
        final String inputMode = "P" + s.charAt(1);
        final int WIDTH = headers.getFirst();
        final int HEIGHT = headers.getSecond();
        final float MAX_VAL_COLOR = headers.getThird();


        ArrayList <Integer> data = new ArrayList<>();

        // El fitxer t'ho passa en ASCII
        if (inputMode.equals("P3")) {
            // Filtro per linies
            String l[] = s.split("\n");
            for (int i = 3; i < l.length; ++i) {
                String aux[] = l[i].split(" ");
                for (int j = 0; j < aux.length; ++j) {
                    if (!aux[j].equals("")) {
                        data.add(parseInt(aux[j]));
                    }
                }
            }
        } else if (inputMode.equals("P6")) {
            int index = 0;
            int line = 0;
            // Posem index al principi de la data
            while (line < 3 && index < s.length()) {
                if (s.charAt(index) == '\n') ++line;
                ++index;
            }
            for (; index < s.length(); ++index) {
                int valor = s.charAt(index);
                data.add(valor);
            }
        }

        Triplet<Integer, Integer, Integer> Pixels [][] = new Triplet[HEIGHT][WIDTH];

        // Llegim els pixels i ja els passem a YCbCr
        int f = 0;
        int c = 0;
        for (int i = 0; i < data.size(); i += 3) {
            float R = data.get(i);
            float G = data.get(i + 1);
            float B = data.get(i + 2);

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

    private static Triplet<Integer, Integer, Float> readHeaders(String s) {
        Triplet<Integer, Integer, Float> retorn = new Triplet<Integer, Integer, Float>();
        // Saltem la primera linia
        int index = 0;
        while (s.charAt(index) != '\n') {
            ++index;
        }
        ++index;

        String linia = new String();

        // Segona linia
        while (s.charAt(index) != '\n' && s.charAt(index) != '\r') {
            linia += s.charAt(index);
            ++index;
        }
        String values[] = linia.split(" ");
        retorn.setFirst(parseInt(values[0]));
        retorn.setSecond(parseInt(values[1]));

        // Tercera linia
        index = (s.charAt(index) == '\r') ? index + 2 : ++index;
        linia = new String();
        while (s.charAt(index) != '\n' && s.charAt(index) != '\r') {
            linia += s.charAt(index);
            ++index;
        }
        retorn.setThird(parseFloat(linia));
        return retorn;
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

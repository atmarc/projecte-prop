package JPEG;

import java.util.ArrayList;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

import Triplet.Triplet;

public class JpegAlgorithm {

    public static String compress(byte s[]) {

        Triplet<Integer, Integer, Float> headers = readHeaders(s);
        final String inputMode = "P" + (char)s[1];
        final int WIDTH = headers.getFirst();
        final int HEIGHT = headers.getSecond();
        final float MAX_VAL_COLOR = headers.getThird();

        ArrayList <Integer> data = new ArrayList<>();

        // Posem index al principi de la data
        int index = 0;
        int line = 0;
        while (line < 3 && index < s.length) {
            if (s[index] == '\n') ++line;
            ++index;
        }

        // El fitxer t'ho passa en ASCII
        if (inputMode.equals("P3")) {
            String num = "";
            while (index < s.length) {
                if (s[index] != ' ' && s[index] != '\r' && s[index] != '\n') {
                    num += (char)s[index];
                } else if (!num.equals("")) {
                    data.add(parseInt(num));
                    num = "";
                }
                ++index;
            }
        }
        else if (inputMode.equals("P6")) {
            // Posem a data tots els valors
            for (; index < s.length; ++index) {
                int valor = s[index];
                if (valor < 0) valor += 256;
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

        Block BlocksArrayY [][] = new Block[nBlocksY][nBlocksX];
        Block BlocksArrayCb [][] = new Block[nBlocksY][nBlocksX];
        Block BlocksArrayCr [][] = new Block[nBlocksY][nBlocksX];

        int numOfBlocks = nBlocksX * nBlocksY;

        for (int y = 0; y < nBlocksY; ++y) {
            for (int x = 0; x < nBlocksX; ++x) {

                int marginX = x * 8;
                int marginY = y * 8;
                Block blockY = new Block(8, 8, "Y");
                Block blockCb = new Block(8, 8, "Cb");
                Block blockCr = new Block(8, 8, "Cr");

                for (int i = 0; i < 8 && i + marginY < HEIGHT; ++i) {
                    for (int j = 0; j < 8 && j + marginX < WIDTH; ++j) {
                        // Aprofitem i centrem els valors a 0
                        int value = Pixels[i + marginY][j + marginX].getFirst() - 128;
                        blockY.setValue(i, j, value);

                        value = Pixels[i + marginY][j + marginX].getSecond() - 128;
                        blockCb.setValue(i, j, value);

                        value = Pixels[i + marginY][j + marginX].getThird() - 128;
                        blockCr.setValue(i, j, value);
                    }
                }
                blockY.DCT();
                BlocksArrayY[y][x] = blockY;
                blockCb.DCT();
                BlocksArrayCb[y][x] = blockCb;
                blockCr.DCT();
                BlocksArrayCr[y][x] = blockCr;
            }
        }

        String file = "";
        for (int y = 0; y < nBlocksY; ++y) {
            for (int x = 0; x < nBlocksX; ++x) {
                file += BlocksArrayY[y][x].zigzag();
                file += BlocksArrayCb[y][x].zigzag();
                file += BlocksArrayCr[y][x].zigzag();
            }
        }

        //file = Huffman.encode(file);
        return file;
    }

    public static String decompress(byte s[]) {
        return "";
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

    private static Triplet<Integer, Integer, Float> readHeaders(byte s[]) {
        Triplet<Integer, Integer, Float> retorn = new Triplet<Integer, Integer, Float>();
        // Saltem la primera linia
        int index = 0;
        while (s[index] != '\n') {
            ++index;
        }
        ++index;

        String linia = new String();

        // Segona linia
        while (s[index] != '\n' && s[index] != '\r') {
            linia += (char)s[index];
            ++index;
        }
        String values[] = linia.split(" ");
        retorn.setFirst(parseInt(values[0]));
        retorn.setSecond(parseInt(values[1]));

        // Tercera linia
        index = (s[index] == '\r') ? index + 2 : ++index;
        linia = new String();
        while (s[index] != '\n' && s[index] != '\r') {
            linia += (char)s[index];
            ++index;
        }
        retorn.setThird(parseFloat(linia));
        return retorn;
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

package dominio;
import java.util.ArrayList;
import java.util.LinkedList;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

/*!
 *  \brief     Extension de la clase Compressor mediante el algoritmo JPEG.
 *  \details
 *  \author    Marc Amorós
 */
public class Compressor_JPEG extends Compressor {

    /**
     * Función abstracta de la clase Compresor que devuelve la extension del archivo.
     * @pre -
     * @post devuelve la extension
     */
    public String getExtension() {
        return ".jpeg";
    }


    /**
     * Implementación de la función abstracta de la clase Compressor utilizando el algoritmo JPEG.
     * @pre Se han inicializado los atributos inputFile y outputFile en el Compressor_controller conveniente.
     * @post Se escribe en outputFile el archivo comprimido.
     */
    public void compress() {

        byte[] s = controller.readAllBytes();

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
            while (s[index] == '#') {
                while (s[index] != '\n') ++index;
                ++index;
            }
        }

        // El fitxer t'ho passa en ASCII
        if (inputMode.equals("P3")) {
            String num = "";
            while (index < s.length) {
                if (s[index] != ' ' && s[index] != '\r' && s[index] != '\n' && s[index] != '\t') {
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

        @SuppressWarnings("unchecked")
        Triplet<Integer, Integer, Integer>[][] Pixels = new Triplet[HEIGHT][WIDTH];

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

        // TODO: Downsampling

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

                for (int i = 0; i < 8; ++i) {
                    for (int j = 0; j < 8 && j + marginX < WIDTH; ++j) {
                        if (i + marginY >= HEIGHT || j + marginX >= WIDTH) {
                            // Quan la foto no és múltiple de 8
                            blockY.setValue(i, j, -128);
                            blockCb.setValue(i, j, -128);
                            blockCr.setValue(i, j, -128);
                        }
                        else {
                            // Aprofitem i centrem els valors a 0
                                int value = Pixels[i + marginY][j + marginX].getFirst() - 128;
                            blockY.setValue(i, j, value);

                            value = Pixels[i + marginY][j + marginX].getSecond() - 128;
                            blockCb.setValue(i, j, value);

                            value = Pixels[i + marginY][j + marginX].getThird() - 128;
                            blockCr.setValue(i, j, value);
                        }
                    }
                }

                // TODO: arreglar el diff
                blockY.DCT(); // Apliquem DCT a cada bloc
                BlocksArrayY[y][x] = blockY;
                //int diff = getDiff(BlocksArrayY, x, y); // Calculem el DC com la diferència amb la del bloc anterior
                //BlocksArrayY[y][x].setDCTValue(0,0, diff);

                blockCb.DCT();
                BlocksArrayCb[y][x] = blockCb;
                //diff = getDiff(BlocksArrayCb, x, y);
                //BlocksArrayCb[y][x].setDCTValue(0,0, diff);

                blockCr.DCT();
                BlocksArrayCr[y][x] = blockCr;
                //diff = getDiff(BlocksArrayCr, x, y);
                //BlocksArrayCr[y][x].setDCTValue(0,0, diff);
            }
        }

        int nivellCompressio = 0;

        int file[] = new int [5 + nBlocksX * nBlocksY * 64 * 3];

        file[0] = nivellCompressio;
        file[1] = nBlocksX;
        file[2] = nBlocksY;
        file[3] = HEIGHT;
        file[4] = WIDTH;

        index = 5;
        for (int y = 0; y < nBlocksY; ++y) {
            for (int x = 0; x < nBlocksX; ++x) {
                BlocksArrayY[y][x].zigzag(file, index);
                index += 64;
                BlocksArrayCb[y][x].zigzag(file, index);
                index += 64;
                BlocksArrayCr[y][x].zigzag(file, index);
                index += 64;
            }
        }


        Huffman huffman = new Huffman();
        LinkedList<Integer> bits = new LinkedList<>();

        huffman.encode(file, bits);

        ArrayList<Byte> arrayBytes = new ArrayList<>();

        stringBinToByte(bits, arrayBytes);

        byte [] bytes = new byte[arrayBytes.size()];
        for (int i = 0; i < arrayBytes.size(); ++i) {
            bytes[i] = arrayBytes.get(i);
        }

        controller.writeBytes(bytes);
    }

    /**
     * Función para pasar de RGB a la base de color YCbCr usando las formulas del estandard de Jfif.
     * @pre -
     * @post devuelve una tripleta de valores enteros con el valor delpixel en YCbCr
     * @param R componente Red del pixel que quieres transformar.
     * @param G componente Green del pixel que quieres transformar.
     * @param B componente Blue del pixel que quieres transformar.
     * @return devuelve una tripleta de valores enteros con el valor del pixel en YCbCr.
     */
    private static Triplet<Integer, Integer, Integer> RGBtoYCbCr (float R, float G, float B) {
        int Y = (int) (0.299 * R + 0.587 * G + 0.114 * B);
        int Cb = (int) (-0.1687 * R - 0.3313 * G + 0.5 * B + 128);
        int Cr = (int) (0.5 * R - 0.4187 * G - 0.0813 * B + 128);
        return new Triplet<>(Y, Cb, Cr);
    }

    /**
     * Funcion que lee los headers del ppm y ignora los comentarios.
     * @pre el parámetro s contiene el archivo en un array de bytes
     * @post se lee la cabecera del archivo y se devuelve
     * @param s Array de bytes que representa el archivo.
     * @return devuelve una tripleta de valores con el la altura, anchura y valor maximo de los pixels.
     */
    private static Triplet<Integer, Integer, Float> readHeaders(byte s[]) {
        Triplet<Integer, Integer, Float> retorn = new Triplet<Integer, Integer, Float>();
        // Saltem la primera linia
        int index = 0;
        while (s[index] != '\n') {
            if (s[index] == '#') {
                while (s[index] != '\n') ++index;
            }
            ++index;
        }
        ++index;

        String linia = new String();

        // Segona linia
        while (s[index] != '\n' && s[index] != '\r') {
            while (s[index] == '#') {
                while (s[index] != '\n') ++index;
                ++index;
            }
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
            while (s[index] == '#') {
                while (s[index] != '\n') ++index;
                ++index;
            }
            linia += (char)s[index];
            ++index;
        }
        retorn.setThird(parseFloat(linia));
        return retorn;
    }

    /**
     * Función que por cada 8 enteros que representan 8 bits, crea un byte y lo añade a arrayBytes.
     * @pre bits es una cadena de bits no vacia
     * @post arrayBytes contiene el valor en bytes de la cadena de bits
     * @param bits Cadena de bits representados con enteros.
     * @param arrayBytes ArrayList de bytes formados por los bits.
     */
    private static void stringBinToByte(LinkedList<Integer> bits, ArrayList<Byte> arrayBytes) {
        int bitsSize = bits.size();
        for (int i = 0; i + 8 <= bitsSize; i += 8) {
            byte b = 0;
            for (int j = 0; j < 8; ++j) {
                if (bits.getFirst() == 1) {
                    b = (byte) (b | (1 << 7 - j));
                }
                bits.removeFirst();
            }
            arrayBytes.add(b);
        }
    }
}

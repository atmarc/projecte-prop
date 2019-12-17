package dominio;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/*!
 *  \brief     Extension de la clase Decompressor mediante el algoritmo JPEG. Esta clase descomprime un archivo
 *  antiguamente comprimido con la classe Compressor_JPEG. Hace los pasos que anteriormente hicimos con el Compressor a
 *  la inversa. Utiliza, como el compresor, las classes Huffman, Block y Triplet para conseguirlo.
 *  \details
 *  \author    Marc Amorós
 */
public class Decompressor_JPEG extends Decompressor {

    /**
     * Implementación de la función abstracta de la clase Decompressor utilizando el algoritmo JPEG.
     * @pre Se han inicializado los atributos inputFile y outputFile en el Compressor_controller conveniente.
     * @post Se escribe en outputFile el archivo descomprimido.
     */
    public void decompress() {

        //byte s [] = controller.readAllBytes();
        byte s [] = new byte[0];
        try {
            s = Files.readAllBytes(Paths.get("/home/usuario/Escritorio/3r-1r/PROP/projecte-prop/testing_files/ppm_images/AAAA.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] bits = new int[s.length * 8];
        byteToBin(s, bits);

        Huffman huffman = new Huffman();
        ArrayList<Integer> valors = new ArrayList<>();
        huffman.decode(bits, valors);

        final int COMPRESS_RATIO = valors.get(0);
        final int nBlocksX = valors.get(1);
        final int nBlocksY = valors.get(2);
        final int HEIGHT = valors.get(3);
        final int WIDTH = valors.get(4);

        Block[][] arrayOfBlocksY = new Block[nBlocksY][nBlocksX];
        Block[][] arrayOfBlocksCb = new Block[nBlocksY][nBlocksX];
        Block[][] arrayOfBlocksCr = new Block[nBlocksY][nBlocksX];

        int index = 5;
        int bi = 0, bj = 0;

        // TODO: Tornar a sumar diferència

        while (index < valors.size()) {
            Block blockY = new Block(8, 8, "Y", COMPRESS_RATIO);
            index += readBlock(blockY, valors, index);
            blockY.inverseQuantizationY();
            blockY.inverseDCT();
            arrayOfBlocksY[bi][bj] = blockY;

            Block blockCb = new Block(8, 8, "Cb", COMPRESS_RATIO);
            index += readBlock(blockCb, valors, index);
            blockCb.inverseQuantizationC();
            blockCb.inverseDCT();
            arrayOfBlocksCb[bi][bj] = blockCb;

            Block blockCr = new Block(8, 8, "Cr", COMPRESS_RATIO);
            index += readBlock(blockCr, valors, index);
            blockCr.inverseQuantizationC();
            blockCr.inverseDCT();
            arrayOfBlocksCr[bi][bj] = blockCr;

            ++bj;
            if (bj >= nBlocksX && bi < nBlocksY) {
                ++bi;
                bj = 0;
            }
        }

        @SuppressWarnings("unchecked")
        Triplet<Byte, Byte, Byte> Pixels [][] = new Triplet[nBlocksY*8][nBlocksX*8];

        for (int y = 0; y < nBlocksY; ++y) {
            for (int x = 0; x < nBlocksX; ++x) {

                int offsetX = x * 8;
                int offsetY = y * 8;
                for (int i = 0; i < 8; ++i) {
                    for (int j = 0; j < 8; ++j) {
                        int Y = arrayOfBlocksY[y][x].getDCTValue(i, j);
                        int Cb = arrayOfBlocksCb[y][x].getDCTValue(i, j);
                        int Cr = arrayOfBlocksCr[y][x].getDCTValue(i, j);
                        Pixels[offsetY + i][offsetX + j] = YCbCrToRGB(Y, Cb, Cr);
                    }
                }
            }
        }

        String capçaleraStr = "P6\n" + nBlocksX * 8 + " " + nBlocksY * 8 + '\n' + "255\n";
        byte[] capçalera = capçaleraStr.getBytes();
        byte[] returnData = new byte[capçalera.length + nBlocksX * nBlocksY * 64 * 3];

        for (int i = 0; i < capçalera.length; ++i) returnData[i] = capçalera[i];

        index = capçalera.length;

        int f = nBlocksY * 8;
        int c = nBlocksX * 8;
        for (int i = 0; i < f; ++i) {
            for (int j = 0; j < c; ++j) {
                returnData[index] = Pixels[i][j].getFirst();
                ++index;
                returnData[index] = Pixels[i][j].getSecond();
                ++index;
                returnData[index] = Pixels[i][j].getThird();
                ++index;
            }
        }

        Path p = Paths.get("/home/usuario/Escritorio/3r-1r/PROP/projecte-prop/testing_files/ppm_images/AAAA_out2.ppm");
        try {
            Files.write(p, returnData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter de la extensión del archivo
     * @return Extension del archivo descomprimido
     */
    public String getExtension() {
        return "_decompressed.ppm";
    }

    /**
     * Funcion para canviar de YCbCr a base de colores RGB.
     * @pre -
     * @post devuelve una tripleta de Bytes con el valor del pixel en RGB.
     * @param Y Componente Y del pixel.
     * @param Cb Componente Cb del pixel.
     * @param Cr Componente Cr del pixel.
     * @return Devuelve el valor del pixel en la base de colores RGB en una tripleta.
     */
    private Triplet<Byte, Byte, Byte> YCbCrToRGB(int Y, int Cb, int Cr) {
        Triplet<Integer, Integer, Integer> retorn = new Triplet<Integer, Integer, Integer>();
        int R = ((int) (Y + 1.402 * (Cr - 128)));
        int G = ((int) (Y - 0.34414 * (Cb-128) - 0.71414*(Cr-128)));
        int B = ((int) (Y + 1.772 * (Cb-128)));
        if (R > 255) R = 255;
        else if (R < 0) R = 0;
        if (G > 255) G = 255;
        else if (G < 0) G = 0;
        if (B > 255) B = 255;
        else if (B < 0) B = 0;

        return new Triplet<Byte, Byte, Byte>((byte)R, (byte)G, (byte)B);
    }

    /**
     * Función que convierte un array de bytes a una cadena de bits representada con enteros.
     * @pre bits tiene el tamaño suficiente para que quepa la representación en bits de la cadena de bytes de s
     * @post bits continene el valor en binario del array de bytes
     * @param s Array de bytes.
     * @param bits Cadena de bits representada con un array de enteros.
     */
    private static void byteToBin(byte[] s, int[] bits) {
        int index = 0;
        for (int i = 0; i < s.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (((s[i] >> 7 - j) & 1) == 1) bits[index] = 1;
                else bits[index] = 0;
                ++index;
            }
        }
    }

    /**
     * Funcion que lee un bloque.
     * @pre data contiene el contenido del archivo y el parámetro i < data.size().
     * @post devuelve un bloque con los valores leidos
     * @param data Array de la que se leen los datos.
     * @param i Indice del array en el que se empieza a leer.
     * @param tipus Tipo de bloque que vamos a leer.
     * @return Devuelve el bloque con todos los valores leidos de data.
     */
    private static int readBlock(Block block, ArrayList<Integer> data, int i) {
        return block.zigzagInvers(data, i);
    }
}

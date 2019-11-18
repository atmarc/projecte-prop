import java.util.ArrayList;

/*!
 *  \brief     Extension de la clase Decompressor mediante el algoritmo JPEG.
 *  \details
 *  \author    Marc Amoros
 */
public class Decompressor_JPEG extends Decompressor {

    public void decompress() {

        byte s [] = readAllBytes();

        int[] bits = new int[s.length * 8];
        charToBinString(s, bits);
        Huffman huffman = new Huffman();
        ArrayList<Integer> valors = new ArrayList<>();
        huffman.decode(bits, valors);

        final int nivellCompressio = valors.get(0);
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
            Block blockY = readBlock(valors, index, "Y");
            blockY.inverseQuantizationY();
            blockY.inverseDCT();
            arrayOfBlocksY[bi][bj] = blockY;
            index += 64;

            Block blockCb = readBlock(valors, index, "Cb");
            blockCb.inverseQuantizationC();
            blockCb.inverseDCT();
            arrayOfBlocksCb[bi][bj] = blockCb;
            index += 64;

            Block blockCr = readBlock(valors, index, "Cr");
            blockCr.inverseQuantizationC();
            blockCr.inverseDCT();
            arrayOfBlocksCr[bi][bj] = blockCr;
            index += 64;

            ++bj;
            if (bj >= nBlocksX && bi < nBlocksY) {
                ++bi;
                bj = 0;
            }
        }

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

        writeBytes(returnData);
    }

    public String getExtension() {
        return ".ppm";
    }

    /*

    R = Y+ 1.402 (Cr-128)
    G = Y - 0.34414 (Cb-128) - 0.71414 (Cr-128)
    B = Y + 1.772 (Cb-128)
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

    private static void charToBinString(byte[] s, int[] bits) {
        int index = 0;
        for (int i = 0; i < s.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (((s[i] >> 7 - j) & 1) == 1) bits[index] = 1;
                else bits[index] = 0;
                ++index;
            }
        }
    }

    // TODO: Pensar com posar que comencen els 0s
    private static Block readBlock(ArrayList<Integer> data, int i, String tipus) {
        Block blockY = new Block(8,8, tipus);
        blockY.zigzagInvers(data, i);
        return blockY;
    }

    private static void sumDC (Block [][] arrayBlock, int x, int y) {
        int value;
        if (x > 0) {
            value = arrayBlock[y][x - 1].getDCTValue(0,0) + arrayBlock[y][x].getDCTValue(0,0);
            arrayBlock[x][y].setDCTValue(0,0, value);
        }
        else if (y > 0) {
            value = arrayBlock[y - 1][arrayBlock[0].length - 1].getDCTValue(0,0) + arrayBlock[y][x].getDCTValue(0,0);
            arrayBlock[x][y].setDCTValue(0,0, value);
        }
    }
}

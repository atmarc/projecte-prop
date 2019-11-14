import FileManager.FileManager;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;

import static java.lang.Integer.parseInt;

public class JPEGDecompressor extends Decompressor {


    public void decompress(String path) {

        byte s [] = new byte[0];
        try {
            s = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String file = charToBinString(s);
        Huffman huffman = new Huffman();
        ArrayList<Integer> valors = huffman.decode(file);

        final int nivellCompressio = valors.get(0);
        final int nBlocksX = valors.get(1);
        final int nBlocksY = valors.get(2);
        final int HEIGHT = valors.get(3);
        final int WIDTH = valors.get(4);

        Block[][] arrayOfBlocksY = new Block[nBlocksX][nBlocksY];
        Block[][] arrayOfBlocksCb = new Block[nBlocksX][nBlocksY];
        Block[][] arrayOfBlocksCr = new Block[nBlocksX][nBlocksY];

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

        String capçaleraStr = "P6\n" + HEIGHT + ' ' + WIDTH + '\n' + "255\n";
        byte[] capçalera = capçaleraStr.getBytes();
        byte[] returnData = new byte[capçalera.length + nBlocksX*nBlocksY*64*3 + 1];

        for (int i = 0; i < capçalera.length; ++i) returnData[i] = capçalera[i];

        index = capçalera.length + 1;

        for (int y = 0; y < nBlocksY; ++y) {
            for (int x = 0; x < nBlocksX; ++x) {

                for (int i = 0; i < 8; ++i) {
                    for (int j = 0; j < 8; ++j) {
                        returnData[index] = (byte) arrayOfBlocksY[y][x].getDCTValue(i, j);
                        ++index;
                        returnData[index] = (byte) arrayOfBlocksCb[y][x].getDCTValue(i, j);
                        ++index;
                        returnData[index] = (byte) arrayOfBlocksCr[y][x].getDCTValue(i, j);
                        ++index;
                    }
                }

            }
        }

        try (BufferedOutputStream bufferedOutputStream =
                     new BufferedOutputStream(new FileOutputStream("testing_files/imgDescompr.ppm"))) {

            bufferedOutputStream.write(returnData);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static String charToBinString(byte[] s) {
        String retorn = "";
        for (int i = 0; i < s.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (((s[i] >> 7 - j) & 1) == 1) retorn += "1";
                else retorn += "0";
            }
        }

        return retorn;
    }

    // TODO: Pensar com posar que comencen els 0s
    private static Block readBlock(ArrayList<Integer> data, int i, String tipus) {
        Block blockY = new Block(8,8, tipus);
        for (int x = 0; x < 8 && x < data.size(); ++x) {
            for (int y = 0; y < 8 && y < data.size(); ++y) {
                    blockY.setDCTValue(x, y, data.get(i));
                    ++i;
                }
            }
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

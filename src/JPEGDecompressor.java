import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.Integer.parseInt;

public class JPEGDecompressor extends Decompressor {


    public void decompress(String path) {

        byte s [] = new byte[0];
        try {
            s = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String file = "";

        for (int i = 0; i < s.length; ++i) {
            file += (char) s[i];
        }
        String[] data = file.split(",");
        // file = huffman.decode(file);

        final int nivellCompressio = parseInt(data[0]);
        final int nBlocksX = parseInt(data[1]);
        final int nBlocksY = parseInt(data[2]);

        Block[][] arrayOfBlocksY = new Block[nBlocksX][nBlocksY];
        Block[][] arrayOfBlocksCb = new Block[nBlocksX][nBlocksY];
        Block[][] arrayOfBlocksCr = new Block[nBlocksX][nBlocksY];

        int index = 3;
        int bi = 0, bj = 0;
        while (index < data.length) {
            // TODO: Mirar si entren 0 no s'ha de sumar 64
            arrayOfBlocksY[bi][bj] = readBlock(data, index);
            index += 64;

            arrayOfBlocksCb[bi][bj] = readBlock(data, index);
            index += 64;

            arrayOfBlocksCr[bi][bj] = readBlock(data, index);
            index += 64;

            ++bj;
            if (bj >= 8 && bi < 7) ++bi;
        }

        // Tornar a sumar diferència
        for (int i = 0; i < nBlocksY; ++i) {
            for (int j = 0; j < nBlocksX; ++j) {
                sumDC(arrayOfBlocksY, j, i);
                arrayOfBlocksY[i][j].inverseQuantizationY();
                arrayOfBlocksY[i][j].inverseDCT();

                sumDC(arrayOfBlocksCb, j, i);
                sumDC(arrayOfBlocksCr, j, i);
            }
        }



       // return "";
    }

    // TODO: Pensar com posar que comencen els 0s
    private static Block readBlock(String data[], int i) {
        Block blockY = new Block(8,8, "Y");
        boolean zero = false;
        for (int x = 0; x < 8 && x < data.length; ++x) {
            for (int y = 0; y < 8 && y < data.length; ++y) {
                int value;
                if (!zero) {
                    value = parseInt(data[i]);
                    if (value == 0) zero = true;
                    blockY.setDCTValue(x, y, parseInt(data[i]));
                    ++i;
                }
                else {
                    blockY.setDCTValue(x, y, 0);
                }
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

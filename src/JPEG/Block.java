package JPEG;

import static java.lang.Math.*;

public class Block {

    private int valors[][];
    private int DCTvalors[][];
    private int width;
    private int height;

    final double PI = Math.PI;
    final double sqrt2 = sqrt(2);

    // Quantization table
    final int[][] QT = new int[][] {
        {16, 11, 10, 16, 24, 40, 51, 61},
        {12, 12, 14, 19, 26, 58, 60, 55},
        {14, 13, 16, 24, 40, 57, 69, 56},
        {14, 17, 22, 29, 51, 87, 80, 62},
        {18, 22, 37, 56, 68, 109, 103, 77},
        {24, 35, 55, 64, 81, 104, 113, 92},
        {49, 64, 78, 87, 103, 121, 120, 101},
        {72, 92, 95, 98, 112, 100, 103, 99}
    };

    public Block() {
    }

    public Block(int width, int height) {
        this.valors = new int[width][height];
        this.DCTvalors = new int[width][height];
        this.height = height;
        this.width = width;
    }

    public int getValue(int i, int j) {
        return valors[i][j];
    }

    public void setValue(int i, int j, int value) {
        valors[i][j] = value;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void DCT() {
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {

                double sumatori = 0;
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        sumatori += valors[y][x] * cos((2 * x + 1) * j * PI/ 16) * cos((2 * y + 1) * i * PI/ 16);
                    }
                }
                double endValue = 0.25 * sumatori;
                if (i == 0) endValue *= 1/sqrt2;
                if (j == 0) endValue *= 1/sqrt2;

                // Quantization
                DCTvalors[i][j] = (int) round(endValue / QT[i][j]);
            }
        }
    }

    public void print (int i) {
        for (int x = 0; x < height; ++x) {
            String aux = "";
            for (int y = 0; y < width; ++y) {
                if (i == 0) aux += valors[x][y] + " ";
                if (i == 1) aux += DCTvalors[x][y] + " ";
            }
            System.out.println(aux);
        }
    }

    public void printBlock () {
        print(0);
    }

    public void printBlockDCT () {
        print(1);
    }
}

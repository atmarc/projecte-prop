import static java.lang.Math.*;

public class Block {

    private int valors[][];
    private int DCTvalors[][];
    private int width;
    private int height;
    private String type;

    final double PI = Math.PI;
    final double sqrt2 = sqrt(2);

    // Quantization table Luminance
    final int[][] QTY = new int[][] {
        {16, 11, 10, 16, 24, 40, 51, 61},
        {12, 12, 14, 19, 26, 58, 60, 55},
        {14, 13, 16, 24, 40, 57, 69, 56},
        {14, 17, 22, 29, 51, 87, 80, 62},
        {18, 22, 37, 56, 68, 109, 103, 77},
        {24, 35, 55, 64, 81, 104, 113, 92},
        {49, 64, 78, 87, 103, 121, 120, 101},
        {72, 92, 95, 98, 112, 100, 103, 99}
    };

    // Quantization table Chrominance
    final int[][] QTCr = new int [][] {
            {17, 18, 24, 47, 99, 99, 99, 99},
            {18, 21, 26, 66, 99, 99, 99, 99},
            {24, 26, 56, 99, 99, 99, 99, 99},
            {47, 66, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99}
    };

    public Block() {
    }

    public Block(int width, int height, String type) {
        this.valors = new int[width][height];
        this.DCTvalors = new int[width][height];
        this.height = height;
        this.width = width;
        this.type = type;
    }

    public int getValue(int i, int j) {
        return valors[i][j];
    }

    public int getDCTValue(int i, int j) {
        return DCTvalors[i][j];
    }

    public void setValue(int i, int j, int value) {
        valors[i][j] = value;
    }

    public void setDCTValue(int i, int j, int value) {
        DCTvalors[i][j] = value;
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
                        sumatori += valors[y][x] * cos((2 * x + 1) * j * PI/ (2.0*width)) * cos((2 * y + 1) * i * PI/ (2.0*height));
                    }
                }
                double endValue = 0.25 * sumatori;
                if (i == 0) endValue *= 1/sqrt2;
                if (j == 0) endValue *= 1/sqrt2;

                // Quantization
                if (type.equals("Y")) {
                    DCTvalors[i][j] = (int) round(endValue / QTY[i][j]);
                }
                else {
                    DCTvalors[i][j] = (int) round(endValue / QTCr[i][j]);
                }
            }
        }
    }

    public void inverseDCT() {
        int auxArray[][] = new int [height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {

                double sumatori = 0;
                for (int i = 0; i < height; ++i) {
                    for (int j = 0; j < width; ++j) {
                        double aux = 1.0;
                        if (i == 0) aux *= 1/sqrt2;
                        if (j == 0) aux *= 1/sqrt2;
                        aux *= DCTvalors[i][j];
                        aux *= cos(((2 * x + 1) * j * PI)/ (2.0*width)) * cos((2 * y + 1) * i * PI/ (2.0*height));

                        sumatori += aux;
                    }
                }
                // Tornem a sumar els 128 que haviem restat per centrar a 0 els valors
                auxArray[y][x] = (int) round(0.25 * sumatori + 128);

                // Vigilem que cap valor faci overflow
                if (auxArray[y][x] < 0) auxArray[y][x] = 0;
                else if (auxArray[y][x] > 255) auxArray[y][x] = 255;
            }
        }
        DCTvalors = auxArray;
    }

    public String zigzag() {
        int row = 0, col = 0;
        boolean row_inc = false;
        int m = height;
        int n = width;

        int mn = Math.min(m, n);

        String retorn = "";

        for (int len = 1; len <= mn; ++len) {
            for (int i = 0; i < len; ++i) {

                retorn += DCTvalors[row][col] + ",";

                if (i + 1 == len)
                    break;

                if (row_inc) {
                    ++row;
                    --col;
                } else {
                    --row;
                    ++col;
                }
            }

            if (len == mn)
                break;

            if (row_inc) {
                ++row;
                row_inc = false;
            } else {
                ++col;
                row_inc = true;
            }
        }

        if (row == 0) {
            if (col == m - 1)
                ++row;
            else
                ++col;
            row_inc = true;
        } else {
            if (row == n - 1)
                ++col;
            else
                ++row;
            row_inc = false;
        }

        int MAX = Math.max(m, n) - 1;
        for (int len, diag = MAX; diag > 0; --diag) {

            if (diag > mn)
                len = mn;
            else
                len = diag;

            for (int i = 0; i < len; ++i) {
                retorn += DCTvalors[row][col] + ",";

                if (i + 1 == len)
                    break;

                if (row_inc) {
                    ++row;
                    --col;
                } else {
                    ++col;
                    --row;
                }
            }

            if (row == 0 || col == m - 1) {
                if (col == m - 1)
                    ++row;
                else
                    ++col;

                row_inc = true;
            }

            else if (col == 0 || row == n - 1) {
                if (row == n - 1)
                    ++col;
                else
                    ++row;

                row_inc = false;
            }
        }

        /*if (retorn.charAt(retorn.length() - 1) == ',') {
            retorn = retorn.substring(0, retorn.length() - 1);
        }*/

        return retorn;
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

    public void inverseQuantizationY() {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                DCTvalors[y][x] *= QTY[y][x];
            }
        }
    }

    public void inverseQuantizationC() {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                DCTvalors[y][x] *= QTCr[y][x];
            }
        }
    }
}

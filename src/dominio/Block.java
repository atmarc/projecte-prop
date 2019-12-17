package dominio;

import java.util.ArrayList;
import java.util.LinkedList;

import static java.lang.Math.*;

/*!
 *  \brief     Clase auxiliar para la implementacion del algortimo JPEG. Representa un subconjunto de los píxeles de una
 *  imagen, normalmente de 8x8 pixeles. En ella encontramos alguna funcion de trasnformación de estos valores que se
 *  usan en el algorítmo de JPEG.
 *  \details
 *  \author    Marc Amorós
 */
public class Block {

    static final int beginZeros = 32767;
    static int countador = 0;
    /**
     * Valores originales del bloque
     */
    private int valors[][];

    /**
     * Valores del bloque tras aplicar-le la DCT.
     */
    private int DCTvalors[][];

    /**
     * Numero de columnas del bloque.
     */
    private int width;

    /**
     * Numero de filas del bloque.
     */
    private int height;

    /**
     * Tipo de bloque.
     */
    private String type;

    /**
     * Nivel de compressión.
     */
    private int nivellCompress;

    final double PI = Math.PI;
    final double sqrt2 = sqrt(2);

    /**
     * Tablas de Quantización para la luminancia.
     */
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

    /**
     * Tablas de Quantización para la crominancia.
     */
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

    /**
     * Constructora por defecto
     */
    public Block() {
    }

    /**
     * Constructora con definiciones de atributos.
     * @param width Número de columnas del bloque.
     * @param height Número de filas del bloque.
     * @param type Tipo de bloque
     */
    public Block(int width, int height, String type, int nivellCompress) {
        this.valors = new int[width][height];
        this.DCTvalors = new int[width][height];
        this.height = height;
        this.width = width;
        this.type = type;
        this.nivellCompress = nivellCompress;

        for (int i = 0; i < QTY.length; ++i)
            for (int j = 0; j < QTY[0].length; ++j) {
                QTY[i][j] *= (nivellCompress/5.0);
                QTCr[i][j] *= (nivellCompress/5.0);
            }
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

    /**
     * Función que aplica la trasformada discreta del coseno a los valores del bloque y guarda su resultado en DCTvalors.
     * @pre El atributo valors contiene todos los valores del bloque
     * @post El atributo DCTvalors contiene todos los valores del bloque después de palicarles la DCT i dividirlos entre la
     * la tabla de quantización
     */
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

    /**
     * Función que aplica la trasformada discreta del coseno inversa a los valores del bloque y guarda su resultado en
     * DCTvalors.
     * @pre El atributo DCTvalors contiene los valores después de aplicar la DCT.
     * @post El atributo DCTvalors contiene los valores después de aplicar la inversa de la DCT.
     */
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

    /**
     * Guarda en el array file a partir del índice los valores de DCTvalors recorridos en zigzag.
     * @pre El atributo DCTvalors contiene los valores después de aplicar la DCT.
     * @post File contiene los valores de DCTvalors haciendo zigzag.
     * @param file Array donde se van guardando los valores. Tiene tamaño mínimo 8 x 8
     */
    public void zigzag(LinkedList<Integer> file) {
        int row = 0, col = 0;
        boolean row_inc = false;
        int m = height;
        int n = width;
        int mn = Math.min(m, n);

        for (int len = 1; len <= mn; ++len) {
            for (int i = 0; i < len; ++i) {

                file.add(DCTvalors[row][col]);

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
                file.add(DCTvalors[row][col]);

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
        if (file.getLast() == 0) {
            for (int i = file.size() - 1; i >= 0; --i) {
                if (file.getLast() != 0) {
                    file.add(beginZeros);
                    break;
                }
                else file.removeLast();
            }
        }
    }

    /**
     * Guarda en DCTvalors el contenido de arr recorriendo DCTvalors en zigzag.
     * @pre index < arr.size()
     * @post El atributo DCTvalors contiene los valores después de guardarlos en zigzag.
     * @param arr Array de donde se leen los valores.
     * @param index Indice a partir del cual se empieza a leer de arr.
     */
    public int zigzagInvers(ArrayList<Integer> arr, int index) {
        int row = 0, col = 0;
        boolean row_inc = false;
        int m = height;
        int n = width;
        int mn = Math.min(m, n);

        String retorn = "";
        int dataIndex = 0;
        ArrayList<Integer> data = new ArrayList<>();
        // Tornem a possar els zeros
        boolean zeros = false;
        int indexTamDades = 0;
        for (int i = 0; i < m * n; ++i) {
            if (zeros) data.add(0);
            else if (arr.get(i + index) == beginZeros) {
                zeros = true;
                indexTamDades = i + 1;
                data.add(0);
            }
            else {
                data.add(arr.get(i + index));
            }
        }

        if (!zeros) indexTamDades = 65;

        for (int len = 1; len <= mn; ++len) {
            for (int i = 0; i < len; ++i) {
                DCTvalors[row][col] = data.get(dataIndex);
                ++dataIndex;

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

                DCTvalors[row][col] = data.get(dataIndex);
                ++dataIndex;

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
        return indexTamDades;
    }

    /**
     * Imprime valores del bloque, usado para debugar.
     * @param i
     */
    public void print (int i) {
        for (int x = 0; x < height; ++x) {
            String aux = "";
            for (int y = 0; y < width; ++y) {
                if (i == 0) aux += valors[x][y] + " ";
                if (i == 1) aux += DCTvalors[x][y] + " ";
            }
        }
    }

    public void printBlock () {
        print(0);
    }

    public void printBlockDCT () {
        print(1);
    }

    /**
     * Multiplica cada valor por la tabla de cuantization de lumincancia
     * @pre El atributo DCTvalors contiene los valores después de aplicar la DCT inversa.
     * @post El atributo DCTvalors contiene los valores después de multiplicarlos por la tabla de quantización.
     */
    public void inverseQuantizationY() {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                DCTvalors[y][x] *= QTY[y][x];
            }
        }
    }

    /**
     * Multiplica cada valor por la tabla de cuantization de crominancia
     * @pre El atributo DCTvalors contiene los valores después de aplicar la DCT inversa.
     * @post El atributo DCTvalors contiene los valores después de multiplicarlos por la tabla de quantización.
     */
    public void inverseQuantizationC() {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                DCTvalors[y][x] *= QTCr[y][x];
            }
        }
    }
}

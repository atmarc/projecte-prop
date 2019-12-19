import dominio.clases.Decompressor_LZ78;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class Decompressor_LZ78Test {

    @Test
    public void decompressA() {

        Decompressor_LZ78 decompressor = new Decompressor_LZ78();
        decompressor.setLength(8);
        decompressor.setDictionary(new ArrayList<>(decompressor.getLength()));
        decompressor.getDictionary().add(null);                              // para empezar desde la posicion 1

        byte[] resultA = new byte[13]; // 1 2 3 12 13 123 121
        resultA[0] = 1;
        resultA[1] = 2;
        resultA[2] = 3;
        resultA[3] = 1;
        resultA[4] = 2;
        resultA[5] = 1;
        resultA[6] = 3;
        resultA[7] = 1;
        resultA[8] = 2;
        resultA[9] = 3;
        resultA[10] = 1;
        resultA[11] = 2;
        resultA[12] = 1;

        byte[] indexA = new byte[7];
        byte[] offsetA = new byte[7];

        indexA[0] = (byte) 0;  offsetA[0] = (byte) 1;
        indexA[1] = (byte) 0;  offsetA[1] = (byte) 2;
        indexA[2] = (byte) 0;  offsetA[2] = (byte) 3;
        indexA[3] = (byte) 1;  offsetA[3] = (byte) 2;
        indexA[4] = (byte) 1;  offsetA[4] = (byte) 3;
        indexA[5] = (byte) 4;  offsetA[5] = (byte) 3;
        indexA[6] = (byte) 4;  offsetA[6] = (byte) 1;

        ArrayList<Byte> decomp = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            byte[] aux = new byte[1];
            aux[0] = indexA[i];
            byte[] word = decompressor.decompress(aux, offsetA[i]);
            for (byte digit : word) decomp.add(digit);
        }

        boolean equal = true;
        for (int i = 0; i < decomp.size(); i++) {
            equal = equal && decomp.get(i) == resultA[i];
        }

        assertTrue(equal);
    }

    @Test
    public void decompressB() {

        Decompressor_LZ78 decompressor = new Decompressor_LZ78();
        decompressor.setLength(8);
        decompressor.setDictionary(new ArrayList<>(decompressor.getLength()));
        decompressor.getDictionary().add(null);                              // para empezar desde la posicion 1

        byte[] resultB = new byte[12]; // 1 2 3 12 13 123 12
        resultB[0] = 1;
        resultB[1] = 2;
        resultB[2] = 3;
        resultB[3] = 1;
        resultB[4] = 2;
        resultB[5] = 1;
        resultB[6] = 3;
        resultB[7] = 1;
        resultB[8] = 2;
        resultB[9] = 3;
        resultB[10] = 1;
        resultB[11] = 2;

        byte[] indexB = new byte[7];
        byte[] offsetB = new byte[7];

        indexB[0] = (byte) 0;  offsetB[0] = (byte) 1;
        indexB[1] = (byte) 0;  offsetB[1] = (byte) 2;
        indexB[2] = (byte) 0;  offsetB[2] = (byte) 3;
        indexB[3] = (byte) 1;  offsetB[3] = (byte) 2;
        indexB[4] = (byte) 1;  offsetB[4] = (byte) 3;
        indexB[5] = (byte) 4;  offsetB[5] = (byte) 3;
        indexB[6] = (byte) 4;  offsetB[6] = (byte) 0;


        ArrayList<Byte> decomp = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            byte[] aux = new byte[1];
            aux[0] = indexB[i];
            byte[] word = decompressor.decompress(aux, offsetB[i]);
            for (byte digit : word) decomp.add(digit);
        }


        boolean equal = true;
        for (int i = 0; i < decomp.size(); i++) {
            equal = equal && decomp.get(i) == resultB[i];
        }

        assertTrue(equal);
    }
}
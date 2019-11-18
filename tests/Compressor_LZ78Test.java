import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

/*!
 *  \brief     Clase de tests unitarios para Compressor_LZ78
 *  \details
 *  \author    Edgar Perez
 */
public class Compressor_LZ78Test {

    private Tree tree;
    private byte[] wordA, wordB;

    private Compressor_LZ78 compressor;
    private ArrayList<Compressor_LZ78.Pair> resultA, resultB;



    @Before
    public void setUp() throws Exception {

        compressor = new Compressor_LZ78();
        compressor.next_index = 0;
        tree = new Tree(1);
    }


    @Test
    public void compressA() {

        wordA = new byte[13]; // 1 2 3 12 13 123 121
        wordA[0] = 1;
        wordA[1] = 2;
        wordA[2] = 3;
        wordA[3] = 1;
        wordA[4] = 2;
        wordA[5] = 1;
        wordA[6] = 3;
        wordA[7] = 1;
        wordA[8] = 2;
        wordA[9] = 3;
        wordA[10] = 1;
        wordA[11] = 2;
        wordA[12] = 1;

        resultA = new ArrayList<>();
        resultA.add(new Compressor_LZ78.Pair(0, (byte) 0)); // -
        resultA.add(new Compressor_LZ78.Pair(0, (byte) 1)); // 1
        resultA.add(new Compressor_LZ78.Pair(0, (byte) 2)); // 2
        resultA.add(new Compressor_LZ78.Pair(0, (byte) 3)); // 3
        resultA.add(new Compressor_LZ78.Pair(1, (byte) 2)); // 12
        resultA.add(new Compressor_LZ78.Pair(1, (byte) 3)); // 13
        resultA.add(new Compressor_LZ78.Pair(4, (byte) 3)); // 123
        resultA.add(new Compressor_LZ78.Pair(4, (byte) 1)); // 121

        boolean new_searching = true;
        for (byte b : wordA) {
            new_searching = compressor.compress(b, tree, new_searching);
        }

        boolean equal = true;
        ArrayList<Compressor_LZ78.Pair> comp_file = compressor.comp_file;
        for (int i = 1; i < comp_file.size(); i++) {
            Compressor_LZ78.Pair pair = comp_file.get(i);
            equal = equal && (pair.index == resultA.get(i).index) && (pair.offset == resultA.get(i).offset);
        }

        assertTrue(equal);
    }

    @Test
    public void compressB() {

        wordB = new byte[12]; // 1 2 3 12 13 123 12
        wordB[0] = 1;
        wordB[1] = 2;
        wordB[2] = 3;
        wordB[3] = 1;
        wordB[4] = 2;
        wordB[5] = 1;
        wordB[6] = 3;
        wordB[7] = 1;
        wordB[8] = 2;
        wordB[9] = 3;
        wordB[10] = 1;
        wordB[11] = 2;

        resultB = new ArrayList<>();
        resultB.add(new Compressor_LZ78.Pair(0, (byte) 0)); // -
        resultB.add(new Compressor_LZ78.Pair(0, (byte) 1)); // 1
        resultB.add(new Compressor_LZ78.Pair(0, (byte) 2)); // 2
        resultB.add(new Compressor_LZ78.Pair(0, (byte) 3)); // 3
        resultB.add(new Compressor_LZ78.Pair(1, (byte) 2)); // 12
        resultB.add(new Compressor_LZ78.Pair(1, (byte) 3)); // 13
        resultB.add(new Compressor_LZ78.Pair(4, (byte) 3)); // 123
        resultB.add(new Compressor_LZ78.Pair(4, (byte) 0)); // 12

        boolean new_searching = true;
        for (byte b : wordB) {
            new_searching = compressor.compress(b, tree, new_searching);
        }

        boolean equal = true;
        ArrayList<Compressor_LZ78.Pair> comp_file = compressor.comp_file;
        for (int i = 1; i < comp_file.size(); i++) {
            Compressor_LZ78.Pair pair = comp_file.get(i);
            equal = equal && (pair.index == resultB.get(i).index) && (pair.offset == resultB.get(i).offset);
        }

        assertTrue(equal);
    }
}
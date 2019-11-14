import SearchTree.Tree;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;

public class LZ78Decompressor {

    private ArrayList<byte[]> dictionary;
    private int length;

    public LZ78Decompressor() {}

    public void decompressor(String inputPath, String outputPath) {

        try {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(inputPath));
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(outputPath));

            byte[] singleByte = new byte[1], index = new byte[4];
            if (reader.read(index) < 0) throw new Exception();

            int i = 1;
            length = new BigInteger(index).intValue();

            dictionary = new ArrayList<>(length);
            dictionary.add(null); // para empezar desde la posicion 1

            // 1 + 1 Byte
            index = new byte[1];
            for (; i < 128 && reader.read(index) >= 0 && reader.read(singleByte) >= 0; i++)
                writer.write(decompress(index, singleByte[0]));
            // 2 + 1 Byte
            index = new byte[2];
            for (; i < 32768 && reader.read(index) >= 0 && reader.read(singleByte) >= 0; i++)
                writer.write(decompress(index, singleByte[0]));
            // 3 + 1 Byte
            index = new byte[3];
            for (; i < 8388608 && reader.read(index) >= 0 && reader.read(singleByte) >= 0; i++)
                writer.write(decompress(index, singleByte[0]));
            // 4 + 1 Byte
            index = new byte[4];
            for (; reader.read(index) >= 0 && reader.read(singleByte) >= 0; i++)
                writer.write(decompress(index, singleByte[0]));


            reader.close();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] decompress(byte[] indexB, byte offset) {

        int index = new BigInteger(indexB).intValue();

        byte[] word;

        if (index == 0) {
            word = new byte[1];
            word[0] = offset;
        } else {
            byte[] prefix = dictionary.get(index);
            word = new byte[prefix.length + 1];
            System.arraycopy(prefix, 0, word, 0, prefix.length);
            if (dictionary.size() < length - 1)
                word[prefix.length] = offset;
        }

        dictionary.add(word);
        return word;
    }

}

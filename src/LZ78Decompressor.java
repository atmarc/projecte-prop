import java.math.BigInteger;
import java.util.ArrayList;

public class LZ78Decompressor extends Decompressor {

    private ArrayList<byte[]> dictionary;
    private int length;

    public LZ78Decompressor() {}

    public void decompress() {

        byte[] singleByte = new byte[1], index = new byte[4];
        readNBytes(index);

        int i = 1;
        length = new BigInteger(index).intValue();

        dictionary = new ArrayList<>(length);
        dictionary.add(null); // para empezar desde la posicion 1

        // 1 + 1 Byte
        index = new byte[1];
        for (; i < 128 && readNBytes(index) >= 0 && readNBytes(singleByte) >= 0; i++)
            writeBytes(decompress(index, singleByte[0]));
        // 2 + 1 Byte
        index = new byte[2];
        for (; i < 32768 && readNBytes(index) >= 0 && readNBytes(singleByte) >= 0; i++)
            writeBytes(decompress(index, singleByte[0]));
        // 3 + 1 Byte
        index = new byte[3];
        for (; i < 8388608 && readNBytes(index) >= 0 && readNBytes(singleByte) >= 0; i++)
            writeBytes(decompress(index, singleByte[0]));
        // 4 + 1 Byte
        index = new byte[4];
        for (; readNBytes(index) >= 0 && readNBytes(singleByte) >= 0; i++)
            writeBytes(decompress(index, singleByte[0]));

        closeReader();
        closeWriter();

    }

    private byte[] decompress(byte[] indexB, byte offset) {

        int index = new BigInteger(indexB).intValue();

        byte[] word;

        if (index == 0) {
            word = new byte[1];
            word[0] = offset;
        }
        else if (dictionary.size() == length - 1){
            word = dictionary.get(index);
        }
        else {
            byte[] prefix = dictionary.get(index);
            word = new byte[prefix.length + 1];
            System.arraycopy(prefix, 0, word, 0, prefix.length);
            word[prefix.length] = offset;
        }

        dictionary.add(word);
        return word;
    }

    String getExtension() {
        return ".txt";
    }

}

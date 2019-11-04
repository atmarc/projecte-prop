package LZ78;

import SearchTree.Tree;

import java.io.*;
import java.util.ArrayList;

public class LZ78_Decompressor {

    private ArrayList<ArrayList<Byte>> dictionary;

    public LZ78_Decompressor() {
        dictionary = new ArrayList<>();
    }

    public void TXdecompressor(String filePath) {

        try {
            FileInputStream in = new FileInputStream(filePath);

            byte[] buffer = new byte[5];

            while (in.read(buffer) != -1)
                dictionary.add(decompress(buffer));

            in.close();

            write_decompressed_file("./testing_files/lz78.out");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Byte> decompress(byte[] buffer) {

        int index = (buffer[0] & 0xff000000) | (buffer[1] & 0x00ff0000) | (buffer[2] & 0x0000ff00) | (buffer[3] & 0x000000ff);
        byte data = buffer[5];

        ArrayList<Byte> subword = new ArrayList<>();

        if (index != 0)
            subword = dictionary.get(index);

        subword.add(data);
        return subword;
    }

    private void write_decompressed_file(String path) throws IOException {
        FileOutputStream file = new FileOutputStream(path);

        for (ArrayList<Byte> subword : dictionary)
            for (Byte character : subword)
                file.write(character);

        file.close();
    }
}

package LZ78;

import FileManager.FileManager;
import SearchTree.Tree;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class LZ78_Compressor {

    private ArrayList<Pair> comp_file;
    private int previous_index;

    public LZ78_Compressor() {
        comp_file = new ArrayList<>();
    }

    public void TXcompressor(String filePath) {

        try {
            Tree tree = new Tree();
            FileInputStream in = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];

            previous_index = 0;
            while (in.read(buffer) != -1) compress(buffer, tree);
            in.close();

            if (previous_index > 0) {
                // Acabar insercion final.
            }

            write_compressed_file("./testing_files/lz78.bin");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compress(byte[] word, Tree tree) {

        // Retorna el estado en el que se encuentra.
        // = 0 -> todo ha quedado insertado
        // > 0 -> hay que continuar llamando a busquedas

        // comp_file.add(new Pair(-1, (byte) 0)); // lo tiene que hacer la controladora

        int index = tree.find(word, 0);

        for (int i = 1; i < word.length; i++) {
            if (index == comp_file.size()) {
                comp_file.add(new Pair(previous_index, word[i]));
                previous_index = 0;
                index = tree.find(word, ++i);
            }
            else {
                previous_index = index;
                index = tree.findNextByte(word[i]);
            }
        }
    }

    private void prepare_writing(int bytes) {

        if (bytes == 1) {

        } else if (bytes == 2) {

        } else {

        }
    }

    private int calculoBaseIndice() {
        int s = comp_file.size();
        if (s < 128) return 1;
        if (s < 32767) return 2;
        return 4;
    }

    private void write_compressed_file(String path) throws IOException {

        FileOutputStream file = new FileOutputStream(path);

        for (Pair entry : comp_file) {
            file.write(entry.index);
            file.write(entry.offset);
        }

    }

}

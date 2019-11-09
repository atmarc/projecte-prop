package LZ78;

import FileManager.FileManager;
import SearchTree.Tree;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class LZ78_Compressor {

    final static int BUFF_SIZE = 1024; // 16KB
    private static ArrayList<ArrayList<Pair>> files;    // Conjunto de archivos comprimidos
    private ArrayList<Pair> comp_file;                  // Archivo sobre el que se escribe la compresion actual
    private int previous_index;

    public LZ78_Compressor() {
        files = new ArrayList<>();
        add_comp_file();
    }

    private void add_comp_file() {
        files.add(new ArrayList<>());
        comp_file = files.get(files.size() - 1);
        comp_file.add(new Pair(0, (byte) 0x00));
    }

    public void TXcompressor(String filePath) {

        try {

            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(filePath));

            int B;
            Tree tree = new Tree(1);
            previous_index = 0;
            boolean new_searching = true;

            while ((B = reader.read()) > 0) {
                new_searching = compress((byte) (B & 0xFF), tree, new_searching);
            }
            reader.close();

            if (!new_searching) compress((byte) 0x00, tree, false);

            /* ---------------------------------------------- TESTING ---------------------------------------------- */
            System.out.println("Texto comprimido:\n");
            if (false) {
                for (int i = 1; i < comp_file.size(); i++) {
                    Pair pair = comp_file.get(i);

                    System.out.print(i);
                    System.out.print(" ->\t");
                    System.out.print(pair.index);
                    System.out.print("\t");
                    System.out.printf("%x", pair.offset);
                    System.out.print("\n");
                }
            }

            System.out.println("Tamano de la salida = " + comp_file.size() + "\n");
            /* ----------------------------------------------------------------------------------------------------- */

            write_compressed_file("./testing_files/lz78.out");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean compress(byte B, Tree tree, boolean new_searching) {

        // Retorna el estado en el que se encuentra.
        //  - true  -> Se ha anadido un mote nuevo. La siguiente entrada empezara a buscar desde arriba en el arbol.
        //  - false -> El mote buscado existe. Se solicita otro caracter.

        int index = tree.progressive_find(B, new_searching);

        if (index == comp_file.size()) {
            comp_file.add(new Pair(previous_index, B));
            previous_index = 0;
            return true;
        }

        previous_index = index;
        return false;
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

        BufferedOutputStream file = new BufferedOutputStream(new FileOutputStream(path));

        for (Pair entry : comp_file) {
            file.write(entry.index);
            file.write(entry.offset);
        }
        file.close();
    }

}

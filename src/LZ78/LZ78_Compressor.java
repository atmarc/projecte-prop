package LZ78;

import FileManager.FileManager;
import SearchTree.Tree;

import java.io.*;
import java.math.BigInteger;
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

    public void TXcompressor(String inputPath, String outputPath) {

        try {

            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(inputPath));

            int B;
            Tree tree = new Tree(1);
            previous_index = 0;
            boolean new_searching = true;

            while ((B = reader.read()) > 0) {
                new_searching = compress((byte) (B & 0xFF), tree, new_searching);
            }
            reader.close();

            if (!new_searching) compress((byte) 0x00, tree, false);

            /* ---------------------------------------------- TESTING ---------------------------------------------- /
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
            / ----------------------------------------------------------------------------------------------------- */

            // Set del tamano total del archivo comprimido
            comp_file.set(0, new Pair(comp_file.size() - 1, (byte) 0x00));

            write_compressed_file(outputPath);

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

    private void debug(byte[] buffer) {

        byte[] aux = new byte[buffer.length - 1];
        System.arraycopy(buffer, 0, aux, 0, buffer.length - 1);

        int test = new BigInteger(aux).intValue();
        if (test < 0)
            System.out.println(test);
    }

    private void write_compressed_file(String path) throws IOException {

        BufferedOutputStream file = new BufferedOutputStream(new FileOutputStream(path));
        int i = 1;
        byte[] buffer = new byte[4];
        int index = comp_file.size();

        buffer[0] = ((byte) ((index & 0xFF000000) >> 24));
        buffer[1] = ((byte) ((index & 0x00FF0000) >> 16));
        buffer[2] = ((byte) ((index & 0x0000FF00) >> 8));
        buffer[3] = ((byte) (index & 0x000000FF));

        file.write(buffer, 0, 4);

        buffer = new byte[2];
        for (; i < 128 && i < comp_file.size(); i++) { // 1 + 1 Byte
            buffer[0] = (byte) (comp_file.get(i).index & 0xFF);
            buffer[1] = comp_file.get(i).offset;

            debug(buffer);

            file.write(buffer, 0, 2);
        }

        buffer = new byte[3];
        for (; i < 32768 && i < comp_file.size(); i++) {    // 2 + 1 Byte
            index = comp_file.get(i).index;
            buffer[0] = ((byte) ((index & 0x0000FF00) >> 8));
            buffer[1] = ((byte) (index & 0x000000FF));
            buffer[2] = comp_file.get(i).offset;

            debug(buffer);

            file.write(buffer, 0, 3);
        }

        buffer = new byte[4];
        for (; i < 8388608 && i < comp_file.size(); i++) { // 3 + 1 Byte
            index = comp_file.get(i).index;
            buffer[0] = ((byte) ((index & 0x00FF0000) >> 16));
            buffer[1] = ((byte) ((index & 0x0000FF00) >> 8));
            buffer[2] = ((byte) (index & 0x000000FF));
            buffer[3] = comp_file.get(i).offset;

            debug(buffer);

            file.write(buffer, 0, 4);
        }

        buffer = new byte[5];
        for (; i < comp_file.size(); i++) {                 // 4 + 1 Byte
            index = comp_file.get(i).index;
            buffer[0] = ((byte) ((index & 0xFF000000) >> 24));
            buffer[1] = ((byte) ((index & 0x00FF0000) >> 16));
            buffer[2] = ((byte) ((index & 0x0000FF00) >> 8));
            buffer[3] = ((byte) (index & 0x000000FF));
            buffer[4] = comp_file.get(i).offset;

            debug(buffer);

            file.write(buffer, 0, 5);
        }

        file.close();
    }











}

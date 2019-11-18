import dominio.*;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Compressor_LZSSTest {

    private static boolean diffFiles(String name1, String name2) throws IOException {
        File f1 = new File(name1);
        File f2 = new File(name2);
        if (f1.length() != f2.length()) return false;
        BufferedInputStream in1 = new BufferedInputStream(new FileInputStream(f1.getPath()));
        BufferedInputStream in2 = new BufferedInputStream(new FileInputStream(f2.getPath()));
        int b1, b2;
        while ((b1 = in1.read()) != -1) {
            b2 = in2.read();
            if (b1 != b2) return false;
        }
        in1.close();
        in2.close();
        return true;
    }

    public static void main(String[] args) throws IOException {
        mainTests();
    }


    @Test
    public static void mainTests() throws IOException {

        Compressor_Controller compressor_lzss = new Compressor_Controller(1);
        Decompressor_Controller decompressor_lzss = new Decompressor_Controller("lzss");

        String[] files = {
                "./src/persistencia/testing_files/lzss/sample1",
                "./src/persistencia/testing_files/lzss/500kb",
                "./src/persistencia/testing_files/lzss/200kb",
                "./src/persistencia/testing_files/lzss/sample2",
                "./src/persistencia/testing_files/lzss/sample3"
        };

        for (int i = 0; i < files.length; ++i) {
            compressor_lzss.startCompression(files[i] + ".txt", null);
            System.out.println();
            decompressor_lzss.startDecompression(files[i] + ".lzss", null);
            System.out.println("Verdict: " + (diffFiles(files[i]+".txt", files[i] + "_decompressed.txt")
                    ? "\u001B[32m" + "OK! Files are equal." + "\u001B[0m"
                    : "\u001B[31m" + "Wrong!!!" + "\u001B[0m"));
            System.out.println();
            System.out.println("-------------------------------------------------");
        }
    }

}
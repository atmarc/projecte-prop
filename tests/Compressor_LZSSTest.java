import dominio.controladores.Compressor_Controller;
import dominio.controladores.Decompressor_Controller;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

    @Test
    public void testA() throws IOException {

        Compressor_Controller compressor_lzss = new Compressor_Controller(1);
        Decompressor_Controller decompressor_lzss = new Decompressor_Controller("lzss");

        String file = "./src/persistencia/testing_files/txt/maxdesp";
        //compressor_lzss.startCompression(file + ".txt", null);
        //decompressor_lzss.startDecompression(file + ".lzss", null);
        assertTrue(diffFiles(file+".txt", file + "_decompressed.txt"));

    }

    @Test
    public void testB() throws IOException {

        Compressor_Controller compressor_lzss = new Compressor_Controller(1);
        Decompressor_Controller decompressor_lzss = new Decompressor_Controller("lzss");

        String file = "./src/persistencia/testing_files/txt/nocoinc";
        //compressor_lzss.startCompression(file + ".txt", null);
        //decompressor_lzss.startDecompression(file + ".lzss", null);
        assertTrue(diffFiles(file+".txt", file + "_decompressed.txt"));

    }

    @Test
    public void testC() throws IOException {

        Compressor_Controller compressor_lzss = new Compressor_Controller(1);
        Decompressor_Controller decompressor_lzss = new Decompressor_Controller("lzss");

        String file = "./src/persistencia/testing_files/txt/500kb";
        //compressor_lzss.startCompression(file + ".txt", null);
        //decompressor_lzss.startDecompression(file + ".lzss", null);
        assertTrue(diffFiles(file+".txt", file + "_decompressed.txt"));

    }

    @Test
    public void testD() throws IOException {

        Compressor_Controller compressor_lzss = new Compressor_Controller(1);
        Decompressor_Controller decompressor_lzss = new Decompressor_Controller("lzss");

        String file = "./src/persistencia/testing_files/txt/1M";
        //compressor_lzss.startCompression(file + ".txt", null);
        //decompressor_lzss.startDecompression(file + ".lzss", null);
        assertTrue(diffFiles(file+".txt", file + "_decompressed.txt"));

    }

}
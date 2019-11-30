import dominio.Compressor_Controller;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class Compressor_JPEGTest {

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

        Compressor_Controller compressor_jpeg = new Compressor_Controller(3);
        String file = "./src/persistencia/testing_files/ppm/boxes_1";
        //compressor_jpeg.startCompression(file + ".ppm", null);
        assertTrue(diffFiles(file+"_test.jpeg", file + ".jpeg"));

    }

    @Test
    public void testB() throws IOException {

        Compressor_Controller compressor_jpeg = new Compressor_Controller(3);
        String file = "./src/persistencia/testing_files/ppm/image";
        //compressor_jpeg.startCompression(file + ".ppm", null);
        assertTrue(diffFiles(file+"_test.jpeg", file + ".jpeg"));
    }

    @Test
    public void testC() throws IOException {

        Compressor_Controller compressor_jpeg = new Compressor_Controller(3);
        String file = "./src/persistencia/testing_files/ppm/house_1";
        //compressor_jpeg.startCompression(file + ".ppm", null);
        assertTrue(diffFiles(file+"_test.jpeg", file + ".jpeg"));
    }

    @Test
    public void testD() throws IOException {

        Compressor_Controller compressor_jpeg = new Compressor_Controller(3);
        String file = "./src/persistencia/testing_files/ppm/west_1";
        //compressor_jpeg.startCompression(file + ".ppm", null);
        assertTrue(diffFiles(file+"_test.jpeg", file + ".jpeg"));
    }

    @Test
    public void testE() throws IOException {

        Compressor_Controller compressor_jpeg = new Compressor_Controller(3);
        String file = "./src/persistencia/testing_files/ppm/france-wallpaper";
        //compressor_jpeg.startCompression(file + ".ppm", null);
        assertTrue(diffFiles(file+"_test.jpeg", file + ".jpeg"));
    }

}
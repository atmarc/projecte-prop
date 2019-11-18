import dominio.*;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Decompressor_JPEGTest {


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

        Decompressor_Controller decompressor_jpeg = new Decompressor_Controller("jpeg");

        String[] files = {
                "../src/persistencia/ppm_images/boxes_1",
                "../src/persistencia/ppm_images/image",
                "../src/persistencia/ppm_images/house_1",
                "../src/persistencia/ppm_images/west_1",
                "../src/persistencia/ppm_images/france-wallpaper"
        };

        for (int i = 0; i < files.length; ++i) {
            decompressor_jpeg.startDecompression(files[i] + ".jpeg", null);
            System.out.println();
            System.out.println("Verdict: " + (diffFiles(files[i]+"_decompressed_test.ppm", files[i] + "_decompressed.ppm")
                    ? "\u001B[32m" + "OK! Files are equal." + "\u001B[0m"
                    : "\u001B[31m" + "Wrong!!!" + "\u001B[0m"));
            System.out.println();
            System.out.println("-------------------------------------------------");
        }
    }

}

import FileManager.FileManager;
import JPEG.JpegAlgorithm;

import java.util.ArrayList;

public class Application {
    public static void main(String [] args) throws Exception
    {
        System.out.println("Hola");
        System.out.println("ktal");
        ArrayList<String> paths = FileManager.readFolder("testing_files", ".ppm");

        JpegAlgorithm.compress(FileManager.readFile(paths.get(0)));

    }

}
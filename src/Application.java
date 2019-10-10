import FileManager.FileManager;

import java.util.ArrayList;

public class Application {
    public static void main(String [] args) throws Exception
    {
        System.out.println("Hola");
        System.out.println("ktal");
        ArrayList<String> paths = FileManager.readFolder("testing_files", ".txt");

        for (String e: paths) {
            System.out.println(e);
        }

    }

}
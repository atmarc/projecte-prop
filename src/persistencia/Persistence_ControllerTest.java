package persistencia;

import org.junit.Test;
import persistencia.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class Persistence_ControllerTest {

    @Test
    public void makeHierarchyTest() {

        /*
        * - ESTRUCTURA DEL ARBOL A TESTEAR -
        *
        * Dir testing_path:
        *   1.txt
        *   2.txt
        *   Dir 3:
        *       5.txt
        *       6.txt
        *   Dir 4:
        *       Dir 8:
        *           14.txt
        *           15.txt
        *       Dir 9:
        *           Dir 12:
                        13.txt
        *       7.txt
        *       10.txt
        *       11.txt
        * */

        HashMap<String,String> sample = new HashMap<>();
        sample.put("testing_path", "testing_path");
        sample.put("1.txt", "testing_path");
        sample.put("2.txt", "testing_path");
        sample.put("3", "testing_path");
        sample.put("4", "testing_path");
        sample.put("5.txt", "3");
        sample.put("6.txt", "3");
        sample.put("7.txt", "4");
        sample.put("8", "4");
        sample.put("9", "4");
        sample.put("10.txt", "4");
        sample.put("11.txt", "4");
        sample.put("12", "9");
        sample.put("13.txt", "12");
        sample.put("14.txt", "8");
        sample.put("15.txt", "8");

        HashMap<String,Integer> carpeta = new HashMap<>();
        carpeta.put("testing_path", 1);
        carpeta.put("1.txt", 0);
        carpeta.put("2.txt", 0);
        carpeta.put("3", 1);
        carpeta.put("4", 1);
        carpeta.put("5.txt", 0);
        carpeta.put("6.txt", 0);
        carpeta.put("7.txt", 0);
        carpeta.put("8", 1);
        carpeta.put("9", 1);
        carpeta.put("10.txt", 0);
        carpeta.put("11.txt", 0);
        carpeta.put("12", 1);
        carpeta.put("13.txt", 0);
        carpeta.put("14.txt", 0);
        carpeta.put("15.txt", 0);

        String path = ".\\src\\persistencia\\testing_files\\testing_path";
        Persistence_Controller pc = new Persistence_Controller();

        int[][] out = pc.makeHierarchy(path);
        boolean control = true;
        int[] dirs = out[0];
        int[] fathers = out[1];

        for (int i = 0; i < dirs.length; i++) {
            int calculated = dirs[i];
            System.out.println(pc.getName(i));
            int original = carpeta.get(pc.getName(i));
            control = control && calculated == original;
            if (!control) break;
        }

        for (int i = 0; i < fathers.length; i++) {
            String entry = pc.getName(fathers[i]);
            control = control && sample.get(pc.getName(i)).equals(entry);
            if (!control) break;
        }
       /*
        PRINTING
        int[] ints = out[1];
        for (int i = 0; i < ints.length; i++) {

            int efe = ints[i];
            System.out.println("Nombre: " + pc.getName(i));
            System.out.println("padre: " + pc.getName(efe));
            String carpeta = (out[0][i] == 1)?"Si":"No";
            System.out.println("carpeta: " + carpeta );
            System.out.println("ID: " + i);
            System.out.println("--------------------");
        }
        */
        assertTrue(control);
    }

    @Test
    public void modifyLong() {
    }
}
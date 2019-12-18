import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import dominio.Domain_Controller;
import persistencia.Persistence_Controller;

public class AutoCompress {
    private static String[] alg = { "LZ78", "LZSS", "LZW", "JPEG" };

    private static String getPathExtension(String path) {
        if (path == null)
            return "";
        int i = path.lastIndexOf('.');
        if (i > 0)
            return path.substring(i + 1);
        return "";
    }

    private static String getOutName(String path) {
        int pos = path.lastIndexOf('.');
        return path.substring(0, pos) + ".qr";
    }

    public static boolean comp(File a, File b) {
        return a.length() > b.length();
    }

    public static class aux implements Comparator<File> {

        @Override
        public int compare(File a, File b) {
            return (int) (a.length() - b.length());
        }
    }

    private static void computeData(int j) throws Exception {
        File root = new File("files/ansi");
        File[] c = root.listFiles();
        Arrays.sort(c, new aux());
        assert c != null;
        for (File s : c) {
            if (getPathExtension(s.getPath()).equals("txt")) {
                String inputPath = s.getPath();
                String outputPath = getOutName(inputPath);
                System.out.printf("%10s\t%11.2f", s.getName(), s.length()/1000.0);

                Persistence_Controller persistence_controller = Persistence_Controller.getPersistence_controller();
                Domain_Controller domain_controller = new Domain_Controller();
                domain_controller.setPersistence_controller(persistence_controller);

                domain_controller.compress(inputPath, outputPath, j);
            }
        }
    }

    public static void clean() {
        File root = new File("files/ansi");
        File[] c = root.listFiles();
        assert c != null;
        for (File s : c) {
            if (getPathExtension(s.getPath()).equals("qr")) {
                if (!s.delete()) System.out.println("The cleaning process has errors!");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.printf("  file_name\tsize(Kbits)\tcompression_ration\ttime(ms)\n");
        for (int i = 1; i < 2; i += 2) {
            System.out.println(alg[i]);
            computeData(i);
            clean();
        }
    }

}

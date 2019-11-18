import dominio.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class DriverLZWCompressor {
    private static final String OK = "OK!";
    private static final String BAD = "WRONG!!! La función no ha pasado el test";

    private void testConstructor() {
        Compressor_LZW compressor = new Compressor_LZW();
        System.out.println("No se puede verificar");
    }

    private void testCompressString(String filePathInput, String filePathCorrect, int testNumber) {
//        System.out.print("Test " + testNumber + ": ");
//        LZWCompressor compressor = new LZWCompressor();
//        try {
//            StringBuilder stringBuilder = new StringBuilder();
//            Scanner scanner = new Scanner(new File(filePathInput));
//            while (scanner.hasNext()) stringBuilder.append(scanner.nextLine());
//            ArrayList<Integer> actual = compressor.compressString(stringBuilder.toString());
//            scanner = new Scanner(new File(filePathCorrect));
//            ArrayList<Integer> esperado = new ArrayList<>();
//            while (scanner.hasNextInt()) esperado.add(scanner.nextInt());
//            if (actual.size() == esperado.size()) {
//                boolean ok = true;
//                for (int i = 0; ok && i < actual.size(); ++i) {
//                    if (!actual.get(i).equals(esperado.get(i))) ok = false;
//                }
//                System.out.println((ok ? OK : BAD));
//            }
//            else System.out.println(BAD);
//        }
//        catch (FileNotFoundException e) {
//            System.out.println("Test no valid. Fichero no encontrado\n" + e.getMessage());
//        }
    }

    private void testCompressPath() {

    }

    private void testCompressFile() {

    }

    private void printMenu() {
        System.out.println("Elige la función a verificar:\n" +
                "1 - compressString(String)\n" +
                "2 - compress(File)\n" +
                "3 - compress(filePath)\n" +
                "0 - salir");
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("\nDriver LZW Compressor\n");
        DriverLZWCompressor driver = new DriverLZWCompressor();
        driver.printMenu();
        Scanner scanner = new Scanner(System.in);
        int op = -1;
        while (op != 0 && (op = scanner.nextInt()) != 0) {
            switch (op) {
                case 1 : System.out.println("Se testea la función compressString(String)");
                    String p = "testing_files/Drivers-I_O/";
                    driver.testCompressString(p+"compressStringTest1.in", p+"compressStringTest1.cor", 1);
                    driver.testCompressString(p+"compressStringTest2.in", p+"compressStringTest2.cor", 2);
                    driver.testCompressString(p+"compressStringTest3.in", p+"compressStringTest3.cor", 3);
                    break;
                case 2 : System.out.println("Se testea la función compress(File)");
                    driver.testCompressFile();
                    break;
                case 3 : System.out.println("Se testea la función compress(filePath)");
                    driver.testCompressPath();
                    break;
                default: break;
            }
            System.out.println("Quiere verificar otra función? (y/n)");
            String c = scanner.next();
            if (c.equals("y")) driver.printMenu();
            else op = 0;
        }
    }
}

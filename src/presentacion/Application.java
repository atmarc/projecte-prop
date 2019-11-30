package presentacion;
import dominio.Domain_Controller;
import persistencia.Persistence_Controller;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Scanner;

public class Application {

    private static String getPathExtension(String path) {
        int i = path.lastIndexOf('.');
        if (i > 0) return path.substring(i+1);
        return null;
    }

    private static boolean existsFile(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static void main(String[] args) throws IllegalArgumentException {

        Persistence_Controller persistence_controller = new Persistence_Controller();
        Domain_Controller domain_controller = new Domain_Controller();

        domain_controller.setPersistence_controller(persistence_controller);

        Scanner in = new Scanner(System.in);
        int mode, route, alg;
        String inputPath;
        String outputPath = null;

        boolean valid;

        System.out.println(
                "Que tarea desea realizar?\n" +
                        "\t [0] - Comprimir archivo.\n" +
                        "\t [1] - Descomprimir archivo.");

        do {
            mode = in.nextInt();
            valid = (mode == 0 || mode == 1);
            if (!valid) System.out.println("Numero no valido. Debe introducir 0 o 1.");
        }
        while (!valid);

        String extension;
        if (mode == 0) { // comprimir
            System.out.println("Proporcione ruta del archivo que desea comprimir (archivos .txt/.ppm):");
            do {
                inputPath = in.next();
                extension = getPathExtension(inputPath);
                valid = (extension != null && (extension.equals("txt") || extension.equals("ppm"))) && existsFile(inputPath);
                if (!valid) System.out.println("O bien el archivo proporcionado no existe, o bien no es ni un .txt ni un .ppm .\nIntroduzca una ruta a un archivo valido:");
            }
            while(!valid);
        }
        else { // descomprimir
            System.out.println("Proporcione ruta del archivo que desea descomprimir (archivos .lz78/.lzss/.lzw/.jpeg):");
            do {
                inputPath = in.next();
                extension = getPathExtension(inputPath);
                valid = (extension != null && (extension.equals("lz78") || extension.equals("lzss") || extension.equals("lzw") || extension.equals("jpeg")));
                if (!valid) System.out.println("El archivo proporcionado no es de un tipo soportado.\nIntroduzca una ruta a un archivo valido (archivos .lz78/.lzss/.lzw/.jpeg):");
            }
            while (!valid);
        }

        System.out.println("Ruta del directorio salida:\n" +
                "\t [0] - Utilizar el mismo directorio que el archivo origen.\n" +
                "\t [1] - Proporcionar manualmente una ruta del directorio destino.\n" +
                "\t [2] - Utilizar el directorio 'src/persistencia/out/' .\n");

        do {
            route = in.nextInt();
            valid = (route == 0 || route == 1 || route == 2);
            if (!valid) System.out.println("Numero no valido. Debe introducir 0, 1 o 2.");
        }
        while (!valid);

        if (route == 1) {
            do {
                System.out.println("Introduzca la ruta del directorio salida (acabado en '/' para UNIX o en '\\' para WINDOWS):");
                outputPath = in.next();
                valid = (outputPath.charAt(outputPath.length() - 1) == '/' || outputPath.charAt(outputPath.length() - 1) == '\\');
                if (!valid) System.out.println("Ruta no valida, debe terminar en '/' ");
            }
            while (!valid);
            outputPath = FileSystems.getDefault().getPath(outputPath).normalize().toAbsolutePath().toString() + "/";
        }
        else if (route == 2) {
            outputPath = FileSystems.getDefault().getPath("./src/persistencia/out/").normalize().toAbsolutePath().toString() + "/";
        }

        if (mode == 0) { // Comprimir

            switch (extension) {
                case "txt":
                    System.out.println(
                            "Que algoritmo de compresion desea utilizar?\n" +
                                    "\t [0] - LZ-78\n" +
                                    "\t [1] - LZ-SS\n" +
                                    "\t [2] - LZ-W\n");
                    do {
                        alg = in.nextInt();
                        valid = (0 <= alg && alg <= 2);
                        if (!valid) System.out.println("Numero no valido. Debe introducir 0, 1 o 2.");
                    }
                    while(!valid);
                    break;

                case "ppm":
                    System.out.println(
                            "Que algoritmo de compresion desea utilizar?\n" +
                                    "\t [0] - JPEG");

                    do {
                        alg = in.nextInt() + 3;
                        valid = alg == 3;
                        if (!valid) System.out.println("Numero no valido. Debe introducir 0.");
                    }
                    while(!valid);
                    break;

                default: throw new IllegalArgumentException("Ha habido un error durante la ejecucion (switch extension)");
            }

            // Solucion temporal para que compile

            inputPath = "C:\\Users\\Edgar\\IdeaProjects\\projecte-prop\\testing_files\\big.txt";
            outputPath = "C:\\Users\\Edgar\\IdeaProjects\\projecte-prop\\testing_files\\big.eggo";

            int inputFile = persistence_controller.newInputFile(inputPath);
            int outputFile = persistence_controller.newOutputFile(outputPath);

            domain_controller.startCompression(inputFile, outputFile, alg);
        }
        else { // Descomprimir

            // Solucion temporal para que compile
            int inputFile = persistence_controller.newInputFile(inputPath);
            int outputFile = persistence_controller.newOutputFile(outputPath);

            ArrayList<Integer> inArr = new ArrayList<>();
            inArr.add(inputFile);
            ArrayList<Integer> outArr = new ArrayList<>();
            outArr.add(outputFile);
            domain_controller.startDecompression(inArr, outArr, extension);
        }
    }
}
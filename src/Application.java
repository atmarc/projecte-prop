import java.util.Scanner;

public class Application {

    private static String getPathExtension(String path) {

        int i = path.lastIndexOf('.');
        if (i > 0) return path.substring(i+1);
        return null;
    }

    public static void main(String[] args) throws IllegalArgumentException {

        Scanner in = new Scanner(System.in);
        int mode, route, alg;
        String inputPath, outputPath = null;

        boolean valid = false;

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
                valid = (extension != null && (extension.equals("txt") || extension.equals("ppm")));
                if (!valid) System.out.println("El archivo proporcionado no es ni un .txt ni un .ppm .\nIntroduzca una ruta a un archivo valido:");
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
                "\t [1] - Proporcionar manualmente una ruta del directorio destino.");

        do {
            route = in.nextInt();
            valid = (route == 0 || route == 1);
            if (!valid) System.out.println("Numero no valido. Debe introducir 0 o 1.");
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
        }

        if (mode == 0) { // Comprimir

            Compressor compressor = null;

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
                                    "\t [0] - LZ-78\n" +
                                    "\t [1] - JPEG\n");

                    do {
                        alg = in.nextInt() * 3;
                        valid = (alg == 0 || alg == 3);
                        if (!valid) System.out.println("Numero no valido. Debe introducir 0 o 1.");
                    }
                    while(!valid);
                    break;

                default: throw new IllegalArgumentException("Ha habido un error durante la ejecucion (switch extension)");
            }

            switch (alg) {
                case 0:
                    compressor = new Compressor_LZ78();
                    break;
                case 1:
                    compressor = new Compressor_LZSS();
                    break;
                case 2:
                    compressor = new Compressor_LZW();
                    break;
                case 3:
                    compressor = new Compressor_JPEG();
                    break;
                default:
                    throw new IllegalArgumentException("Ha habido un error durante la ejecucion (switch alg)");
            }
            compressor.startCompression(inputPath, outputPath);

        }
        else { // Descomprimir

            Decompressor decompressor = null;

            switch (extension){
                case "lz78":
                    decompressor = new Decompressor_LZ78();
                    break;
                case "lzss":
                    decompressor = new Decompressor_LZSS();
                    break;
                case "lzw":
                    decompressor = new Decompressor_LZW();
                    break;
                case "jpeg":
                    decompressor = new Decompressor_JPEG();
                    break;
                default:
                    throw new IllegalArgumentException("Ha habido un error durante la ejecucion (switch extension)");
            }

            decompressor.startDecompression(inputPath, outputPath);

        }
    }
}
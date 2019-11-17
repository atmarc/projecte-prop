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

        System.out.println(
                "Que tarea desea realizar?\n" +
                        "\t [0] - Comprimir archivo.\n" +
                        "\t [1] - Descomprimir archivo.");
        mode = in.nextInt();
        if (mode != 0 && mode != 1)
            throw new IllegalArgumentException("Debe introducir 0 o 1.");

        String action = (mode == 0) ? "comprimir":"descomprimir";
        System.out.println("Proporcione ruta del archivo que desea " + action + "\n");
        inputPath = in.next();

        System.out.println("Ruta de salida:\n" +
                "\t [0] - Utilizar la misma ruta que el archivo origen.\n" +
                "\t [1] - Proporcionar manualmente una ruta.");
        route = in.nextInt();
        if (route == 1) {
            System.out.println("Introduzca ruta de salida:");
            outputPath = in.next();
        }
        else if (route != 0) throw new IllegalArgumentException("Debe introducir 0 o 1.");

        String extension = getPathExtension(inputPath);
        if (extension == null)
            throw new IllegalArgumentException("Ruta no valida.");

        if (mode == 0) { // Comprimir

            Compressor compressor = null;

            switch (extension) {
                case "txt":
                    System.out.println(
                            "Que algoritmo de compresion desea utilizar?\n" +
                                    "\t [0] - LZ-78\n" +
                                    "\t [1] - LZ-SS\n" +
                                    "\t [2] - LZ-W\n");
                    alg = in.nextInt();
                    break;

                case "ppm":
                    System.out.println(
                            "Que algoritmo de compresion desea utilizar?\n" +
                                    "\t [0] - LZ-78\n" +
                                    "\t [1] - JPEG\n");

                    alg = in.nextInt() * 3;
                    break;

                default:
                    throw new IllegalArgumentException("La ruta no hace referencia a un archivo con extension .txt o .ppm.");
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
                    throw new IllegalArgumentException("El numero introducido no hace referencia a ningun algoritmo.");
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
                    throw new IllegalArgumentException("La ruta no hace referencia a un archivo con extension .txt o .ppm.");
            }

            decompressor.startDecompression(inputPath, outputPath);

        }
    }
}
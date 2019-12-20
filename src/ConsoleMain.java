import dominio.controladores.Domain_Controller;
import persistencia.Persistence_Controller;
import presentacion.Presentation_Controller;

import java.util.Scanner;

class ConsoleMain {
    public static void main(String[] args) throws Exception {

        System.out.println("##############################################################################");
        System.out.println("#################### The Egg Compressor (Console Edition) ####################");
        System.out.println("##############################################################################");
        System.out.println(" ");

        Persistence_Controller persistence_controller = Persistence_Controller.getPersistence_controller();
        Domain_Controller domain_controller = new Domain_Controller();
        domain_controller.setPersistence_controller(persistence_controller);

        Scanner in = new Scanner(System.in);
        int mode, route, alg = 0, ratio = -1, type = 0;
        String inputPath = null;
        String outputPath = null;

        boolean valid;

        // SELECCIONAR MODO COMP/DECOMP


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

        // SELECCION DE ARCHIVO ORIGEN

        if (mode == 0) { // comprimir
            System.out.println(
                    "Que desea comprimir?\n" +
                            "\t [0] - Comprimir .txt\n" +
                            "\t [1] - Comprimir .ppm\n" +
                            "\t [2] - Comprimir carpeta");
            do {
                type = in.nextInt();
                valid = (type == 0 || type == 1 || type == 2);
                if (!valid) System.out.println("Numero no valido. Debe introducir 0, 1 o 2");
            }
            while (!valid);


            System.out.println("Proporcione ruta del archivo:");
            switch (type) {
                case 0: // txt
                    do {
                        inputPath = in.next();
                        valid = (inputPath.endsWith(".txt"));
                        if (!valid) System.out.println("Introduzca una ruta a un .txt correctamente.");
                    } while (!valid);
                    break;
                case 1: // ppm
                    do {
                        inputPath = in.next();
                        valid = (inputPath.endsWith(".ppm"));
                        if (!valid) System.out.println("Introduzca una ruta a un .ppm correctamente.");
                    } while (!valid);
                    break;
                case 2: // carpeta
                    do {
                        inputPath = in.next();
                        valid = (domain_controller.isFolder(inputPath));
                        if (!valid) System.out.println("Introduzca una ruta a carpeta correctamente.");
                    } while (!valid);
                    break;
                default:
                    break;
            }
        }
        else { // descomprimir
            System.out.println("Proporcione ruta del archivo que desea descomprimir (archivos .egg):");
            do {
                inputPath = in.next();
                valid = (inputPath.endsWith(".egg"));
                if (!valid) System.out.println("Introduzca una ruta a un .egg correctamente.");
            } while (!valid);
        }

        // SELECCION DE ARCHIVO DESTINO

        System.out.println("Ruta del directorio salida:\n" +
                "\t [0] - Utilizar el mismo directorio que el archivo origen.\n" +
                "\t [1] - Proporcionar manualmente una ruta del directorio destino.\n");
        do {
            route = in.nextInt();
            valid = (route == 0 || route == 1);
            if (!valid) System.out.println("Numero no valido. Debe introducir 0 o 1.");
        }
        while (!valid);


        if (route == 0) {
            int bar = inputPath.lastIndexOf("/");
            if (bar > 0) outputPath = inputPath.substring(0, bar) + "/";
        }
        else {
            do {
                System.out.println("Introduzca la ruta al directorio salida:");
                outputPath = in.next();
                valid = (domain_controller.isFolder(outputPath));
                if (!valid) System.out.println("Ruta no valida, no es un directorio.");
                else outputPath = outputPath + "/";
            }
            while (!valid);
        }

        System.out.println("Introduzca el nombre del archivo destino (sin extensiones):");
        outputPath = outputPath + in.next();

        // SELECCION DE ALGORITMO DE COMPRESION

        if (mode == 0) { // Comprimir
            outputPath = outputPath + ".egg";
            switch (type) {
                case 0:
                    System.out.println(
                            "Que algoritmo de compresion desea utilizar?\n" +
                                    "\t [0] - LZ-78\n" +
                                    "\t [1] - LZ-SS\n" +
                                    "\t [2] - LZ-W\n" +
                                    "\t [3] - AUTOMATICO");
                    do {
                        alg = in.nextInt();
                        valid = (0 <= alg && alg <= 3);
                        if (alg == 3) alg = 4;
                        if (!valid) System.out.println("Numero no valido. Debe introducir 0, 1 o 2.");
                    }
                    while(!valid);
                    break;

                case 1:
                    System.out.println("Que nivel de compresion quiere con el JPEG? [introduzca numero entre 1 y 10]:");
                    do {
                        alg = 3;
                        ratio = in.nextInt();
                        valid = (0 <= ratio && ratio <= 10);
                        if (!valid) System.out.println("Numero no valido. Debe introducir un numero entre 1 y 10.");
                    }
                    while(!valid);
                    break;
                case 2:
                    break;
                default: throw new IllegalArgumentException("Ha habido un error durante la ejecucion (switch extension)");
            }


            if (type == 2) // carpeta
                domain_controller.compress(inputPath, outputPath, false);
            else if (alg == 3)  { // jpeg
                domain_controller.compress(inputPath, outputPath, 3, (byte) ratio, false);
            }
            else if (alg == 4) { // automatico
                domain_controller.compress(inputPath, outputPath, false);
            }
            else domain_controller.compress(inputPath, outputPath, alg, false);
        }
        //Descomprimir
        else domain_controller.decompress(inputPath, outputPath, false);
    }
}
package dominio;

import java.io.File;
import java.util.ArrayList;

public class Compressed_Item {

    private Domain_Controller domain_controller;

    private ArrayList<File> inputFile;   ///< Referencia al archivo/s destino.
    private File outputFile;             ///< Referencia al archivo origen.





    // Lectura

    /**
     * Lee un byte del fichero origen.
     * @return Entero que contiene el byte leido o -1 si no habia nada que leer.
     */
    int readByte() {
        return domain_controller.readByte();
    }

    /**
     * Lee N bytes del fichero origen en una cadena de bytes que se le pasa por parametro.
     * @param word Cadena de bytes sobre la que se introducira la lectura.
     * @return Cantidad de bytes leida o -1 si no habia nada que leer.
     */
    int readNBytes(byte[] word) {
        return domain_controller.readBytes(word);
    }

    /**
     * Cierra el buffer de lectura.
     */
    void closeReader() {
        domain_controller.closeReader();
    }

    /**
     * Lee todos los bytes del fichero origen y los guarda en una cadena.
     * @return Cadena de bytes con todos los bytes del fichero origen.
     */
    byte[] readAllBytes() {
        return domain_controller.readAllBytes();
    }

    /**
     * Lee todos los caracteres del fichero origen y los guarda en un String.
     * @return String con todos los caracteres del fichero origen.
     */
    String readFileString() {
        return domain_controller.readFileString();
    }


    // Escritura

    /**
     * Escribe un byte en fichero de salida.
     * @param B Byte que se desea escribir en el fichero de salida.
     */
    void writeByte(byte B) {
        domain_controller.writeByte(B);
    }

    /**
     * Escribe una cadena de bytes en el fichero de salida.
     * @param word Cadena de bytes que se desea escribir en el fichero de salida.
     */
    void writeBytes(byte[] word) {
        domain_controller.writeBytes(word);
    }

    /**
     * Cierra buffer de escritura.
     */
    void closeWriter() {
        domain_controller.closeWriter();
    }

}

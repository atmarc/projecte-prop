package dominio;

import persistencia.Persistence_Controller;
import presentacion.Presentation_Controller;

import java.io.IOException;

public class Domain_Controller {



    private Persistence_Controller persistence_controller;
    private Presentation_Controller presentation_controller;

    public void setPresentation_controller(Presentation_Controller presentation_controller) {
        this.presentation_controller = presentation_controller;
    }
    public void setPersistence_controller(Persistence_Controller persistence_controller) {
        this.persistence_controller = persistence_controller;
    }

    // Lectura

    /**
     * Lee un byte del fichero origen.
     * @return Entero que contiene el byte leido o -1 si no habia nada que leer.
     */
    int readByte(int id) {
        return persistence_controller.readByte(id);
    }
    /**
     * Lee N bytes del fichero origen en una cadena de bytes que se le pasa por parametro.
     * @param word Cadena de bytes sobre la que se introducira la lectura.
     * @return Cantidad de bytes leida o -1 si no habia nada que leer.
     */
    int readNBytes(int id, byte[] word) {
        return persistence_controller.readBytes(id, word);
    }
    /**
     * Cierra el buffer de lectura.
     */
    void closeReader(int id) throws IOException {
        persistence_controller.closeReader(id);
    }
    /**
     * Lee todos los bytes del fichero origen y los guarda en una cadena.
     * @return Cadena de bytes con todos los bytes del fichero origen.
     */
    byte[] readAllBytes(int id) {
        return persistence_controller.readAllBytes(id);
    }


    // Escritura

    /**
     * Escribe un byte en fichero de salida.
     * @param B Byte que se desea escribir en el fichero de salida.
     */
    void writeByte(int id, byte B) {
        persistence_controller.writeByte(id, B);
    }
    /**
     * Escribe una cadena de bytes en el fichero de salida.
     * @param word Cadena de bytes que se desea escribir en el fichero de salida.
     */
    void writeBytes(int id, byte[] word) {
        persistence_controller.writeBytes(id, word);
    }
    /**
     * Cierra buffer de escritura.
     */
    void closeWriter(int id) throws IOException {
        persistence_controller.closeWriter(id);
    }

    // File Info
    public long getInputFileSize(int id) {
        return persistence_controller.getInputFileSize(id);
    }
    public long getOutputFileSize(int id) {
        return persistence_controller.getOutputFileSize(id);
    }

}

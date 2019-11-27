package dominio;

class Domain_Controller {

    private persistencia.Domain_Controller persistence_controller;

    // File info
    /**
     * Getter del tamano en Bytes del fichero original.
     * @return Tamano en Bytes del fichero original.
     */
    public long getInFileSize() {
        return persistence_controller.getInFileSize();
    }

    /**
     * Getter del tamano en Bytes del fichero comprimido.
     * @return Tamano en Bytes del fichero comprimido.
     */
    public long getOutFileSize() {
        return persistence_controller.getOutFileSize();
    }

    // Lectura

    /**
     * Lee un byte del fichero origen.
     * @return Entero que contiene el byte leido o -1 si no habia nada que leer.
     */
    int readByte() {
        return persistence_controller.readByte();
    }

    /**
     * Lee N bytes del fichero origen en una cadena de bytes que se le pasa por parametro.
     * @param word Cadena de bytes sobre la que se introducira la lectura.
     * @return Cantidad de bytes leida o -1 si no habia nada que leer.
     */
    int readNBytes(byte[] word) {
        return persistence_controller.readBytes(word);
    }

    /**
     * Cierra el buffer de lectura.
     */
    void closeReader() {
        persistence_controller.closeReader();
    }

    /**
     * Lee todos los bytes del fichero origen y los guarda en una cadena.
     * @return Cadena de bytes con todos los bytes del fichero origen.
     */
    byte[] readAllBytes() {
        return persistence_controller.readAllBytes();
    }

    /**
     * Lee todos los caracteres del fichero origen y los guarda en un String.
     * @return String con todos los caracteres del fichero origen.
     */
    String readFileString() {
        return persistence_controller.readFileString();
    }


    // Escritura

    /**
     * Escribe un byte en fichero de salida.
     * @param B Byte que se desea escribir en el fichero de salida.
     */
    void writeByte(byte B) {
        persistence_controller.writeByte(B);
    }

    /**
     * Escribe una cadena de bytes en el fichero de salida.
     * @param word Cadena de bytes que se desea escribir en el fichero de salida.
     */
    void writeBytes(byte[] word) {
        persistence_controller.writeBytes(word);
    }

    /**
     * Cierra buffer de escritura.
     */
    void closeWriter() {
        persistence_controller.closeWriter();
    }
}


}

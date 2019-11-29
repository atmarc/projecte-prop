package dominio;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/*!
 *  \brief     Clase encargada de comunicar los descompresores con otras capas (presentacion y persistencia). Proporciona metodos de entrada y salida ademas realizar el calculo de las estadisticas.
 *  \details
 *  \author    Edgar Perez
 */
public class Decompressor_Controller {

    public Decompressor decompressor;               ///< Referencia al objeto descompresor
    private Domain_Controller domain_controller;    ///< Referencia a la controladora del dominio.
    private long time;                              ///< Tiempo transcurrido durante la descompresion.
    private int inputFile;                          ///< Identificador del archivo original a comprimir.
    private int outputFile;                         ///< Identificador del archivo sobre el que escribir la compresion.

    /**
     * Setter del atributo inputFile.
     * @param inputFile Identificador del archivo que se desea establecer como archivo a comprimir.
     */
    public void setInputFile(int inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * Setter del atributo outputFile.
     * @param outputFile Identificador del archivo que se desea establecer como archivo a comprimir.
     */
    public void setOutputFile(int outputFile) {
        this.outputFile = outputFile;
    }

    public void setDomain_controller(Domain_Controller domain_controller) {
        this.domain_controller = domain_controller;
    }

    /**
     * Constructora que en base al tipo de archivo de comprimido, crea un tipo de descompresor u otro.
     * @param extension Extension del archivo comprimido sobre el que se desea realizar una descompresion.
     */
    public Decompressor_Controller(String extension) {

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

        decompressor.setController(this);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////   Decompression   //////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Inicia la descompresion del archivo referenciado por el identificador
     * @param in Identificador del archivo a descomprimir.
     * @param out Identificador del archivo sobre el que escribir la compresion.
     */
    public void startDecompression(int in, int out) {

        setInputFile(in);
        setOutputFile(out);
        System.out.println("Decompression IN PROGRESS");

        time = System.currentTimeMillis();
        decompressor.decompress();
        time = System.currentTimeMillis() - time;

        closeReader();
        closeWriter();

        System.out.println("Decompression DONE");
        System.out.println("Time: " + this.getTime() + " ms");

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////  Post-Decompression Consultants   /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter del tiempo transcurrido en milisegundos.
     * @return Tiempo transcurrido en milisegundos.
     */
    public long getTime() {
        return time;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////  Lectura   ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Lee un byte del fichero origen.
     * @return Entero que contiene el byte leido o -1 si no habia nada que leer.
     */
    protected int readByte() {
        return domain_controller.readByte(inputFile);
    }
    /**
     * Lee N bytes del fichero origen en una cadena de bytes que se le pasa por parametro.
     * @param word Cadena de bytes sobre la que se introducira la lectura.
     * @return Cantidad de bytes leida o -1 si no habia nada que leer.
     */
    protected int readNBytes(byte[] word) {
        return domain_controller.readNBytes(inputFile, word);
    }
    /**
     * Lee todos los bytes del fichero origen y los guarda en una cadena.
     * @return Cadena de bytes con todos los bytes del fichero origen.
     */
    protected byte[] readAllBytes() {
        return domain_controller.readAllBytes(inputFile);
    }
    /**
     * Cierra el buffer de lectura.
     */
    protected void closeReader() {
        try {
            domain_controller.closeReader(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////  Escritura   ////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Escribe un byte en fichero de salida.
     * @param B Byte que se desea escribir en el fichero de salida.
     */
    protected void writeByte(byte B) {
        domain_controller.writeByte(outputFile, B);
    }
    /**
     * Escribe una cadena de bytes en el fichero de salida.
     * @param word Cadena de bytes que se desea escribir en el fichero de salida.
     */
    protected void writeBytes(byte[] word) {
        domain_controller.writeBytes(outputFile, word);
    }
    /**
     * Cierra buffer de escritura.
     */
    protected void closeWriter() {
        try {
            domain_controller.closeWriter(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

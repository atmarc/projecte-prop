package dominio;
import java.io.*;


/*!
 *  \brief     Clase encargada de comunicar los compresores con otras capas (presentacion y persistencia). Proporciona metodos de entrada y salida ademas realizar el calculo de las estadisticas.
 *  \details
 *  \author    Edgar Perez
 */
public class Compressor_Controller {

    private Compressor compressor;                  ///< Referencia al objeto compresor.
    private Domain_Controller domain_controller;    ///< Referencia a la controladora del dominio.
    private long time;                              ///< Tiempo transcurrido durante la compresion.
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
     * Constructora que en base al tipo de algoritmo de compresion escogido, crea un tipo de compresor u otro.
     * @param alg Entero identificador del tipo de algoritmo que se desea utilizar para comprimir.
     */
    public Compressor_Controller(int alg) {
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

        compressor.setController(this);
    }

    public Compressor_Controller(Compressor cmp) {
        compressor = cmp;
        compressor.setController(this);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////   Compression   //////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Inicia la compresion del archivo referenciado por el path inputPath hacia un nuevo archivo en outputPath
     * @param in Identificador del archivo a comprimir.
     * @param out Identificador del archivo sobre el que escribir la compresion.
     */
    public void startCompression(int in, int out) throws Exception {
        startCompression(in, out, (byte) -1);
    }

    public void startCompression(int in, int out, byte ratio) throws Exception {

        setInputFile(in);
        setOutputFile(out);

        System.out.println("Compression IN PROGRESS");

        time = System.currentTimeMillis();
        if (ratio != -1) compressor.compress();
        else compressor.compress(ratio);
        time = System.currentTimeMillis() - time;

        domain_controller.closeReader(inputFile);


        System.out.println("Compression DONE");
        System.out.println("Time: " + this.getTime() + " ms");
        System.out.printf("Compression ratio: %.2f\n", this.getCompressionRatio());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////  Post-Compression Consultants   /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter del tiempo transcurrido en milisegundos.
     * @return Tiempo transcurrido en milisegundos.
     */
    public long getTime() {
        return time;
    }
    /**
     * Getter del ratio de compresion absoluto de la compresion realizada.
     * @return Ratio de compresion absoluto de la compresion realizada.
     */
    public double getCompressionRatio() {
        return (double) domain_controller.getOutputFileSize(outputFile)/(double) domain_controller.getInputFileSize(inputFile);
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
     * 
     * @return Cadena de bytes con todos los bytes del fichero origen.
     * @throws IOException
     */
    protected byte[] readAllBytes() throws Exception {
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
    ////////////////////////////////////////////////////  Escritura   //////////////////////////////////////////////////
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

package dominio;
import java.io.*;


/*!
 *  \brief     Clase encargada de comunicar los compresores con otras capas (presentacion y persistencia). Proporciona metodos de entrada y salida ademas realizar el calculo de las estadisticas.
 *  \details
 *  \author    Edgar Perez
 */
public class Compressor_Controller {

    public Compressor compressor; ///< Objeto compresor
    private Domain_Controller domain_controller;
    private Item item;

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


    private long time;                  ///< Tiempo transcurrido durante la compresion.

    // Auxiliar PreCompression Methods

    /**
     * Establece cuales seran los ficheros de origen y destino.
     * @param inputPath Path del fichero origen.
     * @param outputPath Path del fichero destino
     */
    private void selectFiles(String inputPath, String outputPath) {
        domain_controller.selectFiles(inputPath, outputPath, ".egg");
    }

    /**
     * Proporciona el path de un archivo destino en base a su archivo origen, con el objetivo que vayan a parar ambos al mismo directorio, con el mismo nombre, pero diferente extension.
     * @param fileName Path del archivo original.
     * @return Path del fichero destino.
     */
    private String getCompressedName(String fileName) {
        domain_controller.getCompressed
    }

    /**
     * Proporciona el path de un archivo destino en base a su archivo origen, con el objetivo que vayan a parar ambos al mismo directorio, con el mismo nombre, pero diferente extension.
     * @param file Fichero que referencia al fichero original.
     * @return Path del fichero destino.
     */
    private String getCompressedName(File file) {
        return getCompressedName(file.getPath());
    }

    // Compression

    /**
     * Inicia la compresion del archivo referenciado por el path inputPath hacia un nuevo archivo en outputPath
     * @param inputPath Path del archivo original.
     * @param outputPath Path del directorio donde se creara el archivo comprimido.
     */
    public void startCompression(String inputPath, String outputPath) {

        selectFiles(inputPath, outputPath);
        System.out.println("Compression IN PROGRESS");

        time = System.currentTimeMillis();
        compressor.compress();
        time = System.currentTimeMillis() - time;

        domain_controller.closeReader();
        domain_controller.closeWriter();

        System.out.println("Compression DONE");
        System.out.println("Time: " + this.getTime() + " ms");
        System.out.printf("Compression ratio: %.2f\n", this.getCompressionRatio());
    }

    // Post-Compression Consultants

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
        return (double) domain_controller.getOutFileSize()/(double) domain_controller.getInFileSize();
    }

}

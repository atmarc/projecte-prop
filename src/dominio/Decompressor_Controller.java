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

    public Decompressor decompressor; ///< Objeto descompresor

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

    private File inputFile;             ///< Referencia al archivo origen.
    private File outputFile;            ///< Referencia al archivo destino.
    private BufferedInputStream in;     ///< Buffer de lectura.
    private BufferedOutputStream out;   ///< Buffer de escritura.
    private long time;                  ///< Tiempo transcurrido durante la descompresion.

    // Auxiliar PreCompression Methods
    public void selectFiles(String inputPath, String outputPath) {
        try {

            inputFile = new File(inputPath);

            if (outputPath == null) outputFile = new File(getCompressedName(inputFile));
            else outputFile = new File(outputPath + getCompressedName(inputFile.getName()));

            in = new BufferedInputStream(new FileInputStream(inputFile));
            out = new BufferedOutputStream(new FileOutputStream(outputFile));

        } catch (FileNotFoundException e) {
            System.out.println("Fichero no encontrado!");
            e.printStackTrace();
        }
    }
    /**
     * Proporciona el path de un archivo destino en base a su archivo origen, con el objetivo que vayan a parar ambos al mismo directorio, con el mismo nombre, pero diferente extension.
     * @param file Fichero que referencia al fichero original.
     * @return Path del fichero destino.
     */
    private String getCompressedName(File file) {
        String fileName = file.getPath();
        int pos = fileName.lastIndexOf('.');
        String compressedFileName;
        if (pos != -1) compressedFileName = fileName.substring(0, pos);
        else throw new IllegalArgumentException("Nombre de fichero incorrecto");
        return compressedFileName + decompressor.getExtension();
    }
    /**
     * Proporciona el path de un archivo destino en base a su archivo origen, con el objetivo que vayan a parar ambos al mismo directorio, con el mismo nombre, pero diferente extension.
     * @param fileName Path del archivo original.
     * @return Path del fichero destino.
     */
    private String getCompressedName(String fileName) {
        int pos = fileName.lastIndexOf('.');
        String compressedFileName;
        if (pos != -1) compressedFileName = fileName.substring(0, pos);
        else throw new IllegalArgumentException("Nombre de fichero incorrecto");
        return compressedFileName + decompressor.getExtension();
    }

    // Compression

    /**
     * Inicia la descompresion del archivo referenciado por el path inputPath hacia un nuevo archivo en outputPath
     * @param inputPath Path del archivo comprimido.
     * @param outputPath Path del directorio donde se creara el archivo descomprimido.
     */
    public void startDecompression(String inputPath, String outputPath) {

        selectFiles(inputPath, outputPath);
        System.out.println("Decompression IN PROGRESS");

        time = System.currentTimeMillis();
        decompressor.decompress();
        time = System.currentTimeMillis() - time;

        closeReader();
        closeWriter();

        System.out.println("Decompression DONE");
        System.out.println("Time: " + this.getTime() + " ms");

    }
    /**
     * Inicia la descompresion del archivo referenciado por el path inputPath hacia un nuevo archivo localizado en el mismo directorio que el original.
     * @param inputPath Path del archivo comprimido.
     */
    public void startDecompression(String inputPath) {
        startDecompression(inputPath, getCompressedName(inputPath));
    }

    // Post-Compression Consultants

    /**
     * Getter del tiempo transcurrido en milisegundos.
     * @return Tiempo transcurrido en milisegundos.
     */
    public long getTime() {
        return time;
    }

    // Lectura

    /**
     * Lee un byte del fichero origen.
     * @return Entero que contiene el byte leido o -1 si no habia nada que leer.
     */
    protected int readByte() {
        try {
            return in.read();
        } catch (IOException e) {
            System.out.println("Error Lectura\n" + e.getMessage());
            return -1;
        }
    }
    /**
     * Lee N bytes del fichero origen en una cadena de bytes que se le pasa por parametro.
     * @param word Cadena de bytes sobre la que se introducira la lectura.
     * @return Cantidad de bytes leida o -1 si no habia nada que leer.
     */
    protected int readNBytes(byte[] word) {
        try {
            return in.read(word);
        } catch (IOException e) {
            System.out.println("Error Lectura\n" + e.getMessage());
            return -1;
        }
    }
    /**
     * Cierra el buffer de lectura.
     */
    protected void closeReader() {
        try {
            if (in != null) in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Lee todos los bytes del fichero origen y los guarda en una cadena.
     * @return Cadena de bytes con todos los bytes del fichero origen.
     */
    protected byte[] readAllBytes() {
        byte[] b = new byte[0];
        try {
            b = Files.readAllBytes(Paths.get(inputFile.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    // Escritura

    /**
     * Escribe un byte en fichero de salida.
     * @param B Byte que se desea escribir en el fichero de salida.
     */
    protected void writeByte(byte B) {
        try {
            out.write(B);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Escribe una cadena de bytes en el fichero de salida.
     * @param word Cadena de bytes que se desea escribir en el fichero de salida.
     */
    protected void writeBytes(byte[] word) {
        try {
            out.write(word);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Cierra buffer de escritura.
     */
    protected void closeWriter() {
        try {
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package dominio;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


/*!
 *  \brief     Clase encargada de comunicar los compresores con otras capas (presentacion y persistencia). Proporciona metodos de entrada y salida ademas realizar el calculo de las estadisticas.
 *  \details
 *  \author    Edgar Perez
 */
public class Compressor_Controller {

    public Compressor compressor; ///< Objeto compresor

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

    private File inputFile;             ///< Referencia al archivo origen.
    private File outputFile;            ///< Referencia al archivo destino.
    private BufferedInputStream in;     ///< Buffer de lectura.
    private BufferedOutputStream out;   ///< Buffer de escritura.
    private long time;                  ///< Tiempo transcurrido durante la compresion.

    // Auxiliar PreCompression Methods

    /**
     * Establece cuales seran los ficheros de origen y destino.
     * @param inputPath Path del fichero origen.
     * @param outputPath Path del fichero destino
     */
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
     * @param fileName Path del archivo original.
     * @return Path del fichero destino.
     */
    public String getCompressedName(String fileName) {
        int pos = fileName.lastIndexOf('.');
        String compressedFileName;
        if (pos != -1) compressedFileName = fileName.substring(0, pos);
        else throw new IllegalArgumentException("Nombre de fichero incorrecto");
        return compressedFileName + compressor.getExtension();
    }

    /**
     * Proporciona el path de un archivo destino en base a su archivo origen, con el objetivo que vayan a parar ambos al mismo directorio, con el mismo nombre, pero diferente extension.
     * @param file Fichero que referencia al fichero original.
     * @return Path del fichero destino.
     */
    public String getCompressedName(File file) {
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

        closeReader();
        closeWriter();

        System.out.println("Compression DONE");
        System.out.println("Time: " + this.getTime() + " ms");
        System.out.printf("Compression ratio: %.2f\n", this.getCompressionRatio());
    }

    /**
     * Inicia la compresion del archivo referenciado por el path inputPath hacia un nuevo archivo localizado en el mismo directorio que el original.
     * @param inputPath Path del archivo original.
     */
    public void startCompression(String inputPath) {
        startCompression(inputPath, getCompressedName(inputPath));
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
     * Getter del tamano en Bytes del fichero original.
     * @return Tamano en Bytes del fichero original.
     */
    public long getOriginalSize() {
        return inputFile.length();
    }

    /**
     * Getter del tamano en Bytes del fichero comprimido.
     * @return Tamano en Bytes del fichero comprimido.
     */
    public long getCompressedSize() {
        return outputFile.length();
    }

    /**
     * Getter del ratio de compresion absoluto de la compresion realizada.
     * @return Ratio de compresion absoluto de la compresion realizada.
     */
    public double getCompressionRatio() {
        return (double)getCompressedSize()/(double)getOriginalSize();
    }

    // Lectura

    /**
     * Lee un byte del fichero origen.
     * @return Entero que contiene el byte leido o -1 si no habia nada que leer.
     */
    protected int readByte() {
        try {
            return in.read();
        }
        catch (IOException e) {
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

    /**
     * Lee todos los caracteres del fichero origen y los guarda en un String.
     * @return String con todos los caracteres del fichero origen.
     */
    protected String readFileString() {
        StringBuffer outString = new StringBuffer();
        try {
            FileReader reader = new FileReader(inputFile.getPath());
            BufferedReader bufferedReader = new BufferedReader(reader);

            int readByte;
            while ((readByte = bufferedReader.read()) != -1) {
                outString.append((char) readByte);
            }
            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return outString.toString();
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

package persistencia;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Domain_Controller {

    private File inputFile;             ///< Referencia al archivo origen.
    private File outputFile;            ///< Referencia al archivo destino.
    private BufferedInputStream in;     ///< Buffer de lectura.
    private BufferedOutputStream out;   ///< Buffer de escritura.


    // File Set-Up
    public void selectFiles(String inputPath, String outputPath, String extension) {
        try {

            inputFile = new File(inputPath);

            if (outputPath == null) outputFile = new File(getCompressedName(inputFile, extension));
            else outputFile = new File(outputPath + getCompressedName(inputFile.getName(), extension));

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
    public String getName(File file) {
        String fileName = file.getPath();
        int pos = fileName.lastIndexOf('.');
        String compressedFileName;
        if (pos != -1) compressedFileName = fileName.substring(0, pos);
        else throw new IllegalArgumentException("Nombre de fichero incorrecto");
        return compressedFileName + extension;
    }

    /**
     * Proporciona el path de un archivo destino en base a su archivo origen, con el objetivo que vayan a parar ambos al mismo directorio, con el mismo nombre, pero diferente extension.
     * @param fileName Path del archivo original.
     * @return Path del fichero destino.
     */
    public String getCompressedName(String fileName, String extension) {
        int pos = fileName.lastIndexOf('.');
        String compressedFileName;
        if (pos != -1) compressedFileName = fileName.substring(0, pos);
        else throw new IllegalArgumentException("Nombre de fichero incorrecto");
        return compressedFileName + extension;
    }

    // File info
    /**
     * Getter del tamano en Bytes del fichero original.
     * @return Tamano en Bytes del fichero original.
     */
    public long getInFileSize() {
        return inputFile.length();
    }

    /**
     * Getter del tamano en Bytes del fichero comprimido.
     * @return Tamano en Bytes del fichero comprimido.
     */
    public long getOutFileSize() {
        return outputFile.length();
    }

    // Lectura

    /**
     * Lee un byte del fichero origen.
     * @return Entero que contiene el byte leido o -1 si no habia nada que leer.
     */
    public int readByte() {
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
    public int readBytes(byte[] word) {
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
    public void closeReader() {
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
    public byte[] readAllBytes() {
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
    public String readFileString() {
        StringBuilder outString = new StringBuilder();
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
    public void writeByte(byte B) {
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
    public void writeBytes(byte[] word) {
        try {
            out.write(word);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cierra buffer de escritura.
     */
    public void closeWriter() {
        try {
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

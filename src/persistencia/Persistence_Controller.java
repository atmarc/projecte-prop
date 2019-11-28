package persistencia;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Persistence_Controller {

    private ArrayList<InputFile> readFiles;  ///< Referencia al archivo origen.
    private ArrayList<OutputFile> writeFiles; ///< Referencia al archivo destino.

    public Persistence_Controller() {
        readFiles = new ArrayList<>();
        writeFiles = new ArrayList<>();
    }

    // File Creation

    public int newInputFile(String path) {
        InputFile aux = new InputFile(path);
        readFiles.add(aux);
        return readFiles.indexOf(aux);
    }
    public int newOutputFile(String path) {
        OutputFile aux = new OutputFile(path);
        writeFiles.add(aux);
        return writeFiles.indexOf(aux);
    }

    // File info

    /**
     * Proporciona el nombre del archivo de lectura identificado por el identificador que recibe por parametro .
     * @param id Identificador del fichero de lectura.
     * @return Nombre del fichero identificado por el parametro id.
     */
    public String getName(int id) {
        return readFiles.get(id).getName();
    }
    /**
     * Getter del tamano en Bytes de un fichero de lectura.
     * @return Tamano en Bytes del fichero original.
     */
    public long getInputFileSize(int id) {
        return readFiles.get(id).length();
    }
    /**
     * Getter del tamano en Bytes del fichero comprimido.
     * @return Tamano en Bytes del fichero comprimido.
     */
    public long getOutputFileSize(int id) {
        return writeFiles.get(id).length();
    }

    // Lectura

    /**
     * Lee un byte del fichero origen.
     * @return Entero que contiene el byte leido o -1 si no habia nada que leer.
     */
    public int readByte(int id) {
        try {
            return readFiles.get(id).getBuffer().read();
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
    public int readBytes(int id, byte[] word) {
        try {
            return readFiles.get(id).getBuffer().read(word);
        } catch (IOException e) {
            System.out.println("Error Lectura\n" + e.getMessage());
            return -1;
        }
    }
    /**
     * Cierra el buffer de lectura.
     */
    public void closeReader(int id) throws IOException {
        readFiles.get(id).closeBuffer();
    }
    /**
     * Lee todos los bytes del fichero origen y los guarda en una cadena.
     * @return Cadena de bytes con todos los bytes del fichero origen.
     */
    public byte[] readAllBytes(int id) {
        byte[] b = new byte[0];
        try {
            b = Files.readAllBytes(Paths.get(readFiles.get(id).getPath()));
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
    public void writeByte(int id, byte B) {
        try {
            writeFiles.get(id).getBuffer().write(B);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Escribe una cadena de bytes en el fichero de salida.
     * @param word Cadena de bytes que se desea escribir en el fichero de salida.
     */
    public void writeBytes(int id, byte[] word) {
        try {
            writeFiles.get(id).getBuffer().write(word);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Cierra buffer de escritura.
     */
    public void closeWriter(int id) throws IOException {
        writeFiles.get(id).closeBuffer();
    }

    /**
     * @pre El fichero de escritura con identificador id existe, posicion < (fichero.length - 7).
     * @post Se han cambiado los 8 Bytes de content por los 8 bytes localizados en la posicion position del fichero id.
     *
     * Reemplaza los 8 Bytes localizados en la posicion indicada de un fichero y por otros 8 Bytes codificados en un long que se recibe por parametro.
     * @param id Identificador del fichero a modificar.
     * @param position Posicion desde la que se quieren modificar los 8 Bytes.
     * @param content Contenido por el cual se desea cambiar el contenido del fichero.
     */
    public void modifyLong(int id, long position, long content) {
        try {
            OutputFile file = writeFiles.get(id);
            if (file.isActive()) file.closeBuffer();

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(position);
            raf.writeLong(content);
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

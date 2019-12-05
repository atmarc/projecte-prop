package dominio;

import persistencia.Persistence_Controller;
import presentacion.Presentation_Controller;

import java.io.IOException;
import java.util.ArrayList;

public class Domain_Controller {

    private Persistence_Controller persistence_controller;

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
    void closeWriter(int id) throws IOException { persistence_controller.closeWriter(id); }

    // File Info
    public long getInputFileSize(int id) {
        return persistence_controller.getInputFileSize(id);
    }

    public long getOutputFileSize(int id) {
        return persistence_controller.getOutputFileSize(id);
    }

    public void startCompression(int in, int out, int alg) {
        Compressor_Controller compressor_controller = new Compressor_Controller(alg);
        compressor_controller.setDomain_controller(this);
        compressor_controller.startCompression(in, out);
    }

    public void startDecompression(int in, int  out, String extension) {
        Decompressor_Controller decompressor_controller = new Decompressor_Controller(extension);
        decompressor_controller.setDomain_controller(this);
        // Si és un sol arxiu
        decompressor_controller.startDecompression(in, out);
    }

    // Crea la jerarquia llegint "l'arbre d'arxius" en preordre, i després de cada fulla posa un -1
    // El faig en bytes, per tant podrem tenir fins a 2^16 fitxers
    // Exemple: a,b(c,d),e(f,g) --> [a, -1, b, c, -1, d, -1, -1, e, f, -1, g, -1, -1]
    private void makeHierarchy(ArrayList<Byte> hierarchy, ArrayList<Integer> files) {
        for (int f : files) {
            if (persistence_controller.isFolder(f)) {
                hierarchy.add((byte) f);
                makeHierarchy(hierarchy, persistence_controller.getFilesFromFolder(f));
                hierarchy.add((byte) -1);
            }
            else {
                hierarchy.add((byte) f);
                hierarchy.add((byte) -1);
            }
        }
    }

    private void writeFiles(int out, ArrayList<Byte> files, int index) {
        for (int i = index; i < files.size(); ++i) {
            int file = files.get(i);
            if (file != (byte)-1) {
                if (persistence_controller.isFolder(file)) {
                    writeFiles(file, files, i + 1);
                }
                else {
                    Compressor_Controller compressor = new Compressor_Controller(getBestCompressor(file));
                    // TODO: 8 bytes pel size potser és massa
                    long fileSize = persistence_controller.getOutputFileSize(file);
                    persistence_controller.writeBytes(file, longToByteArr(fileSize));
                    compressor.startCompression(file, out); //TODO: Parlar de com fer el output bé
                }
            }
            // Si hi ha dos -1 seguits vol dir que ja ha sortit de la carpeta
            else if (i + 1 < files.size() && files.get(i + 1) == (byte)-1) break;
        }
    }

    //TODO: Arreglar aquesta fun
    public byte[] longToByteArr(long l) {
        byte[] arr = new byte[8];
        for (int i = 0; i < 8; ++i) {
            arr[i] = (byte) l;
            l = l >> 8;
        }
        return arr;
    }

    public long byteArrToLong(byte[] b) {
        long l = 0;
        for (int i = 0; i < b.length; ++i) {
            long num = b[i];
            if (num < 0) num += 256;
            long aux = num << 8 * i;
            l += aux;
        }
        return l;
    }

    public void compressFolder(int in, int out) {
        /*ArrayList<Integer> files = new ArrayList<>();
        files.add(1); files.add(3); files.add(2); files.add(0);*/

        ArrayList<Integer> files = persistence_controller.getFilesFromFolder(in);
        ArrayList<Byte> jerarquia = new ArrayList<>();
        makeHierarchy(jerarquia, files);

        // Posem un -1 per indicar el final de la jerarqui
        jerarquia.add((byte) -1);

        String nameFolder = persistence_controller.getName(in);

        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add(nameFolder);
        for (byte fileId : jerarquia) {
            persistence_controller.writeByte(out, fileId);
            if (fileId != -1) {
                fileNames.add(persistence_controller.getName(fileId));
            }
        }
        // Escrivim cada nom amb el mateix ordre i un separador (-1) entre cada un
        for (String name : fileNames) {
            persistence_controller.writeBytes(out, name.getBytes());
            persistence_controller.writeByte(out, (byte) -1);
        }
        // Un -1 indicant que hem acabat els noms, no hauria de ser necessari
        persistence_controller.writeByte(out, (byte) -1);

        writeFiles(out, jerarquia, 0);

    }

    private int getBestCompressor(int file) {
        return 0;
    }

    public void decompressFolder(int in, int out) {

    }

}

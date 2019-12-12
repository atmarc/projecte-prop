package dominio;

import persistencia.Persistence_Controller;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class Domain_Controller {

    private Persistence_Controller GD;

    Domain_Controller() {
        GD = new Persistence_Controller();
    }

    public void setPersistence_controller(Persistence_Controller persistence_controller) {
        this.GD = persistence_controller;
    }

    // Lectura
    /**
     * Lee un byte del fichero origen.
     * 
     * @return Entero que contiene el byte leido o -1 si no habia nada que leer.
     */
    int readByte(int id) {
        return GD.readByte(id);
    }

    /**
     * Lee N bytes del fichero origen en una cadena de bytes que se le pasa por
     * parametro.
     * @param word Cadena de bytes sobre la que se introducira la lectura.
     * @return Cantidad de bytes leida o -1 si no habia nada que leer.
     */
    int readNBytes(int id, byte[] word) {
        return GD.readBytes(id, word);
    }
    /**
     * Cierra el buffer de lectura.
     */
    void closeReader(int id) throws IOException {
        GD.closeReader(id);
        // TO DO: Tal vez no hace falta y se gestiona tododesde la persistencia
    }
    /**
     * Lee todos los bytes del fichero origen y los guarda en una cadena.
     * @return Cadena de bytes con todos los bytes del fichero origen.
     */
    byte[] readAllBytes(int id) {
        return GD.readAllBytes(id);
    }


    // Escritura

    /**
     * Escribe un byte en fichero de salida.
     * @param B Byte que se desea escribir en el fichero de salida.
     */
    void writeByte(int id, byte B) {
        GD.writeByte(id, B);
    }
    /**
     * Escribe una cadena de bytes en el fichero de salida.
     * @param word Cadena de bytes que se desea escribir en el fichero de salida.
     */
    void writeBytes(int id, byte[] word) {
        GD.writeBytes(id, word);
    }
    /**
     * Cierra buffer de escritura.
     */
    void closeWriter(int id) throws IOException { GD.closeWriter(id); }

    // File Info
    public long getInputFileSize(int id) {
        return GD.getInputFileSize(id);
    }

    public long getOutputFileSize(int id) {
        return GD.getOutputFileSize(id);
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
    // El faig en bytes, per tant podrem tenir fins a 2^8 fitxers
    // Exemple: a,b(c,d),e(f,g) --> [a, -1, b, c, -1, d, -1, -1, e, f, -1, g, -1, -1]
    private void makeHierarchy(ArrayList<Byte> hierarchy, ArrayList<Integer> files) {
        for (int f : files) {
            if (GD.isFolder(f)) {
                hierarchy.add((byte) f);
                makeHierarchy(hierarchy, GD.getFilesFromFolder(f));
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
                if (GD.isFolder(file)) {
                    writeFiles(file, files, i + 1);
                }
                else {
                    Compressor_Controller compressor = new Compressor_Controller(getBestCompressor(file));
                    // TODO: 8 bytes pel size potser és massa
                    long fileSize = GD.getOutputFileSize(file);
                    byte [] space = new byte[8];
                    for (int j = 0; j < space.length; ++j) space[j] = 0;

                    GD.writeBytes(file, space);
                    GD.writeBytes(file, longToByteArr(fileSize));

                    long compressedSize = GD.getOutputFileSize(out);
                    compressor.startCompression(file, out);
                    compressedSize = GD.getOutputFileSize(out) - compressedSize;

                    try {
                        GD.randomAccesWriteLong(out, index, compressedSize);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // Si hi ha dos -1 seguits vol dir que ja ha sortit de la carpeta
            else if (i + 1 < files.size() && files.get(i + 1) == (byte)-1) break;
        }
    }

    private String getFileExtension(String file) {
        assert file != null : "El nombre del fichero es nulo";  // TODO: Throw new [FileWithoutExtension] ?
        int i = file.lastIndexOf('.');
        assert i > 0 : "Fichero sin extension"; // TODO: Throw nwe [FileWithoutExtension] ?
        return file.substring(i+1);
    }

    // Retorna el mejor compresor para comprimir el fichero in
    // si el algoritmo es -1 u otra cosa no en [0, 2] entonces
    // assigna el mejor algoritmo para el fichero
    private Compressor getBestCompressor(int in, int alg) {
        String ext = GD.getExtension(in);
        if (ext.equals("ppm")) return new Compressor_JPEG();
        else if (ext.equals("txt")) {
            if (alg == 0) return new Compressor_LZ78();
            else if(alg == 1) return new Compressor_LZSS();
            else if(alg == 2) return new Compressor_LZW();
            else {
                long size = GD.getInputFileSize(in);
                if (size < 100L) throw new IllegalArgumentException("FicheroDemasiadoPequeno");
                if (size <= 50000L) return new Compressor_LZSS();
                else if (size <= 1000000L) return new Compressor_LZW();
                else return new Compressor_LZ78();
            }
        }
        else throw new IllegalArgumentException("Extension incorrecta, quiere utilizar los algoritmos universales?");
    }

    private class Hierarchy {
        private int root;
        private int[][] m;
        ArrayList<ArrayList<Integer> > rep;

        public ArrayList<Integer> getFilesList() {
            ArrayList<Integer> res = new ArrayList<>();
            for (int i = 0; i < res.size(); ++i) 
                res.add(i);
            return res;
        }

        public Hierarchy(int[][] src) {
            m = src;
            int len = src[0].length;
            rep = new ArrayList<>();
            for (int i = 0; i < len; ++i) {
                rep.add(new ArrayList<>());
            }
            for (int i = 0; i < len; ++i) {
                if (src[1][i] == i)
                    root = i;
                else
                    rep.get(src[1][i]).add(i);
            }
        }

        byte[] toByteArray() {
            int nr = m[0].length;
            int headerSize = 6 + nr * 2 + (int) Math.ceil(nr / 8.0);
            byte[] res = new byte[headerSize];
            for (byte i : res)
                i = 0;
            res[3] = (byte) (headerSize & 0xFF);
            res[2] = (byte) ((headerSize >> 8) & 0xFF);
            res[1] = (byte) ((headerSize >> 16) & 0xFF);
            res[0] = (byte) ((headerSize >> 24) & 0xFF);
            res[5] = (byte) (nr & 0xFF);
            res[4] = (byte) ((nr >> 8) & 0xFF);

            int pos = 6;
            byte b = 0;
            for (int i = 0; i < rep.size(); ++i) {
                if (i % 8 == 0 && i > 1) {
                    res[pos++] = b;
                    b = 0;
                }
                if (!rep.get(i).isEmpty())
                    b |= (1 << (7 - (i % 8)));
            }
            if (rep.size() % 8 != 0)
                res[pos++] = b;
            for (int i = 0; i < nr; ++i) {
                res[pos++] = (byte) m[0][i];
                res[pos++] = (byte) m[1][i];
            }
            return res;
        }

        ArrayList<Integer> dfs() {
            return dfsAux(root);
        }

        ArrayList<Integer> dfsAux(int i) {
            ArrayList<Integer> crr = rep.get(i);
            ArrayList<Integer> res = new ArrayList<>();
            if (crr.isEmpty()) res.add(i);
            else {
                for (Integer v : crr) {
                    res.addAll(dfsAux(v));
                }
            }
            return res;
        }

    }
    
    public void compress(String inputPath, String outputPath, int alg) {
        int in  = GD.newInputFile(inputPath);
        int out = GD.newOutputFile(outputPath);
        if (GD.isFolder(in)) {
            Hierarchy H = new Hierarchy(GD.makeHierarchy(inputPath)); // TODO Se podria y del identificador
            writeFolderMetadata(in, H);
            for (int id : H.dfs()) {
                Compressor compressor = getBestCompressor(id, alg);
                long cursor_ini = GD.getWrittenBytes(id);
                compressor.compress();
                long cursor_fi = GD.getWrittenBytes(id);
                GD.modifyLong(id, cursor_ini, encodeMeta(compressor, cursor_fi-cursor_ini));
            }
        }
    }

    private void writeFolderMetadata(int id, Hierarchy h) {
        GD.writeBytes(id, h.toByteArray());

        for (int i : h.getFilesList()) {
            GD.writeBytes(id, GD.getName(id).getBytes());
            GD.writeByte(id, (byte) '\n');
        }

    }

    private long encodeMeta(Compressor compressor, long i) {
        long res = 0L;
        // por default esta Compressor_LZ78
        if (compressor instanceof Compressor_LZSS)
            res |= (1L << 62);
        if (compressor instanceof Compressor_LZW)
            res |= (2L << 62);
        if (compressor instanceof Compressor_JPEG)
            res |= (3L << 62);
        if (i >= (1L << 62))
            throw new IllegalArgumentException("LongitudFicheroDemasiadoGrande");
        return res;
    }

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

        ArrayList<Integer> files = GD.getFilesFromFolder(in);
        ArrayList<Byte> jerarquia = new ArrayList<>();
        makeHierarchy(jerarquia, files);

        // Posem un -1 per indicar el final de la jerarqui
        jerarquia.add((byte) -1);

        ArrayList<String> fileNames = new ArrayList<>();

        for (byte fileId : jerarquia) {
            GD.writeByte(out, fileId);
            if (fileId != -1) {
                fileNames.add(GD.getName(fileId));
            }
        }
        // Escrivim cada nom amb el mateix ordre i un separador (-1) entre cada un
        for (String name : fileNames) {
            GD.writeBytes(out, name.getBytes());
            GD.writeByte(out, (byte) -1);
        }
        // Un -1 indicant que hem acabat els noms, no hauria de ser necessari
        GD.writeByte(out, (byte) -1);

        writeFiles(out, jerarquia, 0);
    }

    private int getBestCompressor(int file) {
        return 0;
    }

    public void decompressFolder(int in, int out) {

    }

    // public void passPath(String path, String out) {
    //     int pathIn = GD.passPathIn(path);
    //     int pathOut = GD.passPathOut(out);

    //     if (GD.isFolder(pathIn))
    //         compressFolder(pathIn, pathOut);
    //     else startCompression(pathIn, pathOut, getBestCompressor(pathIn));
    // }

}

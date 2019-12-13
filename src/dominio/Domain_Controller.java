package dominio;

import persistencia.Persistence_Controller;

import java.io.IOException;
import java.util.ArrayList;

public class Domain_Controller {

    private Persistence_Controller persistence_controller;

    public Domain_Controller() {
        persistence_controller = new Persistence_Controller();
    }

    public void setPersistence_controller(Persistence_Controller persistence_controller) {
        this.persistence_controller = persistence_controller;
    }

    // Lectura
    /**
     * Lee un byte del fichero origen.
     * 
     * @return Entero que contiene el byte leido o -1 si no habia nada que leer.
     */
    int readByte(int id) {
        return persistence_controller.readByte(id);
    }

    /**
     * Lee N bytes del fichero origen en una cadena de bytes que se le pasa por
     * parametro.
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
        // TO DO: Tal vez no hace falta y se gestiona tododesde la persistencia
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
        Compressor_Controller compressor_controller = new Compressor_Controller(getBestCompressor(in, alg));
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
                    byte [] space = new byte[8];
                    for (int j = 0; j < space.length; ++j) space[j] = 0;

                    persistence_controller.writeBytes(file, space);
                    persistence_controller.writeBytes(file, longToByteArr(fileSize));

                    long compressedSize = persistence_controller.getOutputFileSize(out);
                    compressor.startCompression(file, out);
                    compressedSize = persistence_controller.getOutputFileSize(out) - compressedSize;

                    persistence_controller.modifyLong(out, index, compressedSize);
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
    private Compressor getBestCompressor1(int in, int alg) {
        String ext = persistence_controller.getExtension(in);
        if (ext.equals("ppm")) return new Compressor_JPEG();
        else if (ext.equals("txt")) {
            if (alg == 0) return new Compressor_LZ78();
            else if(alg == 1) return new Compressor_LZSS();
            else if(alg == 2) return new Compressor_LZW();
            else {
                long size = persistence_controller.getInputFileSize(in);
                if (size < 100L) throw new IllegalArgumentException("FicheroDemasiadoPequeno");
                if (size <= 50000L) return new Compressor_LZSS();
                else if (size <= 1000000L) return new Compressor_LZW();
                else return new Compressor_LZ78();
            }
        }
        else throw new IllegalArgumentException("Extension incorrecta, quiere utilizar los algoritmos universales?");
    }

    private int getBestCompressor(int in, int alg) {
        String ext = persistence_controller.getExtension(in);
        if (ext.equals("ppm")) return 3;
        else if (ext.equals("txt")) {
            if (alg >= 0 && alg <= 2) return alg;
            else {
                long size = persistence_controller.getInputFileSize(in);
                if (size < 100L) throw new IllegalArgumentException("FicheroDemasiadoPequeno");
                if (size <= 50000L) return 1;
                else if (size <= 1000000L) return 2;
                else return 3;
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
            for (int i = 0; i < rep.size(); ++i) 
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
            if (nr == 1) return new byte[]{0, 0, 0, 0};
            int headerSize = 6 + nr + (int) Math.ceil(nr / 8.0);
            byte[] res = new byte[headerSize];
            for (int i = 0; i < res.length; ++i) res[i] = 0;
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
            for (int i : m[1])
                res[pos++] = (byte) i;
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

        public int getRoot() {
            return root;
        }

        public boolean isFile() {
            return m[0].length == 1;
        }

    }

    public void compress(String inputPath, String outputPath, int alg) {
        Hierarchy H = new Hierarchy(persistence_controller.makeHierarchy(inputPath));
        int in = H.getRoot();
        int out = persistence_controller.newOutputFile(outputPath);
        int[][] temp = persistence_controller.makeHierarchy(inputPath);
        for (int i = 0; i < temp[1].length; ++i) {
            System.out.printf("id = %d -> name: %s\n", i, persistence_controller.getName(i));
        }
        in = H.getRoot();

        writeFolderMetadata(in, H);
        for (int id : H.dfs()) {
            int bestCompressor = getBestCompressor(in, alg);
            Compressor_Controller cc = new Compressor_Controller(bestCompressor);
            cc.setDomain_controller(this);
            long cursor_ini = persistence_controller.getWrittenBytes(out);
            persistence_controller.writeBytes(out, new byte[8]);
            cc.startCompression(id, out);
            long cursor_fi = persistence_controller.getWrittenBytes(out);
            // persistence_controller.modifyLong(out, cursor_ini, encodeMeta(bestCompressor,
            // cursor_fi-cursor_ini));
        }
        try {
            persistence_controller.closeWriter(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // No llamar a estos por ahora
    // public void compress(String in) {
    //     compress(in, getOnlyPathName(in) + ".egg", -1);
    // }

    // public void compress(String in, int alg) {
    //     compress(in, getOnlyPathName(in) + ".egg", alg);
    // }

    // public void compress(String in, String out) {
    //     compress(in, out, -1);
    // }

    // ejemplo: folder1/folder2/fichero.txt --> folder1/folder2/fichero
    private String getOnlyPathName(String file) {
        if (file == null) throw new IllegalArgumentException("El nombre del fichero es nulo");
        int i = file.lastIndexOf('.');
        if (i <= 0) throw new IllegalArgumentException("Fichero sin extension");
        return file.substring(0, i-1);
    }

    private void writeFolderMetadata(int id, Hierarchy h) {
        persistence_controller.writeBytes(id, h.toByteArray());
        if (!h.isFile()) {
            for (int i : h.getFilesList()) {
                persistence_controller.writeBytes(id, persistence_controller.getName(i).getBytes());
                persistence_controller.writeByte(id, (byte) '\n');
            }
        }
    }

    private long encodeMeta(long compressor, long i) {
        long res = 0L;
        // por default esta Compressor_LZ78
        if (compressor >= 0 && compressor <= 3) res |= (compressor << 62);
        if (i >= (1L << 62))
            throw new IllegalArgumentException("LongitudFicheroDemasiadoGrande");
        return res | i;
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

        ArrayList<Integer> files = persistence_controller.getFilesFromFolder(in);
        ArrayList<Byte> jerarquia = new ArrayList<>();
        makeHierarchy(jerarquia, files);

        // Posem un -1 per indicar el final de la jerarqui
        jerarquia.add((byte) -1);

        ArrayList<String> fileNames = new ArrayList<>();

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

    // public void passPath(String path, String out) {
    //     int pathIn = GD.passPathIn(path);
    //     int pathOut = GD.passPathOut(out);

    //     if (GD.isFolder(pathIn))
    //         compressFolder(pathIn, pathOut);
    //     else startCompression(pathIn, pathOut, getBestCompressor(pathIn));
    // }

}

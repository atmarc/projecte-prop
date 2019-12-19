package dominio;

import persistencia.Persistence_Controller;

import java.io.IOException;
import java.util.ArrayList;

public class Domain_Controller {

    private Persistence_Controller persistence_controller;

    /**
     * Constructora del controlador del dominio
     */
    public Domain_Controller() {
        persistence_controller = Persistence_Controller.getPersistence_controller();
    }

    /**
     * Assigna al controlador del dominio actual un nuevo controlador que recibe como parametro
     * @param persistence_controller el nuevo controlador de persistencia
     */
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
     * Lee N bytes del fichero identificado por el numero id en una cadena de bytes que se le pasa por
     * parametro. El N representa la longitud de la cadena que tiene como parametro.
     * 
     * @param word Cadena de bytes sobre la que se introducira la lectura.
     * @return Cantidad de bytes leida o -1 si no habia nada que leer.
     */
    int readNBytes(int id, byte[] word) {
        return persistence_controller.readBytes(id, word);
    }

    /**
     * Cierra el buffer de lectura que tiene el identificador id .
     */
    void closeReader(int id) throws IOException {
        persistence_controller.closeReader(id);
    }

    /**
     * Lee todos los bytes del fichero origen y los guarda en una cadena.
     * 
     * @return Cadena de bytes con todos los bytes del fichero origen.
     * @throws Exception // TODO : Que excepciones?
     */
    byte[] readAllBytes(int id) throws Exception {
        return persistence_controller.readAllBytes(id);
    }

    // Escritura

    /**
     * Escribe un byte en fichero de salida.
     * 
     * @param B Byte que se desea escribir en el fichero de salida.
     */
    void writeByte(int id, byte B) {
        persistence_controller.writeByte(id, B);
    }

    /**
     * Escribe una cadena de bytes en el fichero de salida.
     * 
     * @param word Cadena de bytes que se desea escribir en el fichero de salida.
     */
    void writeBytes(int id, byte[] word) {
        persistence_controller.writeBytes(id, word);
    }

    /**
     * Cierra buffer de escritura.
     */
    void closeWriter(int id) throws IOException {
        persistence_controller.closeWriter(id);
    }

    // File Info
    public long getInputFileSize(int id) {
        return persistence_controller.getInputFileSize(id);
    }

    public long getOutputFileSize(int id) {
        return persistence_controller.getOutputFileSize(id);
    }

    public void startCompression(int in, int out, int alg) throws Exception {
        Compressor_Controller compressor_controller = new Compressor_Controller(getBestCompressor(in, alg));
        compressor_controller.setDomain_controller(this);
        compressor_controller.startCompression(in, out);
    }

    public void startDecompression(int in, int out, int alg) throws Exception {
        Decompressor_Controller decompressor_controller = new Decompressor_Controller(alg);
        decompressor_controller.setDomain_controller(this);
        // Si és un sol arxiu
        decompressor_controller.startDecompression(in, out);
    }

    public String getNameNE(String path) {
        return persistence_controller.getNameNE(path);
    }
    ///////////////////////////
    // Metodos auxiliares


    // Crea la jerarquia llegint "l'arbre d'arxius" en preordre, i després de cada
    // fulla posa un -1
    // El faig en bytes, per tant podrem tenir fins a 2^8 fitxers
    // Exemple: a,b(c,d),e(f,g) --> [a, -1, b, c, -1, d, -1, -1, e, f, -1, g, -1,
    // -1]
    private void makeHierarchy(ArrayList<Byte> hierarchy, ArrayList<Integer> files) {
        for (int f : files) {
            if (persistence_controller.isFolder(f)) {
                hierarchy.add((byte) f);
                makeHierarchy(hierarchy, persistence_controller.getFilesFromFolder(f));
                hierarchy.add((byte) -1);
            } else {
                hierarchy.add((byte) f);
                hierarchy.add((byte) -1);
            }
        }
    }

    private void writeFiles(int out, ArrayList<Byte> files, int index) throws Exception {
        for (int i = index; i < files.size(); ++i) {
            int file = files.get(i);
            if (file != (byte) -1) {
                if (persistence_controller.isFolder(file)) {
                    writeFiles(file, files, i + 1);
                } else {
                    Compressor_Controller compressor = new Compressor_Controller(getBestCompressor(1, file));
                    // TODO: 8 bytes pel size potser és massa
                    long fileSize = persistence_controller.getOutputFileSize(file);
                    byte[] space = new byte[8];
                    for (int j = 0; j < space.length; ++j)
                        space[j] = 0;

                    persistence_controller.writeBytes(file, space);
                    persistence_controller.writeBytes(file, longToByteArr(fileSize));

                    long compressedSize = persistence_controller.getOutputFileSize(out);
                    compressor.startCompression(file, out);
                    compressedSize = persistence_controller.getOutputFileSize(out) - compressedSize;

                    persistence_controller.modifyLong(out, index, compressedSize);
                }
            }
            // Si hi ha dos -1 seguits vol dir que ja ha sortit de la carpeta
            else if (i + 1 < files.size() && files.get(i + 1) == (byte) -1)
                break;
        }
    }

    /**
     * Calcula el algoritmo de compression en dependencia del fichero y algoritmo que recibe como parametro.
     * @param in identificador del fichero a comprimir
     * @param alg el algoritmo con el que se tiene que comprimir
     * @return Si la extension es .ppm  entonces retorna el algoritmo 3, si la extension es .txt y 0 <= alg <= 2,
     retorna el algoritmo correspondiente si no retorna el algoritmo mas adecuado para este fichero
     */
    private int getBestCompressor(int in, int alg) {
        String ext = persistence_controller.getExtension(in);
        if (ext.equals("ppm"))
            return 3;
        else if (ext.equals("txt")) {
            if (alg >= 0 && alg <= 2)
                return alg;
            else {
                long size = persistence_controller.getInputFileSize(in);
                // if (size < 100L)
                //     throw new IllegalArgumentException("FicheroDemasiadoPequeno");
                if (size <= 50000L)
                    return 1;
                else if (size <= 1000000L)
                    return 2;
                else
                    return 0;
            }
        } else
            throw new IllegalArgumentException("Extension incorrecta, quiere utilizar los algoritmos universales?");
    }

    /**
     * Calcula la direccion y el nombre de un fichero eliminando la extension
     * @param file la direccion con el nombre de un fichero
     * @return la direccion con el nombre del fichero sin la extension
     */
    // ejemplo: folder1/folder2/fichero.txt --> folder1/folder2/fichero
    private String getPathAndName(String file) {
        if (file == null) throw new IllegalArgumentException("El nombre del fichero es nulo");
        int i = file.lastIndexOf('.');
        if (i <= 0) throw new IllegalArgumentException("Fichero sin extension");
        return file.substring(0, i-1);
    }

    /**
     * Escribe la estructura de una carpeta/fichero en un fichero codificado por id
     * representado por el identificador id
     * @param id identificador del fichero
     * @param h la estructura de la carpeta/fichero
     */
    private void writeFolderMetadata(int id, Hierarchy h) {
        persistence_controller.writeBytes(id, h.toByteArray());
        if (!h.isFile()) {
            for (int i : h.getFilesList()) {
                if (i != h.getRoot()) {
                    persistence_controller.writeBytes(id, persistence_controller.getName(i).getBytes());
                    persistence_controller.writeByte(id, (byte) '\n');
                }
            }
        }
    }

    /**
     * Codifica un compresor
     * @param compressor
     * @param i
     * @return
     */
    private long encodeMeta(long compressor, long i) {
        long res = 0L;
        // por default esta Compressor_LZ78
        if (compressor >= 0 && compressor <= 3) res |= (compressor << 62);
        if (i >= (1L << 62))
            throw new IllegalArgumentException("LongitudFicheroDemasiadoGrande");
        return res | i;
    }

    private int[][] makeHierarchy(String inputPath, String outputPath, boolean sobrescribir) throws Exception {
        Hierarchy h = new Hierarchy(persistence_controller.makeHierarchy(inputPath));
        int in = h.getRoot();
        // Calcular el tamano del 'folder' header
        byte[] aux = new byte[4];
        persistence_controller.readBytes(in, aux);
        int headerSize = toInt(aux);
        // Calcular el numero de ficheros comprmimdos en el fichero de entrada
        int nrFiles;
        if (headerSize == 0) {
            // Si es un solo fichero el header se acaba aqui
            nrFiles = 1;
            int out = persistence_controller.newOutputFile(outputPath, sobrescribir);
            return new int[][]{{0},{out}};
        }
        aux = new byte[2];
        persistence_controller.readBytes(in, aux);
        nrFiles = toInt(aux);
        // Calcular la estructura de la carpeta que fue comprimida
        int[][] res = new int[2][nrFiles];
        aux = new byte[(int)Math.ceil(nrFiles/8.0)];
        persistence_controller.readBytes(in, aux);
        for (int i = 0; i < nrFiles; ++i) {
            res[0][i] = ((aux[i/8] & (1 << (7 - i%8))) != 0) ? 1 : 0;
            res[1][i] = persistence_controller.readByte(in);
        }
        int[] corr = new int[nrFiles];
        int pos = 0;

        corr[pos++] = persistence_controller.newDir(outputPath);
        for (int i = 1; i < nrFiles; ++i) {
            StringBuilder fileName = new StringBuilder();
            char c;
            while ((c = (char)persistence_controller.readByte(in)) != '\n') fileName.append(c);
            if (res[0][i] == 1) corr[pos++] = persistence_controller.newDir(fileName.toString(), res[1][i]);
            else corr[pos++] = persistence_controller.newOutputFile(fileName.toString(), res[1][i]);
        }
        for (int i = 0; i < nrFiles; ++i) {
            res[1][i] = corr[res[1][i]];
        }
        return res;
    }

    private int toInt(byte[] aux) {
        int res = 0, j = aux.length-1;
        for (byte i : aux)
            res |= ((i&0xFF) << (8*j--));
        return res;
    }

    public long toLong(byte[] aux) {
        long res = 0, j = aux.length-1;
        for (byte i : aux)
            res |= ((i&0xFF) << (8*j--));
        return res;
    }

    private static class Hierarchy {
        private int root;
        private int[][] m;
        ArrayList<ArrayList<Integer>> rep;

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
            if (nr == 1)
                return new byte[] { 0, 0, 0, 0 };
            int headerSize = 6 + nr + (int) Math.ceil(nr / 8.0);
            byte[] res = new byte[headerSize];
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

        ArrayList<Integer> getLeafs() {
            return getLeafsAux(root);
        }

        ArrayList<Integer> getLeafsAux(int i) {
            ArrayList<Integer> crr = rep.get(i);
            ArrayList<Integer> res = new ArrayList<>();
            if (crr.isEmpty())
                res.add(i);
            else {
                for (Integer v : crr) {
                    res.addAll(getLeafsAux(v));
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

        public ArrayList<Integer> getNodes() {
            return getNodesAux(root);
        }

        ArrayList<Integer> getNodesAux(int i) {
            ArrayList<Integer> crr = rep.get(i);
            ArrayList<Integer> res = new ArrayList<>();
            res.add(i);
            if (!crr.isEmpty()) {
                for (Integer v : crr) {
                    res.addAll(getNodesAux(v));
                }
            }
            return res;
        }

        public void setRoot(int root) {
            this.root = root;
        }

    }

    ///////////////////
    // Compression
    /**
     * Comprime un fichero con el algoritmo mas adecuado.
     * @pre El fichero in existe y es de
     * @param in direccion del fichero a comprimir
     * @throws Exception Ilegal argument exception TODO: <- this
     * @post En
     */
    public void compress(String in) throws Exception {
        compress(in, getPathAndName(in) + ".egg", -1, (byte) -1, false);
    }

    public void compress(String in, byte ratio) throws Exception {
        compress(in, getPathAndName(in) + ".egg", -1, ratio, false);
    }

    public void compress(String in, int alg) throws Exception {
        compress(in, getPathAndName(in) + ".egg", alg, (byte) -1, false);
    }

    public void compress(String in, int alg, byte ratio) throws Exception {
        compress(in, getPathAndName(in) + ".egg", alg, ratio, false);
    }

    public void compress(String in, String out) throws Exception {
        compress(in, out, -1, (byte) -1, false);
    }

    public void compress(String in, String out, byte ratio) throws Exception {
        compress(in, out, -1, ratio, false);
    }

    public void compress(String in, String out, int alg) throws Exception {
        compress(in, out, alg, (byte) -1, false);
    }

    public void compress(String inputPath, String outputPath, int alg, byte ratio, boolean sobrescribir) throws Exception {
        persistence_controller.clear();
        Hierarchy H = new Hierarchy(persistence_controller.makeHierarchy(inputPath));
        int in = H.getRoot();
        int out = persistence_controller.newOutputFile(outputPath, sobrescribir);

        writeFolderMetadata(in, H);
        for (int id : H.getLeafs()) {
            int bestCompressor = getBestCompressor(id, alg);
            System.out.println("Compresor: " + bestCompressor);
            Compressor_Controller cc = new Compressor_Controller(bestCompressor);
            cc.setDomain_controller(this);
            long cursor_ini = persistence_controller.getWrittenBytes(out);
            persistence_controller.writeBytes(out, new byte[8]);
            if (bestCompressor == 3 && ratio != -1) cc.startCompression(in, out, ratio);
            else cc.startCompression(id, out);
            long cursor_fi = persistence_controller.getWrittenBytes(out);
            persistence_controller.modifyLong(out, cursor_ini, encodeMeta(bestCompressor,
            cursor_fi-cursor_ini-8));
        }
        persistence_controller.closeWriter(out);
    }

    ///////////////////
    // Decompression
    public void decompress(String inputPath, String outputPath, boolean sobrescribir) throws Exception {
        persistence_controller.clear();
        Hierarchy H = new Hierarchy(makeHierarchy(inputPath, outputPath, sobrescribir));
        int in = H.getRoot();
        ArrayList<Integer> q = H.getLeafs();
        for (int id : H.getLeafs()) {
            byte[] aux = new byte[8];
            persistence_controller.readBytes(in, aux);
            int alg = (aux[0] & 0xFF) >>> 6;
            aux[0] &= 0x3F;
            long size = toLong(aux);
            persistence_controller.setReadLimit(in, size);
            Decompressor_Controller dc = new Decompressor_Controller(alg);
            dc.setDomain_controller(this);
            dc.startDecompression(in, id);
            persistence_controller.rmReadLimit(in);
        }
        try {
            persistence_controller.closeReader(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void compressFolder(int in, int out) throws Exception {
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

}

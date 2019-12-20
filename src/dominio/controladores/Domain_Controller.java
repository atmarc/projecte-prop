package dominio.controladores;

import persistencia.Persistence_Controller;
import presentacion.Presentation_Controller;

import java.io.IOException;
import java.util.ArrayList;

public class Domain_Controller {

    private Persistence_Controller persistence_controller;
    private Presentation_Controller presentation_controller;
    private long time;
    private double comp_size;
    private double orig_size;

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

    /**
     * Setter de la controladora de presentacion
     * @param pc Controladora de presentacion a settear
     */
    public void setPresentation_controller(Presentation_Controller pc) {
        presentation_controller = pc;
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

    /**
     * Calcula la dimension del fichero de entrada con el identificador id
     * @param id identificador del fichero
     * @return la dimension en bytes del fichero
     */
    // File Info
    public long getInputFileSize(int id) {
        return persistence_controller.getInputFileSize(id);
    }

    /**
     * Calcula la dimension del fichero de salida con el identificador id
     * @param id identificador del fichero
     * @return la dimension en bytes del fichero
     */
    public long getOutputFileSize(int id) {
        return persistence_controller.getOutputFileSize(id);
    }

    /** Elimina la extension de una direccion. Si en la direccion path esta una carpeta,
     * devuelve el path de entrada si no, devuelve el path sin la extension
     * @param path la direccion desde cual se quiere eliminar la extension
     * @return Si en el path es una carpeta, devuelve el path de la entrada si no, la direccion sin la extension
     */
    public String getNameNE(String path) {
        return persistence_controller.getNameNE(path);
    }

    /**
     * Verifica si el elemento de la direccion path es una carpeta o no
     * @param path la direccion del elemento
     * @return true - si es una carpeta, no - en caso contrario
     */
    public boolean isFolder(String path) {
        return persistence_controller.isFolder(path);
    }
    ///////////////////////////
    // Metodos auxiliares

    /**
     * Calcula el algoritmo de compression en dependencia del fichero y algoritmo que recibe como parametro.
     * @param in identificador del fichero a comprimir
     * @param alg el algoritmo con el que se tiene que comprimir
     * @return Si la extension es .ppm  entonces retorna el algoritmo 3, si la extension es .txt y 0 <= alg <= 2,
     * retorna el algoritmo correspondiente si no retorna el algoritmo mas adecuado para este fichero. Si el fichero es de otro tipo
     * retorna en dependencia del tamano del fichero el algoritmo mas apropiado.
     */
    private int getBestCompressor(int in, int alg) {
        String ext = persistence_controller.getExtension(in);
        long size = persistence_controller.getInputFileSize(in);
        if (ext.equals("ppm"))
            return 3;
        else if (ext.equals("txt")) {
            if (alg >= 0 && alg <= 2)
                    return alg;
            else {
                // if (size < 100L)
                //     throw new IllegalArgumentException("FicheroDemasiadoPequeno");
                if (size <= 50000L)
                    return 1;
                else if (size <= 1000000L)
                    return 2;
                else
                    return 0;
            }
        }
        else {
            if (size <= 50000L)
                return 1;
            else
                return 2;
        }
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
     * Escribe la estructura de una carpeta/fichero en un fichero codificado por id.
     * Al principio del fichero se escribe el tamano de la cabecera (4 bytes), a continuacion va el numero de ficheros
     * comprimidos en este fichero (2 bytes) seguido de la codificacion en bits para cada fichero de si es una carpeta (1)
     * o si es un fichero (0)( ceil(#elementos/8) ). A continuacion para cada fichero se escribe el identificador de la carpeta padre (1 byte),
     * para el fichero raiz el padre es el mismo. La extension del fichero comprimido(cadena vacia en el caso de
     * si es una carpeta) seguido de un salto de linea, seguido de todos los nombres, salvo el elemento raiz, que
     * se van a comprimir separados por saltos de lineas.
     * -------------------------------------------------------------------------------------------------------------
     * |tamano cabecera | # ficheros/carpetas | typos elementos | | extesion elemento raiz| \n | nombre1 | \n | ...|
     * -------------------------------------------------------------------------------------------------------------
     * @param id identificador del fichero
     * @param h la estructura de la carpeta/fichero
     */
    private void writeFolderMetadata(int id, Hierarchy h) {
        persistence_controller.writeBytes(id, h.toByteArray());
        int root = h.getRoot();
        if (!persistence_controller.isFolder(root))
            persistence_controller.writeBytes(id, persistence_controller.getExtension(root).getBytes());
        persistence_controller.writeByte(id, (byte) '\n');
        for (int i : h.getFilesList()) {
            if (i != h.getRoot()) {
                persistence_controller.writeBytes(id, persistence_controller.getName(i).getBytes());
                persistence_controller.writeByte(id, (byte) '\n');
            }
        }
    }

    /**
     * Codifica el identificador de un compresor y la longitud de un ficheros es un long
     * Los primeros 2 bits de mayor peso representa el algoritmo utilizado para comprimir el fichero,
     * los otros 62 bits representan el tamano del fichero comprimido.
     * @param compressor el tipo de compresor 0 - LZ78, 1 - LZSS, 2 - LZW, 3 - JPEG
     * @param i la longitud del fichero comprimido
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

    /**
     * @param inputPath
     * @param outputPath
     * @param sobrescribir
     * @return
     * @throws Exception
     */
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
            char c;
            StringBuilder type = new StringBuilder();
            while ((c = (char) persistence_controller.readByte(in)) != '\n') type.append(c);
            int out;
            if (type.length() != 0)
                out = persistence_controller.newOutputFile(outputPath + '.' + type.toString(), sobrescribir);
            else out = persistence_controller.newOutputFile(outputPath, sobrescribir);
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
        char c;
        StringBuilder type = new StringBuilder();
        while ((c = (char) persistence_controller.readByte(in)) != '\n') type.append(c);
        if (type.length() == 0) corr[pos++] = persistence_controller.newDir(outputPath);
        else corr[pos++] = persistence_controller.newDir(outputPath + '.' + type);
        for (int i = 1; i < nrFiles; ++i) {
            StringBuilder fileName = new StringBuilder();
            while ((c = (char)persistence_controller.readByte(in)) != '\n') fileName.append(c);
            if (res[0][i] == 1) corr[pos++] = persistence_controller.newDir(fileName.toString(), res[1][i]);
            else corr[pos++] = persistence_controller.newOutputFile(fileName.toString(), res[1][i]);
        }
        for (int i = 0; i < nrFiles; ++i) {
            res[1][i] = corr[res[1][i]];
        }
        return res;
    }

    /**
     * @param aux
     * @return
     */
    private int toInt(byte[] aux) {
        int res = 0, j = aux.length-1;
        for (byte i : aux)
            res |= ((i&0xFF) << (8*j--));
        return res;
    }

    /**
     * @param aux
     * @return
     */
    public long toLong(byte[] aux) {
        long res = 0, j = aux.length-1;
        for (byte i : aux)
            res |= ((i&0xFF) << (8*j--));
        return res;
    }

    /**
     * @return
     */
    public long getTime() {
        return time;
    }

    /**
     * @return
     */
    public double getRatio() {
        return comp_size/orig_size;
    }

    /**
     *
     */
    private static class Hierarchy {
        private int root;
        private int[][] m;
        ArrayList<ArrayList<Integer>> rep;

        /**
         * @return
         */
        public ArrayList<Integer> getFilesList() {
            ArrayList<Integer> res = new ArrayList<>();
            for (int i = 0; i < rep.size(); ++i)
                res.add(i);
            return res;
        }

        /**
         * @param src
         */
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

        /**
         * @return
         */
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

        /**
         * @return
         */
        ArrayList<Integer> getLeafs() {
            return getLeafsAux(root);
        }

        /**
         * @param i
         * @return
         */
        ArrayList<Integer> getLeafsAux(int i) {
            ArrayList<Integer> crr = rep.get(i);
            ArrayList<Integer> res = new ArrayList<>();
            if (crr.isEmpty() && m[0][i] == 0) {
                res.add(i);
            } else {
                for (Integer v : crr) {
                    res.addAll(getLeafsAux(v));
                }
            }
            return res;
        }

        /**
         * @return
         */
        public int getRoot() {
            return root;
        }

        /**
         * @return
         */
        public boolean isFile() {
            return m[0].length == 1;
        }

        /**
         * @return
         */
        public ArrayList<Integer> getNodes() {
            return getNodesAux(root);
        }

        /**
         * @param i
         * @return
         */
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

        /**
         * @param root
         */
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
    public void compress(String in, boolean sobreescribir) throws Exception {
        compress(in, getPathAndName(in) + ".egg", -1, (byte) -1, sobreescribir);
    }

    /**
     * @param in
     * @param ratio
     * @param sobreescribir
     * @throws Exception
     */
    public void compress(String in, byte ratio, boolean sobreescribir) throws Exception {
        compress(in, getPathAndName(in) + ".egg", -1, ratio, sobreescribir);
    }

    /**
     * @param in
     * @param alg
     * @param sobreescribir
     * @throws Exception
     */
    public void compress(String in, int alg, boolean sobreescribir) throws Exception {
        compress(in, getPathAndName(in) + ".egg", alg, (byte) -1, sobreescribir);
    }

    /**
     * @param in
     * @param alg
     * @param ratio
     * @param sobreescribir
     * @throws Exception
     */
    public void compress(String in, int alg, byte ratio, boolean sobreescribir) throws Exception {
        compress(in, getPathAndName(in) + ".egg", alg, ratio, sobreescribir);
    }

    /**
     * @param in
     * @param out
     * @param sobreescribir
     * @throws Exception
     */
    public void compress(String in, String out, boolean sobreescribir) throws Exception {
        compress(in, out, -1, (byte) -1, sobreescribir);
    }

    /**
     * @param in
     * @param out
     * @param ratio
     * @param sobreescribir
     * @throws Exception
     */
    public void compress(String in, String out, byte ratio, boolean sobreescribir) throws Exception {
        compress(in, out, -1, ratio, sobreescribir);
    }

    /**
     * @param in
     * @param out
     * @param alg
     * @param sobreescribir
     * @throws Exception
     */
    public void compress(String in, String out, int alg, boolean sobreescribir) throws Exception {
        compress(in, out, alg, (byte) -1, sobreescribir);
    }

    /**
     * @param inputPath
     * @param outputPath
     * @param alg
     * @param ratio
     * @param sobrescribir
     * @throws Exception
     */
    public void compress(String inputPath, String outputPath, int alg, byte ratio, boolean sobrescribir) throws Exception {

        time = 0;
        comp_size = 0;
        orig_size = 0;

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

            time =+ cc.getTime();

            long cursor_fi = persistence_controller.getWrittenBytes(out);
            persistence_controller.modifyLong(out, cursor_ini, encodeMeta(bestCompressor,
            cursor_fi-cursor_ini-8));
        }
        persistence_controller.closeWriter(out);

        orig_size = persistence_controller.getInputFileSize(-1);
        comp_size = persistence_controller.getOutputFileSize(out);
    }

    ///////////////////
    // Decompression

    /**
     * @param inputPath
     * @param outputPath
     * @param sobrescribir
     * @throws Exception
     */
    public void decompress(String inputPath, String outputPath, boolean sobrescribir) throws Exception {

        System.out.println(outputPath);
//        presentation_controller.setMode(-1);
        persistence_controller.clear();
        Hierarchy H = new Hierarchy(makeHierarchy(inputPath, outputPath, sobrescribir));
        int in = H.getRoot();
        ArrayList<Integer> q = H.getLeafs();
        int count = 0;
        for (int id : H.getLeafs()) {
            ++count;
            time = 0;
            byte[] aux = new byte[8];
            persistence_controller.readBytes(in, aux);

            int alg = (aux[0] & 0xFF) >>> 6;
            aux[0] &= 0x3F;
            long size = toLong(aux);

            persistence_controller.setReadLimit(in, size);

            Decompressor_Controller dc = new Decompressor_Controller(alg);
            dc.setDomain_controller(this);
            dc.startDecompression(in, id);
            time =+ dc.getTime();

            persistence_controller.rmReadLimit(in);
        }

        // Si solo se ha descomprimido un archivo, lo muestra.
        if (count == 1) presentation_controller.setOutPath(persistence_controller.getOutPath(0));

        persistence_controller.closeReader(in);
    }

    /**
     * @param l
     * @return
     */
    public byte[] longToByteArr(long l) {
        byte[] arr = new byte[8];
        for (int i = 0; i < 8; ++i) {
            arr[i] = (byte) l;
            l = l >> 8;
        }
        return arr;
    }

    /**
     * @param b
     * @return
     */
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


    /**
     * @param path
     */
    public void visualiceFile(String path) {
        try {
            persistence_controller.visualizeFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

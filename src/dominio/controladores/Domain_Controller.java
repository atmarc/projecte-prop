package dominio.controladores;

import persistencia.Persistence_Controller;
import presentacion.Presentation_Controller;

import java.io.IOException;
import java.util.ArrayList;

public class Domain_Controller {

    private Persistence_Controller persistence_controller;
    private Presentation_Controller presentation_controller = null;
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
                if (size <= 50000L) return 1;
                else if (size <= 1000000L) return 2;
                else return 0;
            }
        }
        else {
            if (size <= 50000L) return 1;
            else return 2;
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
     * @return Un long en el cual los primeros 2 bits representan el algoritmo y los restos el tamano del fichero
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
     * Desde un fichero comprimido lee la cabecera y crea una tabla de 2XN donde la primera fila representa si un
     * elemento es carpeta o fichero y la segunda representa para cada elemento cual es su padre. Ademas pide a
     * persistencia que cree todos los ficheros y carpetas de esta jerarquia.
     * @pre el inputPath es un fichero .egg
     * @param inputPath la direccion del fichero comprimido
     * @param outputPath la direccion donde se quiere replicar la jerarquia en carpetas y ficheros
     * @param sobrescribir si est true  y ja existe esta carpeta en la direccion outputPath la sobrescribe
     * @return una tabla de 2XN donde la primera fila representa si un
     * elemento es carpeta o fichero y la segunda representa para cada elemento cual es su padre, el N es el numero de
     * elementos
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

    /** Convierte un array de bytes en un integer.
     * @pre la dimension de la tabla es <= 4
     * @param aux el valor a convertir
     * @return el valor convertido
     */
    private int toInt(byte[] aux) {
        int res = 0, j = aux.length-1;
        for (byte i : aux)
            res |= ((i&0xFF) << (8*j--));
        return res;
    }

    /** Convierte una array de bytes en un long.
     * @pre la dimension de la tabla es <= 8
     * @param aux el valor a convertir
     * @return el valor convertido
     */
    public long toLong(byte[] aux) {
        long res = 0, j = aux.length-1;
        for (byte i : aux)
            res |= ((i&0xFF) << (8*j--));
        return res;
    }

    /**
     * Obtiene el tiempo de la ultima compresion
     * @return el tiempo de la ultima compresion
     */
    public long getTime() {
        return time;
    }

    /**
     * Obtiene el ratio de la ultima compresion
     * @return el ratio de la ultima compresion
     */
    public double getRatio() {
        return Math.round(1000*(comp_size/orig_size))/1000.0;
    }

    public void deleteFile(String s) {
        persistence_controller.deleteFile(s);
    }

    /**
     * Clase que representa una jerarquia de una carpeta/fichero
     * Si la raiz de la jerarquia es un fichero entonces la jerarquia solo contiene un elemento
     */
    private static class Hierarchy {

        private int root;                   ///< La carpeta/fichero raiz de la jerarquia
        private int[][] m;                  ///< codificacion alternativa con acceso rapido a los antecesores de un elemento
        ArrayList<ArrayList<Integer>> rep;  ///< representacion mediante listas de adiacencia de la jerarquia

        /**
         * Obtiene todos los identificadores de las carpetas y ficheros de la jerarquia
         * @return una lista con identificadores de todos los ficheros y carpetas
         */
        public ArrayList<Integer> getFilesList() {
            ArrayList<Integer> res = new ArrayList<>();
            for (int i = 0; i < rep.size(); ++i)
                res.add(i);
            return res;
        }

        /**
         * Constructora de la clase que crea un objecto a partir de una tabla 2xN, en la cual la primera linea
         * representa si el elemento con el identificador numero de columna es carpeta o fichero y la segunda
         * representa el antecesor de este elemento en la jerarquia
         * @param src la tabla a partir de la cual se crea la jerarquia
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
         * Codifica una jerarquia en una array de bytes.
         * Los primeros 4 bytes de la tabla representa la longitud del array en el que se convertira la jerarquia.
         * Los siguientes 2 representan el numero de elementos en la jerarquia (carpetas y ficheros).
         * Los siguientes ceil(#elemento/8) bytes representan el tipo de cada elemento de la jerarquia (carpeta o fichero)
         * Los siguientes #elementos bytes representan el identificador del antecesor de cada elemento en particular
         * @return la codificacion de la jerarquia
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
         * Obtiene todos los identificadores de los ficheros de la jerarquia
         * @return una lista con los identificadores de todos los ficheros de la jerarquia
         */
       public ArrayList<Integer> getLeafs() {
            return getLeafsAux(root);
        }

        /**
         * Funcion auxiliar para obtener todos los identificadores de los ficheros de la jerarquia
         * @param i el elemento que se analiza ajora para obtener dodos los ficheros dentro de el
         * @return una lista con todos los identificadores de todos los ficheros en el elemento i
         */
        private ArrayList<Integer> getLeafsAux(int i) {
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
         * Obtiene la raiz de la jerarquia
         * @return la raiz de la jerarquia
         */
        public int getRoot() {
            return root;
        }

        /**
         * Calcula si la raiz de la jerarquia es una carpeta
         * @return si es true le
         */
        public boolean isFile() {
            return m[0].length == 1;
        }

    }

    ///////////////////
    // Compression

    /**
     * Comprime un fichero con el algoritmo mas adecuado.
     * Si el fichero es PPM el ration de compresion es 5
     * @pre El fichero <<in>> existe
     * @param in direccion del fichero a comprimir
     * @param sobrescribir sobrescribir o no, si el fichero de salida existe
     * @throws Exception Errores de lectura {@link IOException}
     * @post En la direccion del fichero original se ha creado un nuevo fichero con la extension ".egg" y
     * con el mismo nombre que el original que representa el fichero original comprimido
     */
    public void compress(String in, boolean sobrescribir) throws Exception {
        compress(in, getPathAndName(in) + ".egg", -1, (byte) -1, sobrescribir);
    }

    /**
     * Comprime un fichero PPM.
     * @pre El fichero <<in>> existe
     * @param in direccion del fichero ppm
     * @param ratio el ratio de la compresion
     * @param sobrescribir sobrescribir o no, si el fichero de salida existe
     * @throws Exception Errores de lectura
     * @post En la direccion del fichero original se ha creado un nuevo fichero con la extension ".egg" y
     * con el mismo nombre que el original que representa el fichero original comprimido
     */
    public void compress(String in, byte ratio, boolean sobrescribir) throws Exception {
        compress(in, getPathAndName(in) + ".egg", -1, ratio, sobrescribir);
    }

    /**
     * Comprime un fichero con el algoritmo especifico
     * @pre El fichero <<in>> existe
     * @param in direccion del fichero a comprimir
     * @param sobrescribir sobrescribir o no, si el fichero de salida existe
     * @param alg el algoritmo para la compresion
     * @post En la direccion del fichero original se ha creado un nuevo fichero con la extension ".egg" y
     * con el mismo nombre que el original que representa el fichero original comprimido
     */
    public void compress(String in, int alg, boolean sobrescribir) throws Exception {
        compress(in, getPathAndName(in) + ".egg", alg, (byte) -1, sobrescribir);
    }

    /**
     * Comprime un fichero con el algoritmo mas adecuado y lo guarda en la direccion y con el nombre <<out>>
     * Si el fichero es PPM el ration de compresion es 5
     * @pre El fichero <<in>> existe
     * @param in direccion del fichero a comprimir
     * @param out la direccion donde guardar el fichero comprimido y su nombre con la extension ".egg"
     *            ejemplo: carpeta1/carpeta2/fichero_comprimido.egg
     * @param sobrescribir sobrescribir o no, si el fichero de salida existe
     * @post En la direccion <<out>> se ha creado un nuevo fichero con la extension ".egg" y
     */
    public void compress(String in, String out, boolean sobrescribir) throws Exception {
        compress(in, out, -1, (byte) -1, sobrescribir);
    }

    /**
     * Comprime un fichero PPM.
     * @pre El fichero <<in>> existe
     * @param in direccion del fichero ppm
     * @param out la direccion donde guardar el fichero comprimido y su nombre con la extension ".egg"
     *            ejemplo: carpeta1/carpeta2/fichero_comprimido.egg
     * @param ratio el ratio de la compresion
     * @param sobrescribir sobrescribir o no, si el fichero de salida existe
     * @post En la direccion <<out>> se ha creado un nuevo fichero con la extension ".egg" y
     */
    public void compress(String in, String out, byte ratio, boolean sobrescribir) throws Exception {
        compress(in, out, -1, ratio, sobrescribir);
    }

    /**
     * Comprime un fichero con un algoritmo especifico
     * Si el fichero es PPM el ration de compresion es el por defecto (5)
     * @pre El fichero <<in>> existe
     * @param in direccion del fichero a comprimir
     * @param out la direccion donde guardar el fichero comprimido y su nombre con la extension ".egg"
     *            ejemplo: carpeta1/carpeta2/fichero_comprimido.egg
     * @param alg algoritmo para utilizar a la compression
     * @param sobrescribir sobrescribir o no, si el fichero de salida existe
     */
    public void compress(String in, String out, int alg, boolean sobrescribir) throws Exception {
        compress(in, out, alg, (byte) -1, sobrescribir);
    }

    /**
     * Comprime un fichero cualquiera con el algoritmo especifico.
     * Si el fichero es PPM utiliza el ratio especificado como parametro
     * @detail La compression empieza con identificar la jerarquia del elemento que se quiere comprimir.
     * Haberlo identificado, se crea y se escribe la estructura ,la extension del elemento a comprimir y
     * si es una carpeta, los nombres de cada elemento de esta carpeta.
     * @param inputPath direccion del fichero a comprimir
     * @param outputPath la direccion donde guardar el fichero comprimido y su nombre con la extension ".egg"
     *                   ejemplo: carpeta1/carpeta2/fichero_comprimido.egg
     * @param alg  algoritmo para utilizar a la compression
     * @param ratio el ratio de la compresion PPM
     * @param sobrescribir sobrescribir o no, si el fichero de salida existe
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

            time += cc.getTime();

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
     * Descomprime un fichero con la extension ".egg"
     * @detail La decompression empieza por leer la cabecera del fichero a descomprimir. Haberlo acabado,
     * se genera la jerarquia del fichero descomprimido, mientras esta generando la jerarquia esta pidiendo
     * a la capa de datos que replique la jerarquia en carpetas y ficheros (la extension de la raiz de la
     * jerarquia se halla al leer la cabecera). Despues, para cada elemento de esta jerarquia escribe su
     * contenido en los ficheros creados.
     * @param inputPath direccion del fichero a descomprimir
     * @param outputPath la direccion donde guardar el fichero descomprimido y su nombre sin extension.
     *                   La extension se halla durante el proceso de decodificacion de la cabecera
     *                   ejemplo: carpeta1/carpeta2/nuevo_nombre_fichero_descomprimido
     * @param sobrescribir sobrescribir o no, si el fichero de salida existe
     */
    public void decompress(String inputPath, String outputPath, boolean sobrescribir) throws Exception {

        System.out.println(outputPath);

        persistence_controller.clear();
        Hierarchy H = new Hierarchy(makeHierarchy(inputPath, outputPath, sobrescribir));
        int in = H.getRoot();
        ArrayList<Integer> q = H.getLeafs();
        int count = 0;
        time = 0;
        for (int id : H.getLeafs()) {
            ++count;
            byte[] aux = new byte[8];
            persistence_controller.readBytes(in, aux);

            int alg = (aux[0] & 0xFF) >>> 6;
            aux[0] &= 0x3F;
            long size = toLong(aux);

            persistence_controller.setReadLimit(in, size);

            Decompressor_Controller dc = new Decompressor_Controller(alg);
            dc.setDomain_controller(this);
            dc.startDecompression(in, id);
            time += dc.getTime();

            persistence_controller.rmReadLimit(in);
        }

        // Si solo se ha descomprimido un archivo, lo muestra.
        if (presentation_controller != null && count == 1) {
            presentation_controller.setMode(-1);
            presentation_controller.setOutPath(persistence_controller.getOutPath(0));
        }

        persistence_controller.closeReader(in);
    }

    /**
     * Visualiza los ficheros ".txt" y ".ppm"
     * @param path la direccion del fichero a visualizar
     */
    public void visualiceFile(String path) {
        try {
            persistence_controller.visualizeFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

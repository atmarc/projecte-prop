package persistencia;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;

/*!
 *  \brief     Controladora de la capa de persistencia, encargada de todos los procesos de entrada y salida.
 *  \details   La comunicacion para los operaciones de entrada y salida con el dominio va ligada siempre a un identificador asociado un fichero de sobre el que se desea leer/escribir. Todos los proceso de lectura y escritura se realizan mediante buffers. Tambien incluye metodos para obtener informacion de archivos y directorios (nombre, tamano, etc.)
 *  \author    Edgar Perez
 */
public class Persistence_Controller {

    private ArrayList<InputFile> readFiles;             ///< Lista de archivos/directorios para realizar operaciones de lectura (la posición en la lista es el identificador asociado al mismo).
    private ArrayList<OutputFile> writeFiles;           ///< Lista de archivos/directorios para realizar operaciones de escritura (la posición en la lista es el identificador asociado al mismo).
    private static Persistence_Controller persistence_controller = new Persistence_Controller(); ///< Referencia a la unica instancia de la Controladora de Persistencia

    /**
     * Creadora privada para evitar mas de una instanciacion. (Patron singleton)
     */
    private Persistence_Controller() {
        clear();
    }
    /**
     * Getter de la unica instancia de la Controladora de Persistencia.
     * @return Retorna la instancia de la Controladora de Persistencia.
     */
    public static Persistence_Controller getPersistence_controller() {
        return persistence_controller;
    }

    // File Creation

    /**
     * Anade al sistema un fichero (puede ser directorio) sobre el que se pueden realizar lecturas y se la asocia un identificador.
     * @param path Ruta del fichero sobre el que se quieren realizar lecturas.
     * @return Identificador asociado al fichero.
     */
    private int newInputFile(String path) {
        InputFile aux = new InputFile(path);
        readFiles.add(aux);
        return readFiles.indexOf(aux);
    }
    /**
     * Anade al sistema un fichero (puede ser directorio) sobre el que se pueden realizar escrituras y se la asocia un identificador.
     * @param path Ruta del fichero sobre el que se quieren realizar escrituras.
     * @param sobreescribir Indica si se debe tener en cuenta si el archivo existe o no.
     * @return Identificador asociado al fichero.
     */
    public int newOutputFile(String path, boolean sobreescribir) throws FileAlreadyExistsException {
        if (sobreescribir) deleteFile(path);
        OutputFile aux = new OutputFile(path, sobreescribir);
        writeFiles.add(aux);
        return writeFiles.indexOf(aux);
    }
    /**
     * Anade al sistema un fichero (puede ser directorio) sobre el que se pueden realizar escrituras, se la asocia un identificador y una ruta = ruta del fichero padre + nombre
     * @param name Nombre del nuevo fichero.
     * @param padre Identificador asociado al fichero padre del cual se extraera la ruta.
     * @return Identificador asociado al nuevo fichero.
     */
    public int newOutputFile(String name, int padre) throws Exception {
        return newOutputFile(writeFiles.get(padre).getAbsolutePath() + '/' + name, false);
    }

    // Dir Creation

    /**
     * Anade al sistema un nuevo directorio.
     * @param path Path del directorio que se desea anadir al sistema.
     * @return Identificador asociado al directorio anadido.
     */
    public int newDir(String path) throws Exception {
        OutputFile aux = new OutputFile(path, false);
        if (aux.exists()) throw new FileAlreadyExistsException("Este directorio ya existe.");
        if (!aux.mkdir()) throw new Exception("No se ha podido crear el directorio " + path);
        writeFiles.add(aux);
        return writeFiles.indexOf(aux);
    }
    /**
     * Anade al sistema un nuevo directorio.
     * @param padre Identificador del directorio padre del directorio que se desea anadir al sistema.
     * @param name Nombre del directorio que se desea anadir al sistema.
     * @return Identificador asociado al directorio anadido.
     */
    public int newDir(String name, int padre) throws Exception {
        return newDir(writeFiles.get(padre).getAbsolutePath() + '/' + name);
    }

    /**
     * Funcion que restaura al completo la persistencia. Elimina todas las referencias a archivos, tanto de lectura como de escritura.
     */
    public void clear() {
        readFiles = new ArrayList<>();
        writeFiles = new ArrayList<>();
    }

    // Read Limits

    /**
     * Establece un limite virtual de bytes a leer de un archivo. A partir de establecer un limite, todas las funciones de lectura trabajan en funcion de este mismo.
     * @param id Identificador del fichero de lectura.
     * @param num Cantidad que se desea establecer como limite de lectura.
     */
    public void setReadLimit(int id, long num) {
        readFiles.get(id).setNum(num);
    }
    /**
     * Elimina el limite virtual, volviendo asi todas las funciones de lectura a su estado natural de funcionamiento.
     * @param id Identificador del fichero de lectura.
     */
    public void rmReadLimit(int id) {
        readFiles.get(id).rmNum();
    }
    /**
     * Getter de la cantidad de bytes restantes por leer segun el limite establecido. En caso de no estar establecido o este haberse agotado, se retorna un -1.
     * @param id Identificador del fichero de lectura.
     * @return Retorna la cantidad de bytes por leer respecto al limite, o -1 si este no esta definido.
     */
    public long getReadLimit(int id) {
        if (!readFiles.get(id).isLimited()) return -1;
        return readFiles.get(id).getNum();
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
     * Funcion que calcula el nombre sin extension de un path concreto
     * @param path Path del que se quiere obtener el nombre.
     * @return Retorna el nombre del path sin la extension.
     */
    public String getNameNE(String path) {
        File file = new File(path);
        if (file.isDirectory()) return file.getName();

        String name = file.getName();
        String[] parts = name.split(".");
        for (String part : parts) {
            System.out.println(part);
        }
        return parts[0];
    }

    /**
     * Proporciona la extencion del archivo de lectura identificado por el identificador que recibe por parametro .
     * @param id Identificador del fichero de lectura.
     * @return Extension del fichero (si la tiene).
     */
    public String getExtension(int id) {
        // Lo he implementado yo puedes cambiar lo que no te parece ok
        String file = getName(id);
        assert file != null : "El nombre del fichero es nulo";
        int i = file.lastIndexOf('.');
        assert i > 0 : "Fichero sin extension";
        return file.substring(i + 1);
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
    /**
     * Getter de la cantidad de bytes escritos has el momento en un archivo referenciado por id.
     * @param id Identificador que referencia al archivo de escritura sobre el que hacemos el get.
     * @return Retorna la cantidad de bytes escritos en el fichero referenciado por id.
     */
    public long getWrittenBytes(int id) {
        return writeFiles.get(id).getNum();
    }


    // Lectura

    /**
     * Lee un byte del fichero origen.
     * @param id Identificador del fichero de escritura.
     * @return Entero que contiene el byte leido o -1 si no habia nada que leer.
     */
    public int readByte(int id) {
        try {
            InputFile in = readFiles.get(id);
            if (in.isLimited()) {
                if (in.subNum(1) > 0) return in.getBuffer().read();
                return -1;
            }
            return in.getBuffer().read();
        }
        catch (IOException e) {
            System.out.println("Error Lectura\n" + e.getMessage());
            return -1;
        }
    }
    /**
     * Lee N bytes del fichero origen en una cadena de bytes que se le pasa por parametro.
     * @param id Identificador del fichero de escritura.
     * @param word Cadena de bytes sobre la que se introducira la lectura.
     * @return Cantidad de bytes leida o -1 si no habia nada que leer.
     */
    public int readBytes(int id, byte[] word) {
        try {
            InputFile in = readFiles.get(id);
            if (in.isLimited()) {
                int n = in.subNum(word.length);
                if (n != word.length) {
                    if (n > 0) word = new byte[n];
                    else return -1;
                }
                return in.getBuffer().read(word);
            }
            return in.getBuffer().read(word);
        } catch (IOException e) {
            System.out.println("Error Lectura\n" + e.getMessage());
            return -1;
        }
    }
    /**
     * @param id Identificador del fichero de escritura.
     * Cierra el buffer de lectura.
     */
    public void closeReader(int id) throws IOException {
        readFiles.get(id).closeBuffer();
    }
    /**
     * Lee todos los bytes del fichero origen y los guarda en una cadena.
     * @param id Identificador del fichero de escritura.
     * @return Cadena de bytes con todos los bytes del fichero origen.
     */
    public byte[] readAllBytes(int id) throws Exception {
        InputFile in = readFiles.get(id);

        if (in.isLimited()) {
            byte[] res = new byte[(int) in.getNum()];
            in.setNum(0);
            if (in.getBuffer().read(res) < 0) throw new Exception("No hay mas que leer. Algo ha salido mal.");
            return res;
        }

        return Files.readAllBytes(Paths.get(in.getPath()));
    }

    // Escritura

    /**
     * Escribe un byte en fichero de salida.
     * @param id
     * @param B Byte que se desea escribir en el fichero de salida.
     */
    public void writeByte(int id, byte B) {
        try {
            OutputFile of = writeFiles.get(id);
            of.sumNum(1);
            of.getBuffer().write(B);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Escribe una cadena de bytes en el fichero de salida.
     * @param id Identificador del fichero de escritura.
     * @param word Cadena de bytes que se desea escribir en el fichero de salida.
     */
    public void writeBytes(int id, byte[] word) {
        try {
            OutputFile of = writeFiles.get(id);
            of.sumNum(word.length);
            of.getBuffer().write(word);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @param id Identificador del fichero de escritura.
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
            file.flushBuffer();
            // if (file.isActive()) file.closeBuffer();

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(position);
            raf.writeLong(content);
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Carpetas

    private void deleteFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) recursiveDelete(file.listFiles());
        file.delete();
    }
    private void recursiveDelete(File[] files) {
        if (files == null || files.length == 0) return;
        for (File file : files) {
            if (file.isDirectory()) recursiveDelete(file.listFiles());
            file.delete();
        }

    }
    /**
     * Funcion que dice si un fichero es directorio o no lo es.
     * @pre El entero que identifica el archivo ya esta introducido en el sistema (readFiles)
     * @param id Identificador del item a inspeccionar.
     * @return Retorna true en caso de ser un directorio o falso en caso contrario.
     */
    public boolean isFolder (int id) {
        return readFiles.get(id).isDirectory();
    }
    /**
     * Funcion que retorna una lista con todos los ficheros que contiene una carpeta.
     * @pre El entero que identifica el archivo ya esta introducido en el sistema (readFiles) y este es un directorio.
     * @post Todos sus ficheros han sido anadidos al sistema y estos estan asociados a un identificador.
     * @param id Identificador del directorio.
     * @return Retorna una lista que contiene los identificadores que referencian a los archivos que contenia el directorio referenciado por el parametro.
     */
    public ArrayList<Integer> getFilesFromFolder (int id) {
        File dir = readFiles.get(id);
        ArrayList<Integer> identifiers = new ArrayList<>();

        File[] dirFiles = dir.listFiles();

        assert dirFiles != null;
        for (File file : dirFiles) {
            identifiers.add(newInputFile(file.getAbsolutePath()));
        }

        return identifiers;
    }
    /**
     * Funcion que retorna la jerarquia completa de ficheros a partir del path pasado por parametro.
     * @param path Ruta absoluta del fichero del que se quiere obtener la jerarquia.
     * @return Matriz de 2xN donde N es el numero de identificadores/ficheros que hay dentro del path. Cada uno de los
     * identificadores se corresponde con las columnas de la matriz.
     *  - Fila 0: Contiene 1 si el fichero es un directorio, o 0 si no lo es.
     *  - Fila 1: Contiene el identificador del directorio padre del fichero. (El mismo en el caso de la raiz).
     */
    public int[][] makeHierarchy(String path) {

        assert readFiles.size() == 0;

        int root = newInputFile(path);
        if (!isFolder(root))
            return new int[][] {{0}, {0}};

        int father;
        ArrayList<Integer> hierarchy = new ArrayList<>(); hierarchy.add(0);
        ArrayDeque<Integer> folders = new ArrayDeque<>(); folders.add(0);

        while (!folders.isEmpty()) {
            father = folders.pollLast();
            ArrayList<Integer> files = getFilesFromFolder(father);
            for (Integer file : files) {
                if (isFolder(file)) folders.addFirst(file);
                hierarchy.add(father);
            }
        }

        int n = hierarchy.size();

        int[][] res = new int[2][n];
        for (int i = 0; i < n; i++) res[1][i] = hierarchy.get(i);
        for (int i = 0; i < n; i++) res[0][i] = isFolder(i)? 1:0;

        return res;
    }

}

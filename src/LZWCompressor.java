import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class LZWCompressor extends Compressor {
    private static final String extension = ".zero";
    private static final int BYTE_SIZE = 8;
    private HashMap<String, Integer> dictionary;
    private StringBuilder pattern;
    private int codewordRepresentation;   // la longitud en bits para escribir la codificación

    /**
     * Crea un objecto compressor con el diccionario básico.
     */
    public LZWCompressor() {
        this.initialize();
    }


    /**
     * @param file El fichero desde cual se tiene que calcular el nombre
     * @return     El nombre con la extension del fichero a comprimir
     */
    private String getCompressedName(File file) {
        String fileName = file.getPath();
        int pos = fileName.lastIndexOf('.');
        String compressedFileName;
        if (pos != -1) compressedFileName = fileName.substring(0, pos);
        else throw new IllegalArgumentException("Nombre de fichero incorrecto");
        return compressedFileName + extension;
    }

    /**
     * @param data cadena de caracteres
     * @return     una lista de enteros que representa la cadena {@code data} en forma comprimida
     */
    public ArrayList<Integer> compressString(String data) {
        ArrayList<Integer> outList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            String s = pattern.toString();
            if (!dictionary.containsKey(s + c)) {
                outList.add(dictionary.get(s));
                int temp = dictionary.size();
                dictionary.put(s + c, temp);
                pattern = new StringBuilder();
                pattern.append(c);
            }
            else pattern.append(c);
        }
        if (pattern.length() > 0 ) outList.add(dictionary.get(pattern.toString()));
        return outList;
    }

    public void compress(String path) {
        compress(new File(path));
    }

    /**
     * Comprime un fichero text que recibe como parametro y crea un nuevo fichero con el contenido comprimido
     * @param file el fichero a comprimir
     */
    public void compress(File file) {
        File compressedFile = new File(getCompressedName(file));
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(
                new FileInputStream(file.getPath()));
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                     new FileOutputStream(compressedFile.getPath()))
        ) {
            int readByte;
            int codeword;
            while ((readByte = bufferedInputStream.read()) != -1) {
                char c   = (char)readByte;
                String s = pattern.toString();
                if (!dictionary.containsKey(s + c)) {
                    codeword = dictionary.get(s);
                    byte[] codewordAsByte = toByteArray(codeword);
                    bufferedOutputStream.write(codewordAsByte);
                    int index = dictionary.size();
                    dictionary.put(s + c, index);
                    if (dictionary.size() >= (1 << codewordRepresentation)) codewordRepresentation += BYTE_SIZE;
                    pattern   = new StringBuilder();
                    pattern.append(c);
                }
                else pattern.append(c);
            }
            if (pattern.length() > 0) {
                codeword = dictionary.get(pattern.toString());
                if (codeword >= (1 << codewordRepresentation)) codewordRepresentation += BYTE_SIZE;
                byte[] codewordAsByte = toByteArray(codeword);
                bufferedOutputStream.write(codewordAsByte);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error de lectura/escritura");
            e.printStackTrace();
        };
    }

    /**
     * Convierte un int en un array con elementos de 8 bits
     * (Es igual a pasar un integer desde la base 10 a base 256)
     * @param codeword número a convertir
     * @return         un array con la representación de {@code codeword}
     */
    private byte[] toByteArray(int codeword) {
        byte[] codewordAsByte = new byte[codewordRepresentation/BYTE_SIZE];
        int pos = 0;
        for (int i = codewordRepresentation - BYTE_SIZE; i >= 0; i -= 8, ++pos) {
            codewordAsByte[pos] = (byte) ((codeword >>> i) & 0xFF);
        }
        return codewordAsByte;
    }

    /**
     * Inicializa el diccionario del compresor al diccionario básico
     */
    public void initialize() {
        codewordRepresentation = 8;
        dictionary = new HashMap<>();
        for (char i = 0; i < 256; ++i) dictionary.put(String.valueOf(i), (int) i);
        pattern = new StringBuilder();
    }

}
import java.io.*;
import java.util.ArrayList;

public class LZWDecompressor extends Decompressor {
    private static final int BYTE_SIZE = 8;
    private ArrayList<String> dictionary;
    private int codewordSize;    // la longitud en bits para escribir la codificación

    public LZWDecompressor() {
        inicializar();
    }

    public void inicializar() {
        dictionary = new ArrayList<>();
        for (char i = 0; i < 256; ++i) dictionary.add(String.valueOf(i));
        codewordSize =  16;
    }

    /**
     * @param file El fichero desde cual se tiene que calcular el nombre
     * @return     El nombre con la extension del fichero a comprimir
     */
    private String getPathName(File file) {
        String fileName = file.getPath();
        int pos = fileName.lastIndexOf('.');
        String compressedFileName;
        if (pos != -1) compressedFileName = fileName.substring(0, pos);
        else throw new IllegalArgumentException("Nombre de fichero incorrecto");
        return compressedFileName;
    }

    /**
     * Descomprime un fichero codificado con el algoritmo LZW
     * @param filePath dirección del fichero a descomprimir
     */
    public void decompress(String filePath) {
        decompress(new File(filePath));
    }

    public static boolean t = false;
    public static int nr = 0;
    public static int ix = 0;

    /**
     * Descomprime un fichero codificado con el algoritmo LZW
     * @param file fichero a descomprimir
     */
    public void decompress(File file) {
        File decompressedFile = new File(getPathName(file) + "_decompressed.txt");
        try (BufferedInputStream bufferedInputStream =
                     new BufferedInputStream(new FileInputStream(file.getPath()));
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                     new FileOutputStream(decompressedFile.getPath()))
        ) { // 65279
            int q = 0;
            boolean t = false;
            int nr = 0;
            int index = getNextIndex(bufferedInputStream);
            ix = index;
            String pattern = dictionary.get(index);
            for (int i = 0; i < pattern.length(); ++i) bufferedOutputStream.write((byte)pattern.charAt(i));
            while ((index = getNextIndex(bufferedInputStream)) != -1) {
                String out = "";
                if (index < dictionary.size()) {
                    out = dictionary.get(index);
                }
                else out = pattern + pattern.charAt(0);
                dictionary.add(pattern + out.charAt(0));
                for (int i = 0; i < out.length(); ++i) bufferedOutputStream.write((byte)out.charAt(i));
                pattern = out;
                if (dictionary.size() >= (1 << codewordSize)-1) codewordSize += BYTE_SIZE;
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("Fichero no encontrado");
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("Error de escritura/lectura");
            e.printStackTrace();
        }
    }

    private int getNextIndex(BufferedInputStream bufferedInputStream) throws IOException {
        int index = 0;
        byte[] aux = new byte[codewordSize/BYTE_SIZE];
        for (int i = 0; i < codewordSize; i += BYTE_SIZE) {
            int readByte = bufferedInputStream.read();
            aux[i/8] = (byte) readByte;
            if (readByte == -1) return -1;
            index = (index << BYTE_SIZE) | readByte;
        }
        if ((t || 65280 - ix < 10) && nr < 20) {
            for (byte b : aux) System.out.print(String.format("%02X ", b & 0xFF));
            System.out.println();
            nr++;
        }
        return index;
    }

    public String decompress_list(ArrayList<Integer> data) {
        StringBuilder outString = new StringBuilder();
        String pattern = dictionary.get(data.get(0));
        outString.append(dictionary.get(data.get(0)));

        for (int i = 1; i < data.size(); ++i) {
            int index = data.get(i);
            if (index < dictionary.size()) {
                String out = dictionary.get(index);
                outString.append(out);
                dictionary.add(pattern + out.charAt(0));
                pattern = out;
            }
            else {
                dictionary.add(pattern + pattern.charAt(0));
                outString.append(pattern).append(pattern.charAt(0));
            }
        }

        return outString.toString();
    }
}
package LZW;


import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class LZWDecompressor {
    private static final int BYTE_SIZE = 8;
    private ArrayList<String> dictionary;
    private StringBuilder pattern;
    private int codewordRepresentation;    // la longitud en bits para escribir la codificación

    LZWDecompressor() {
        initialize();
    }

    public void initialize() {
        codewordRepresentation = 8;
        dictionary = new ArrayList<>();
        for (char i = 0; i < 256; ++i) dictionary.add(String.valueOf(i));
        pattern = new StringBuilder();
    }

    static private ArrayList<String> basicDictionary() {
        ArrayList <String> dictionary = new ArrayList<>();
        for (int i = 0; i < 256; ++i) {
            dictionary.add(String.valueOf((char)i));
        }
        return dictionary;
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

    public String decompress(ArrayList<Integer> data) {
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
                outString.append(pattern + pattern.charAt(0));
            }
        }

        return outString.toString();
    }

    public void decompress_file(File file) {
        File decompressedFile = new File(getPathName(file) + "_decompressed.txt");
        try (BufferedInputStream bufferedInputStream =
                     new BufferedInputStream(new FileInputStream(file.getPath()));
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                     new FileOutputStream(decompressedFile.getPath()))
        ) {
            int index = getNextIndex(bufferedInputStream);
            String pattern = dictionary.get(index);
            for (int i = 0; i < pattern.length(); ++i) bufferedOutputStream.write((byte)pattern.charAt(i));
            if (dictionary.size() >= (1 << codewordRepresentation)) codewordRepresentation += BYTE_SIZE;
            while ((index = getNextIndex(bufferedInputStream)) != -1) {
                if (index < dictionary.size()) {
                    String out = dictionary.get(index);
                    for (int i = 0; i < out.length(); ++i)
                        bufferedOutputStream.write((byte) out.charAt(i));
                    dictionary.add(pattern + out.charAt(0));
                    pattern = out;
                }
                else {
                    dictionary.add(pattern + pattern.charAt(0));
                    String out = pattern + pattern.charAt(0);
                    for (int i = 0; i < out.length(); ++i) bufferedOutputStream.write((byte)pattern.charAt(i));
                }
                if (dictionary.size() >= (1 << codewordRepresentation)) codewordRepresentation += BYTE_SIZE;
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
        for (int i = 0; i < codewordRepresentation; i += BYTE_SIZE) {
            int readByte = bufferedInputStream.read();
            if (readByte == -1) return -1;
            index = (index << i) | readByte;
        }
        return index;
    }
}
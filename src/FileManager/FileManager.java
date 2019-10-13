package FileManager;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileManager {

    public static String readFile(String filePath) throws IOException {
        StringBuffer outString = new StringBuffer();
        try {
            FileReader reader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int readByte;
            while ((readByte = bufferedReader.read()) != -1) {
                outString.append((char) readByte);
            }
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return outString.toString();
    }

    public static ArrayList<Integer> readFileBytes(String filePath) throws IOException {
        ArrayList outString = new ArrayList();
        try {
            FileReader reader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int readByte;
            while ((readByte = bufferedReader.read()) != -1) {
                outString.add(readByte);
            }
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return outString;
    }

    public static void createFile(String data, String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(data);
        writer.close();

        // Mejoras https://howtodoinjava.com/java/io/java-write-to-file/
    }

    public static void createFile(ArrayList<Integer> data, String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        for (Integer i : data) writer.write(i);
        writer.close();
    }

/*
    // No es fiable
    public static String readFile_Byte(String filePath) throws IOException {
        StringBuilder outString = new StringBuilder();
        try {
            FileReader reader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int readByte;
            while ((readByte = bufferedReader.read()) != -1) {
                outString.append((byte) readByte%256);
                outString.append((byte) readByte/256);
            }
            bufferedReader.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return outString.toString();
    }
*/
    // Retorna un ArrayList amb els paths dels arxius de tipus type de la carpeta
    public static ArrayList<String> readFolder(String folderpath, String fileType) throws Exception {
        ArrayList <String> paths = new ArrayList<>();
        DirectoryStream<Path> p = Files.newDirectoryStream(Paths.get(folderpath),
                path -> path.toString().endsWith(fileType));
        for (Object o: p) {
            paths.add(o.toString());
        }
        return paths;
    }
}

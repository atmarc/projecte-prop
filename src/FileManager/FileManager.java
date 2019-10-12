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
        StringBuffer outString = new StringBuffer(); // Parece que String no tiene la funcion append y siempre cuando concatenas crea otro estring, por esto lo he cambiado por un StringBuffer. Pero despues retorna un string.
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

    public static byte[] readFile_Byte(String filePath) throws IOException {
        byte[] file = new byte[0];
        try {
            FileReader reader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(reader);

            ByteBuffer B = ByteBuffer.allocate(4);
            int readByte;
            while ((readByte = bufferedReader.read()) != -1) {
                B.putInt(readByte);
            }
            bufferedReader.close();

            file = B.array();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return file;
    }

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

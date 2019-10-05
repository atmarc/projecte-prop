import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileManager {

    public static String readFile(String filePath) throws IOException {
        StringBuffer outString = new StringBuffer(); // Parece que String no tiene la funcion append y siempre cuando concatenas crea otro estring, por esto lo he cambiado por un StringBuffer. Pero despues retorna un string.
        try {
            FileReader reader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(reader);

//            Posibles problemas con \n \r porque readLine devuelve el string hasta estos caracteres sin incluirlos
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                outString.append(line + '\n');
//            }

            int readByte;
            while ((readByte = bufferedReader.read()) != -1) {
                outString.append((char)readByte);
            }
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return outString.toString();

    public static String readFolder(String foldername) {
        
    
    }

}

// public void listFilesForFolder(final File folder) {
//     for (final File fileEntry : folder.listFiles()) {
//         if (fileEntry.isDirectory()) {
//             listFilesForFolder(fileEntry);
//         } else {
//             System.out.println(fileEntry.getName());
//         }
//     }
// }

//final File folder = new File("/home/you/Desktop");
//listFilesForFolder(folder);

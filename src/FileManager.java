import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileManager {

    public static String readFile(String filename) {
        try {
            FileReader reader = new FileReader(filename);
            BufferedReader br = new BufferedReader(reader);

            // read line by line
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        }
        catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    
    }

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
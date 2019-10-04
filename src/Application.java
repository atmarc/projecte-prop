import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Application {
    public static void main(String [] args) throws Exception
    {
        System.out.println("Hola");
        System.out.println("ktal");

        try (
            FileReader reader = new FileReader("filename.txt");
            BufferedReader br = new BufferedReader(reader)
        ) {

            // read line by line
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }


}
import javax.sound.midi.Soundbank;
import java.io.*;

public abstract class Compressor {
    private BufferedInputStream in;
    private BufferedOutputStream out;

    public void selectFiles(String inputPath, String outputPath) {
        try {
            in = new BufferedInputStream(new FileInputStream(inputPath));
            out = new BufferedOutputStream(new FileOutputStream(outputPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void inicializar(String filePath) throws FileNotFoundException {
        in = new BufferedInputStream(new FileInputStream(filePath));
    }

    public abstract void compress(String filePath);

    public byte readByte() {
        try {
            return (byte)in.read();
        }
        catch (IOException e) {
            System.out.println("Error Lectura\n" + e.getMessage());
            return -1;
        }
    }

    public byte[] readByte(int nrB) {
        try {
            byte[] word = new byte[nrB];
            in.read(word);
            return word;
        }
        catch (IOException e) {
            System.out.println("Error Lectura\n" + e.getMessage());
            return new byte[0];
        }
    }

    protected void writeByte(byte[] word) {
        try {
            out.write(word);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
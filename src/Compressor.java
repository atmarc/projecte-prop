import javax.sound.midi.Soundbank;
import java.io.*;

public abstract class Compressor {

    private File inputFile;
    private File outputFile;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private long time;



    // Auxiliar PreCompression Methods

    public void selectFiles(String inputPath, String outputPath) {
        try {

            inputFile = new File(inputPath);

            if (outputPath == null) outputPath = getCompressedName(inputFile);

            outputFile = new File(outputPath);

            in = new BufferedInputStream(new FileInputStream(inputFile));
            out = new BufferedOutputStream(new FileOutputStream(outputFile));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    abstract String getExtension();
    private String getCompressedName(File file) {
        String fileName = file.getPath();
        int pos = fileName.lastIndexOf('.');
        String compressedFileName;
        if (pos != -1) compressedFileName = fileName.substring(0, pos);
        else throw new IllegalArgumentException("Nombre de fichero incorrecto");
        return compressedFileName + getExtension();
    }

    // Compression

    public void StartCompression(String inputPath, String outputPath) {

        selectFiles(inputPath, outputPath);
        System.out.println("Compression IN PROGRESS");

        time = System.currentTimeMillis();
        compress();
        time = System.currentTimeMillis() - time;

        this.closeReader();
        this.closeWriter();

        System.out.println("Compression DONE");
        System.out.println("Time: " + this.getTime() + " ms");
        System.out.println("Compression ratio: " + this.getCompressionRatio());
    }
    protected abstract void compress();

    // Post-Compression Consultants

    public long getTime() {
        return time;
    }
    public long getOriginalSize() {
        return inputFile.length();
    }
    public long getCompressedSize() {
        return outputFile.length();
    }
    public double getCompressionRatio() {
        return (double)getCompressedSize()/(double)getOriginalSize();
    }

    // Lectura

    protected int readByte() {
        try {
            return in.read();
        }
        catch (IOException e) {
            System.out.println("Error Lectura\n" + e.getMessage());
            return -1;
        }
    }
    protected byte[] readNBytes(int n) {
        try {
            byte[] word = new byte[n];
            if (in.read(word) < 0) return new byte[0];
            return word;
        }
        catch (IOException e) {
            System.out.println("Error Lectura\n" + e.getMessage());
            return new byte[0];
        }
    }
    protected void closeReader() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected byte[] readAllBytes() {
        return new byte[0];
    }

    // Escritura

    protected void writeByte(byte B) {
        try {
            out.write(B);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void writeBytes(byte[] word) {
        try {
            out.write(word);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void closeWriter() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
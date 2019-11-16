import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Decompressor {

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
            System.out.println("Fichero no encontrado!");
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

    private String getCompressedName(String fileName) {
        int pos = fileName.lastIndexOf('.');
        String compressedFileName;
        if (pos != -1) compressedFileName = fileName.substring(0, pos);
        else throw new IllegalArgumentException("Nombre de fichero incorrecto");
        return compressedFileName + getExtension();
    }

    // Compression

    public void startDecompression(String inputPath, String outputPath) {

        selectFiles(inputPath, outputPath);
        System.out.println("Decompression IN PROGRESS");

        time = System.currentTimeMillis();
        decompress();
        time = System.currentTimeMillis() - time;

        closeReader();
        closeWriter();

        System.out.println("Decompression DONE");
        System.out.println("Time: " + this.getTime() + " ms");

    }

    public void startDecompression(String inputPath) {
        startDecompression(inputPath, getCompressedName(inputPath));
    }

    protected abstract void decompress();

    // Post-Compression Consultants

    public long getTime() {
        return time;
    }

    // Lectura

    protected int readByte() {
        try {
            return in.read();
        } catch (IOException e) {
            System.out.println("Error Lectura\n" + e.getMessage());
            return -1;
        }
    }

    protected int readNBytes(byte[] word) {
        try {
            return in.read(word);
        } catch (IOException e) {
            System.out.println("Error Lectura\n" + e.getMessage());
            return -1;
        }
    }

    protected void closeReader() {
        try {
            if (in != null) in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected byte[] readAllBytes() {
        byte[] b = new byte[0];
        try {
            b = Files.readAllBytes(Paths.get(inputFile.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
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
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
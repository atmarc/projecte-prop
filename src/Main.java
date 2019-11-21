import java.io.*;

public class Main {

    // los 2 bits de mayor peso representan el algoritmo y los restos
    //  la longitud del fichero
    static long getHeader(String alg, long fileSize) {
        long META = 0;
        if (alg.equals("LZSS")) META = 0;
        if (alg.equals("LZ78")) META = (1L << 62);
        if (alg.equals("LZW")) META = (2L << 61);
        if (alg.equals("JPEG")) META = (3L << 61);
        if (fileSize > (1L << 62) - 1) {
            System.out.println("File size out of bounds");
            return -1;
        }
        else return  META | fileSize;
    }


    public static void main(String[] args) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("file.txt"));
        byte[] space = new byte[8];
        String message = "Hola";
        out.write(space);
        out.write(message.getBytes());
        out.close();

        BufferedInputStream in = new BufferedInputStream(new FileInputStream("file.txt"));
        byte[] content = in.readAllBytes();
        String S = new String(content);
        System.out.println("Contenido del fichero despues de dejar espacio y escribir el contenido:");
        System.out.println(S);
        in.close();

        RandomAccessFile raf = new RandomAccessFile("file.txt", "rw");
        raf.seek(0);
        raf.writeLong(getHeader("LZW", 0xFFFFFFFL));
        raf.close();

        in = new BufferedInputStream(new FileInputStream("file.txt"));
        content = in.readAllBytes();
        S = new String(content);
        System.out.println("Contenido despues de actualizar el header");
        System.out.println(S);
        in.close();
        // 2 ^ 62 bytes = 4.611.686 TeraBytes
    }
}

// Que se abre el finder con el fichero comprimido

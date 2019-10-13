import FileManager.FileManager;
import JPEG.JpegAlgorithm;
import LZ78.LZ78_Compressor;
import LZSS.LZSS_Compressor;
import LZW.*;

import java.util.ArrayList;

public class Application {
    public static void main(String [] args) throws Exception
    {
        System.out.println("Testing output:\n");
        /*
        ArrayList<String> paths = FileManager.readFolder("testing_files", ".ppm");
        JpegAlgorithm.compress(FileManager.readFile(paths.get(0)));
        */

        /*
        ArrayList<String> paths = FileManager.readFolder("testing_files", ".txt");
        LZ78_Compressor LZ78 = new LZ78_Compressor();
        LZ78.compress(FileManager.readFile(paths.get(0)));
        LZ78.compress(FileManager.readFile_Byte(paths.get(0)));
        */


        /*
        String data = FileManager.readFile("testing_files/filename.txt");
        ArrayList<Integer> compressedData = LZWCompressor.compress(data);
        FileManager.createFile(compressedData, "testing_files/output.zero");
        compressedData = FileManager.readFileBytes("testing_files/output.zero");
        String decompressedData = LZWDecompressor.decompress(compressedData);
        FileManager.createFile(decompressedData, "testing_files/decompress.txt");
        */

        /*
        String path = FileManager.readFile("testing_files/petit.txt");
        LZSS_Compressor LZSS = new LZSS_Compressor();
        LZSS.Compress(path);
         */

    }

}
import FileManager.FileManager;
import JPEG.JpegAlgorithm;
import LZ78.LZ78_Compressor;
import LZSS.LZSS_Compressor;
import LZW.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Application {
    public static void main(String [] args) throws Exception {
        System.out.println("Testing output:\n");

        if (true) { // JPEG
            // ArrayList<String> paths = FileManager.readFolder("testing_files", ".ppm");
            byte file [] = Files.readAllBytes(Paths.get("testing_files/boxes_1.ppm"));
            JpegAlgorithm.compress(file);
        }

        if (false) { // LZ78
            //ArrayList<String> paths = FileManager.readFolder("testing_files", ".txt");
            LZ78_Compressor LZ78 = new LZ78_Compressor();
            //LZ78.compress(FileManager.readFile(paths.get(0)));
            LZ78.compress(FileManager.readFile("./testing_files/petit.txt"));
            //LZ78.compress(FileManager.readFile_Byte(paths.get(0)));
        }


        if (false) { // LZW
            String data = FileManager.readFile("testing_files/filename.txt");
            ArrayList<Integer> compressedData = LZWCompressor.compress(data);
            FileManager.createFile(compressedData, "testing_files/output.zero");
            //compressedData = FileManager.readFileBytes("testing_files/output.zero");
            String decompressedData = LZWDecompressor.decompress(compressedData);
            FileManager.createFile(decompressedData, "testing_files/decompress.txt");
        }

        if (false) { // LZSS
            String path = FileManager.readFile("testing_files/filename.txt");
            LZSS_Compressor LZSS = new LZSS_Compressor();
            LZSS.Compress(path);
        }

    }

}
import FileManager.FileManager;
import JPEG.JpegAlgorithm;
import LZ78.LZ78_Compressor;
import LZ78.LZ78_Decompressor;
import LZSS.LZSS_Compressor;
import LZW.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Application {
    public static void main(String [] args) throws Exception {
        System.out.println("Testing output:\n");

        if (false) { // JPEG
            // ArrayList<String> paths = FileManager.readFolder("testing_files", ".ppm");
            byte file [] = Files.readAllBytes(Paths.get("testing_files/boxes_1.ppm"));
            JpegAlgorithm.compress(file);
        }

        if (true) { // LZ78

            String input_comp = "./testing_files/big.txt";
            String output_comp = "./testing_files/LZ78_testing/comp.txt";
/*
            LZ78_Compressor LZ78 = new LZ78_Compressor();
            LZ78.TXcompressor(input_comp, output_comp);
*/
            String input_decomp = output_comp;
            String output_decomp = "./testing_files/LZ78_testing/decomp.txt";

            LZ78_Decompressor decompressor = new LZ78_Decompressor();
            decompressor.TXdecompressor(input_decomp, output_decomp);
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
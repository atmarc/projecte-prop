import FileManager.FileManager;
import LZSS.LZSS_Compressor;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class Application {
    public static void main(String[] args) throws Exception {

        // TODO: Mirar com evitar els comentaris

        String inputPath = "";
        String outputPath = "";



        if (false) { // JPEG

            // ArrayList<String> paths = FileManager.readFolder("testing_files", ".ppm");
            JPEGCompressor jpegCompressor = new JPEGCompressor();
            System.out.println("Start compression...");
            //jpegCompressor.compress("testing_files/ppm_images/west_2.ppm");
            //jpegCompressor.compress("testing_files/image.ppm");
            jpegCompressor.compress("testing_files/ppm_images/france-wallpaper.ppm");
            System.out.println("Finish compressing!");

            System.out.println("Start decompression...");
            JPEGDecompressor jpegDecompressor = new JPEGDecompressor();
            jpegDecompressor.decompress("testing_files/image.comp");
            System.out.println("Finish decompressing!");
            // ArrayList<String> paths = FileManager.readFolder("testing_files", ".ppm");
            //System.out.println(compimit);
            //FileManager.createFile(compimit, "testing_files/image.comp");
            //file = Files.readAllBytes(Paths.get("testing_files/image.comp"));
            //JpegAlgorithm.decompress(file);
        }

        if (true) { // LZ78

            String input_comp = "./testing_files/1M.txt";
            String output_comp = "./testing_files/comp.txt";

            Compressor compressor = new LZ78Compressor();
            compressor.StartCompression(input_comp, output_comp);

            System.out.println("Time: " + compressor.getTime());
            System.out.println("Ratio: " + compressor.getCompressionRatio());

            String input_decomp = "./testing_files/1M.lz78";
            String output_decomp = "./testing_files/decomp.txt";

            LZ78Decompressor decompressor = new LZ78Decompressor();
            decompressor.decompressor(input_decomp, output_decomp);
        }


        if (false) { // LZW
            Compressor compressor = new LZWCompressor();
            compressor.compress("testing_files/lzw/ansi.txt");
            Decompressor decompressor = new LZWDecompressor();
            decompressor.decompress("testing_files/lzw/ansi.zero");
        }

        if (false) { // LZSS
            String path = FileManager.readFile("testing_files/filename.txt");
            LZSS_Compressor LZSS = new LZSS_Compressor();
            // LZSS.Compress(path);
        }

        /*
        Block block = new Block(8,8, "Y");
        block.setValue(0,0,-76);
        block.setValue(0,1,-73);
        block.setValue(0,2,-67);
        block.setValue(0,3,-62);
        block.setValue(0,4,-58);
        block.setValue(0,5,-67);
        block.setValue(0,6,-64);
        block.setValue(0,7,-55);

        block.setValue(1,0,-65);
        block.setValue(1,1,-69);
        block.setValue(1,2,-73);
        block.setValue(1,3,-38);
        block.setValue(1,4,-19);
        block.setValue(1,5,-43);
        block.setValue(1,6,-59);
        block.setValue(1,7,-56);

        block.setValue(2,0,-66);
        block.setValue(2,1,-69);
        block.setValue(2,2,-60);
        block.setValue(2,3,-15);
        block.setValue(2,4,16);
        block.setValue(2,5,-24);
        block.setValue(2,6,-62);
        block.setValue(2,7,-55);

        block.setValue(3,0,-65);
        block.setValue(3,1,-70);
        block.setValue(3,2,-57);
        block.setValue(3,3,-6);
        block.setValue(3,4,26);
        block.setValue(3,5,-22);
        block.setValue(3,6,-58);
        block.setValue(3,7,-59);

        block.setValue(4,0,-61);
        block.setValue(4,1,-67);
        block.setValue(4,2,-60);
        block.setValue(4,3,-24);
        block.setValue(4,4,-2);
        block.setValue(4,5,-40);
        block.setValue(4,6,-60);
        block.setValue(4,7,-58);

        block.setValue(5,0,-49);
        block.setValue(5,1,-63);
        block.setValue(5,2,-68);
        block.setValue(5,3,-58);
        block.setValue(5,4,-51);
        block.setValue(5,5,-60);
        block.setValue(5,6,-70);
        block.setValue(5,7,-53);

        block.setValue(6,0,-43);
        block.setValue(6,1,-57);
        block.setValue(6,2,-64);
        block.setValue(6,3,-69);
        block.setValue(6,4,-73);
        block.setValue(6,5,-67);
        block.setValue(6,6,-63);
        block.setValue(6,7,-45);

        block.setValue(7,0,-41);
        block.setValue(7,1,-49);
        block.setValue(7,2,-59);
        block.setValue(7,3,-60);
        block.setValue(7,4,-63);
        block.setValue(7,5,-52);
        block.setValue(7,6,-50);
        block.setValue(7,7,-34);

        block.DCT();

        block.printBlockDCT();
        block.inverseQuantizationY();
        block.printBlockDCT();
        block.inverseDCT();
        block.printBlockDCT();

        //System.out.println(block.zigzag());

        FileManager.createFile(block.zigzag(), "testing_files/provacreateFile.txt");
        */

    }
}
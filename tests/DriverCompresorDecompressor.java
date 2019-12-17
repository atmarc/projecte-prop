import dominio.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DriverCompresorDecompressor {

	private static boolean diffFiles(String name1, String name2) throws IOException {
		File f1 = new File(name1);
		File f2 = new File(name2);
		if (f1.length() != f2.length()) return false;
		BufferedInputStream in1 = new BufferedInputStream(new FileInputStream(f1.getPath()));
		BufferedInputStream in2 = new BufferedInputStream(new FileInputStream(f2.getPath()));
		int b1, b2;
		while ((b1 = in1.read()) != -1) {
			b2 = in2.read();
			if (b1 != b2) return false;
		}
		in1.close();
		in2.close();
		return true;
	}

	public static void main(String[] args) throws IOException {
		ArrayList<String> files = new ArrayList<>(Arrays.asList(
				"src/persistencia/testing_files/txt/emoji",
				"src/persistencia/testing_files/txt/japones",
				"src/persistencia/testing_files/txt/good_one",
				"src/persistencia/testing_files/txt/1M",
				"src/persistencia/testing_files/txt/big",
				"src/persistencia/testing_files/txt/quicksort"
		));
		Compressor_Controller compressor = new Compressor_Controller(2);
		Decompressor_Controller decompressor = new Decompressor_Controller(2);
		for (String file : files) {
			System.out.println("File: " + file);
			//compressor.startCompression(file+".txt", null);
			System.out.println("----------");
			//decompressor.startDecompression(file+".lzw", null);
			System.out.println("Verdict: " + (diffFiles(file+"_decompressed.txt", file+".txt")
					? "\u001B[32m" + "OK! Files are equal." + "\u001B[0m"
					: "\u001B[31m" + "Wrong!!!" + "\u001B[0m"));
			System.out.println();
		}
	}
}

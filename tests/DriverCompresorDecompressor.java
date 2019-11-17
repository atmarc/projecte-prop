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
				"testing_files/lzw/ansi",
				"testing_files/lzw/catala",
				"testing_files/lzw/emoji",
				"testing_files/lzw/español",
				"testing_files/lzw/japones",
				"testing_files/lzw/ruso",
				"testing_files/newD/good_one",
				"testing_files/newD/another_good_one",
				"testing_files/1M",
				"testing_files/big",
				"testing_files/quicksort"
		));
		Compressor compressor = new Compressor_LZW();
		Decompressor decompressor = new Decompressor_LZW();
		for (String file : files) {
			System.out.println("File: " + file);
			compressor.startCompression(file+".txt");
			System.out.println("----------");
			decompressor.startDecompression(file+".zero");
			System.out.println("Verdict: " + (diffFiles(file+"_decompressed.txt", file+".txt")
					? "\u001B[32m" + "OK! Files are equal." + "\u001B[0m"
					: "\u001B[31m" + "Wrong!!!" + "\u001B[0m"));
			System.out.println();
		}
	}
}

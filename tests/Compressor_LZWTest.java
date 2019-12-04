import dominio.Compressor_Controller;
import dominio.Compressor_LZW;
import dominio.Decompressor_Controller;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class Compressor_LZWTest {

	@Test
	public void getExtension() {
		Compressor_LZW compressor_lzw = new Compressor_LZW();
		String expected = ".lzw";
		String actual = compressor_lzw.getExtension();
		assertEquals(actual, expected);
	}

	@Test
	public void compress1() throws IOException {
		Compressor_Controller cc = new Compressor_Controller(2);
		//cc.startCompression("src/persistencia/testing_files/txt/good_one.txt",
		//		"src/persistencia/testing_files/txt/");
		Decompressor_Controller dc = new Decompressor_Controller("lzw");
		//dc.startDecompression("src/persistencia/testing_files/txt/good_one.lzw",
		//		"src/persistencia/testing_files/txt/");
		assertTrue(diffFiles("src/persistencia/testing_files/txt/good_one_decompressed.txt",
				"src/persistencia/testing_files/txt/good_one.txt"));
	}

	@Test
	public void compress2() throws IOException {
		Compressor_Controller cc = new Compressor_Controller(2);
		//cc.startCompression("src/persistencia/testing_files/txt/big.txt",
		//		"src/persistencia/testing_files/txt/");
		Decompressor_Controller dc = new Decompressor_Controller("lzw");
		//dc.startDecompression("src/persistencia/testing_files/txt/big.lzw",
		//		"src/persistencia/testing_files/txt/");
		assertTrue(diffFiles("src/persistencia/testing_files/txt/big_decompressed.txt",
				"src/persistencia/testing_files/txt/big.txt"));
	}

	@Test
	public void compress3() throws IOException {
		Compressor_Controller cc = new Compressor_Controller(2);
		//cc.startCompression("src/persistencia/testing_files/txt/emoji.txt",
		//		"src/persistencia/testing_files/txt/");
		Decompressor_Controller dc = new Decompressor_Controller("lzw");
		//dc.startDecompression("src/persistencia/testing_files/txt/emoji.lzw",
		//		"src/persistencia/testing_files/txt/");
		boolean r1 = diffFiles("src/persistencia/testing_files/txt/emoji_decompressed.txt",
				"src/persistencia/testing_files/txt/emoji.txt");

		//cc.startCompression("src/persistencia/testing_files/txt/japones.txt",
		//		"src/persistencia/testing_files/txt/");
		//dc.startDecompression("src/persistencia/testing_files/txt/japones.lzw",
		//		"src/persistencia/testing_files/txt/");
		boolean r2 = diffFiles("src/persistencia/testing_files/txt/japones_decompressed.txt",
				"src/persistencia/testing_files/txt/japones.txt");
		assertTrue(r1 && r2);
	}

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
}
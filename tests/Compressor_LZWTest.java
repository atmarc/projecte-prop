import dominio.*;
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
		cc.startCompression("testing_files/newD/good_one.txt");
		Decompressor_Controller dc = new Decompressor_Controller("lzw");
		dc.startDecompression("testing_files/newD/good_one.lzw");
		assertTrue(diffFiles("testing_files/newD/good_one_decompressed.txt", "testing_files/newD/good_one.txt"));
	}

	@Test
	public void compress2() throws IOException {
		Compressor_Controller cc = new Compressor_Controller(2);
		cc.startCompression("testing_files/big.txt");
		Decompressor_Controller dc = new Decompressor_Controller("lzw");
		dc.startDecompression("testing_files/big.lzw");
		assertTrue(diffFiles("testing_files/big_decompressed.txt", "testing_files/big.txt"));
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

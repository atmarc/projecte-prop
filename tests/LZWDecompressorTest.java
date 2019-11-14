import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class LZWDecompressorTest {

	@Test
	public void decompress() {
		LZWDecompressor decompressor = new LZWDecompressor();
		long inicio = System.currentTimeMillis();
		decompressor.decompress(new File("testing_files/1M.zero"));
		long fin = System.currentTimeMillis();
		System.out.println("Duración: " + (fin-inicio) + " ms");

		long comprSize = (new File("testing_files/1M.zero")).length();
		long decomSize = (new File("testing_files/1M_decompressed.txt")).length();
		System.out.println("Space: " + comprSize + " bytes");
		System.out.println("Space: " + decomSize + " bytes");
		System.out.printf("Ratio de compression: %.2f", ((float)comprSize/decomSize));
	}

	@Test
	public void testDecompress() {
	}

	@Test
	public void decompress_list() {
	}
}
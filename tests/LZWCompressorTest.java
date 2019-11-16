import org.junit.Test;

import java.io.File;

public class LZWCompressorTest {

	@Test
	public void compress() {
	}

	@Test
	public void compress_file() {
		File file = new File("testing_files/1M.txt");
		LZWCompressor compressor = new LZWCompressor();
		long inicio = System.currentTimeMillis();
		compressor.compress(file);
		long fin = System.currentTimeMillis();
		System.out.println("Duración: " + (fin-inicio) + " ms");
	}

	@Test
	public void initialize() {
	}
}
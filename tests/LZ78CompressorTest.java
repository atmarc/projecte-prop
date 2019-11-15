import org.junit.Test;

import java.io.File;

public class LZ78CompressorTest {

	@Test
	public void compress() {
	}

	@Test
	public void compress_file() {
		File file = new File("testing_files/1M.txt");
		LZ78Compressor compressor = new LZ78Compressor();
		long inicio = System.currentTimeMillis();
		//compressor.compress(file);
		long fin = System.currentTimeMillis();
		System.out.println("Duración: " + (fin-inicio) + " ms");
	}

	@Test
	public void initialize() {
	}
}
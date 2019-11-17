import org.junit.Test;

import java.io.File;

public class LZWCompressorTest {

	@Test
	public void compress() {
		Compressor compressor = new Compressor_LZW();
		compressor.startCompression("testing_files/quicksort.txt");
	}

	@Test
	public void compress_file() {
		File file = new File("testing_files/1M.txt");
		Compressor_LZW compressor = new Compressor_LZW();
		long inicio = System.currentTimeMillis();
//		compressor.compress(file);
		long fin = System.currentTimeMillis();
		System.out.println("Duración: " + (fin-inicio) + " ms");
	}

	@Test
	public void inicializar() {
		Compressor_LZW compressor = new Compressor_LZW();
		// compressor.inicializar();
	}
}
import org.junit.Test;

import java.io.File;

public class LZWCompressorTest {

	@Test
	public void compress() {
	}

	@Test
	public void compress_file() {
		File file = new File("testing_files/big.txt");
		LZWCompressor compressor = new LZWCompressor();
		compressor.compress(file);
	}

	@Test
	public void initialize() {
	}
}
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class LZWCompressorTest {

	@Test
	public void compress() {
	}

	@Test
	public void compress_file() {
		File file = new File("testing_files/filename.txt");
		LZWCompressor compressor = new LZWCompressor();
		compressor.compress(file);
	}

	@Test
	public void initialize() {
	}
}
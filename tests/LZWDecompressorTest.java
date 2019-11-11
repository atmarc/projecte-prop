import org.junit.Test;

import java.io.File;

public class LZWDecompressorTest {

	@Test
	public void initialize() {
	}

	@Test
	public void decompress() {
	}

	@Test
	public void decompress_file() {
		LZWDecompressor decompressor = new LZWDecompressor();
		decompressor.decompress(new File("testing_files/filename.zero"));
	}
}
import org.junit.Test;

public class DecompressorTest {

    @Test
    public void decompress() {
        Decompressor decompressor = new LZWDecompressor();
        decompressor.decompress("testing_files/filename.zero");
    }
}
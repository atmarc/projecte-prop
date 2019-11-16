import org.junit.Test;

class JPEGCompressorTest {

    @Test
    void compress() {
        Compressor jpegCompressor = new JPEGCompressor();
        jpegCompressor.startCompression("/testing_files/image.ppm", null);
    }

}
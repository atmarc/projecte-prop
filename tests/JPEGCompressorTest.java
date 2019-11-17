import org.junit.Test;

class JPEGCompressorTest {

    @Test
    void compress() {
        Compressor jpegCompressor = new Compressor_JPEG();
        jpegCompressor.startCompression("/testing_files/image.ppm", null);
    }

}
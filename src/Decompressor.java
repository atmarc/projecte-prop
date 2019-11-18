import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Decompressor {


    public void setController(Decompressor_Controller controller) {
        this.controller = controller;
    }

    protected Decompressor_Controller controller;

    protected abstract String getExtension();
    protected abstract void decompress();

}
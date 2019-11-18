import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Compressor {

    public void setController(Compressor_Controller controller) {
        this.controller = controller;
    }

    protected Compressor_Controller controller;

    protected abstract String getExtension();
    protected abstract void compress();

}
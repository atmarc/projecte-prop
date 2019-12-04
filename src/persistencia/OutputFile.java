package persistencia;

import java.io.*;

public class OutputFile extends File {

    private BufferedOutputStream out;
    boolean active;

    public boolean isActive() {
        return active;
    }

    public OutputFile(String pathname) {
        super(pathname);
        active = false;
    }

    BufferedOutputStream getBuffer() throws FileNotFoundException {
        if (!active) {
            out = new BufferedOutputStream(new FileOutputStream(this));
            active = true;
        }
        return out;
    }

    void closeBuffer() throws IOException {
        if (active) {
            out.close();
            active = false;
        }
    }

}

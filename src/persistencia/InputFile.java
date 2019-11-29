package persistencia;

import java.io.*;

public class InputFile extends File {

    private BufferedInputStream in;
    boolean active;

    public boolean isActive() {
        return active;
    }

    public InputFile(String pathname) {
        super(pathname);
        active = false;
    }

    BufferedInputStream getBuffer() throws FileNotFoundException {
        if (!active) {
            in = new BufferedInputStream(new FileInputStream(this));
            active = true;
        }
        return in;
    }

    void closeBuffer() throws IOException {
        if (active) {
            in.close();
            active = false;
        }
    }
}

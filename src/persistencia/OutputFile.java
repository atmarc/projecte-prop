package persistencia;

import java.io.*;

public class OutputFile extends File {

    private BufferedOutputStream out;
    boolean active;
    long num;            ///< Contador de cuantos btes se han escrito

    public boolean isActive() {
        return active;
    }

    public OutputFile(String pathname) {
        super(pathname);
        active = false;
        num = 0;
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

    public void sumNum(int i) {
        num += i;
    }

    public long getNum() { return num; }
    public void setNum(long num) { this.num = num; }
}

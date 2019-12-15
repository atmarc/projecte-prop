package persistencia;

import java.io.*;

public class InputFile extends File {

    private BufferedInputStream in;
    boolean active;
    long num;            ///< Contador de cuantos bytes quedan por leer

    public long getNum() { return num; }
    public void setNum(long num) { this.num = num; }


    public int subNum(int n) {
        if (num >= n) {
            num = num - n;
            return n;
        }
        if (num > 0) return subNum((int) num);
        num = -1;
        return -1;
    }

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

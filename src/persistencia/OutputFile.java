package persistencia;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

public class OutputFile extends File {

    private BufferedOutputStream out;
    boolean active;
    long num;            ///< Contador de cuantos btes se han escrito

    public boolean isActive() {
        return active;
    }

    public OutputFile(String pathname) throws FileAlreadyExistsException {
        super(pathname);
        if (this.isFile()) throw new FileAlreadyExistsException("Este fichero de salida ya existe.");
        active = false;
        num = 0;
    }

    BufferedOutputStream getBuffer() throws FileNotFoundException {
        if (!active) {
            out = new BufferedOutputStream(new FileOutputStream(this, true));
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

	public void flushBuffer() throws IOException {
        out.flush();
	}
}

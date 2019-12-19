package persistencia;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

/*!
 *  \brief     Extension de la clase File de uso exclusivo para ficheros de escritura.
 *  \details   Ademas de contar con todas las funcionalidades propias de un File, cuenta con un buffer propio de escritura y un contador de bytes escritos en cada fichero.
 *  \author    Edgar Perez
 */
public class OutputFile extends File {

    private BufferedOutputStream out;        ///< Buffer de escritura del archivo asociado.
    private boolean active = false;         ///< Control sobre el buffer del escritura. Si esta activo o no.
    private boolean append = false;          ///< Control sobre la primera vez que se escribe
    private long num = 0;                   ///< Contador de cuantos bytes se han escrito


    // Constructora

    /**
     * Constructora modificada que crea un File con el path pasado por parametro.
     * @pre El fichero de salida no existe.
     * @post Se ha creado un fichero nuevo en el path pasado por parametro.
     * @param pathname Path del item asociado al File.
     */
    public OutputFile(String pathname, boolean sobreescribir) throws FileAlreadyExistsException {
        super(pathname);
        if (!sobreescribir && (this.isFile())) throw new FileAlreadyExistsException("Este fichero de salida ya existe.");
    }


    // Contador Num

    /**
     * Incrementa el contador de bytes escritos.
     * @param i Cantidad que se incrementa el contador.
     */
    public void sumNum(int i) {
        num += i;
    }

    /**
     * Getter del contador de cuantos bytes se han escrito.
     * @return Cantidad de bytes escritos hasta el momento.
     */
    public long getNum() { return num; }


    // Buffer de escritura

    /**
     * Retorna el buffer de escritura. Si este no esta activo, lo activa. La primera vez que se ejecuta, estara en modo no append. A partir de esta, se pondra en modo append.
     * @return Retorna el buffer de escritura.
     */
    public BufferedOutputStream getBuffer() throws FileNotFoundException {
        if (!active) {
            out = new BufferedOutputStream(new FileOutputStream(this, append));
            append = true;
            active = true;
        }
        return out;
    }

    /**
     * Funcion que consulta si el buffer de escritura esta activo o no.
     * @return Retorna un booleano que indica si el buffer de escritura esta abierto o no.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Cierra el buffer se escritura.
     */
    public void closeBuffer() throws IOException {
        if (active) {
            out.close();
            active = false;
        }
    }

    /**
     * Realiza un flush del buffer de escritura.
     */
    public void flushBuffer() throws IOException {
        out.flush();
	}
}

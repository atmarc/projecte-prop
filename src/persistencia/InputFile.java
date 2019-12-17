package persistencia;

import java.io.*;
/*!
 *  \brief     Extension de la clase File de uso exclusivo para ficheros de lectura.
 *  \details   Ademas de contar con todas las funcionalidades propias de un File, cuenta con un buffer propio de lectura y un sistema de limites de lectura en bytes.
 *  \author    Edgar Perez
 */
public class InputFile extends File {

    private BufferedInputStream in;     ///< Buffer de lectura del archivo asociado.
    private boolean active;             ///< Control sobre el buffer del lectura. Si esta activo o no.
    private boolean limited = false;    ///< Control de si las lecturas son con limite de bytes o no
    private long num;                   ///< Limite de bytes que se pueden leer.

    // Constructora

    /**
     * Constructora modificada que crea un File con el path pasado por parametro.
     * @param pathname Path del item asociado al File.
     */
    InputFile(String pathname) {
        super(pathname);
        active = false;
    }

    // Numero limite

    /**
     * Getter del limite actual (cantidad restante) de bytes que se pueden leer.
     * @return Numero de bytes que se pueden leer segun el limite establecido.
     */
    long getNum() { return num; }
    /**
     * Setter del limite de bytes que se pueden leer.
     * @param num Cantidad de bytes que se desea establecer como limite.
     */
    void setNum(long num) {
        limited = true;
        this.num = num;
    }
    /**
     * Desactiva el numero limite de bytes de lectura.
     */
    void rmNum() {
        limited = false;
        num = -1;
    }
    /**
     * Funcion que decrementa el limite de bytes a leer. Devuelve la cantidad restada. Si este ha llegado a 0, se desactiva y se establece a -1.
     * @param n Cantidad de bytes a decrementar.
     * @return Cantidad de bytes decrementada = min(n, num).
     */
    int subNum(int n) {
        if (num >= n) {
            num = num - n;
            return n;
        }
        if (num > 0) return subNum((int) num);
        limited = false;
        num = -1;
        return -1;
    }
    /**
     * Funcion para saber si el limite de bytes de lectura esta activo.
     * @return Retorna un booleano que indica si el limite esta activo o no.
     */
    public boolean isLimited() {
        return limited;
    }

    // Buffer de lectura

    /**
     * Funcion que consulta si el buffer de lectura esta activo o no.
     * @return Retorna un booleano que indica si el buffer de lectura esta abierto o no.
     */
    public boolean isActive() {
        return active;
    }
    /**
     * Retorna el buffer de lectura. Si este no esta activo, lo activa.
     * @return Retorna el buffer de lectura.
     */
    BufferedInputStream getBuffer() throws FileNotFoundException {
        if (!active) {
            in = new BufferedInputStream(new FileInputStream(this));
            active = true;
        }
        return in;
    }
    /**
     * Cierra el buffer de lectura.
     * @throws IOException
     */
    void closeBuffer() throws IOException {
        if (active) {
            in.close();
            active = false;
        }
    }
}

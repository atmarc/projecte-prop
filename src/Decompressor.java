/**
 * Clase con metodos abstractos para implementar un descompresor. Contiene una variable que hace referencia a su controlador para poder comunicarse con otras capas.
 */
public abstract class Decompressor {

    /**
     * Setter de la controladora.
     * @param controller Controladora del descompresor.
     */
    public void setController(Decompressor_Controller controller) {
        this.controller = controller;
    }

    protected Decompressor_Controller controller; ///< Controlador del descompresor.

    /**
     * Retorna la extension del archivo original (actualemente comprimido).
     * @return Retorna la extension del archivo original.
     */
    abstract String getExtension();
    /**
     * Funcion encargada de descomprimir el archivo proporcionado por la controladora y escribirlo en el archivo de salida tambien a traves de la controladora.
     */
    abstract void decompress();

}
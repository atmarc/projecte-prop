package dominio;

import java.io.IOException;

/**
 * Clase con metodos abstractos para implementar un compresor. Contiene una variable que hace referencia a su controlador para poder comunicarse con otras capas.
 */
public abstract class Compressor {

    /**
     * Setter de la controladora.
     * @param controller Controladora del compresor.
     */
    public void setController(Compressor_Controller controller) {
        this.controller = controller;
    }

    protected Compressor_Controller controller; ///< Controlador del compresor.

    /**
     * Retorna la extension del archivo comprimido (depende del tipo de algoritmo utilizado).
     * @return Retorna la extension del archivo comprimido.
     */
    public abstract String getExtension();
    /**
     * Funcion encargada de comprimir el archivo proporcionado por la controladora y escribirlo en el archivo de salida tambien a traves de la controladora.
     */
    public abstract void compress() throws Exception;

}
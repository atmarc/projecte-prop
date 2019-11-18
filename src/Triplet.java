/*!
 *  \brief     Clase auxiliar para la implementacion del algortimo JPEG.
 *  \details
 *  \author    Marc Amoros
 */
public class Triplet <E, T, A> {
    /**
     * Primer elemento de la tripleta
     */
    private E first;
    /**
     * Segundo elemento de la tripleta
     */
    private T second;
    /**
     * Tercer elemento de la tripleta
     */
    private A third;

    /**
     * Constructora por defecto
     */
    public Triplet() {

    }

    /**
     * Constructora con la que añadimos los valores
     * @param first Primer valor
     * @param second Segundo valor
     * @param third Tercer valor
     */
    public Triplet(E first, T second, A third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * Imprime el valor de las tres componentes de la tripleta
     */
    public void print() {
        System.out.println(first.toString() + ' ' + second.toString() + ' ' + third.toString());
    }

    public E getFirst() {
        return first;
    }

    public void setFirst(E first) {
        this.first = first;
    }

    public T getSecond() {
        return second;
    }

    public void setSecond(T second) {
        this.second = second;
    }

    public A getThird() {
        return third;
    }

    public void setThird(A third) {
        this.third = third;
    }
}

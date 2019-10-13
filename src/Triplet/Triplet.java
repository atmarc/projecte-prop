package Triplet;

public class Triplet <E, T, A> {

    private E firstElement;
    private T secondElement;
    private A thirdElement;

    public Triplet() {

    }

    public Triplet(E firstElement, T secondElement, A thirdElement) {
        this.firstElement = firstElement;
        this.secondElement = secondElement;
        this.thirdElement = thirdElement;
    }

    public void print() {
        System.out.println(firstElement.toString() + ' ' + secondElement.toString() + ' ' + thirdElement.toString());
    }

    public E getFirstElement() {
        return firstElement;
    }

    public void setFirstElement(E firstElement) {
        this.firstElement = firstElement;
    }

    public T getSecondElement() {
        return secondElement;
    }

    public void setSecondElement(T secondElement) {
        this.secondElement = secondElement;
    }

    public A getThirdElement() {
        return thirdElement;
    }

    public void setThirdElement(A thirdElement) {
        this.thirdElement = thirdElement;
    }
}

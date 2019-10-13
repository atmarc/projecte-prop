package Triplet;

public class Triplet <E, T, A> {

    private E first;
    private T second;
    private A third;

    public Triplet() {

    }

    public Triplet(E first, T secondElement, A thirdElement) {
        this.first = first;
        this.second = secondElement;
        this.third = thirdElement;
    }

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

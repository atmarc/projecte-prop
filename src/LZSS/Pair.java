package LZSS;

public class Pair<I extends Integer, I1 extends Integer> {

    public int index;
    public int offset;

    public Pair(int i, int b) {
        index = i;
        offset = b;
    }

    public int getFirst() {
        return index;
    }

    public int getSecond() {
        return offset;
    }
}
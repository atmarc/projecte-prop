package JPEG;

public class Block {

    private int valors[][];
    private int width;
    private int height;

    public Block() {
    }

    public Block(int width, int height) {
        this.valors = new int[width][height];
        this.height = height;
        this.width = width;
    }

    public int getValue(int i, int j) {
        return valors[i][j];
    }

    public void setValue(int i, int j, int value) {
        valors[i][j] = value;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
}

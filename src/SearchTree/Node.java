package SearchTree;

public class Node {
    private byte id;
    private int index;
    private Tree sons;

    public Node(byte id, int index, Tree sons) {
        this.id = id;
        this.index = index;
        this.sons = sons;
    }

    public byte getId() {
        return id;
    }

    public boolean areYou(byte id) {
        return (this.id == id);
    }

    public void setId(byte id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Tree getSons() {
        return sons;
    }

    public void setSons(Tree sons) {
        this.sons = sons;
    }

}

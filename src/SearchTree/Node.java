package SearchTree;

import java.util.List;

public class Node {
    private byte id;
    private int data;
    private Tree sons;

    public Node(byte id, int data, Tree sons) {
        this.id = id;
        this.data = data;
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

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public Tree getSons() {
        return sons;
    }

    public void setSons(Tree sons) {
        this.sons = sons;
    }

}

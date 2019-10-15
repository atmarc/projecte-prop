package SearchTree;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchTree {

    HashMap<Byte,Node> nodes;

    public SearchTree() {
        nodes = new HashMap<Byte, Node>();
    }

    public int find(byte B) {


        return -1;
    }

    public int find(ArrayList<Byte> B, int digit) {
        return this.find(B.get(digit));
    }

}

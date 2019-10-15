package SearchTree;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchTreeRoot {

    private HashMap<Byte,Node> Tree;

    // LZ78 acceleration
    private ArrayList<Byte> previous_word;
    private SearchTree previous_tree;

    public SearchTreeRoot() {
        Tree = new HashMap<Byte, Node>();
    }

    public int find(ArrayList<Byte> pre_word, byte B) {

        /*
        * Pre: Todos los prefijos de word han sido previamente buscados. Lo comprobamos
        * Post:
        *   - Si la cadena word no estaba
        * */

        if (pre_word.equals(previous_word))
            previous_tree.find(B);




        return -1;
    }

}

package SearchTree;

public class Node {
    int index;
    SearchTree sons;

    public Node(int i, SearchTree s) {
        index = i;
        sons = s;
    }
}

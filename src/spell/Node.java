package spell;

public class Node implements INode {

    private int value;          // Frequency count for the word represented by the node. (how many words finish on this node)
    private Node[] children;    // Child nodes of this node

    public Node() {
        value = 0;
        children = new Node[26];
    }

    @Override
    public int getValue() { // Returns the frequency count for the word represented by the node.
        return value;
    }

    @Override
    public void incrementValue() {  // Increments the frequency count for the word represented by the node.
        value++;
    }

    @Override
    public Node[] getChildren() {  // Returns the child nodes of this node.
        return children;
    }
}

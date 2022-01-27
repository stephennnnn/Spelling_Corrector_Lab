package spell;

public class Trie implements ITrie {

    private Node root;
    private int wordCount;
    private int nodeCount;

    public Trie() {
        root = new Node();
        wordCount = 0;
        nodeCount = 1;
    }

    @Override
    public void add(String word) {
        word = word.toLowerCase();
        Node currentNode = root;

        for (int i = 0; i < word.length(); i++) {
            if (currentNode.getChildren()[word.charAt(i) - 'a'] == null) {      // If corresponding node does not exist
                currentNode.getChildren()[word.charAt(i) -  'a'] = new Node();  // create corresponding node
                nodeCount++;
            }

            currentNode = currentNode.getChildren()[word.charAt(i) - 'a'];              // go to corresponding node

            if (i == (word.length() - 1)) {
                if (currentNode.getValue() == 0) {  // if a new word was just created, increment wordCount and node value.
                    wordCount++;
                }
                currentNode.incrementValue();
            }
        }
    }

    @Override
    public Node find(String word) {
        word = word.toLowerCase();  // will be very closely related to add, just that you return the final node;
        Node currentNode = root;

        for (int i = 0; i < word.length(); i++) {                               // For the size of the word...
            if (currentNode.getChildren()[word.charAt(i) - 'a'] == null) {      // ...see if corresponding child exits.
                return null;                                                    // If it doesn't, return null.
            }
            else {
                currentNode = currentNode.getChildren()[word.charAt(i) - 'a'];  // If it does, move to corresponding node.
            }
        }
        if (currentNode.getValue() != 0) {
            return currentNode;
        }
        return null;
    }

    @Override
    public int getWordCount() {
        return wordCount;
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }


    //------------------------------------------ toString (recursive) ------------------------------------------
    // Returns list of all unique strings in trie in alphabetical order

    @Override
    public String toString() {
        StringBuilder currWord = new StringBuilder();
        StringBuilder output = new StringBuilder();

        toStringHelper(root, currWord, output);

        return output.toString();
    }

    private void toStringHelper(Node n, StringBuilder currWord, StringBuilder output) {
        if (n.getValue() > 0) { // If value is greater than 0, append the node's word to the output
            output.append(currWord.toString());
            output.append("\n");
        }
        for (int i = 0; i < n.getChildren().length; i++) { // Recurse on non-null children
            Node child = n.getChildren()[i];
            if (child != null) {

                char childLetter = (char)('a' + i);
                currWord.append(childLetter);

                toStringHelper(child, currWord, output);

                currWord.deleteCharAt(currWord.length() - 1);
            }
        }
    }

    //------------------------------------------ equals (recursive) ------------------------------------------

    @Override
    public boolean equals (Object o) {
        if (o == null) { // is o == null
            return false;
        }
        if (o == this) { // is o == this?
            return true;  // if they are the same object, return true
        }
        if (this.getClass() != o.getClass()) { // do this and o have the same type?
            return false;
        }

        // Once you know you are comparing two dictionaries:
        Trie dictionary = (Trie) o;

        // Do this and d have the same wordCount and nodeCount?
        if (this.getWordCount() != dictionary.getWordCount()) {
            return false;
        }
        if (this.getNodeCount() != dictionary.getNodeCount()) {
            return false;
        }

        // if this point is reached, you have to check both trees (check in at the same time)
        return equals_helper(this.root, dictionary.root);
    }

    private boolean equals_helper(Node n1, Node n2) {
        // compare n1 & n2:
            // do n1 & n2 have the same count?
            // do n1 & n2 have non-null children in exactly the same indexes?
        if (n1.getValue() != n2.getValue()) {
//            System.out.println("nodes do not have the same count"); // testing
            return false;
        }
        for (int i = 0; i < n1.getChildren().length; i++) {
            if ((n1.getChildren()[i] == null && n2.getChildren()[i] != null) || (n1.getChildren()[i] != null && n2.getChildren()[i] == null)) { // if one is null and th other isn't
//                System.out.println("children nodes do not match"); // testing
                return false;
            }
        }

        // recurse on children and compare the child subtrees
//        System.out.println("checking through children");
        for (int i = 0; i < n1.getChildren().length; i++) {
            if ((n1.getChildren()[i] != null) && (n2.getChildren()[i] != null)) {   // If nodes are not null, check children.
                if (!equals_helper(n1.getChildren()[i], n2.getChildren()[i])) {     // If false, stop checking. If true, keep checking.
                    return false;
                }
            }
        }

        // Else:
        return true;
    }

    //------------------------------------------ hashCode ------------------------------------------

    @Override
    public int hashCode() {
        /*
        You can achieve this by taking the index of the first non-null
        node of the root and multiplying that index by the node count and
        word count of the Trie. The hashCode() function then returns this variable.
         */
        int index = 0;
        for (int i = 0; i < root.getChildren().length; i++) {
            if (root.getChildren()[i] != null) {
                break; // If non-null node is found, exit for loop.
            }
            index++;  // Keep track current node index.
        }

        index++; // Add 1 to counter to make sure it is never 0.

        return 31*((index * 13) + (wordCount * 7) + (nodeCount * 3));
    }
}

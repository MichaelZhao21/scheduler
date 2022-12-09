public class Node {
    Process data;

    Node left;
    Node right;
    Node parent;

    int height;
    boolean color;

    /**
     * Constructs a new node with the given data.
     *
     * @param data the data to store in the node
     */
    public Node(Process data) {
        this.data = data;
    }

    public Process getData() {
        return data;
    }
}
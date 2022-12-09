public class BinaryTree {
    protected Node root;

    public Node getRoot() {
        return root;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (root == null) return "empty";
        appendNodeToStringRecursive(getRoot(), builder);
        return builder.toString();
    }

    private void appendNodeToStringRecursive(Node node, StringBuilder builder) {
        appendNodeToString(node, builder);
        if (node.left != null) {
            builder.append(" L{");
            appendNodeToStringRecursive(node.left, builder);
            builder.append('}');
        }
        if (node.right != null) {
            builder.append(" R{");
            appendNodeToStringRecursive(node.right, builder);
            builder.append('}');
        }
    }

    protected void appendNodeToString(Node node, StringBuilder builder) {
        builder.append(node.data);
    }

    protected void increaseWaitTimeForAllNodes() {
        if (root != null) {
            inorder(root);
        }
    }

    protected void inorder(Node n) {
        if (n == null) {
            return;
        }

        inorder(n.left);
        n.data.waitingTime++;
        inorder(n.right);
    }
}
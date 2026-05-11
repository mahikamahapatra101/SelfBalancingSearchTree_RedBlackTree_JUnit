import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/*
 * Author: Mahika Mahapatra
 * Email: mmahapatra2@wisc.edu
 * Assignment: Program 4
 * Course: Compsci400 Date: 2/14/2025
 * Citations: NA- (Worked on by myself)
 */

/**
 * Class implements a Red-Black Tree, which is a self-balancing BST.
 * Class xtends BSTRotation and maintains Red-Black Tree properties.
 */
public class RedBlackTree<T extends Comparable<T>> extends BSTRotation<T> {

    /**
     * constructs an empty Red-Black Tree.
    */
    public RedBlackTree() {
       super(); //call parent function
   }

  /**
   * Inserts a new data value into the sorted collection.
   * @param data the new value being inserted
   * @throws NullPointerException if data argument is null, we do not allow
   * null values to be stored within a
   * SortedCollection
   */
   @Override
   public void insert(T data) throws NullPointerException {
    if (data == null) {
        throw new NullPointerException("can't insert null value");
    }

    RedBlackNode<T> newNode = new RedBlackNode<>(data);

    if (root == null) {
        root = newNode;
    } else {
        insertHelper(newNode, root);
        ensureRedProperty(newNode);
    }

    RedBlackNode<T> rootNode = (RedBlackNode<T>) root;
    if (!rootNode.isBlackNode()) {
        rootNode.flipColor();
    }



}

     /**
     * Checks if a new red node in the RedBlackTree causes a red property violation
     * by having a red parent. If this is not the case, the method terminates without
     * making any changes to the tree. If a red property violation is detected, then
     * the method repairs this violation and any additional red property violations
     * that are generated as a result of the applied repair operation.
     * Using this method might cause nodes with a value equal to the value of one of
     * their ancestors to appear within the left and the right subtree of that ancestor,
     * even if the original insertion procedure consistently inserts such nodes into only
     * the left or the right subtree. But it will preserve the ordering of nodes within
     * the tree.
     * @param newNode a newly inserted red node, or a node turned red by previous repair
     */
    protected void ensureRedProperty(RedBlackNode<T> newNode) {

        //root node or null node, no red property violation, this means nothing needs to be done here,
        //insert method will make sure node is black
        //this can be used to determine whether parent is left or right child
        if (newNode == null || newNode.getUp() == null) {
            return;
        }

        RedBlackNode<T> parent = newNode.getUp();
        //this means parent is black, so red property violation can not occur
        if (parent.isBlackNode()) {
            return;
        }
        RedBlackNode<T> grandParent = parent.getUp();
        RedBlackNode<T> aunt;
        if (grandParent.getLeft() == parent) { //parent is left child

        aunt = grandParent.getRight();
        }
        else { //parent is right child
            aunt = grandParent.getLeft();
        }

        if (aunt != null && !aunt.isBlackNode()) { // Recolor
            grandParent.flipColor();
            parent.flipColor();
            aunt.flipColor();
            ensureRedProperty(grandParent);
        }
        else {
            if (grandParent.getLeft() == parent && parent.getRight() == newNode) { //leftRotate
                rotate(newNode, parent);
                 //swap parent and newNode using temp variable
                 RedBlackNode<T> temp = parent;
                 parent = newNode;
                 newNode = temp;
            }
            else if (grandParent.getRight()== parent && parent.getLeft() == newNode) { //rightRotate
                rotate(newNode, parent);
                // swap parent and newNode using temp variable
                RedBlackNode<T> temp = parent;
                parent = newNode;
                newNode = temp;
            }

            //rotate parent and grandparent
            rotate(parent, grandParent);
            grandParent.flipColor();
            parent.flipColor();
        }
    }
    /**
     * This is the first tester method, it tests a rotate and color swap
     * (top operation, but skips first step of operation),
     * inserts the nodes: 10, 20, 30 so when 30 is inserted as right child of 20 (both red),
     * it causes red property violation.
     */
    @Test
    public void bst_test1() {
        RedBlackTree<Integer> redBlackTree1 = new RedBlackTree<>();
        redBlackTree1.insert(10);
        RedBlackNode<Integer> root = (RedBlackNode<Integer>) redBlackTree1.root; //root belongs to redBlackTree1
        assertTrue(root.isBlackNode()); //root should be black
        assertEquals(10, (int) root.getData()); //makes sure 10 is root

        redBlackTree1.insert(20);
        RedBlackNode<Integer> node20 = root.getRight();
        assertEquals(10, (int) root.getData()); //root is still 10
        assertTrue(!root.getRight().isBlackNode()); //node should be red
        assertEquals(20, (int) root.getRight().getData()); // right child of 10 should be 20


        redBlackTree1.insert(30);
        root = (RedBlackNode<Integer>) redBlackTree1.root; //update root reference

        // after left rotation, 20 becomes black root
        assertEquals(20, (int) root.getData());
        assertTrue(root.isBlackNode()); //root should be black

        //after rotation, left child of 20 should be 10
        RedBlackNode<Integer> node10 = root.getLeft();
        assertEquals(10, (int) root.getLeft().getData());
        assertTrue(!node10.isBlackNode()); //node should be red

        //after rotation, 30 should be the red right child
        RedBlackNode<Integer> node30 = root.getRight();
        assertEquals(30, (int) root.getRight().getData());
        assertTrue(!node30.isBlackNode());
        assertEquals(3, redBlackTree1.size()); //size check

    }
    /**
     * This is the second tester method, it tests red aunt case with multiple recolorings.
     * it inserts the sequence 3, 16, 4, 17, 1, 5, 18 from Q03.RBTInsert Question 3.
     * This tests cases where aunt/uncle is red, which requires recoloring.
     */
    @Test
    public void bst_test2() {
        RedBlackTree<Integer> redBlackTree2 = new RedBlackTree<>();

        // Insert the sequence from the quiz
        redBlackTree2.insert(3);
        redBlackTree2.insert(16);
        redBlackTree2.insert(4);
        redBlackTree2.insert(17);
        redBlackTree2.insert(1);
        redBlackTree2.insert(5);
        redBlackTree2.insert(18);


        RedBlackNode<Integer> root = (RedBlackNode<Integer>) redBlackTree2.root;
        assertTrue(root.isBlackNode()); // verify root is black

        assertEquals(7, redBlackTree2.size()); //checks size of tree

        int redCount = countRedNodes((RedBlackNode<T>)root);
        assertEquals(3, redCount);

        assertTrue(redBlackTree2.contains(3));
        assertTrue(redBlackTree2.contains(16));
        assertTrue(redBlackTree2.contains(4));
        assertTrue(redBlackTree2.contains(17));
        assertTrue(redBlackTree2.contains(1));
        assertTrue(redBlackTree2.contains(5));
        assertTrue(redBlackTree2.contains(18));

    }

    /**
     * this method  is a helper method to count red nodes in the tree recursively,
     * @param node is the root of the subtree to count red nodes in
     * @return is the # of red nodes in the subtree
     */
    private int countRedNodes(RedBlackNode<T> node) {
        if (node == null) {
            return 0;
        }
        int count = 0;
        if (!node.isBlackNode()) {
            count = 1;
        }
        int leftRedCount = countRedNodes(node.getLeft());
        int rightRedCount = countRedNodes(node.getRight());
        count = count + leftRedCount + rightRedCount;

        return count;
}
    /**
     * This is the third tester method, it tests right rotation.
     * This tests the scenario where you inserts 30, 20, 10 which creates like a mirror of test1.
     * When 10 is inserted as left child of 20 (both red), red property violation occurs.
     * It requires, right rotation (opposite direction from test1).
    */
    @Test
    public void bst_test3() {
        RedBlackTree<Integer> redBlackTree3 = new RedBlackTree<>();
        redBlackTree3.insert(30);
        RedBlackNode<Integer> root = (RedBlackNode<Integer>) redBlackTree3.root;
        assertTrue(root.isBlackNode()); //root should be black
        assertEquals(30, (int) root.getData()); //makes sure 30 is root

        redBlackTree3.insert(20);
        RedBlackNode<Integer> node20 = root.getLeft();
        assertTrue(!root.getLeft().isBlackNode()); //node should be red
        assertEquals(20, (int) root.getLeft().getData()); //makes sure 30 is root

        redBlackTree3.insert(10);
        root = (RedBlackNode<Integer>) redBlackTree3.root; //update root reference

        // after right rotation, 20 becomes black root
        assertEquals(20, (int) root.getData());
        assertTrue(root.isBlackNode()); //root should be black

        //after rotation, left child of 20 should be 10
        RedBlackNode<Integer> node10 = root.getLeft();
        assertEquals(10, (int) root.getLeft().getData());
        assertTrue(!node10.isBlackNode()); //node should be red

        //after rotation, 30 should be the red right child
        RedBlackNode<Integer> node30 = root.getRight();
        assertEquals(30, (int) root.getRight().getData());
        assertTrue(!node30.isBlackNode());
        assertEquals(3, redBlackTree3.size()); //size check


    }





}
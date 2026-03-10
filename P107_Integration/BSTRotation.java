/*
 * Author: Mahika Mahapatra
 * Email: mmahapatra2@wisc.edu
 * Assignment: Program 2
 * Course: Compsci300 Date: 2/2/2026
 * Citations: TA office hours (Xuancheng Tu)
 */

/**
 * This class implements a rotation operation for a BST
 * it extends the BinarySearchTree class and
 * performs left and right rotations to restructure the tree
 */
public class BSTRotation<T extends Comparable<T>> extends BinarySearchTree<T> {

        /**
         * This is a single no-argument constructor (as required)
         */
        public BSTRotation() {
            super(); // calls constructor of the BinarySearchTree
        }

    /**
     * Performs the rotation operation on the provided nodes within this tree.
     * When the provided child is a left child of the provided parent, this
     * method will perform a right rotation. When the provided child is a right
     * child of the provided parent, this method will perform a left rotation.
     *
     * @param child is the node being rotated from child to parent position
     * @param parent is the node being rotated from parent to child position
     */
    protected void rotate(BinaryNode<T> child, BinaryNode<T> parent) {
        //make sure child node is actually child of parent node
        if (child == null || parent == null || child.getUp() != parent) {
            return;
        }
        if (parent.getLeft() == child) { //rotate right
            rotateRight(child, parent);
        }
        if (parent.getRight() == child) { //rotate left
            rotateLeft(child, parent);
        }

    }
/**
 * Helper method to perform a left rotation
 * @param child is the node being rotated from child to parent position
 * @param parent is the node being rotated from parent to child position
 */
private void rotateLeft(BinaryNode<T> child, BinaryNode<T> parent) {
        BinaryNode<T> grandparent = parent.getUp(); //Need grandparent for reattaching the subtree
        //child left -> parents right
        parent.setRight(child.getLeft()); // okay if childs left is null
        if (child.getLeft() != null) {
            child.getLeft().setUp(parent);
        }

        //flip parent and child
        child.setLeft(parent);
        parent.setUp(child);


         //connect child to grandparent
        child.setUp(grandparent);
        if (grandparent == null) { //checks if grandparent is root
            root = child;
        }

        else {

            if (grandparent.getLeft() == parent) {
                grandparent.setLeft(child);
            }
            if (grandparent.getRight() == parent) {
                grandparent.setRight(child);
           }

        }
    }

    /**
     * Helper method to perform a right rotation
     * @param child is the node being rotated from child to parent position
     * @param parent is the node being rotated from parent to child position
     */
    private void rotateRight(BinaryNode<T> child, BinaryNode<T> parent) {
        BinaryNode<T> grandparent = parent.getUp(); //Need grandparent for reattaching the subtree
        //child right -> parents left
        parent.setLeft(child.getRight()); // okay if childs right is null
        if (child.getRight() != null) {
            child.getRight().setUp(parent);
        }

        //flip parent and child
        child.setRight(parent);
        parent.setUp(child);

        //connect child to grandparent
        child.setUp(grandparent);
        if (grandparent == null) { //checks if grandparent is root
            root = child;
        }

        else {

            if (grandparent.getLeft() == parent) {
                grandparent.setLeft(child);
            }
            if (grandparent.getRight() == parent) {
                grandparent.setRight(child);
           }

        }

   }

   /**
     * Tests root rotation (0 shared children) and internal rotation (1 shared child).
     */
   public boolean test1() {

    // basic (no shared children, root node gets rotated)
    root = null; // removes only reference class has to tree (same as in clear() func.)


    BinaryNode<Integer> child = new BinaryNode<>(3);
    BinaryNode<Integer> parent = new BinaryNode<>(8);

    // zero shared children

    root = (BinaryNode<T>)parent;
    parent.setLeft(child);
    child.setUp(parent);

    //rotate right
    rotate((BinaryNode<T>)child, (BinaryNode<T>)parent);

    if (child != root){
        return false;
    }

    if (child.getRight() != parent) {
        return false;
    }

    if (parent.getUp() != child) {
        return false;
    }

    // verify rotation actually moved child node's original position
    if (parent.getLeft() != null) {
        return false;
    }


    // tree2
    // 1 shared children, root node does not get rotated
    root = null;

    BinaryNode<Integer> rootNode = new BinaryNode<>(52);
    BinaryNode<Integer> parent2 = new BinaryNode<>(82);
    BinaryNode<Integer> child2 = new BinaryNode<>(73);
    // 1 shared child
    BinaryNode<Integer> sharedChild = new BinaryNode<>(75);


    root = (BinaryNode<T>)rootNode;
    rootNode.setRight(parent2);
    parent2.setUp(rootNode);
    parent2.setLeft(child2);
    child2.setUp(parent2);
    child2.setRight(sharedChild);
    sharedChild.setUp(child2);

    //rotate right
    rotate((BinaryNode<T>)child2, (BinaryNode<T>)parent2);

    if (this.root != rootNode) {
        return false;
    }

    if (rootNode.getRight() != child2 || child2.getUp() != rootNode) {
        return false;
    }

    if (child2.getRight() != parent2 || parent2.getUp() != child2) {
        return false;
    }

    if (parent2.getLeft() != sharedChild || sharedChild.getUp() != parent2) {
        return false;
    }

    if (child2.getLeft() != null) {
        return false;
    }
    return true;
   }


   /**
     * Tests rotation with 2 shared children.
     */
   public boolean test2() {

    root = null; // removes only reference class has to tree (same as in clear() func.)
    BinaryNode<Integer> grandparent = new BinaryNode<>(20);
    BinaryNode<Integer> parent = new BinaryNode<>(10);
    BinaryNode<Integer> child = new BinaryNode<>(15);
    // two shared children
    BinaryNode<Integer> sharedChild1 = new BinaryNode<>(12); // left child of child
    BinaryNode<Integer> sharedChild2 = new BinaryNode<>(8); // left child of parent

    root = (BinaryNode<T>)grandparent;
    grandparent.setLeft(parent);
    parent.setUp(grandparent);
    parent.setRight(child);
    child.setUp(parent);
    // Add shared children
    parent.setLeft(sharedChild2);
    sharedChild2.setUp(parent);
    child.setLeft(sharedChild1);
    sharedChild1.setUp(child);
    //left rotate
    rotate((BinaryNode<T>)child, (BinaryNode<T>)parent);

    if (grandparent.getLeft() != child || child.getUp() != grandparent) {
        return false;
    }

    if (child.getLeft() != parent || parent.getUp() != child) {
        return false;
    }

    if (parent.getRight() != sharedChild1 || sharedChild1.getUp() != parent) {
        return false;
    }

    return true;

   }

   /**
     * Tests rotation with 3 shared children.
     */
   public boolean test3() {
    root = null; // removes only reference class has to tree (same as in clear() func.)


    BinaryNode<Integer> rootNode = new BinaryNode<>(51);
    BinaryNode<Integer> parent = new BinaryNode<>(26);
    BinaryNode<Integer> child = new BinaryNode<>(36);
    // three shared children
    BinaryNode<Integer> sharedChild1 = new BinaryNode<>(11);        // left of parent
    BinaryNode<Integer> sharedChild2 = new BinaryNode<>(31);        // left of child
    BinaryNode<Integer> sharedChild3 = new BinaryNode<>(41);        // right of child

    root = (BinaryNode<T>)rootNode;
    rootNode.setLeft(parent);
    parent.setUp(rootNode);
    parent.setLeft(sharedChild1);
    sharedChild1.setUp(parent);
    parent.setRight(child);
    child.setUp(parent);
    child.setLeft(sharedChild2);
    sharedChild2.setUp(child);
    child.setRight(sharedChild3);
    sharedChild3.setUp(child);

    //left rotate (does not involve node)
    rotate((BinaryNode<T>)child, (BinaryNode<T>)parent);

    if (root != rootNode) {
        return false;
    }
    if (rootNode.getLeft() != child || child.getUp() != rootNode) {
        return false;
    }
    if (child.getLeft() != parent || parent.getUp() != child) {
        return false;
    }
    if (parent.getRight() != sharedChild2 || sharedChild2.getUp() != parent) {
        return false;
    }
    //sharedChild1 and sharedChild3 do not change node refs (so don't need to check node refs excessivly)
    if (parent.getLeft() != sharedChild1 || child.getRight() != sharedChild3) {
        return false;
    }

    return true;

   }



    /**
     * Main method to run all tester methods and display the results.
     * @param args (part of main method)
     */


    public static void main(String[] args){
      BSTRotation<Integer> tester = new BSTRotation<>();
      boolean test1 = tester.test1();
      boolean test2 = tester.test2();
      boolean test3 = tester.test3();

      if (test1) {
         System.out.println("test 1: PASS");
      } else {
         System.out.println("test 1: FAIL");
      }

      if (test2) {
         System.out.println("test 2: PASS");
      } else {
         System.out.println("test 2: FAIL");
      }

      if (test3) {
         System.out.println("test 3: PASS");
      } else {
         System.out.println("test 3: FAIL");
      }


    }



}
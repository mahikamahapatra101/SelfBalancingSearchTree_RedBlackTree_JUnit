/*
 * Author: Mahika Mahapatra
 * Email: mmahapatra2@wisc.edu
 * Assignment: Program 1
 * Course: Compsci300 Date: 1/26/2025
 * Citations: NA- (Worked on by myself)
 */


/**
 * This is a class for a Binary Search Tree
 * that maintains elements in sorted order using their natural ordering.
 * In it duplicates are allowed and stored in the left subtree and each node
 * maintains a reference to its parent. It incorperates standard operations including insertion,
 * searching, size calculation, and clearing.
 */
public class BinarySearchTree<T extends Comparable<T>> implements SortedCollection<T> {
   // private allows for encapsulation
   protected BinaryNode<T> root;

   /**
    * Constructs an empty BST
    * initialized with a null root (tree with no nodes)
    */
   public BinarySearchTree() {
      root = null;
   }

  /**
   * This helper method goes through the tree recursively to insert newNode
   * If newNode data <= subtree data then it moves to the left child
   * If newNode's data > subtree's data then it moves to the right child
   * Once an empty child slot is found, the node is linked, and it's
   * parent pointer is updated.
   * @param newNode is the node to be inserted
   * @param subtree is the current node being compared in recursion
   */
   protected void insertHelper(BinaryNode<T> newNode, BinaryNode<T> subtree) {
      if (subtree == null) {
         return;
      }
      int compareResult = newNode.getData().compareTo(subtree.getData());

      // go left
      if (compareResult <= 0) {
         if (subtree.getLeft() == null) {
            subtree.setLeft(newNode);
            // updates the newNode point back to parent
            newNode.setUp(subtree);
         } else {
            insertHelper(newNode, subtree.getLeft());
         }

      }
      // go right
      if (compareResult > 0) {
         if (subtree.getRight() == null) {
            subtree.setRight(newNode);
            // updates the newNode point back to parent
            newNode.setUp(subtree);
         } else {
            insertHelper(newNode, subtree.getRight());
         }
      }
   }

   /**
    * Inserts a new data value into the sorted collection.
    *
    * @param data the new value being inserted
    * @throws NullPointerException if data argument is null, we do not allow
    * null values to be stored within a
    * SortedCollection
    */
   public void insert(T data) throws NullPointerException {
      if (data == null) {
         throw new NullPointerException("can't insert null value");
      }
      BinaryNode<T> newNode = new BinaryNode<T>(data);
      if (root == null) {
         root = newNode;
      } else {
         insertHelper(newNode, root);
      }
   }

    /**
     * Check whether data is stored in the tree.
     * @param find the value to check for in the collection
     * @return true if the collection contains data one or more times,
     * and false otherwise
     */
   public boolean contains(Comparable<T> find) {
      return containsHelper(root, find);
   }
   /**
    * This helper method recursively searches for a specific value within the tree
    * @param node is the current node being examined in the search
    * @param find is the target value to search for
    * @return true if the value is found in the subtree and
    * false otherwise.
    */
   private boolean containsHelper(BinaryNode<T> node, Comparable<T> find) {
      if (node == null) {
         return false;
      }
      int compareResult = find.compareTo(node.getData()); //stores result after comparison as int
      if (compareResult == 0) {
         return true;
      }
      if (compareResult < 0) {
         return containsHelper(node.getLeft(), find);
      } else {
         return containsHelper(node.getRight(), find);
      }
   }

   /**
    * Counts the number of values in the collection, with each duplicate value
    * being counted separately within the value returned.
    * @return the number of values in the collection, including duplicates
    */
   public int size() {
      return sizeHelper(root);
   }

   /**
    * This helper method recursively calculates the number of nodes in a given subtree
    * @param node is the root of the subtree to measure.
    * @return the total count of nodes (the current node plus all descendants),
    * returns 0 if the node is null
    */
   private int sizeHelper(BinaryNode<T> node) {
      if (node == null) {
         return 0;
      } else {
         return 1 + sizeHelper(node.getLeft()) + sizeHelper(node.getRight());
      }
   }

   /**
    * Checks if the collection is empty.
    * @return true if the collection contains 0 values, false otherwise
    */
   public boolean isEmpty() {
      if (root == null) { //if root is null, BST is empty (used in clear method)
         return true;
      } else {
         return false;
      }
   }

   /**
     * Removes all values and duplicates from the collection.
     */

   public void clear() {
      // removes only reference class has to tree
      root = null;
   }

   /**
     * This is the first tester method, it verifies insertion of multiple integers and checks tree shape/contains.
     * it also checks root, interior nodes, and leaves.
     */
    public boolean test1() {
      BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        bst.insert(50);
        bst.insert(30);
        bst.insert(70);
        bst.insert(20); // left leaf
        bst.insert(40); // interior leaf
        bst.insert(80); // right leaf
        // checks if size is correct
        if (bst.size() != 6) {
         return false;
        }
        //look for some values (node, left leaf, right leaf and inner leaf)
        if (!bst.contains(50) || !bst.contains(20) || !bst.contains(80) || !bst.contains(40)){
         return false;
        }
        // 100 should not be in the BST (random value)
        if (bst.contains(100)) {
         return false;
        }
        return true;
    }

    /**
     * This is the second tester method that has BST with multiple data types (Integer and String) and tree shapes
     * it verifies that size() and clear() work correctly for a balanced
     * Integer tree, a skewed Integer tree, and a String tree.
     * @return true if all size and clear operations behave as supposed too and false otherwise
     */
    public boolean test2() {
      BinarySearchTree<Integer> bst2 = new BinarySearchTree<>();
        bst2.insert(58);
        bst2.insert(37);
        bst2.insert(72);
        bst2.insert(19);
        bst2.insert(41);
        bst2.insert(79);
        bst2.insert(93);
        if (bst2.size() != 7) {
        return false;
        }
        bst2.clear();
        if (bst2.size() != 0) {
         return false;
        }
        if (bst2.root != null) {
         return false;
        }

        bst2.insert(58); //check to make sure you can reuse BST after cleared
        bst2.insert(37);
        if (bst2.size() != 2) {
         return false;
        }


      //skewed BST
      BinarySearchTree<Integer> bstSkew = new BinarySearchTree<>();
        bstSkew.insert(1);
        bstSkew.insert(2);
        bstSkew.insert(3);
        bstSkew.insert(4);
        bstSkew.insert(5);
        if (bstSkew.size() != 5) {
        return false;
        }
        bstSkew.clear();
        if (bstSkew.size() != 0) {
         return false;
        }

        if (bstSkew.root != null) {
         return false;
        }

        bstSkew.insert(1);
        bstSkew.insert(2);
        bstSkew.insert(3);
        if (bstSkew.size() != 3) {
         return false;
        }

      //BST with string values
      BinarySearchTree<String> bst3 = new BinarySearchTree<>();
        bst3.insert("Apple");
        bst3.insert("Cat");
        bst3.insert("Bro");
        bst3.insert("Hungry");
        bst3.insert("Food");
        bst3.insert("Okay");
        if (bst3.size() != 6) {
         return false;
        }
        bst3.clear();
         if (bst3.size() != 0) {
         return false;
         }
         if (bst3.root != null) {
         return false;
        }

        bst3.insert("Apple");
        bst3.insert("Cat");
        bst3.insert("Bro");
        if (bst3.size() != 3) {
         return false;
         }

        return true;
    }


    /**
     * This is the third tester method, in it duplicate values are present in the BST and
     * the test shows that duplicates are allowed and correctly increment the
     * tree size, and are stored in the left subtree of the parent node
     * with the same value.
     * @return true if duplicates are stored and found correctly in BST (false otherwise.)
     */
    public boolean test3() {
      BinarySearchTree<Integer> bst4 = new BinarySearchTree<>();
        bst4.insert(42);
        bst4.insert(42);
        bst4.insert(42);

         if (bst4.size() != 3) {
         return false;
        }

        if (!bst4.contains(42)){
         return false;
        }
        //bst4.root instead of just root bc. root refers to the root of the instance, not bst4
        if (bst4.root.getLeft() == null || !bst4.root.getLeft().getData().equals(42)) {
         return false;
        }

        if (bst4.root.getLeft().getLeft() == null || !bst4.root.getLeft().getLeft().getData().equals(42)) {
         return false;
        }

        if (bst4.root.getRight() != null) {
         return false;
        }
        return true;
    }


    /**
     * Main method to run all tester methods and display the results.
     * @param args (part of main method)
     */
    public static void main(String[] args) {
      BinarySearchTree<Integer> bstTesters = new BinarySearchTree<>();
      boolean test1 = bstTesters.test1();
      boolean test2 = bstTesters.test2();
      boolean test3 = bstTesters.test3();

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

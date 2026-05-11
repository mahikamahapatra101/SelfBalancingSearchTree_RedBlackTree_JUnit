import java.util.Iterator;
import java.util.Stack;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * Author: Mahika Mahapatra
 * Email: mmahapatra2@wisc.edu
 * Assignment: Program 6
 * Course: Compsci400 Date: 3/3/2026
 * Citations: NA- (Worked on by myself)
 */

/**
 * This class extends RedBlackTree into a tree that supports iterating over the values it
 * stores in sorted, ascending order.
 */
public class RBTreeIterable<T extends Comparable<T>>
        extends RedBlackTree<T> implements IterableSortedCollection<T> {
    private Comparable<T> iterator_Max = null; // field that stores the max for the iterator
    private Comparable<T> iterator_Min = null; // field that stores the min for the iterator

    /**
     * Allows setting the start (minimum) value of the iterator. When this method is called,
     * every iterator created after it will use the minimum set by this method until this method
     * is called again to set a new minimum value.
     *
     * @param min the minimum for iterators created for this tree, or null for no minimum
     */
    public void setIteratorMin(Comparable<T> min) {
        this.iterator_Min = min;

    }

    /**
     * Allows setting the stop (maximum) value of the iterator. When this method is called,
     * every iterator created after it will use the maximum set by this method until this method
     * is called again to set a new maximum value.
     *
     * @param max the maximum for iterators created for this tree, or null for no maximum
     */
    public void setIteratorMax(Comparable<T> max) {
        this.iterator_Max =  max;

    }

    /**
     * Returns an iterator over the values stored in this tree. The iterator uses the
     * start (minimum) value set by a previous call to setIteratorMin, and the stop (maximum)
     * value set by a previous call to setIteratorMax. If setIteratorMin has not been called
     * before, or if it was called with a null argument, the iterator uses no minimum value
     * and starts with the lowest value that exists in the tree. If setIteratorMax has not been
     * called before, or if it was called with a null argument, the iterator uses no maximum
     * value and finishes with the highest value that exists in the tree.
     */
    public Iterator<T> iterator() {
        return new TreeIterator(root,iterator_Min, iterator_Max);
    }

    /**
     * Nested class for Iterator objects created for this tree and returned by the iterator method.
     * This iterator follows an in-order traversal of the tree and returns the values in sorted,
     * ascending order.
     */
    protected static class TreeIterator<R extends Comparable<R>> implements Iterator<R> {

        // stores the start point (minimum) for the iterator
        Comparable<R> min = null;
        // stores the stop point (maximum) for the iterator
        Comparable<R> max = null;
        // stores the stack that keeps track of the inorder traversal
        Stack<BinaryNode<R>> stack = null;



        /**
         * Constructor for a new iterator if the tree with root as its root node, and
         * min as the start (minimum) value (or null if no start value) and max as the
         * stop (maximum) value (or null if no stop value) of the new iterator.
         * Time complexity should be O(log n).
         *
         * @param root root node of the tree to traverse
         * @param min  the minimum value that the iterator will return
         * @param max  the maximum value that the iterator will return
         */
        public TreeIterator(BinaryNode<R> root, Comparable<R> min, Comparable<R> max) {

            this.min = min; // stores the min bounds for this iterator
            this.max = max; // stores the max bounds for this iterator

            this.stack = new Stack<>(); // initializes stack to an empty stack

            updateStack(root);
        }

        /**
         * Helper method for initializing and updating the stack. This method both
         * - finds the next data value stored in the tree (or subtree) that is between
         * start(minimum) and stop(maximum) point (including start and stop points
         * themselves), and
         * - builds up the stack of ancestor nodes that contain values between
         * start(minimum) and stop(maximum) values (including start and stop values
         * themselves) so that those nodes can be visited in the future.
         *
         * @param node the root node of the subtree to process
         */
        private void updateStack(BinaryNode<R> node) {
            if (node == null) { //base case
                return;
            }
        // if node is smaller than min
         if (min != null && min.compareTo(node.getData()) > 0) { // have to use compareTo bc node.getData() > min -->> only works on primitive types
                updateStack(node.getRight());
            }
        //values greater than or equal to min
         else {
            stack.push(node); //save node to visit later
            updateStack(node.getLeft());
         }


        }

        /**
         * Returns true if the iterator has another value to return, and false otherwise.
         */
        public boolean hasNext() {

            if (stack.isEmpty()) { // if stack is empty, no more nodes to visit
                return false;
            }

           if (max == null) { //no max set -> any remaining node is valid
               return true;
            }

            // make sure top of stack is within max bound (incl.)
            return max.compareTo(stack.peek().getData()) >= 0;  // reurns false if stack is empty or top > max ( elements exist but are outside the max bound)


        }

        /**
         * Returns the next value of the iterator.
         * Amortized time complexity should be O(1).
         * Worst case time complexity should be O(log n).
         * Do not implement this method by linearly walking through the
         * entire tree from the smallest element until the start bound is reached.
         * That process should occur only once during construction of the
         * iterator object.
         *
         * @throws NoSuchElementException if the iterator has no more values to return
         */
        public R next() {
            if (hasNext() == false) {
                throw new NoSuchElementException("no more elements in iteratorr"); // throw exception if no more valid nodes remain
            }

            // pop the next node (smallest remaining valid value on the stack)
            BinaryNode<R> current = stack.pop();
            R value = current.getData();

            // update stack with right subtree so we can find the next in-order node
            updateStack(current.getRight());

            return value;
            }
    }

    /**
    * This tests a String tree with only a max bound set and no min and also
    * verifies that only values up to and including max are returned in ascending order (max, no min).
    */
    @Test
    public void testStringWithMaxOnly() {

        // create tree and insert strings
        RBTreeIterable<String> tree = new RBTreeIterable<>();
        tree.insert("a");
        tree.insert("b");
        tree.insert("c");
        tree.insert("d");
        tree.insert("e");

        // no min set, max set to "c" should only return values that are less and equal "c"
        tree.setIteratorMax("c");

        Iterator<String> iterator = tree.iterator();

        // should return a, b, c only
        assertTrue(iterator.hasNext());
        assertEquals("a", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("b", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("c", iterator.next());

        // d and e are above max, should not appear (no more integers)
        assertTrue(!iterator.hasNext());
    }

    /**
    * This tests a integer tree with only a min bound set and no max and also
    * verifies that only values above min are returned in ascending order (min,no max)
    */
    @Test
    public void testIntegerWithMinOnly() {

        // create tree and insert integers
        RBTreeIterable<Integer> tree2 = new RBTreeIterable<>();
        tree2.insert(1);
        tree2.insert(2);
        tree2.insert(3);
        tree2.insert(4);
        tree2.insert(5);

        // no max set, min set to 2 should only return values that are more than and equal to 2
        tree2.setIteratorMin(2);

        Iterator<Integer> iterator = tree2.iterator();

        // should return 2, 3, 4, 5 only
        assertTrue(iterator.hasNext());
        assertEquals(2, iterator.next()); //proves 1st integer is not 1!!!
        assertTrue(iterator.hasNext());
        assertEquals(3, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(4, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(5, iterator.next());

        //no more integers
        assertTrue(!iterator.hasNext());
    }

    /**
    * This tests an Integer tree with both a min and max bound set and it also
    * verifies only values within the min, max range are returned in sorted ascending order,
    * and that duplicates within the range are included as well.
    */
    @Test
    public void testIntegerWithMinAndMax() {

        // create tree and insert integers including a duplicate within range
        RBTreeIterable<Integer> tree3 = new RBTreeIterable<>();
        tree3.insert(1);
        tree3.insert(3);
        tree3.insert(3); // duplicate within range
        tree3.insert(5);
        tree3.insert(7);
        tree3.insert(9);

        // min set to 3, max set to 7 (should return only values within [3,7])
        tree3.setIteratorMin(3);
        tree3.setIteratorMax(7);

        Iterator<Integer> iterator = tree3.iterator();

        // should return 3, 3 (dupe.), 5, 7 only
        assertTrue(iterator.hasNext());
        assertEquals(3, iterator.next()); //shows 1 is NOT first integer!!
        assertTrue(iterator.hasNext());
        assertEquals(3, iterator.next()); // duplicate
        assertTrue(iterator.hasNext());
        assertEquals(5, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(7, iterator.next());

        // no more elements remain
        assertTrue(!iterator.hasNext());
    }












}
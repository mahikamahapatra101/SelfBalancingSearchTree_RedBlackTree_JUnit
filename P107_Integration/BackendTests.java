import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * BackendTests - JUnit 5 Test Suite for Backend Class
 * Tests the functionality of the Backend class including adding records,
 * loading from CSV, filtering by level range, filtering by completion time,
 * and retrieving the top 10 records by collectables.
 */
public class BackendTests {

    /**
     * roleTest1: Tests addRecord() and getAndSetRange() methods
     * Verifies that records are added to the tree and that
     * level ranges are correctly applied to filter results
     */
    @Test
    public void roleTest1() {
        // Create backend with placeholder tree
        Tree_Placeholder tree = new Tree_Placeholder();
        Backend backend = new Backend(tree);

        // Add a test record
        GameRecord testRecord = new GameRecord("TestPlayer", GameRecord.Continent.NORTH_AMERICA,
                                                50000, 200, 400, "200:00:00");
        backend.addRecord(testRecord);

        // Verify record was added to tree
        assertEquals(testRecord, tree.lastAddedGameRecord,
                     "addRecord should store the record in the tree");

        // Test getAndSetRange with bounds that include placeholder records
        // v0idt3mp0 has level 353, speedRoyalty has level 479
        List<String> results = backend.getAndSetRange(300, 400);

        // Should include v0idt3mp0 (level 353 is in range [300, 400])
        assertTrue(results.contains("v0idt3mp0"),
                   "v0idt3mp0 (level 353) should be in range [300, 400]");

        // Should exclude speedRoyalty (level 479 is outside range)
        assertFalse(results.contains("speedRoyalty"),
                    "speedRoyalty (level 479) should NOT be in range [300, 400]");

        // Result should not be null
        assertNotNull(results, "getAndSetRange should return a non-null list");
    }

    /**
     * roleTest2: Tests readData() and applyAndSetFilter() methods
     * Verifies that readData throws IOException for missing files,
     * and that applyAndSetFilter correctly filters by completion time
     */
    @Test
    public void roleTest2() {
        Tree_Placeholder tree = new Tree_Placeholder();
        Backend backend = new Backend(tree);

        // Test readData with non-existent file
        assertThrows(IOException.class, () -> {
            backend.readData("this_file_does_not_exist.csv");
        }, "readData should throw IOException for missing files");

        // Test applyAndSetFilter
        // v0idt3mp0 has time "634:06:42" - should pass filter "635:00:00"
        // speedRoyalty has time "640:09:00" - should NOT pass filter "635:00:00"
        List<String> filtered = backend.applyAndSetFilter("635:00:00");

        assertTrue(filtered.contains("v0idt3mp0"),
                   "v0idt3mp0 (time 634:06:42) should pass filter for times < 635:00:00");

        assertFalse(filtered.contains("speedRoyalty"),
                    "speedRoyalty (time 640:09:00) should NOT pass filter for times < 635:00:00");

        // Result should not be null
        assertNotNull(filtered, "applyAndSetFilter should return a non-null list");
    }

    /**
     * roleTest3: Tests getTopTen() method
     * Verifies that getTopTen returns records sorted by collectables
     * and respects both level ranges and time filters
     */
    @Test
    public void roleTest3() {
        Tree_Placeholder tree = new Tree_Placeholder();
        Backend backend = new Backend(tree);

        // Reset range and filter to get all records
        backend.getAndSetRange(null, null);
        backend.applyAndSetFilter(null);

        // Get top 10 records by collectables
        List<String> topTen = backend.getTopTen();

        // Should not be empty (placeholder has 3 records)
        assertFalse(topTen.isEmpty(), "getTopTen should return results from placeholder");

        // Should not exceed 10 records
        assertTrue(topTen.size() <= 10,
                   "getTopTen should never return more than 10 records");

        // Result should not be null
        assertNotNull(topTen, "getTopTen should return a non-null list");

        // Placeholder records sorted by collectables:
        // xXxgamer47xXx has 130 collectables (highest)
        // speedRoyalty has 120 collectables
        // v0idt3mp0 has 140 collectables (highest)
        // So the top should be v0idt3mp0
        assertEquals("v0idt3mp0", topTen.get(0),
                     "Top record should be v0idt3mp0 with 140 collectables");
    }
    // Integration tests below:

     /**
     * This tests that the help command displays instructions
     * using the actual Backend and Frontend together with the RBTreeIterable
     */
    @Test
    public void integrationTest1() {
        // this simulate user typing "help" then "quit"
        TextUITester tester = new TextUITester("help\nquit\n");

        //this creates real tree and backend and then run frontend
        RBTreeIterable<GameRecord> tree = new RBTreeIterable<>();
        Backend backend = new Backend(tree);
        Scanner in = new Scanner(System.in);
        Frontend frontend = new Frontend(in, backend);
        frontend.runCommandLoop(); // <- called on Frontend

        // this checks that help output was displayed
        String output = tester.checkOutput(); /// <- called on TextUITester
        assertTrue(output.contains("submit"));
    }

    /**
     * This tests that submit command adds a record using
     * real Backend and Frontend together with RBTreeIterable.
     */
    @Test
    public void integrationTest2() {
        // simulates user submitting a record then quitting
        TextUITester tester = new TextUITester(
            "submit TestPlayer NORTH_AMERICA 50000 200 350 200:00:00\nquit\n");

        // creates real tree and backend and then run frontend
        RBTreeIterable<GameRecord> tree = new RBTreeIterable<>();
        Backend backend = new Backend(tree);
        Scanner in = new Scanner(System.in);
        Frontend frontend = new Frontend(in, backend);
        frontend.runCommandLoop();

        // verifies frontend confirmed the record was submitted
        String output = tester.checkOutput();
        assertTrue(output.contains("Submitted record for: TestPlayer"));
    }
       /**
     * This tests that level range command filters records
     * using real Backend and Frontend together with RBTreeIterable
     */
    @Test
    public void integrationTest3() throws IOException {
        // simulates loading CSV, setting level range, then showing records
        TextUITester tester = new TextUITester(
            "submit multiple records.csv\nlevel 300 to 400\nshow 10\nquit\n");

        // creates real tree and backend and then run frontend
        RBTreeIterable<GameRecord> tree = new RBTreeIterable<>();
        Backend backend = new Backend(tree);
        Scanner in = new Scanner(System.in);
        Frontend frontend = new Frontend(in, backend);
        frontend.runCommandLoop();

        // this verifies level range was updated
        String output = tester.checkOutput();
        assertTrue(output.contains("Level range updated"));
    }
     /**
     * This tests that show most collectables displays top ten
     * using real Backend and Frontend together with RBTreeIterable.
     */
    @Test
    public void integrationTest4() throws IOException {
        // simulates loading CSV then showing top collectables
        TextUITester tester = new TextUITester(
            "submit multiple records.csv\nshow most collectables\nquit\n");

        // this creates real tree and backend and then run frontend
        RBTreeIterable<GameRecord> tree = new RBTreeIterable<>();
        Backend backend = new Backend(tree);
        Scanner in = new Scanner(System.in);
        Frontend frontend = new Frontend(in, backend);
        frontend.runCommandLoop();

        // verifies top records were displayed
        String output = tester.checkOutput();
        assertTrue(output.contains("Top records (most collectables)"));
    }
}
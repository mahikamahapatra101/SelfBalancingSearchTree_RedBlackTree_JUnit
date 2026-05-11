import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Scanner;

public class FrontendTests {

    /**
     * roleTest1:
     * Goal: Verify that the "help" command displays command instructions
     * and that the program can quit cleanly
     */
    @Test
    public void roleTest1() {
        // IMPORTANT: TextUITester must be created BEFORE any Scanner(System.in)
        TextUITester tester = new TextUITester("help\nquit\n");

        IterableSortedCollection<GameRecord> tree = new Tree_Placeholder();
        BackendInterface backend = new Backend_Placeholder(tree);
        FrontendInterface frontend = new Frontend(new Scanner(System.in), backend);

        frontend.runCommandLoop();

        String output = tester.checkOutput();
        assertTrue(output.contains("Commands"), "Help should display command instructions.");
    }

    /**
     * roleTest2:
     * Goal: Verify that a single record submit command is parsed and processed
     * without crashing, and that the level command is accepted
     * (Also ends with quit to stop the command loop)
     */
    @Test
    public void roleTest2() {
        // Runs: submit (single) + level + quit
        TextUITester tester = new TextUITester(
                "submit Max NORTH_AMERICA 100 5 10 001:02:03\n" +
                "level 1 to 10\n" +
                "quit\n"
        );

        IterableSortedCollection<GameRecord> tree = new Tree_Placeholder();
        BackendInterface backend = new Backend_Placeholder(tree);
        FrontendInterface frontend = new Frontend(new Scanner(System.in), backend);

        frontend.runCommandLoop();

        String output = tester.checkOutput().toLowerCase();
        assertTrue(output.contains("submitted record") || output.contains("invalid submit"),
                "Submit single should be acknowledged or rejected with a clear message.");
        assertTrue(output.contains("level range updated"),
                "Level command should acknowledge that the range was updated.");
    }

    /**
     * roleTest3:
     * Goal: Verify that the time filter and submit multiple commands are parsed and processed,
     * and that show most collectables produces some output (or a no records message)
     */
    @Test
    public void roleTest3() {
        // Runs: time + submit multiple + show most collectables + quit
        TextUITester tester = new TextUITester(
                "time 001:00:00\n" +
                "submit multiple records.csv\n" +
                "show most collectables\n" +
                "quit\n"
        );

        IterableSortedCollection<GameRecord> tree = new Tree_Placeholder();
        BackendInterface backend = new Backend_Placeholder(tree);
        FrontendInterface frontend = new Frontend(new Scanner(System.in), backend);

        frontend.runCommandLoop();

        String output = tester.checkOutput().toLowerCase();
        assertTrue(output.contains("time filter updated") || output.contains("invalid time"),
                "Time command should be acknowledged or rejected with a clear message.");
        assertTrue(output.contains("loaded records") || output.contains("error reading file"),
                "Submit multiple should report success or an IO-related error message.");
        assertTrue(output.contains("top records") || output.contains("no records"),
                "Show most collectables should print results or indicate no records.");
    }
}
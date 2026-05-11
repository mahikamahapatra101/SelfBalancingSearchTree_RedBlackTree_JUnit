import java.util.Scanner;
import java.util.List;
import java.io.IOException;

public class Frontend implements FrontendInterface {

    private Scanner in;
    private BackendInterface backend;

    private Integer lastLowLevel = null;
    private Integer lastHighLevel = null;

    public Frontend(Scanner in, BackendInterface backend) {
        if (in == null) throw new IllegalArgumentException("Scanner cannot be null");
        if (backend == null) throw new IllegalArgumentException("Backend cannot be null");
        this.in = in;
        this.backend = backend;
    }

    @Override
    public void runCommandLoop() {
        showCommandInstructions();

        while (true) {
            System.out.print("\nEnter command: ");
            if (!in.hasNextLine()) return;

            String command = in.nextLine();
            if (command != null && command.trim().equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                return;
            }

            try {
                processSingleCommand(command);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void showCommandInstructions() {
        System.out.println("Commands (examples):");
        System.out.println("  submit NAME CONTINENT SCORE COLLECTABLES LEVEL COMPLETION_TIME");
        System.out.println("  submit multiple FILEPATH");
        System.out.println("  level MAX");
        System.out.println("  level MIN to MAX");
        System.out.println("  time TIME");
        System.out.println("  show MAX_COUNT");
        System.out.println("  show most collectables");
        System.out.println("  help");
        System.out.println("  quit");
    }

    @Override
    public void processSingleCommand(String command) {
        String line = (command == null) ? "" : command.trim();
        if (line.length() == 0) {
            System.out.println("Invalid command: empty input. Type help.");
            return;
        }

        String[] parts = line.split("\\s+");
        String first = parts[0].toLowerCase();

        if (first.equals("help")) {
            showCommandInstructions();
            return;
        }

        if (first.equals("submit")) {
            if (parts.length >= 2 && parts[1].equalsIgnoreCase("multiple")) {
                handleSubmitMultiple(parts);
            } else {
                handleSubmitSingle(parts);
            }
            return;
        }

        if (first.equals("level")) {
            handleLevel(line);
            return;
        }

        if (first.equals("time")) {
            handleTime(parts);
            return;
        }

        if (first.equals("show")) {
            handleShow(line, parts);
            return;
        }

        System.out.println("Invalid command: unknown keyword '" + parts[0] + "'. Type help.");
    }

    private void handleSubmitMultiple(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Invalid submit multiple syntax. Usage: submit multiple FILEPATH");
            return;
        }
        String filepath = parts[2];
        try {
            backend.readData(filepath);
            System.out.println("Loaded records from: " + filepath);
        } catch (IOException e) {
            System.out.println("Error reading file: " + filepath);
        }
    }

    private void handleSubmitSingle(String[] parts) {
        if (parts.length != 7) {
            System.out.println("Invalid submit syntax. Usage: submit NAME CONTINENT SCORE COLLECTABLES LEVEL COMPLETION_TIME");
            return;
        }

        String name = parts[1];
        String continentStr = parts[2];

        Integer score = parseInt(parts[3]);
        Integer collectables = parseInt(parts[4]);
        Integer level = parseInt(parts[5]);
        String time = parts[6];

        if (score == null || collectables == null || level == null) {
            System.out.println("Invalid submit syntax: SCORE/COLLECTABLES/LEVEL must be integers.");
            return;
        }
        if (!isValidTime(time)) {
            System.out.println("Invalid submit syntax: COMPLETION_TIME must be formatted hhh:mm:ss");
            return;
        }

        GameRecord.Continent continent;
        try {
            continent = GameRecord.Continent.valueOf(continentStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid continent: " + continentStr);
            return;
        }

        GameRecord record = new GameRecord(name, continent, score, collectables, level, time);
        backend.addRecord(record);
        System.out.println("Submitted record for: " + name);
    }

    private void handleLevel(String fullLine) {
        String rest = fullLine.trim().substring(5).trim();
        if (rest.length() == 0) {
            System.out.println("Invalid level syntax. Usage: level MAX  OR  level MIN to MAX");
            return;
        }

        Integer low = null;
        Integer high = null;

        String lowerRest = rest.toLowerCase();
        if (lowerRest.contains(" to ")) {
            String[] halves = lowerRest.split("\\s+to\\s+");
            if (halves.length != 2) {
                System.out.println("Invalid level syntax. Usage: level MIN to MAX");
                return;
            }
            low = parseInt(halves[0].trim());
            high = parseInt(halves[1].trim());
            if (low == null || high == null) {
                System.out.println("Invalid level syntax: MIN and MAX must be integers.");
                return;
            }
            if (low > high) {
                System.out.println("Invalid level syntax: MIN cannot be greater than MAX.");
                return;
            }
        } else {
            high = parseInt(rest);
            if (high == null) {
                System.out.println("Invalid level syntax: MAX must be an integer.");
                return;
            }
        }

        backend.getAndSetRange(low, high);
        lastLowLevel = low;
        lastHighLevel = high;
        System.out.println("Level range updated.");
    }

    private void handleTime(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Invalid time syntax. Usage: time hhh:mm:ss");
            return;
        }
        String time = parts[1];
        if (!isValidTime(time)) {
            System.out.println("Invalid time syntax: TIME must be formatted hhh:mm:ss");
            return;
        }
        backend.applyAndSetFilter(time);
        System.out.println("Time filter updated.");
    }

    private void handleShow(String fullLine, String[] parts) {
        String lower = fullLine.trim().toLowerCase();

        if (lower.equals("show most collectables")) {
            List<String> top = backend.getTopTen();
            if (top == null || top.size() == 0) {
                System.out.println("No records found.");
                return;
            }
            System.out.println("Top records (most collectables):");
            for (String name : top) System.out.println(name);
            return;
        }

        if (parts.length != 2) {
            System.out.println("Invalid show syntax. Usage: show MAX_COUNT  OR  show most collectables");
            return;
        }

        Integer maxCount = parseInt(parts[1]);
        if (maxCount == null || maxCount < 0) {
            System.out.println("Invalid show syntax: MAX_COUNT must be a non-negative integer.");
            return;
        }

        List<String> names = backend.getAndSetRange(lastLowLevel, lastHighLevel);
        if (names == null || names.size() == 0) {
            System.out.println("No records found.");
            return;
        }

        int limit = Math.min(maxCount, names.size());
        System.out.println("Records:");
        for (int i = 0; i < limit; i++) System.out.println(names.get(i));
    }

    private Integer parseInt(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.length() == 0 || s.length() > 12) return null;
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isValidTime(String time) {
        if (time == null) return false;
        String[] p = time.split(":");
        if (p.length != 3) return false;

        Integer h = parseInt(p[0]);
        Integer m = parseInt(p[1]);
        Integer s = parseInt(p[2]);
        if (h == null || m == null || s == null) return false;

        return h >= 0 && m >= 0 && m <= 59 && s >= 0 && s <= 59;
    }
}
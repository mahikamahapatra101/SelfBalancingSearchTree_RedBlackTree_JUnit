/*
 * Author: Mahika Mahapatra
 * Email: mmahapatra2@wisc.edu
 * Assignment: Program 3
 * Course: Compsci400 Date: 2/11/2026
 * Citations: worked on by self
*/

//code is tested on microsoft powershell (bash)
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * the class implements the BackendInterface to manage game records and
 * it provides functionality to read data from a CSV, add records,
 * and filter records by level ranges or completion times.
 */
public class Backend implements BackendInterface {

    //tree is type:IterableSortedCollection<GameRecord>
    private IterableSortedCollection<GameRecord> tree;
    //initialize to make sure there are no level restrictions or time filters
    private Integer lowLevel = null;
    private Integer highLevel = null;
    private String timeFilter = null;


    /**
     * constructs a new backend object associated with the provided tree and
     * @param tree is the sorted collection used for storing game records
     */
    public Backend(IterableSortedCollection<GameRecord> tree) {
        this.tree = tree; // use saved tree
    }


    /** Add and stores the specified record to the tree. Don't forget that the GameRecord
     *  must have the Comparator set. This will be used to store these records in order within your
     *  tree, and to retrieve them by level range in the getRange method.
     * @param record the game record to add
     */
    @Override //lets compiler know that this method overrides a method from BackendInterface
    public void addRecord(GameRecord record) {
        //null check
        if (record == null) {
            throw new IllegalArgumentException("can't add null GameRecord to backend");
        }
        tree.insert(record);
    }

    /**
     * Loads data from the .csv file referenced by filename.  You can rely
     * on the exact headers found in the provided records.csv, but you should
     * not rely on them always being presented in this order or on there
     * not being additional columns describing other record qualities.
     * After reading records from the file, the records are inserted into
     * the tree passed to this backend's constructor. This will be used to store these records in order within your
     * tree, and to retrieve them by level range in the getRange method.
     * @param filename is the name of the csv file to load data from
     * @throws IOException when there is trouble finding/reading file
     */
    @Override
    public void readData(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) { // if file == null may through error
            throw new IOException("errror analyzing the CSV data");
        }
            Scanner scanner = new Scanner(file);

            if (scanner.hasNextLine() == false) {
                scanner.close();
                return;
            }

            String lineOne = scanner.nextLine();
            String [] headers = lineOne.split(",");//splits string by comma ","

            int nameIndex = -1;
            int continentIndex = -1;
            int scoreIndex = -1;
            int collectablesIndex = -1;
            int levelIndex = -1;
            int completionTimeIndex = -1;

            for (int i = 0; i< headers.length; i++) {
                String header = headers[i].trim();
                if (header.equals("name")) {
                    nameIndex = i; // it will remember that name is position 1

                }
                if (header.equals("continent")) {
                    continentIndex = i;
                }
                if (header.equals("score")) {
                    scoreIndex = i;
                }
                if (header.equals("collectables")) {
                    collectablesIndex = i;
                }
                if (header.equals("completion_time")) {
                    completionTimeIndex = i;
                }
                if (header.equals("level")) {
                    levelIndex = i;
                }
            }
            // check all indices are valid before using them
           if (nameIndex == -1 || continentIndex == -1 || scoreIndex == -1 ||
           collectablesIndex == -1 || levelIndex == -1 || completionTimeIndex == -1) {
            scanner.close();
            throw new IOException(" The CSV file is missing required column headers");
           }

             while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) {
                // skips rest of code inside current loop body, jumps back to condition
                continue;
                }
                String[] csvParts = line.split(",");
                if (csvParts.length < 6) {
                continue; // breaks one iteration
                }

                String name = csvParts[nameIndex];

                // parse continent safely with try/catch
                GameRecord.Continent continent;
                try {
                //trim() removes spaces that might occur when parsing
                continent = GameRecord.Continent.valueOf(csvParts[continentIndex].trim());
                } catch (IllegalArgumentException e) {
                    // skip row with invalid continent value
                    continue;
                }

                int score = Integer.parseInt(csvParts[scoreIndex].trim());
                int collectables = Integer.parseInt(csvParts[collectablesIndex].trim());
                int level = Integer.parseInt(csvParts[levelIndex].trim());
                String completionTime = csvParts[completionTimeIndex].trim();

                GameRecord record = new GameRecord(name, continent, score, collectables, level, completionTime);
               addRecord(record);
               }
             scanner.close();

    }

    /**
     * Retrieves a list of names from the tree passed to the constructor.
     * The records should be ordered by the record's level, and fall within
     * the specified range of level values.  This level range will
     * also be used by future calls to filterRecords and getTopTen.
     *
     * If a completion time filter has been set using the filterRecords method
     * below, then only records that pass that filter should be included in the
     * list of names returned by this method.
     *
     * When null is passed as either the low or high argument to this method,
     * that end of the range is understood to be unbounded.  For example, a
     * null argument for the high parameter means that there is no maximum
     * level to include in the returned list.
     *
     * @param low is the minimum level of records in the returned list
     * @param high is the maximum level of records in the returned list
     * @return List of names for all records from low to high that pass any
     * set filter, or an empty list when no such records can be found
     */
     @Override
public List<String> getAndSetRange(Integer low, Integer high) {
    this.lowLevel = low;
    this.highLevel = high;

    if (low != null) {
        tree.setIteratorMin(new GameRecord("", null, 0, 0, low, ""));
    } else {
        tree.setIteratorMin(null);
    }
    if (high != null) {
        tree.setIteratorMax(new GameRecord("", null, 0, 0, high, ""));
    } else {
        tree.setIteratorMax(null);
    }

    List<String> pplNames = new ArrayList<>();
    for (GameRecord gameRecord : tree) {
        // Check both Level Range AND Time Filter
        boolean inLevelRange = (low == null || gameRecord.getLevel() >= low) &&
                               (high == null || gameRecord.getLevel() <= high);

        // converts time to seconds for correct numerical comparison
        // fixes bug where "10:00:00".compareTo("2:00:00") gives wrong result
        boolean passesTime;
        if (timeFilter == null) {
            passesTime = true;
        } else {
            String[] recordTimeParts = gameRecord.getCompletionTime().split(":");
            long recordSeconds = Long.parseLong(recordTimeParts[0]) * 3600 +
                                 Long.parseLong(recordTimeParts[1]) * 60 +
                                 Long.parseLong(recordTimeParts[2]);

            String[] filterTimeParts = timeFilter.split(":");
            long filterSeconds = Long.parseLong(filterTimeParts[0]) * 3600 +
                                 Long.parseLong(filterTimeParts[1]) * 60 +
                                 Long.parseLong(filterTimeParts[2]);

            passesTime = recordSeconds < filterSeconds;
        }

        if (inLevelRange && passesTime) {
            pplNames.add(gameRecord.getName());
        }
    }
    return pplNames;
}

    /**
     * Retrieves a list of record names that have a completion time that is smaller than the specified
     * completion time.
     * Similar to the getRange method: this list of record names should be ordered by the records'
     * level, and should only include records that fall within the specified
     * range of level values that was established by the most recent call
     * to getRange.  If getRange has not previously been called, then no low
     * or high level bound should be used.  The filter set by this method
     * will be used by future calls to the getRange and getTopTen methods.
     *
     * When null is passed as the completion time to this method, then no
     * completion time filter should be used.  This clears the filter.
     *
     * @param completion time filters returned record names to only include records that
     *     have a completion time that are smaller than the specified value. Formatted hhh:mm:ss
     * @return List of names for records that meet this filter requirement and
     *     are within any previously set level range, or an empty list
     *     when no such records can be found
     */
    @Override
    public List<String> applyAndSetFilter(String time) {
        this.timeFilter = time; //update timefilter (what gets used in loop)
        return getAndSetRange(this.lowLevel, this.highLevel); //pass saved low + high levels
    }

    /**
     * This method returns a list of record names representing the top
     * ten records with the most collectables that both fall within any attribute range specified
     * by the most recent call to getRange, and conform to any filter set by
     * the most recent call to filteredRecords.  The order of the record names
     * in this returned list is up to you.
     *
     * If fewer than ten such records exist, return all of them.  And return an
     * empty list when there are no such records.
     *
     * @return List of ten record names with the most collectables
     */

    @Override
public List<String> getTopTen() {
    List<GameRecord> allFiltered = new ArrayList<>();

    for (GameRecord gameRecord : tree) {
        //Must check saved levels and time filter here too!
        boolean inLevelRange = (this.lowLevel == null || gameRecord.getLevel() >= this.lowLevel) &&
                               (this.highLevel == null || gameRecord.getLevel() <= this.highLevel);

        // converts time to seconds for correct numerical comparison
        // fixes bug where "10:00:00".compareTo("2:00:00") gives wrong result
        boolean passesTime;
        if (timeFilter == null) {
            passesTime = true;
        } else {
            String[] recordTimeParts = gameRecord.getCompletionTime().split(":");
            long recordSeconds = Long.parseLong(recordTimeParts[0]) * 3600 +
                                 Long.parseLong(recordTimeParts[1]) * 60 +
                                 Long.parseLong(recordTimeParts[2]);

            String[] filterTimeParts = timeFilter.split(":");
            long filterSeconds = Long.parseLong(filterTimeParts[0]) * 3600 +
                                 Long.parseLong(filterTimeParts[1]) * 60 +
                                 Long.parseLong(filterTimeParts[2]);

            passesTime = recordSeconds < filterSeconds;
        }

        if (inLevelRange && passesTime) {
            allFiltered.add(gameRecord);
        }
    }

    int picks;
    if (allFiltered.size() < 10) {
        picks = allFiltered.size(); // less than 10 records, take all of them
    } else {
        picks = 10; // more than 10 records, cap at 10
    }

    List<String> topTenNames = new ArrayList<>();

    for (int i = 0; i < picks; i++) {
        int maxIdx = 0;
        for (int j = 1; j < allFiltered.size(); j++) {
            if (allFiltered.get(j).getCollectables() > allFiltered.get(maxIdx).getCollectables()) {
                maxIdx = j;
            }
        }
        topTenNames.add(allFiltered.get(maxIdx).getName());
        allFiltered.remove(maxIdx); // shrinks list by 1 each iteration
    }
    return topTenNames;
}
}





















package Run;

import Run.ui.SortResultUI;
import SortingAlgorithm.InsertionSort;
import SortingAlgorithm.MergeSort;
import SortingAlgorithm.QuickSort;
import SortingAlgorithm.Sorter;
import DataLoader.SortDataLoader;
import java.util.*;

//Main entry point for Task C as described in CW3 specification
public class TaskCRun {

    private static final String[] FILE_NAMES = {
            "1000places_sorted.csv",
            "1000places_random.csv",
            "10000places_sorted.csv",
            "10000places_random.csv"
    };

    private static long measureSortTime(Sorter sorter, List<String> data) {
        int trials = 5;
        long totalTime = 0L;
        for (int i = 0; i < trials; i++) {
            List<String> copy = new ArrayList<>(data);
            long start = System.nanoTime();
            sorter.sort(copy);
            long end = System.nanoTime();
            if (i > 0) totalTime += (end - start);
        }
        return totalTime / trials;
    }

    public static void main(String[] args) {
        // 1. instance of sort algorithm
        Sorter insertionSorter = new InsertionSort();
        Sorter quickSorter     = new QuickSort();
        Sorter mergeSorter     = new MergeSort();

        // 2. The map used for the UI must be put() in the loop; otherwise, it will be empty later!
        Map<String,long[]> resultMap = new LinkedHashMap<>();

        // 3. Print the console header
        System.out.println("\t\tDataset\t\t\t\t\tInsertion (ns)\tQuick (ns)\tMerge (ns)");

        // 4. Measure and fill in the resultMap
        for (String fileName : FILE_NAMES) {
            List<String> data = SortDataLoader.load(fileName);
            if (data == null || data.isEmpty()) {
                System.out.println(fileName + ": Failed to read or empty file.");
                continue;
            }

            long insertionTime = measureSortTime(insertionSorter, data);
            long quickTime     = measureSortTime(quickSorter, data);
            long mergeTime     = measureSortTime(mergeSorter, data);

            // Key point: Put the result into the map
            resultMap.put(fileName, new long[]{insertionTime, quickTime, mergeTime});

            // Console output
            System.out.printf("%-24s\t%12d\t%12d\t%12d\n",
                    fileName, insertionTime, quickTime, mergeTime);
        }

        // 5. Pop up a visual window
        SortResultUI.showUI(resultMap);
    }
}

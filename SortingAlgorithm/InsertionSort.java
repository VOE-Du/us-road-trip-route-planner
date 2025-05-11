package SortingAlgorithm;

import java.util.ArrayList;
import java.util.List;

//Implement the insertion sort algorithm
public class InsertionSort implements Sorter {

    @Override
    public List<String> sort(List<String> data) {
        // To avoid modifying the original list, make a copy first
        List<String> sorted = new ArrayList<>(data);
        for (int i = 1; i < sorted.size(); i++) {
            String key = sorted.get(i);
            int j = i - 1;
            // Move the element larger than the key back by one position
            while (j >= 0 && sorted.get(j).compareTo(key) > 0) {
                sorted.set(j + 1, sorted.get(j));
                j--;
            }
            // Enter the slot position
            sorted.set(j + 1, key);
        }
        return sorted;
    }
}

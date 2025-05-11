package SortingAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Implement the quick sort algorithm
public class QuickSort implements Sorter {

    @Override
    public List<String> sort(List<String> data) {
        // Copy the data to ensure that the original input is not modified
        List<String> sorted = new ArrayList<>(data);
        quickSort(sorted, 0, sorted.size() - 1);
        return sorted;
    }

    private void quickSort(List<String> arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    private int partition(List<String> arr, int low, int high) {
        // Select the last element as the pivot
        String pivot = arr.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr.get(j).compareTo(pivot) <= 0) {
                i++;
                Collections.swap(arr, i, j);
            }
        }
        Collections.swap(arr, i + 1, high);
        return i + 1;
    }
}

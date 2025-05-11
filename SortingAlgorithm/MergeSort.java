package SortingAlgorithm;

import java.util.ArrayList;
import java.util.List;

//Implement the merge sort algorithm
public class MergeSort implements Sorter {

    @Override
    public List<String> sort(List<String> data) {
        // Copy a copy of the data for merge and sort
        List<String> sorted = new ArrayList<>(data);
        mergeSort(sorted, 0, sorted.size() - 1);
        return sorted;
    }

    private void mergeSort(List<String> arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private void merge(List<String> arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        // Temporary array
        String[] L = new String[n1];
        String[] R = new String[n2];

        // Copy Database
        for (int i = 0; i < n1; i++) {
            L[i] = arr.get(left + i);
        }
        for (int j = 0; j < n2; j++) {
            R[j] = arr.get(mid + 1 + j);
        }

        // Merge back to the original array
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i].compareTo(R[j]) <= 0) {
                arr.set(k++, L[i++]);
            } else {
                arr.set(k++, R[j++]);
            }
        }

        // Merge back to the original array
        while (i < n1) {
            arr.set(k++, L[i++]);
        }
        while (j < n2) {
            arr.set(k++, R[j++]);
        }
    }
}

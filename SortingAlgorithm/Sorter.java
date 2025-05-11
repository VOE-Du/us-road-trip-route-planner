package SortingAlgorithm;
import java.util.List;
/*
The Sorter interface: Exposes a unified sort method to the outside
It is convenient to flexibly switch between different sorting implementations in the code
*/
public interface Sorter {
    List<String> sort(List<String> data);
}

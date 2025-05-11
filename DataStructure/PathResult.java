package DataStructure;

import java.util.*;

// DataStructure.PathResult class to store the path and the total distance of the route
public class PathResult {
    public List<String> path;
    public int totalDistance;

    public PathResult(List<String> path, int totalDistance) {
        this.path = path;
        this.totalDistance = totalDistance;
    }

    @Override
    public String toString() {
        return "Optimal Route: " + path + "\nTotal Distance: " + totalDistance + " miles";
    }
}


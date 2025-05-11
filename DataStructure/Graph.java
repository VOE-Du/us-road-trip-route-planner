package DataStructure;
import java.util.*;

// DataStructure.Graph class to store roads and cities
public class Graph {
    private Map<String, List<Road>> adjacencyList = new HashMap<>();
    private Map<String, Map<String, Integer>> distanceMap = new HashMap<>();

    // Add a road between two cities (undirected graph)
    public void addRoad(String cityA, String cityB, int distance) {
        // adjacency list
        adjacencyList.computeIfAbsent(cityA, k -> new ArrayList<>()).add(new Road(cityA, cityB, distance));
        adjacencyList.computeIfAbsent(cityB, k -> new ArrayList<>()).add(new Road(cityB, cityA, distance));

        // Distance hash table
        distanceMap.computeIfAbsent(cityA, k -> new HashMap<>()).put(cityB, distance);
        distanceMap.computeIfAbsent(cityB, k -> new HashMap<>()).put(cityA, distance);
    }

    // Get all roads (neighbors) of a city
    public List<Road> getNeighbours(String city) {
        return adjacencyList.getOrDefault(city, new ArrayList<>());
    }

    // Get the distance between two cities
    public int getDistance(String cityA, String cityB) {
        return distanceMap.getOrDefault(cityA, Collections.emptyMap()).getOrDefault(cityB, Integer.MAX_VALUE);
    }

    // Provide methods for obtaining all cities for the Floyd-Warshall algorithm
    public Set<String> getVertices() {
        return adjacencyList.keySet();
    }

    // Determine whether there is a negative weight edge
    public boolean hasNegativeEdge() {
        for (Map<String, Integer> m : distanceMap.values()) {
            for (int d : m.values()) {
                if (d < 0) return true;
            }
        }
        return false;
    }
}

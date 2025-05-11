package ShortestPathAlgorithm;

import DataStructure.*;
import java.util.*;

// Bellman Ford Algorithm
public class BellmanFordAlgorithm extends AbstractShortestPathAlgorithm {
    // Call the constructor of the abstract parent class
    public BellmanFordAlgorithm(Graph graph) {
        super(graph);
    }

    @Override
    public PathResult findShortestPath(String start, String end) {
        if (!graph.getVertices().contains(start) || !graph.getVertices().contains(end)) {
            return new PathResult(Collections.emptyList(), Integer.MAX_VALUE);
        }

        Set<String> vertices = graph.getVertices();
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> predecessor = new HashMap<>();
        for (String v : vertices) {
            distance.put(v, Integer.MAX_VALUE);
            predecessor.put(v, null);
        }
        distance.put(start, 0);

        List<Road> edges = new ArrayList<>();
        for (String u : vertices) {
            edges.addAll(graph.getNeighbours(u));
        }

        int V = vertices.size();
        for (int i = 1; i < V; i++) {
            for (Road edge : edges) {
                String u = edge.cityA;
                String v = edge.cityB;
                int w = edge.distance;

                if (distance.get(u) != Integer.MAX_VALUE && distance.get(u) + w < distance.get(v)) {
                    distance.put(v, distance.get(u) + w);
                    predecessor.put(v, u);
                }
                if (distance.get(v) != Integer.MAX_VALUE && distance.get(v) + w < distance.get(u)) {
                    distance.put(u, distance.get(v) + w);
                    predecessor.put(u, v);
                }
            }
        }

        // Check the negative weight loop
        for (Road edge : edges) {
            String u = edge.cityA;
            String v = edge.cityB;
            int w = edge.distance;

            if (distance.get(u) != Integer.MAX_VALUE && distance.get(u) + w < distance.get(v)) {
                System.out.println("Warning: Negative-weight cycle detected.");
                break;
            }
        }

        if (distance.get(end) == Integer.MAX_VALUE) {
            return new PathResult(Collections.emptyList(), Integer.MAX_VALUE);
        }

        List<String> path = new ArrayList<>();
        for (String at = end; at != null; at = predecessor.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        return new PathResult(path, distance.get(end));
    }

}

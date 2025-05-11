package ShortestPathAlgorithm;

import DataStructure.*;
import java.util.*;

//Dijkstra Algorithm
public class DijkstraAlgorithm extends AbstractShortestPathAlgorithm {
    // Call the constructor of the abstract parent class
    public DijkstraAlgorithm(Graph graph) {
        super(graph);
    }

    @Override
    public PathResult findShortestPath(String start, String end) {
        if (!graph.getVertices().contains(start) || !graph.getVertices().contains(end)) {
            return new PathResult(Collections.emptyList(), Integer.MAX_VALUE);
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();

        class Node {
            String city;
            int dist;
            Node(String city, int dist) {
                this.city = city;
                this.dist = dist;
            }
        }

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.dist));
        distances.put(start, 0);
        pq.add(new Node(start, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            String city = current.city;

            if (visited.contains(city)) continue;
            visited.add(city);

            if (city.equals(end)) break;

            for (Road road : graph.getNeighbours(city)) {
                String neighbor = road.cityA.equals(city) ? road.cityB : road.cityA;
                int newDist = distances.get(city) + road.distance;

                if (newDist < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, city);
                    pq.add(new Node(neighbor, newDist));
                }
            }
        }

        if (!distances.containsKey(end)) {
            return new PathResult(Collections.emptyList(), Integer.MAX_VALUE);
        }

        List<String> path = new ArrayList<>();
        for (String city = end; city != null; city = previous.get(city)) {
            path.add(city);
        }
        Collections.reverse(path);

        return new PathResult(path, distances.get(end));
    }

}

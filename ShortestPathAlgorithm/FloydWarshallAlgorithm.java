package ShortestPathAlgorithm;

import DataStructure.*;
import java.util.*;

//Floyd Warshall Algorithm
public class FloydWarshallAlgorithm extends AbstractShortestPathAlgorithm {
    private Map<String, Map<String, Integer>> dist;
    private Map<String, Map<String, String>> next;

    public FloydWarshallAlgorithm(Graph graph) {
        super(graph);
        computeAllPairsShortestPath();
    }

    private void computeAllPairsShortestPath() {
        Set<String> verticesSet = graph.getVertices();
        List<String> vertices = new ArrayList<>(verticesSet);  // 稳定遍历顺序

        dist = new HashMap<>();
        next = new HashMap<>();

        // Initialize dist and next
        for (String u : vertices) {
            dist.put(u, new HashMap<>());
            next.put(u, new HashMap<>());
            for (String v : vertices) {
                if (u.equals(v)) {
                    dist.get(u).put(v, 0);
                } else {
                    dist.get(u).put(v, Integer.MAX_VALUE);
                }
                next.get(u).put(v, null);
            }
        }

        // The initial distance of the added edge
        for (String u : vertices) {
            for (Road road : graph.getNeighbours(u)) {
                String v = road.cityA.equals(u) ? road.cityB : road.cityA;
                int d = road.distance;
                if (d < dist.get(u).get(v)) {
                    dist.get(u).put(v, d);
                    next.get(u).put(v, v);
                }
            }
        }

        // The core Floyd-Warshall triple cycle
        for (String k : vertices) {
            for (String i : vertices) {
                Integer ik = dist.get(i).get(k);
                if (ik == Integer.MAX_VALUE) continue;
                for (String j : vertices) {
                    Integer kj = dist.get(k).get(j);
                    if (kj == Integer.MAX_VALUE) continue;
                    int newDist = ik + kj;
                    if (newDist < dist.get(i).get(j)) {
                        dist.get(i).put(j, newDist);
                        next.get(i).put(j, next.get(i).get(k));
                    }
                }
            }
        }
    }

    private List<String> reconstructPath(String start, String end) {
        if (next.get(start).get(end) == null) {
            return Collections.emptyList();
        }
        List<String> path = new ArrayList<>();
        String current = start;
        path.add(current);
        while (!current.equals(end)) {
            current = next.get(current).get(end);
            path.add(current);
        }
        return path;
    }

    @Override
    public PathResult findShortestPath(String start, String end) {
        if (!dist.containsKey(start) || !dist.get(start).containsKey(end)) {
            return new PathResult(Collections.emptyList(), Integer.MAX_VALUE);
        }

        int distance = dist.get(start).get(end);
        if (distance == Integer.MAX_VALUE) {
            return new PathResult(Collections.emptyList(), Integer.MAX_VALUE);
        }

        List<String> path = reconstructPath(start, end);
        return new PathResult(path, distance);
    }
}

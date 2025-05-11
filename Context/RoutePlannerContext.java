package Context;
import DataStructure.*;
import RouteGenerationStrategy.*;
import ShortestPathAlgorithm.*;
import java.util.*;

//Context controller, coordinating the shortest path algorithm with the route planning strategy
public class RoutePlannerContext {
    private final Graph graph;
    private final Map<String, String> attractionMap;
    private final ShortestPathAlgorithmInterface algorithm;
    private final RoutePlannerStrategyInterface strategy;

    public RoutePlannerContext(Graph graph, Map<String, String> attractionMap, ShortestPathAlgorithmInterface algorithm, RoutePlannerStrategyInterface strategy) {
        this.graph = graph;
        this.attractionMap = attractionMap;
        this.algorithm = algorithm;
        this.strategy = strategy;
    }

    /*
        Only return the list of city names passed through in sequence
     */
    public List<String> route(String startingCity, String endingCity, List<String> attractions) {
        List<String> cities = new ArrayList<>();
        for (String attr : attractions) {
            String city = attractionMap.get(attr);
            if (city == null) {
                return Collections.emptyList();
            }
            cities.add(city);
        }
        PathResult res = strategy.planRoute(startingCity, endingCity, cities, graph, algorithm);
        return (res == null || res.path == null) ? Collections.emptyList() : res.path;
    }

    /*
        Return the complete result (path + total distance)
     */
    public PathResult execute(String start, String end, List<String> attractions) {
        // 1. Get the path first
        List<String> path = route(start, end, attractions);
        if (path.isEmpty()) {
            // There is no way out
            return new PathResult(Collections.emptyList(), Integer.MAX_VALUE);
        }
        // 2. Calculate the total distance
        int totalDist = 0;
        for (int i = 1; i < path.size(); i++) {
            totalDist += graph.getDistance(path.get(i - 1), path.get(i));
        }
        // 3. Package and return
        return new PathResult(path, totalDist);
    }
}

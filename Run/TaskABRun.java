package Run;

import DataStructure.Graph;
import DataStructure.PathResult;
import DataLoader.RoutePlannerDataLoader;
import RouteGenerationStrategy.BruteForceRoutePlanner;
import RouteGenerationStrategy.DynamicProgrammingRoutePlanner;
import RouteGenerationStrategy.RoutePlannerStrategyInterface;
import ShortestPathAlgorithm.BellmanFordAlgorithm;
import ShortestPathAlgorithm.DijkstraAlgorithm;
import ShortestPathAlgorithm.FloydWarshallAlgorithm;
import ShortestPathAlgorithm.ShortestPathAlgorithmInterface;
import Context.RoutePlannerContext;
import Run.ui.ShortestRouteUI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class TaskABRun {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        RoutePlannerDataLoader planner = new RoutePlannerDataLoader();

        // 1. load data
        InputStream roadsStream = TaskABRun.class.getClassLoader().getResourceAsStream("roads.csv");
        if (roadsStream == null) {
            throw new FileNotFoundException("roads.csv not found on classpath");
        }
        planner.loadRoads(roadsStream);

        InputStream attrStream = TaskABRun.class.getClassLoader().getResourceAsStream("attractions.csv");
        if (attrStream != null) {
            planner.loadAttractions(attrStream);
        }

        Graph graph = planner.getGraph();

        // 2. Read user input
        System.out.print("Enter starting city (e.g., New York NY): ");
        String startCity = scanner.nextLine().trim();
        if (!graph.getVertices().contains(startCity)) {
            System.err.println("Error: Starting city not found.");
            System.exit(1);
        }

        System.out.print("Enter destination city (e.g., Chicago IL): ");
        String endCity = scanner.nextLine().trim();
        if (!graph.getVertices().contains(endCity)) {
            System.err.println("Error: Destination city not found.");
            System.exit(1);
        }

        System.out.print("Enter attractions (comma-separated, e.g., Hollywood Sign): ");
        String attractionsInput = scanner.nextLine();
        List<String> attractions = attractionsInput.trim().isEmpty()
                ? new ArrayList<>()
                : Arrays.asList(attractionsInput.split("\\s*,\\s*"));
        // Verify the legality of the scenic spot
        Set<String> validAttractions = planner.getAttractionMap().keySet();
        for (String attr : attractions) {
            if (!validAttractions.contains(attr)) {
                throw new IllegalArgumentException("Error: Unknown attraction: " + attr);
            }
        }
        // If the city where the scenic spot is located is the same as the starting or ending point, it will be removed from the list
        Map<String, String> attractionMap = planner.getAttractionMap();
        List<String> filtered = new ArrayList<>();
        for (String attr : attractions) {
            String loc = attractionMap.get(attr);
            if (loc.equals(startCity) || loc.equals(endCity)) {
                //System.out.println("Note: Removing \"" + attr + "\" since it is in " + loc);
            } else {
                filtered.add(attr);
            }
        }
        List<String> resultAttractions = attractions;
        attractions = filtered;  // Update the list of attractions

        // 3. Automatic selection algorithm & strategy
        boolean hasAttr  = !attractions.isEmpty();
        boolean hasNeg   = graph.hasNegativeEdge();
        int     attrCnt  = attractions.size();

        ShortestPathAlgorithmInterface algorithm;
        String algoName;
        if (!hasAttr) {
            // No scenic spots → Single query
            if (hasNeg) {
                algorithm = new BellmanFordAlgorithm(graph);
                algoName = "Bellman-Ford";
            } else {
                algorithm = new DijkstraAlgorithm(graph);
                algoName = "Dijkstra";
            }
        } else {
            // No scenic spots → Single query
            if (hasNeg) {
                algorithm = new BellmanFordAlgorithm(graph);
                algoName = "Bellman-Ford";
            } else if (attrCnt <= 2) {
                algorithm = new DijkstraAlgorithm(graph);
                algoName = "Dijkstra";
            } else {
                algorithm = new FloydWarshallAlgorithm(graph);
                algoName = "Floyd-Warshall";
            }
        }

        RoutePlannerStrategyInterface strategy;
        String stratName;
        if (hasAttr) {
            strategy  = new DynamicProgrammingRoutePlanner();
            stratName = "Dynamic Programming";
        } else {
            strategy  = new BruteForceRoutePlanner();
            stratName = "Brute Force";
        }

        // 4. Calculate and present
        RoutePlannerContext context = new RoutePlannerContext(graph, planner.getAttractionMap(), algorithm, strategy);
        PathResult result = context.execute(startCity, endCity, attractions);

        // 5. Pop up a visual window
        if (result != null) {
            new ShortestRouteUI(result, graph, algoName, stratName, startCity, endCity, resultAttractions);
        }

        // 6. console output
        System.out.println("\nResult:");
        System.out.println("Start: " + startCity);
        System.out.println("Destination: " + endCity);
        System.out.println("Attractions: " + resultAttractions);
        System.out.println(result);

        scanner.close();
    }
}

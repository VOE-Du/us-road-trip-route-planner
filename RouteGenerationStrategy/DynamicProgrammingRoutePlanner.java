package RouteGenerationStrategy;

import DataStructure.*;
import ShortestPathAlgorithm.*;
import java.util.*;

// Route planning strategy based on dynamic programming (solving the problem of fixed starting and ending points + multiple intermediate scenic spots)
public class DynamicProgrammingRoutePlanner implements RoutePlannerStrategyInterface {
    @Override
    public PathResult planRoute(String start, String end, List<String> attractions, Graph graph, ShortestPathAlgorithmInterface pathAlgorithm) {
        // If there are no scenic spots, take the shortest route directly from the starting point to the destination
        if (attractions == null || attractions.isEmpty()) {
            return pathAlgorithm.findShortestPath(start, end);
        }

        int n = attractions.size();

        // Estimated calculation: starting point -> the shortest path and distance for each scenic spot
        int[] distStart = new int[n];
        List<List<String>> pathStart = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            PathResult res = pathAlgorithm.findShortestPath(start, attractions.get(i));
            distStart[i] = res.totalDistance;
            pathStart.add(res.path);
        }

        // Estimation: The shortest path and distance between scenic spots
        int[][] distMatrix = new int[n][n];
        // Store the complete paths between each pair of scenic spots simultaneously (for the final reconstruction of the complete route)
        List<List<List<String>>> pathMatrix = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            pathMatrix.add(new ArrayList<>());
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distMatrix[i][j] = 0;
                    // The path from oneself to oneself only includes oneself
                    pathMatrix.get(i).add(new ArrayList<>(Arrays.asList(attractions.get(i))));
                } else {
                    PathResult res = pathAlgorithm.findShortestPath(attractions.get(i), attractions.get(j));
                    distMatrix[i][j] = res.totalDistance;
                    pathMatrix.get(i).add(res.path);
                }
            }
        }

        // Estimated calculation: The shortest path and distance for each scenic spot to the destination
        int[] distEnd = new int[n];
        List<List<String>> pathEnd = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            PathResult res = pathAlgorithm.findShortestPath(attractions.get(i), end);
            distEnd[i] = res.totalDistance;
            pathEnd.add(res.path);
        }

        // Status dp[mask][i] represents the collection of scenic spots represented by mask starting from the starting point.
        //The shortest cumulative distance to finally reach the scenic spot corresponding to index i.
        int N = 1 << n;
        int[][] dp = new int[N][n];
        int[][] parent = new int[N][n];
        for (int mask = 0; mask < N; mask++) {
            Arrays.fill(dp[mask], Integer.MAX_VALUE);
            Arrays.fill(parent[mask], -1);
        }

        // Initialization: The situation where only one scenic spot is visited
        for (int i = 0; i < n; i++) {
            dp[1 << i][i] = distStart[i];
        }

        // DP recursion: Traverse all states. For the last attraction i in the current mask, attempt to transfer to the unvisited attraction j
        for (int mask = 0; mask < N; mask++) {
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) == 0) continue;  // If the current mask does not contain "i", skip it
                if (dp[mask][i] == Integer.MAX_VALUE) continue;
                // Try to add the unvisited attraction j
                for (int j = 0; j < n; j++) {
                    if ((mask & (1 << j)) != 0) continue;  // I have already visited j
                    int nextMask = mask | (1 << j);
                    if (distMatrix[i][j] == Integer.MAX_VALUE) continue;
                    int newDist = dp[mask][i] + distMatrix[i][j];
                    if (newDist < dp[nextMask][j]) {
                        dp[nextMask][j] = newDist;
                        parent[nextMask][j] = i;
                    }
                }
            }
        }

        // The final answer: Among all the states where all the scenic spots have been visited, the solution that minimizes the total sum by adding that distance from the last scenic spot to the destination
        int best = Integer.MAX_VALUE;
        int last = -1;
        int fullMask = N - 1;
        for (int i = 0; i < n; i++) {
            if (dp[fullMask][i] == Integer.MAX_VALUE || distEnd[i] == Integer.MAX_VALUE) continue;
            int total = dp[fullMask][i] + distEnd[i];
            if (total < best) {
                best = total;
                last = i;
            }
        }

        if (best == Integer.MAX_VALUE) {
            return new PathResult(Collections.emptyList(), Integer.MAX_VALUE);
        }

        // Refactor the access order (index order) based on the parent array
        List<Integer> order = new ArrayList<>();
        int mask = fullMask;
        int cur = last;
        while (cur != -1) {
            order.add(cur);
            int temp = parent[mask][cur];
            mask = mask & ~(1 << cur);
            cur = temp;
        }
        Collections.reverse(order);

        // Reconstruct the complete route: Starting point -> First scenic spot ->... -> The last scenic spot -> the finish line
        List<String> fullPath = new ArrayList<>();
        // From the starting point to the first scenic spot
        fullPath.addAll(pathStart.get(order.get(0)));
        // The paths between the middle scenic spots (Note to avoid adding adjacent nodes repeatedly)
        for (int i = 0; i < order.size() - 1; i++) {
            int u = order.get(i);
            int v = order.get(i + 1);
            List<String> segment = pathMatrix.get(u).get(v);
            // Remove the repetitive starting points (there is already the ending of the previous paragraph)
            fullPath.addAll(segment.subList(1, segment.size()));
        }
        // The last scenic spot is the finish line
        List<String> lastSegment = pathEnd.get(order.get(order.size() - 1));
        fullPath.addAll(lastSegment.subList(1, lastSegment.size()));

        return new PathResult(fullPath, best);
    }
}

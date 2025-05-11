package RouteGenerationStrategy;

import DataStructure.Graph;
import DataStructure.PathResult;
import ShortestPathAlgorithm.ShortestPathAlgorithmInterface;
import java.util.*;
/*
    Brute-force route planner
 */
public class BruteForceRoutePlanner implements RoutePlannerStrategyInterface {

    @Override
    public PathResult planRoute(String start, String end, List<String> attractions, Graph graph, ShortestPathAlgorithmInterface pathAlgorithm) {
        //If there are no scenic spots, calculate directly from the starting point to the destination
        if (attractions == null || attractions.isEmpty()) {
            return pathAlgorithm.findShortestPath(start, end);
        }

        //List the order of visiting scenic spots
        List<List<String>> permutations = new ArrayList<>();
        permute(attractions, 0, permutations);

        PathResult bestResult = null;

        for (List<String> order : permutations) {
            int totalDist = 0;
            List<String> fullPath = new ArrayList<>();
            String cur = start;
            fullPath.add(cur);
            boolean valid = true;

            //Starting Point → Each Scenic Spot
            for (String next : order) {
                PathResult segment = pathAlgorithm.findShortestPath(cur, next);
                if (segment.totalDistance == Integer.MAX_VALUE) {
                    valid = false;
                    break;
                }
                fullPath.addAll(segment.path.subList(1, segment.path.size()));
                totalDist += segment.totalDistance;
                cur = next;
            }
            if (!valid) continue;

            //The final scenic spot → the finish line
            PathResult last = pathAlgorithm.findShortestPath(cur, end);
            if (last.totalDistance == Integer.MAX_VALUE) continue;

            fullPath.addAll(last.path.subList(1, last.path.size()));
            totalDist += last.totalDistance;

            //Update the optimal solution
            if (bestResult == null || totalDist < bestResult.totalDistance) {
                bestResult = new PathResult(fullPath, totalDist);
            }
        }
        return bestResult;
    }

    //Recursively generate the full permutation of the list
    private void permute(List<String> list, int idx, List<List<String>> res) {
        if (idx >= list.size()) {
            res.add(new ArrayList<>(list));
            return;
        }
        for (int i = idx; i < list.size(); i++) {
            Collections.swap(list, idx, i);
            permute(list, idx + 1, res);
            Collections.swap(list, idx, i);
        }
    }
}

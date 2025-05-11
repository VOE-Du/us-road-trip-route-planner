package RouteGenerationStrategy;

import DataStructure.Graph;
import DataStructure.PathResult;
import ShortestPathAlgorithm.ShortestPathAlgorithmInterface;
import java.util.*;

//Route Planner Strategy interface
public interface RoutePlannerStrategyInterface {
    PathResult planRoute(String start,
                         String end,
                         List<String> attractions,
                         Graph graph,
                         ShortestPathAlgorithmInterface pathAlgorithm);
}




package ShortestPathAlgorithm;

import DataStructure.Graph;
import DataStructure.PathResult;

//Abstract base class of the shortest path algorithm
public abstract class AbstractShortestPathAlgorithm implements ShortestPathAlgorithmInterface {
    protected Graph graph;

    public AbstractShortestPathAlgorithm(Graph graph) {
        this.graph = graph;
    }

    @Override
    public abstract PathResult findShortestPath(String start, String end);
}

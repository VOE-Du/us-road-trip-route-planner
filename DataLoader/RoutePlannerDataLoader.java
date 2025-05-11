package DataLoader;
import DataStructure.*;
import java.io.*;
import java.util.*;

//Be responsible for loading road information and scenic spot data from CSV files
public class RoutePlannerDataLoader {
    private Graph graph = new Graph();
    private Map<String, String> attractionMap = new HashMap<>();

    public void loadRoads(InputStream roadsInput) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(roadsInput))) {
            String line;
            if ((line = reader.readLine()) != null && !line.contains("CityA")) {
                processRoadLine(line);
            }
            while ((line = reader.readLine()) != null) {
                processRoadLine(line);
            }
        }
    }

    private void processRoadLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 3) return;
        String cityA = parts[0].trim();
        String cityB = parts[1].trim();
        int distance = Integer.parseInt(parts[2].trim());
        graph.addRoad(cityA, cityB, distance);
    }

    public void loadAttractions(InputStream attractionsInput) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(attractionsInput))) {
            String line;
            if ((line = reader.readLine()) != null && !line.contains("AttractionName")) {
                processAttractionLine(line);
            }
            while ((line = reader.readLine()) != null) {
                processAttractionLine(line);
            }
        }
    }

    private void processAttractionLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 2) return;
        String attractionName = parts[0].trim();
        String cityName = parts[1].trim();
        attractionMap.put(attractionName, cityName);
    }

    public Graph getGraph() {
        return graph;
    }

    public Map<String, String> getAttractionMap() {
        return attractionMap;
    }
}

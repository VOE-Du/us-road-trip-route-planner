package DataStructure;

// DataStructure.Road class to represent a road between two cities
public class Road {
    public String cityA, cityB;
    public int distance;

    public Road(String cityA, String cityB, int distance) {
        this.cityA = cityA;
        this.cityB = cityB;
        this.distance = distance;
    }
}

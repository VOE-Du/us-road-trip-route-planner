package DataLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Read the sorted data set
 */
public class SortDataLoader {

    public static List<String> load(String fileName) {
        List<String> data = new ArrayList<>();

        try (InputStream inputStream = SortDataLoader.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line.trim());
            }

        } catch (Exception e) {
            System.err.println("Read failed: " + fileName + " => " + e.getMessage());
            return new ArrayList<>();
        }

        return data;
    }
}

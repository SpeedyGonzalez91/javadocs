package net.javajuan.springboot;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Service
public class GraphService {
	
	public List<Frequency> getGraphData(MultipartFile csvFile) throws IOException, CsvValidationException {
        List<Frequency> graphData = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile.getInputStream()))) {
            String[] line;
            // Read the CSV file line by line
            while ((line = reader.readNext()) != null) {
                // Assuming the labels are in the first column and the frequencies are in the second column
                String label = line[0];
                int frequency = Integer.parseInt(line[1]);

                // Create a Frequency object and add it to the graph data
                Frequency frequencyObj = new Frequency(label, frequency);
                graphData.add(frequencyObj);
            }
        } catch (IOException | CsvValidationException e) {
            // Handle any IO exception or CSV validation exception
            throw e;
        }
        
        return graphData;
    }
    // ...
}

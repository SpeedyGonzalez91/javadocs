package net.javajuan.springboot.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import net.javajuan.springboot.entities.Frequency;
import net.javajuan.springboot.entities.Invitation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
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

    public byte[] retrieveFrequencyDataFromFile(MultipartFile file) {
        List<Invitation> invitationList = new ArrayList<>();
        try {
            Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_16);
            CSVParser csvParser = new CSVParserBuilder().withSeparator('\t').build();
            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withCSVParser(csvParser)
                    .build();
            String[] header = csvReader.readNext(); //we are extracting the header here
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                Invitation invitation = new Invitation();
                invitation.setKioskGroupId(line[0]);
                invitation.setLocation(line[5]);
                invitation.setReason(line[6]);
                invitation.setTimeZone(line[7]);
                invitationList.add(invitation);
            }
        } catch (Exception e) {
            //TODO: do something about this
        }
        Map<String, Long> result = invitationList.stream().collect(Collectors.groupingBy(
                Invitation::getKioskGroupId,
                Collectors.counting()
        ));
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry entry : result.entrySet()) {
            dataset.addValue((Number) entry.getValue(),"Frequency", (Comparable) entry.getKey());
        }
        JFreeChart chart = ChartFactory.createBarChart("Frequency Graph", "Category", "Frequency", dataset);
        // Convert the chart to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 400);
        } catch (Exception e) {
            //TODO: make something with me
        }
        return outputStream.toByteArray();
    }
}

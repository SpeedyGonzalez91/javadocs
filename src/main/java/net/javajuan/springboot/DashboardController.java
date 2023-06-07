package net.javajuan.springboot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import javax.servlet.http.HttpSession;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;




@Controller
public class DashboardController {
	

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("frequencyData", Collections.emptyList());
        return "dashboard";
    }
    
    @GetMapping("/frequency-table")
    public String frequencyTable(Model model) {
        List<Frequency> frequencyData = getFrequencyData(); // Replace with your actual frequency data retrieval logic
        model.addAttribute("frequencyData", frequencyData);
        return "frequency_table";
    }


    private List<Frequency> getFrequencyData() {
        List<Frequency> frequencyData = new ArrayList<>();

        // TODO: Retrieve the frequency data from your data source
        // For demonstration purposes, let's assume we have some sample data
        frequencyData.add(new Frequency("Category 1", 10));
        frequencyData.add(new Frequency("Category 2", 5));
        frequencyData.add(new Frequency("Category 3", 8));
        frequencyData.add(new Frequency("Category 4", 3));

        return frequencyData;
    }



    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        // Check if the file is empty
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload.");
            return "dashboard";
        }

        try {
            // Save the uploaded file to a temporary location
            Path tempFilePath = Files.createTempFile(null, null);
            Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);

            // Redirect to the graph page with the uploaded file information
            return "redirect:/graph?filename=" + tempFilePath.getFileName().toString();
        } catch (IOException e) {
            // Handle any IO exception
            model.addAttribute("message", "An error occurred during file upload: " + e.getMessage());
            return "dashboard";
        }
    }
    
    
    
    @PostMapping(path = "/graph", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<byte[]> displayGraph(@RequestPart() MultipartFile file) throws IOException {
        // Read the uploaded CSV file and retrieve the frequency data
        List<DataObject> frequencyList = retrieveFrequencyDataFromFile(file);

        // Generate the graph
        byte[] graphBytes = generateGraph(frequencyList);

        // Set the appropriate response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        // Return the graph image as a response
        return new ResponseEntity<>(graphBytes, headers, HttpStatus.OK);
    }

    private List<DataObject> retrieveFrequencyDataFromFile(MultipartFile file) throws IOException {
        List<DataObject> frequencyList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = reader.readNext(); // Assuming the first row contains the header
            System.out.println(header);
            String[] line;
            while ((line = reader.readNext()) != null) {
                String label = line[0]; // Assuming the label is in the first column
                System.out.println(label);
                int frequency = Integer.parseInt(line[1]); // Assuming the frequency is in the second column
            // frequencyList.add(new DataObject(label, frequency)); 
            }
        }catch(Exception e) {
        }
        
        
        return frequencyList;
    }


    
    @Autowired
    private GraphService graphService;
    @GetMapping("/graph-data")
    public List<Frequency> getGraphData(String filePath) throws IOException, CsvValidationException {
        // Logic to read the CSV file based on the provided filePath
        List<Frequency> graphData = new ArrayList<>();

        try (Reader reader = new FileReader(filePath);
             CSVReader csvReader = new CSVReaderBuilder(reader).build()) {

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String category = line[0]; // Assuming the category is in the first column
                int frequency = Integer.parseInt(line[1]); // Assuming the frequency is in the second column

                Frequency frequencyObj = new Frequency(category, frequency);
                graphData.add(frequencyObj);
            }
        }

        return graphData;
    }


    @GetMapping("/downloadGraph")
    public ResponseEntity<Resource> downloadGraph(HttpSession session) {
        // Retrieve the graph bytes from session
        byte[] graphBytes = (byte[]) session.getAttribute("graphBytes");

        // Create an InputStreamResource from the byte array
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(graphBytes));

        // Set the appropriate content type for the response
        MediaType mediaType = MediaType.IMAGE_PNG; // Assuming the graph is in PNG format

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"graph.png\"")
                .body(resource);
    }

    @GetMapping("/downloadExcel")
    public ResponseEntity<Resource> downloadExcel(HttpSession session) {
        // Retrieve the Excel file bytes from session
        byte[] excelBytes = (byte[]) session.getAttribute("excelBytes");

        // Create an InputStreamResource from the byte array
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(excelBytes));

        // Set the appropriate content type for the response
        MediaType mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"data.xlsx\"")
                .body(resource);
    }

    private List<Frequency> processCsvFile(MultipartFile file) throws IOException {
        List<Frequency> frequencyList = new ArrayList<>();

        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReaderBuilder(reader).build()) {

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                try {
                    String category = line[0]; // Assuming the category is in the first column
                    int frequency = Integer.parseInt(line[1]); // Assuming the frequency is in the second column

                    Frequency frequencyObj = new Frequency(category, frequency);
                    frequencyList.add(frequencyObj);
                } catch (NumberFormatException e) {
                    // Handle any parsing errors for the frequency value
                    // You can log the error or skip the line depending on your requirement
                    System.out.println("Invalid frequency value: " + line[1]);
                }
            }
        } catch (CsvValidationException e) {
            // Handle any CSV validation errors
            // You can log the error or handle it as per your requirement
            System.out.println("CSV validation error: " + e.getMessage());
        }

        return frequencyList;
    }


    private byte[] generateGraph(List<DataObject> dataList) throws IOException {
        // Calculate the frequencies from the data
        Map<String, Integer> labelFrequencies = new HashMap<>();

        // Iterate over the data objects
        for (DataObject data : dataList) {
            List<String> labelList = data.getLabel();

            // Iterate over the labels and update the frequency count
            for (String label : labelList) {
                labelFrequencies.put(label, labelFrequencies.getOrDefault(label, 0) + 1);
            }
        }

        // Create a list of Frequency objects from the label frequencies
        List<Frequency> frequencyList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : labelFrequencies.entrySet()) {
            String label = entry.getKey();
            int frequency = entry.getValue();
            frequencyList.add(new Frequency(label, frequency));
        }

        // Generate the graph using the frequency data
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Frequency frequency : frequencyList) {
            dataset.addValue(frequency.getFrequency(), "Frequency", frequency.getCategory());
        }

        JFreeChart chart = ChartFactory.createBarChart("Frequency Graph", "Category", "Frequency", dataset);

        // Convert the chart to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(outputStream, chart, 800, 400);
        return outputStream.toByteArray();
    }








	public void setGraphService(GraphService graphService) {
		this.graphService = graphService;
	}
}

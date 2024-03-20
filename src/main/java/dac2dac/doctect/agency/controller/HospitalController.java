package dac2dac.doctect.agency.controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HospitalController {
    public static void main(String[] args) {
        try {
            // API Endpoint
            String endpoint = "http://apis.data.go.kr/B552657/HsptlAsembySearchService/getHsptlMdcncLcinfoInqire?";

            // Your API Key (replace 'YOUR_API_KEY' with your actual API key)
            String apiKey = "oWY%2BYdEiTAhMkDZyS%2Fd%2BNoqY3DWC%2B4avf89bpSUqlFjebPiGet0kRez1aBFaIIaGmp7LrePALrS%2FtzQyfPKhBQ%3D%3D";

            // Sample parameters (you may need to adjust these according to the API documentation)
            String value1 = "127.085156592737";
            String value2 = "37.4881325624879";
            String number = "100";

            String parameters = "WGS84_LON=" + value1 + "&WGS84_LAT=" + value2 + "&numOfRows=" + number;

            // Construct the URL with parameters
            URL url = new URL(endpoint + parameters);

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Set request headers
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response
            BufferedReader reader;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            // Read the response line by line
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Print the response
            System.out.println("Response: " + response.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

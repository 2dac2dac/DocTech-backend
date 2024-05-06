package dac2dac.doctect.common.component;

import dac2dac.doctect.agency.service.HospitalService;
import dac2dac.doctect.agency.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class OpenApiDataLoader implements ApplicationRunner {

    private final PharmacyService pharmacyService;
    private final HospitalService hospitalService;
    private final WebClient webClient;

    @Value("${open-api.pharmacy.key}")
    private String PHARMACY_API_KEY;

    @Value("${open-api.pharmacy.endpoint}")
    private String PHARMACY_ENDPOINT;

    @Value("${open-api.hospital.key}")
    private String HOSPITAL_API_KEY;

    @Value("${open-api.hospital.endpoint}")
    private String HOSPITAL_ENDPOINT;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        saveAllPharmacyInfo();
        saveAllHospitalInfo();
    }

    public void saveAllPharmacyInfo() throws ParseException {
        String pharmacyInfo = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PHARMACY_ENDPOINT)
                        .queryParam("serviceKey", PHARMACY_API_KEY)
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(pharmacyInfo);
        JSONObject response = (JSONObject) jsonObject.get("response");
        JSONObject body = (JSONObject) response.get("body");

        int totalCount = ((Long) body.get("totalCount")).intValue();
        int numOfRows = ((Long) body.get("numOfRows")).intValue();
        int totalPage = (int) Math.ceil(totalCount / numOfRows);

        for (int i = 1; i <= totalPage; i++) {
            pharmacyService.savePharmacyInfo(i);
        }
    }

    public void saveAllHospitalInfo() throws ParseException {
        String hospitalInfo = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(HOSPITAL_ENDPOINT)
                        .queryParam("serviceKey", HOSPITAL_API_KEY)
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(hospitalInfo);
        JSONObject response = (JSONObject) jsonObject.get("response");
        JSONObject body = (JSONObject) response.get("body");

        int totalCount = ((Long) body.get("totalCount")).intValue();
        int numOfRows = ((Long) body.get("numOfRows")).intValue();
        int totalPage = (int) Math.ceil(totalCount / numOfRows);

        for (int i = 1; i <= totalPage; i++) {
            hospitalService.saveHospitalInfo(i);
        }
    }
}

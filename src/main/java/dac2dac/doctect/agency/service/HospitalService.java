<<<<<<< HEAD
package dac2dac.doctect.agency.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class HospitalService {

    // 병원 정보를 가져오는 메서드

    @Value("${spring.API-KEY.hospital-api-key}")
    String hospitalApiKey;

    public String getHospitalInfo() {
        try {
            // API 요청 URL 및 키
            String endpoint = "http://apis.data.go.kr/B552657/HsptlAsembySearchService/getHsptlMdcncLcinfoInqire?";

            // 요청 파라미터 설정
            String value1 = "127.085156592737";
            String value2 = "37.4881325624879";
            String number = "100";
            String parameters = "WGS84_LON=" + value1 + "&WGS84_LAT=" + value2 + "&numOfRows=" + number;

            // 요청 URL 구성
            URL url = new URL(endpoint + parameters);

            // HTTP 연결 설정
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + hospitalApiKey);

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 응답 읽기
            BufferedReader reader;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            // 응답 내용 읽기
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 응답 출력
            //System.out.println("Response: " + response.toString());
            return response.toString();

        } catch (Exception e) {
            // 오류 발생 시 처리
            e.printStackTrace();
            System.out.println("error");
            return "error";
        }
    }
}
=======
package dac2dac.doctect.agency.service;

import dac2dac.doctect.agency.entity.Hospital;
import dac2dac.doctect.agency.repository.HospitalRepository;
import dac2dac.doctect.agency.vo.HospitalItems;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class HospitalService {

    private final WebClient webClient;
    private final HospitalRepository hospitalRepository;

    @Value("${open-api.hospital.key}")
    private String HOSPITAL_API_KEY;

    @Value("${open-api.hospital.endpoint}")
    private String HOSPITAL_ENDPOINT;

    @Async
    public void saveHospitalInfo(int pageNo) {
        HospitalItems hospitalItems = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(HOSPITAL_ENDPOINT)
                        .queryParam("serviceKey", HOSPITAL_API_KEY)
                        .queryParam("_type", "json")
                        .queryParam("pageNo", pageNo)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(HospitalItems.class)
                .retry(3)
                .block();

        try {
            hospitalItems.getHospitalItems().forEach(item -> {
                Hospital hospital = item.toEntity();
                hospitalRepository.save(hospital);
            });
        } catch (DataIntegrityViolationException e) {
        }

        log.info("pageNo: {} :: hospitalItems: {}", pageNo, hospitalItems);
    }
}
>>>>>>> 00519c84d3789f1032cc4664871b6b0cc7aa9736

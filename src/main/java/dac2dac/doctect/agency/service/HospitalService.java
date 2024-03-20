package dac2dac.doctect.agency.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class HospitalService {

    // 병원 정보를 가져오는 메서드
    public String getInfo() {
        try {
            // API 요청 URL 및 키
            String endpoint = "http://apis.data.go.kr/B552657/HsptlAsembySearchService/getHsptlMdcncLcinfoInqire?";
            String apiKey = "oWY%2BYdEiTAhMkDZyS%2Fd%2BNoqY3DWC%2B4avf89bpSUqlFjebPiGet0kRez1aBFaIIaGmp7LrePALrS%2FtzQyfPKhBQ%3D%3D";

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
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

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

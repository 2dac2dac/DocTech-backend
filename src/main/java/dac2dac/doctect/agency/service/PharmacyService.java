package dac2dac.doctect.agency.service;

import dac2dac.doctect.agency.entity.Pharmacy;
import dac2dac.doctect.agency.repository.PharmacyRepository;
import dac2dac.doctect.agency.vo.PharmacyItems;
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
public class PharmacyService {

    private final WebClient webClient;
    private final PharmacyRepository pharmacyRepository;

    @Value("${open-api.pharmacy.key}")
    private String PHARMACY_API_KEY;

    @Value("${open-api.pharmacy.endpoint}")
    private String PHARMACY_ENDPOINT;

    @Async
    public void savePharmacyInfo(int pageNo) {
        PharmacyItems pharmacyItems = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PHARMACY_ENDPOINT)
                        .queryParam("serviceKey", PHARMACY_API_KEY)
                        .queryParam("_type", "json")
                        .queryParam("pageNo", pageNo)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PharmacyItems.class)
                .retry(3)
                .block();

        try {
            pharmacyItems.getPharmacyItems().forEach(item -> {
                Pharmacy pharmacy = item.toEntity();
                pharmacyRepository.save(pharmacy);
            });
        } catch (DataIntegrityViolationException e) {
        }

        log.info("pageNo: {} :: pharmacyItems: {}", pageNo, pharmacyItems);
    }

}
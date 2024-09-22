package dac2dac.doctect.agency.controller;

import dac2dac.doctect.agency.dto.request.SearchCriteria;
import dac2dac.doctect.agency.service.AgencyService;
import dac2dac.doctect.common.constant.SuccessCode;
import dac2dac.doctect.common.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "병원&약국 기관", description = "Agency API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agency")
public class AgencyController {

    private final AgencyService agencyService;

    @Operation(summary = "병원 & 약국 검색 API", description = "유저의 위치 정보(위도, 경도)를 기반으로 2km 이내 약국 & 병원 정보를 조회한다.")
    @PostMapping("/search/{userId}")
    public ApiResult searchAgency(@PathVariable Long userId, @Valid @RequestBody SearchCriteria searchCriteria) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, agencyService.searchAgency(userId, searchCriteria));
    }

    @Operation(summary = "병원 & 약국 검색 API", description = "유저의 위치 정보와 상관없이 약국 & 병원 정보를 조회한다.")
    @PostMapping("/search/v1/{userId}")
    public ApiResult findAgencyInfo(@PathVariable Long userId, @Valid @RequestBody SearchCriteria searchCriteria) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, agencyService.searchAgency(userId, searchCriteria));
    }


    @Operation(summary = "약국 상세조회 API", description = "약국의 상세 정보를 조회한다.")
    @GetMapping("/search/pharmacies/{userId}/{pharmacyId}")
    public ApiResult getDetailPharmacy(@PathVariable Long userId, @PathVariable Long pharmacyId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, agencyService.getDetailPharmacy(userId, pharmacyId));
    }

    @Operation(summary = "병원 상세조회 API", description = "병원의 상세 정보를 조회한다.")
    @GetMapping("/search/hospitals/{userId}/{hospitalId}")
    public ApiResult getDetailHospital(@PathVariable Long userId, @PathVariable Long hospitalId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, agencyService.getDetailHospital(userId, hospitalId));
    }

    @Operation(summary = "병원 리스트 조회 API", description = "모든 병원의 정보를 조회한다.")
    @GetMapping("/hospitals")
    public ApiResult getAllHospitals() {
        System.out.println("여기로 들어옴" + agencyService.getAllHospitals());
        return ApiResult.success(SuccessCode.GET_SUCCESS, agencyService.getAllHospitals());
    }


}

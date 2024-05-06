package dac2dac.doctect.agency.controller;

import dac2dac.doctect.agency.dto.request.SearchCriteria;
import dac2dac.doctect.agency.service.AgencyService;
import dac2dac.doctect.common.constant.SuccessCode;
import dac2dac.doctect.common.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    @PostMapping("/search")
    public ApiResult searchAgency(@Valid @RequestBody SearchCriteria searchCriteria) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, agencyService.searchAgency(searchCriteria));
    }

}

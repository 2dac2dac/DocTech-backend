package dac2dac.doctect.health_list.controller;

import dac2dac.doctect.common.constant.SuccessCode;
import dac2dac.doctect.common.response.ApiResult;
import dac2dac.doctect.health_list.dto.request.UserAuthenticationDto;
import dac2dac.doctect.health_list.service.HealthListService;
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

@Tag(name = "내역", description = "HealthList API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/healthList")
public class HealthListController {

    private final HealthListService healthListService;

    @Operation(summary = "마이데이터 연동 API", description = "마이데이터 서버로부터 유저의 마이데이터를 연동한다.")
    @PostMapping("/sync/{userId}")
    public ApiResult syncMydata(@Valid @RequestBody UserAuthenticationDto userAuthenticationDto, @PathVariable Long userId) {
        healthListService.syncMydata(userAuthenticationDto, userId);
        return ApiResult.success(SuccessCode.SYNC_SUCCESS);
    }

    @Operation(summary = "진료 내역(대면, 비대면) 조회 API", description = "유저의 진료 내역(대면, 비대면)을 조회한다.")
    @GetMapping("/diagnosis/{userId}")
    public ApiResult getDiagnosisList(@PathVariable Long userId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, healthListService.getDiagnosisList(userId));
    }

    @Operation(summary = "진료 내역(대면) 상세 조회 API", description = "유저의 진료 내역(대면)을 상세 조회한다.")
    @GetMapping("/diagnosis/contact/{userId}/{diagId}")
    public ApiResult getContactDiagnosis(@PathVariable Long userId, @PathVariable Long diagId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, healthListService.getDetailContactDiagnosis(userId, diagId));
    }

    @Operation(summary = "투약 내역 조회 API", description = "유저의 투약 내역을 조회한다.")
    @GetMapping("/prescription/{userId}")
    public ApiResult getPrescriptionList(@PathVariable Long userId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, healthListService.getPrescriptionList(userId));
    }

    @Operation(summary = "투약 내역 상세 조회 API", description = "유저의 투약 내역을 상세 조회한다.")
    @GetMapping("/prescription/{userId}/{prescriptionId}")
    public ApiResult getPrescription(@PathVariable Long userId, @PathVariable Long prescriptionId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, healthListService.getDetailPrescription(userId, prescriptionId));
    }

    @Operation(summary = "예방접종 내역 조회 API", description = "유저의 예방접종 내역을 조회한다.")
    @GetMapping("/vaccination/{userId}")
    public ApiResult getVaccinationList(@PathVariable Long userId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, healthListService.getVaccinationList(userId));
    }

    @Operation(summary = "예방접종 내역 상세 조회 API", description = "유저의 예방접종 내역을 상세 조회한다.")
    @GetMapping("/vaccination/{userId}/{vaccId}")
    public ApiResult getVaccination(@PathVariable Long userId, @PathVariable Long vaccId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, healthListService.getDetailVaccination(userId, vaccId));
    }

    @Operation(summary = "건강검진 내역 조회 API", description = "유저의 건강검진 내역을 조회한다.")
    @GetMapping("/healthScreening/{userId}")
    public ApiResult getHealthScreeningList(@PathVariable Long userId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, healthListService.getHealthScreeningList(userId));
    }

    @Operation(summary = "건강검진 내역 상세 조회 API", description = "유저의 건강검진 내역을 상세 조회한다.")
    @GetMapping("/healthScreening/{userId}/{hsId}")
    public ApiResult getHealthScreening(@PathVariable Long userId, @PathVariable Long hsId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, healthListService.getDetailHealthScreening(userId, hsId));
    }

}

package dac2dac.doctect.noncontact_diag.controller;

import dac2dac.doctect.common.constant.SuccessCode;
import dac2dac.doctect.common.response.ApiResult;
import dac2dac.doctect.noncontact_diag.dto.request.NoncontactDiagAppointmentRequestDto;
import dac2dac.doctect.noncontact_diag.service.NoncontactDiagService;
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

@Tag(name = "비대면진료", description = "비대면진료 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/noncontactDiag")
public class NoncontactDiagController {

    private final NoncontactDiagService noncontactDiagService;

    @Operation(summary = "진료과목 리스트 조회 API", description = "진료 과목 리스트를 조회한다.")
    @GetMapping("/departments")
    public ApiResult getDepartmentList() {
        return ApiResult.success(SuccessCode.GET_SUCCESS, noncontactDiagService.getDepartmentList());
    }

    @Operation(summary = "진료과목 별 의사 리스트 조회 API", description = "진료 과목 별 의사 리스트를 조회한다.")
    @GetMapping("/departments/{departmentId}/doctors")
    public ApiResult getDoctorList(@PathVariable Long departmentId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, noncontactDiagService.getDoctorList(departmentId));
    }

    @Operation(summary = "의사 상세 조회 API", description = "의사 정보를 상세 조회한다.")
    @GetMapping("/doctors/{doctorId}")
    public ApiResult getDoctorDetailInfo(@PathVariable Long doctorId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, noncontactDiagService.getDoctorDetailInfo(doctorId));
    }

    @Operation(summary = "비대면진료 신청 API", description = "환자가 비대면 진료를 신청한다.")
    @PostMapping("/{userId}")
    public ApiResult appointNoncontactDiag(
        @PathVariable Long userId,
        @Valid @RequestBody NoncontactDiagAppointmentRequestDto requestDto
    ) {
        noncontactDiagService.appointNoncontactDiag(userId, requestDto);
        return ApiResult.success(SuccessCode.CREATED_SUCCESS);
    }

}

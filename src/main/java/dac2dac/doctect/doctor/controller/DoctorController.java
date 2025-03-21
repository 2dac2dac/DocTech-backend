package dac2dac.doctect.doctor.controller;


import dac2dac.doctect.common.constant.SuccessCode;
import dac2dac.doctect.common.response.ApiResult;
import dac2dac.doctect.doctor.dto.request.DiagCompleteRequestDto;
import dac2dac.doctect.doctor.dto.request.RejectReservationRequest;
import dac2dac.doctect.doctor.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "(의사) 진료 예약", description = "의사 진료 예약 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/doctors/reservations")
class DoctorController {

    private final DoctorService doctorService;

    @Operation(summary = "날짜별 예약 조회 API", description = "날짜별 요청된 예약과 수락된 예약을 조회한다.")
    @GetMapping("/{doctorId}/{reservationDate}")
    public ApiResult getReservations(@PathVariable Long doctorId, @PathVariable("reservationDate") @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") String reservationDate) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, doctorService.getReservations(doctorId, reservationDate));
    }

    @Operation(summary = "예약 신청서 조회 API", description = "예약 신청서를 조회한다.")
    @GetMapping("/form/{doctorId}/{reservationId}")
    public ApiResult getReservations(@PathVariable Long doctorId, @PathVariable Long reservationId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, doctorService.getReservationForm(doctorId, reservationId));
    }

    @Operation(summary = "예약 수락 API", description = "예약을 수락한다.")
    @PostMapping("/accept/{doctorId}/{reservationId}")
    public ApiResult acceptReservation(@PathVariable Long doctorId, @PathVariable Long reservationId) {
        doctorService.acceptReservation(doctorId, reservationId);
        return ApiResult.success(SuccessCode.ACCEPT_SUCCESS);
    }

    @Operation(summary = "예약 거절 API", description = "예약을 거절한다.")
    @PostMapping("/reject/{doctorId}/{reservationId}")
    public ApiResult rejectReservation(@PathVariable Long doctorId, @PathVariable Long reservationId, @RequestBody RejectReservationRequest request) {
        doctorService.rejectReservation(doctorId, reservationId, request);
        return ApiResult.success(SuccessCode.REJECT_SUCCESS);
    }

    @Operation(summary = "예약 환자 정보 조회 API", description = "비식별화 처리된 환자 정보를 조회한다.")
    @GetMapping("/{doctorId}/{reservationId}/patientInfo")
    public ApiResult getPatientInfo(@PathVariable Long doctorId, @PathVariable Long reservationId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, doctorService.getPatientInfo(doctorId, reservationId));
    }

    @Operation(summary = "다가오는 진료 예약 조회 API", description = "메인에 보여지는 다가오는 의사의 진료 예약 정보를 조회한다.")
    @GetMapping("/{doctorId}/upcoming")
    public ApiResult getUpcomingReservation(@PathVariable Long doctorId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, doctorService.getUpcomingReservation(doctorId));
    }

    @Operation(summary = "진료 완료 API", description = "진료가 끝난 건에 대해 완료 처리한다.")
    @PostMapping("/complete/{doctorId}/{reservationId}")
    public ApiResult completeReservation(@RequestBody DiagCompleteRequestDto request, @PathVariable Long doctorId, @PathVariable Long reservationId) throws Exception {
        doctorService.completeReservation(request, doctorId, reservationId);
        return ApiResult.success(SuccessCode.PRESCRIPTION_CREATE_SUCCESS);
    }

    @Operation(summary = "오늘 진료 예약 목록 조회 API", description = "메인에 보여지는 오늘 진료 예약 목록을 조회한다.")
    @GetMapping("/{doctorId}/today")
    public ApiResult getTodayReservation(@PathVariable Long doctorId) {
        return ApiResult.success(SuccessCode.GET_SUCCESS, doctorService.getTodayReservation(doctorId));
    }

}


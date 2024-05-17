package dac2dac.doctect.health_list.service;

import dac2dac.doctect.agency.entity.Agency;
import dac2dac.doctect.agency.entity.Hospital;
import dac2dac.doctect.agency.entity.Pharmacy;
import dac2dac.doctect.agency.repository.HospitalRepository;
import dac2dac.doctect.agency.repository.PharmacyRepository;
import dac2dac.doctect.common.constant.ErrorCode;
import dac2dac.doctect.common.error.exception.NotFoundException;
import dac2dac.doctect.common.error.exception.UnauthorizedException;
import dac2dac.doctect.health_list.dto.request.*;
import dac2dac.doctect.health_list.dto.response.*;
import dac2dac.doctect.health_list.entity.*;
import dac2dac.doctect.health_list.repository.ContactDiagRepository;
import dac2dac.doctect.health_list.repository.HealthScreeningRepository;
import dac2dac.doctect.health_list.repository.PrescriptionRepository;
import dac2dac.doctect.health_list.repository.VaccinationRepository;
import dac2dac.doctect.mydata.repository.MydataJdbcRepository;
import dac2dac.doctect.user.entity.User;
import dac2dac.doctect.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthListService {

    private final MydataJdbcRepository mydataJdbcRepository;
    private final UserRepository userRepository;
    private final ContactDiagRepository contactDiagRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final HealthScreeningRepository healthScreeningRepository;
    private final VaccinationRepository vaccinationRepository;
    private final HospitalRepository hospitalRepository;
    private final PharmacyRepository pharmacyRepository;

    @Transactional
    public void syncMydata(UserAuthenticationDto userAuthenticationDto, Long userId) {
        // 유저 정보(이름, 주민등록번호)를 통해 마이데이터 DB의 유저 아이디 가져오기 (본인 인증)
        Long mydataUserId = authenticateUser(userAuthenticationDto);

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // 마이데이터 유저의 아이디를 이용하여 마이데이터 조회 후 Doc'tech 서버 DB에 저장한다.
        //* 진료 내역
        contactDiagRepository.deleteByUserId(userId);

        List<DiagnosisDto> diagnosisData = mydataJdbcRepository.findDiagnosisByUserId(mydataUserId);
        diagnosisData.forEach(diagnosisDto ->
                contactDiagRepository.save(diagnosisDto.toEntity(user)));

        //* 투악 내역
        prescriptionRepository.deleteByUserId(userId);

        List<PrescriptionDto> prescriptionData = mydataJdbcRepository.findPrescriptionByUserId(mydataUserId);
        prescriptionData.forEach(prescriptionDto -> {
            Prescription prescription = prescriptionDto.toEntity(user);

            prescriptionDto.getDrugDtoList().forEach(drugDto -> {
                PrescriptionDrug prescriptionDrug = drugDto.toEntity();
                prescription.addPrescriptionDrug(prescriptionDrug);
            });

            prescriptionRepository.save(prescription);
        });

        //* 건강검진 내역
        healthScreeningRepository.deleteByUserId(userId);

        List<HealthScreeningDto> healthScreeningData = mydataJdbcRepository.findHealthScreeningByUserId(mydataUserId);
        healthScreeningData.forEach(healthScreeningDto -> {
            HealthScreening healthScreening = healthScreeningDto.toEntity(user);

            MeasurementTest measurementTest = healthScreeningDto.getMeasurementTest().toEntity();
            healthScreening.setMeasurementTest(measurementTest);

            BloodTest bloodTest = healthScreeningDto.getBloodTest().toEntity();
            healthScreening.setBloodTest(bloodTest);

            OtherTest otherTest = healthScreeningDto.getOtherTest().toEntity();
            healthScreening.setOtherTest(otherTest);

            healthScreeningRepository.save(healthScreening);
        });

        //* 예방접종 내역
        vaccinationRepository.deleteByUserId(userId);

        List<VaccinationDto> vaccinationData = mydataJdbcRepository.findVaccinationByUserId(mydataUserId);
        vaccinationData.forEach(vaccinationDto ->
                vaccinationRepository.save(vaccinationDto.toEntity(user)));

    }

    public DiagnosisListDto getDiagnosisList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // 비대면 진료 내역
        List<NoncontactDiagItemDto> noncontactDiagItemList = new ArrayList<>();

        NoncontactDiagItemListDto noncontactDiagItemListDto = NoncontactDiagItemListDto.builder()
                .totalCnt(noncontactDiagItemList.size())
                .noncontactDiagItemList(noncontactDiagItemList)
                .build();

        // 대면 진료 내역
        List<ContactDiagItemDto> contactDiagItemList = contactDiagRepository.findByUserId(userId)
                .stream()
                .map(c -> {
                    Hospital findHospital = hospitalRepository.findByName(c.getAgencyName())
                            .stream()
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException(ErrorCode.HOSPITAL_NOT_FOUND));

                    return ContactDiagItemDto.builder()
                            .diagDate(c.getDiagDate())
                            .agencyName(findHospital.getName())
                            .agencyAddress(findHospital.getAddress())
                            .agencyIsOpenNow(isAgencyOpenNow(findHospital))
                            .agencyTodayOpenTime(findTodayOpenTime(findHospital))
                            .agencyTodayCloseTime(findTodayCloseTime(findHospital))
                            .build();
                })
                .collect(Collectors.toList());

        ContactDiagItemListDto contactDiagItemListDto = ContactDiagItemListDto.builder()
                .totalCnt(contactDiagItemList.size())
                .contactDiagItemList(contactDiagItemList)
                .build();

        return DiagnosisListDto.builder()
                .noncontactDiagItemListDto(noncontactDiagItemListDto)
                .contactDiagItemListDto(contactDiagItemListDto)
                .build();
    }

    public ContactDiagDto getDetailContactDiagnosis(Long userId, Long diagId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        ContactDiag findContactDiag = contactDiagRepository.findById(diagId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ENTITY_NOT_FOUND));

        //* 유저와 조회한 진료 내역에 해당하는 유저가 다를 경우
        if (!user.getId().equals(findContactDiag.getUser().getId())) {
            new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }

        Hospital findHospital = hospitalRepository.findByName(findContactDiag.getAgencyName())
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorCode.HOSPITAL_NOT_FOUND));

        return ContactDiagDto.builder()
                .agencyName(findHospital.getName())
                .agencyAddress(findHospital.getAddress())
                .agencyIsOpenNow(isAgencyOpenNow(findHospital))
                .agencyTodayOpenTime(findTodayOpenTime(findHospital))
                .agencyTodayCloseTime(findTodayCloseTime(findHospital))
                .diagDate(findContactDiag.getDiagDate())
                .diagType(findContactDiag.getDiagType())
                .prescription_cnt(findContactDiag.getPrescription_cnt())
                .medication_cnt(findContactDiag.getMedication_cnt())
                .visit_days(findContactDiag.getVisit_days())
                .build();
    }

    public PrescriptionItemListDto getPrescriptionList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        List<PrescriptionItemDto> prescriptionItemList = prescriptionRepository.findByUserId(userId)
                .stream()
                .map(p -> {
                    Pharmacy pharmacy = pharmacyRepository.findByName(p.getAgencyName())
                            .stream()
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException(ErrorCode.PHARMACY_NOT_FOUND));

                    return PrescriptionItemDto.builder()
                            .treatDate(p.getTreatDate())
                            .agencyName(pharmacy.getName())
                            .agencyAddress(pharmacy.getAddress())
                            .agencyTel(pharmacy.getTel())
                            .build();

                })
                .collect(Collectors.toList());

        return PrescriptionItemListDto.builder()
                .totalCnt(prescriptionItemList.size())
                .prescriptionItemList(prescriptionItemList)
                .build();
    }

    private Long authenticateUser(UserAuthenticationDto userAuthenticationDto) {
        // 유저 아이디가 존재할 경우 본인 인증에 성공한 것으로 간주한다.
        return mydataJdbcRepository.findByNameAndPin(userAuthenticationDto.getName(), userAuthenticationDto.getPin())
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.MYDATA_AUTHENTICATION_FAILED));
    }

    private boolean isAgencyOpenNow(Agency agency) {
        Integer todayOpenTime = findTodayOpenTime(agency);
        Integer todayCloseTime = findTodayCloseTime(agency);

        if (todayOpenTime != null && todayCloseTime != null) {
            LocalTime now = LocalTime.now();
            LocalTime startTime = LocalTime.parse(String.format("%04d", todayOpenTime), DateTimeFormatter.ofPattern("HHmm"));
            LocalTime endTime;
            if (todayCloseTime == 2400) {
                endTime = LocalTime.MAX;
            } else {
                endTime = LocalTime.parse(String.format("%04d", todayCloseTime), DateTimeFormatter.ofPattern("HHmm"));
            }
            // 현재 시간이 오픈 시간과 클로즈 시간 사이에 있는지 확인
            return !now.isBefore(startTime) && now.isBefore(endTime);
        } else {
            return false;
        }
    }

    private static Integer findTodayOpenTime(Agency agency) {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        switch (dayOfWeek) {
            case MONDAY:
                return agency.getDiagTimeMonOpen();
            case TUESDAY:
                return agency.getDiagTimeTuesOpen();
            case WEDNESDAY:
                return agency.getDiagTimeWedsOpen();
            case THURSDAY:
                return agency.getDiagTimeThursOpen();
            case FRIDAY:
                return agency.getDiagTimeFriOpen();
            case SATURDAY:
                return agency.getDiagTimeSatOpen();
            case SUNDAY:
                return agency.getDiagTimeSunOpen();
        }
        return 0;
    }

    private static Integer findTodayCloseTime(Agency agency) {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        switch (dayOfWeek) {
            case MONDAY:
                return agency.getDiagTimeMonClose();
            case TUESDAY:
                return agency.getDiagTimeTuesClose();
            case WEDNESDAY:
                return agency.getDiagTimeWedsClose();
            case THURSDAY:
                return agency.getDiagTimeThursClose();
            case FRIDAY:
                return agency.getDiagTimeFriClose();
            case SATURDAY:
                return agency.getDiagTimeSatClose();
            case SUNDAY:
                return agency.getDiagTimeSunClose();
        }
        return 0;
    }
}

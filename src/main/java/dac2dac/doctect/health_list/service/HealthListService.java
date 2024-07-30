package dac2dac.doctect.health_list.service;

import static dac2dac.doctect.common.utils.DiagTimeUtils.findTodayCloseTime;
import static dac2dac.doctect.common.utils.DiagTimeUtils.findTodayOpenTime;
import static dac2dac.doctect.common.utils.DiagTimeUtils.isAgencyOpenNow;

import dac2dac.doctect.agency.entity.Hospital;
import dac2dac.doctect.agency.entity.Pharmacy;
import dac2dac.doctect.agency.repository.HospitalRepository;
import dac2dac.doctect.agency.repository.PharmacyRepository;
import dac2dac.doctect.common.constant.ErrorCode;
import dac2dac.doctect.common.error.exception.NotFoundException;
import dac2dac.doctect.common.error.exception.UnauthorizedException;
import dac2dac.doctect.health_list.dto.request.DiagnosisDto;
import dac2dac.doctect.health_list.dto.request.HealthScreeningDto;
import dac2dac.doctect.health_list.dto.request.PrescriptionDto;
import dac2dac.doctect.health_list.dto.request.UserAuthenticationDto;
import dac2dac.doctect.health_list.dto.request.VaccinationDto;
import dac2dac.doctect.health_list.dto.response.DoctorInfo;
import dac2dac.doctect.health_list.dto.response.HostpitalInfo;
import dac2dac.doctect.health_list.dto.response.PharmacyInfo;
import dac2dac.doctect.health_list.dto.response.diagnosis.ContactDiagDetailDto;
import dac2dac.doctect.health_list.dto.response.diagnosis.ContactDiagItem;
import dac2dac.doctect.health_list.dto.response.diagnosis.ContactDiagItemList;
import dac2dac.doctect.health_list.dto.response.diagnosis.DiagDetailInfo;
import dac2dac.doctect.health_list.dto.response.diagnosis.DiagnosisListDto;
import dac2dac.doctect.health_list.dto.response.diagnosis.NoncontactDiagItem;
import dac2dac.doctect.health_list.dto.response.diagnosis.NoncontactDiagItemList;
import dac2dac.doctect.health_list.dto.response.healthScreening.BloodTestInfo;
import dac2dac.doctect.health_list.dto.response.healthScreening.HealthScreeningDetailDto;
import dac2dac.doctect.health_list.dto.response.healthScreening.HealthScreeningInfo;
import dac2dac.doctect.health_list.dto.response.healthScreening.HealthScreeningItem;
import dac2dac.doctect.health_list.dto.response.healthScreening.HealthScreeningItemListDto;
import dac2dac.doctect.health_list.dto.response.healthScreening.MeasurementTestInfo;
import dac2dac.doctect.health_list.dto.response.healthScreening.OtherTestInfo;
import dac2dac.doctect.health_list.dto.response.prescription.PrescriptionDetailDto;
import dac2dac.doctect.health_list.dto.response.prescription.PrescriptionDrugItem;
import dac2dac.doctect.health_list.dto.response.prescription.PrescriptionDrugItemList;
import dac2dac.doctect.health_list.dto.response.prescription.PrescriptionItem;
import dac2dac.doctect.health_list.dto.response.prescription.PrescriptionItemListDto;
import dac2dac.doctect.health_list.dto.response.vaccination.VaccinationDetailDto;
import dac2dac.doctect.health_list.dto.response.vaccination.VaccinationDetailInfo;
import dac2dac.doctect.health_list.dto.response.vaccination.VaccinationItem;
import dac2dac.doctect.health_list.dto.response.vaccination.VaccinationItemListDto;
import dac2dac.doctect.health_list.entity.BloodTest;
import dac2dac.doctect.health_list.entity.ContactDiag;
import dac2dac.doctect.health_list.entity.HealthScreening;
import dac2dac.doctect.health_list.entity.MeasurementTest;
import dac2dac.doctect.health_list.entity.OtherTest;
import dac2dac.doctect.health_list.entity.Prescription;
import dac2dac.doctect.health_list.entity.PrescriptionDrug;
import dac2dac.doctect.health_list.entity.Vaccination;
import dac2dac.doctect.health_list.repository.ContactDiagRepository;
import dac2dac.doctect.health_list.repository.HealthScreeningRepository;
import dac2dac.doctect.health_list.repository.PrescriptionDrugRepository;
import dac2dac.doctect.health_list.repository.PrescriptionRepository;
import dac2dac.doctect.health_list.repository.VaccinationRepository;
import dac2dac.doctect.mydata.repository.MydataJdbcRepository;
import dac2dac.doctect.user.entity.User;
import dac2dac.doctect.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PrescriptionDrugRepository prescriptionDrugRepository;

    @Transactional
    public void syncMydata(UserAuthenticationDto userAuthenticationDto, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // 유저 정보(이름, 주민등록번호)를 통해 마이데이터 DB의 유저 아이디 가져오기 (본인 인증)
        Long mydataUserId = authenticateUser(userAuthenticationDto, user);

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

        //* 비대면 진료 내역
        List<NoncontactDiagItem> noncontactDiagItemList = new ArrayList<>();

        NoncontactDiagItemList noncontactDiagItemListDto = NoncontactDiagItemList.builder()
            .totalCnt(noncontactDiagItemList.size())
            .noncontactDiagItemList(noncontactDiagItemList)
            .build();

        //* 대면 진료 내역
        List<ContactDiagItem> contactDiagItemList = contactDiagRepository.findByUserId(userId)
            .stream()
            .map(c -> {
                Hospital findHospital = hospitalRepository.findByName(c.getAgencyName())
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(ErrorCode.HOSPITAL_NOT_FOUND));

                return ContactDiagItem.builder()
                    .diagId(c.getId())
                    .diagDate(c.getDiagDate())
                    .agencyName(findHospital.getName())
                    .agencyAddress(findHospital.getAddress())
                    .agencyIsOpenNow(isAgencyOpenNow(findHospital.getDiagTime()))
                    .agencyTodayOpenTime(findTodayOpenTime(findHospital.getDiagTime()))
                    .agencyTodayCloseTime(findTodayCloseTime(findHospital.getDiagTime()))
                    .build();
            })
            .sorted(Comparator.comparing(ContactDiagItem::getDiagDate).reversed())
            .collect(Collectors.toList());

        ContactDiagItemList contactDiagItemListDto = ContactDiagItemList.builder()
            .totalCnt(contactDiagItemList.size())
            .contactDiagItemList(contactDiagItemList)
            .build();

        return DiagnosisListDto.builder()
            .noncontactDiagList(noncontactDiagItemListDto)
            .contactDiagList(contactDiagItemListDto)
            .build();
    }

    public ContactDiagDetailDto getDetailContactDiagnosis(Long userId, Long diagId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        ContactDiag findContactDiag = contactDiagRepository.findById(diagId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.CONTACT_DIAGNOSIS_NOT_FOUND));

        //* 유저와 조회한 진료 내역에 해당하는 유저가 다를 경우
        if (!user.getId().equals(findContactDiag.getUser().getId())) {
            new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }

        Hospital findHospital = hospitalRepository.findByName(findContactDiag.getAgencyName())
            .stream()
            .findFirst()
            .orElseThrow(() -> new NotFoundException(ErrorCode.HOSPITAL_NOT_FOUND));

        //* 진료 기관
        HostpitalInfo hospitalInfo = HostpitalInfo.builder()
            .diagDate(findContactDiag.getDiagDate())
            .agencyName(findHospital.getName())
            .agencyAddress(findHospital.getAddress())
            .agencyIsOpenNow(isAgencyOpenNow(findHospital.getDiagTime()))
            .agencyTodayOpenTime(findTodayOpenTime(findHospital.getDiagTime()))
            .agencyTodayCloseTime(findTodayCloseTime(findHospital.getDiagTime()))
            .build();

        //* 진료 세부 정보
        DiagDetailInfo diagDetailInfo = DiagDetailInfo.builder()
            .diagType(findContactDiag.getDiagType().getDiagTypeName())
            .prescription_cnt(findContactDiag.getPrescription_cnt())
            .medication_cnt(findContactDiag.getMedication_cnt())
            .visit_days(findContactDiag.getVisit_days())
            .build();

        return ContactDiagDetailDto.builder()
            .agencyInfo(hospitalInfo)
            .diagDetailInfo(diagDetailInfo)
            .build();
    }

    public PrescriptionItemListDto getPrescriptionList(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        List<PrescriptionItem> prescriptionItemList = prescriptionRepository.findByUserId(userId)
            .stream()
            .map(p -> {
                Pharmacy pharmacy = pharmacyRepository.findByName(p.getAgencyName())
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(ErrorCode.PHARMACY_NOT_FOUND));

                return PrescriptionItem.builder()
                    .prescriptionId(p.getId())
                    .treatDate(p.getTreatDate())
                    .agencyName(pharmacy.getName())
                    .agencyAddress(pharmacy.getAddress())
                    .agencyTel(pharmacy.getTel())
                    .build();

            })
            .sorted(Comparator.comparing(PrescriptionItem::getTreatDate).reversed())
            .collect(Collectors.toList());

        return PrescriptionItemListDto.builder()
            .totalCnt(prescriptionItemList.size())
            .prescriptionItemList(prescriptionItemList)
            .build();
    }

    public PrescriptionDetailDto getDetailPrescription(Long userId, Long prescriptionId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        Prescription findPrescription = prescriptionRepository.findById(prescriptionId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.PRESCRIPTION_NOT_FOUND));

        //* 유저와 조회한 처방 내역에 해당하는 유저가 다를 경우
        if (!user.getId().equals(findPrescription.getUser().getId())) {
            new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }

        Pharmacy findPharmacy = pharmacyRepository.findByName(findPrescription.getAgencyName())
            .stream()
            .findFirst()
            .orElseThrow(() -> new NotFoundException(ErrorCode.PHARMACY_NOT_FOUND));

        //* 처방 기관
        PharmacyInfo pharmacyInfo = PharmacyInfo.builder()
            .treatDate(findPrescription.getTreatDate())
            .agencyName(findPharmacy.getName())
            .agencyAddress(findPharmacy.getAddress())
            .agencyTel(findPharmacy.getTel())
            .build();

        //* 처방전
        List<PrescriptionDrugItem> prescriptionDrugList = prescriptionDrugRepository.findByPrescriptionId(prescriptionId)
            .stream()
            .map(pd -> PrescriptionDrugItem.builder()
                .drugName(pd.getDrugName())
                .prescriptionCnt(pd.getPrescriptionCnt())
                .medicationDays(pd.getMedicationDays())
                .build())
            .collect(Collectors.toList());

        PrescriptionDrugItemList prescriptionDrugInfo = PrescriptionDrugItemList.builder()
            .prescriptionDrugList(prescriptionDrugList)
            .build();

        return PrescriptionDetailDto.builder()
            .agencyInfo(pharmacyInfo)
            .prescriptionDrugInfo(prescriptionDrugInfo)
            .build();
    }

    public VaccinationItemListDto getVaccinationList(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        List<VaccinationItem> vaccinationItemList = vaccinationRepository.findByUserId(userId)
            .stream()
            .map(v -> {
                Hospital findHospital = hospitalRepository.findByName(v.getAgencyName())
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(ErrorCode.HOSPITAL_NOT_FOUND));

                return VaccinationItem.builder()
                    .vaccId(v.getId())
                    .vaccDate(v.getVaccDate())
                    .vaccName(v.getVaccine())
                    .vaccSeries(v.getVaccSeries())
                    .agencyName(findHospital.getName())
                    .agencyAddress(findHospital.getAddress())
                    .agencyTodayOpenTime(findTodayOpenTime(findHospital.getDiagTime()))
                    .agencyTodayCloseTime(findTodayCloseTime(findHospital.getDiagTime()))
                    .agencyIsOpenNow(isAgencyOpenNow(findHospital.getDiagTime()))
                    .build();
            })
            .sorted(Comparator.comparing(VaccinationItem::getVaccDate).reversed())
            .collect(Collectors.toList());

        return VaccinationItemListDto.builder()
            .totalCnt(vaccinationItemList.size())
            .vaccinationItemList(vaccinationItemList)
            .build();
    }

    public VaccinationDetailDto getDetailVaccination(Long userId, Long vaccId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        Vaccination findVaccination = vaccinationRepository.findById(vaccId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.VACCINATION_NOT_FOUND));

        //* 유저와 조회한 예방접종 내역에 해당하는 유저가 다를 경우
        if (!user.getId().equals(findVaccination.getUser().getId())) {
            new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }

        Hospital findHospital = hospitalRepository.findByName(findVaccination.getAgencyName())
            .stream()
            .findFirst()
            .orElseThrow(() -> new NotFoundException(ErrorCode.HOSPITAL_NOT_FOUND));

        //* 접종 기관
        HostpitalInfo hospitalInfo = HostpitalInfo.builder()
            .diagDate(findVaccination.getVaccDate())
            .agencyName(findHospital.getName())
            .agencyAddress(findHospital.getAddress())
            .agencyTodayOpenTime(findTodayOpenTime(findHospital.getDiagTime()))
            .agencyTodayCloseTime(findTodayCloseTime(findHospital.getDiagTime()))
            .agencyIsOpenNow(isAgencyOpenNow(findHospital.getDiagTime()))
            .build();

        // 접종경과일 계산
        LocalDateTime currentDateTime = LocalDateTime.now();
        long postVaccinationDays = ChronoUnit.DAYS.between(findVaccination.getVaccDate(), currentDateTime);

        //* 접종 정보
        VaccinationDetailInfo vaccinationDetailInfo = VaccinationDetailInfo.builder()
            .vaccDate(findVaccination.getVaccDate())
            .vaccName(findVaccination.getVaccine())
            .vaccSeries(findVaccination.getVaccSeries())
            .postVaccinationDays(postVaccinationDays)
            .build();

        return VaccinationDetailDto.builder()
            .agencyInfo(hospitalInfo)
            .vaccinationDetailInfo(vaccinationDetailInfo)
            .build();
    }

    public HealthScreeningItemListDto getHealthScreeningList(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        List<HealthScreeningItem> healthScreeningItemList = healthScreeningRepository.findByUserId(userId)
            .stream()
            .map(h -> HealthScreeningItem.builder()
                .hsId(h.getId())
                .doctorHospital(h.getAgencyName())
                .doctorName(h.getDoctorName())
                .diagDate(h.getCheckupDate())
                .build()
            )
            .sorted(Comparator.comparing(HealthScreeningItem::getDiagDate).reversed())
            .collect(Collectors.toList());

        return HealthScreeningItemListDto.builder()
            .totalCnt(healthScreeningItemList.size())
            .healthScreeningItemList(healthScreeningItemList)
            .build();
    }

    public HealthScreeningDetailDto getDetailHealthScreening(Long userId, Long hsId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        HealthScreening findHealthScreening = healthScreeningRepository.findById(hsId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.HEALTHSCREENING_NOT_FOUND));

        //* 유저와 조회한 건강검진 내역에 해당하는 유저가 다를 경우
        if (!user.getId().equals(findHealthScreening.getUser().getId())) {
            new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }

        //* 의사 정보
        DoctorInfo doctorInfo = DoctorInfo.builder()
            .diagDate(findHealthScreening.getCheckupDate())
            .doctorHospital(findHealthScreening.getAgencyName())
            .doctorName(findHealthScreening.getDoctorName())
            .build();

        //* 건강검진 결과 정보
        HealthScreeningInfo healthScreeningInfo = HealthScreeningInfo.builder()
            .diagDate(findHealthScreening.getCheckupDate())
            .doctorHospital(findHealthScreening.getAgencyName())
            .doctorName(findHealthScreening.getDoctorName())
            .opinion(findHealthScreening.getOpinion())
            .build();

        //* 계측 검사 정보
        MeasurementTestInfo measurementTestInfo = MeasurementTestInfo.builder()
            .height(findHealthScreening.getMeasurementTest().getHeight())
            .weight(findHealthScreening.getMeasurementTest().getWeight())
            .waist(findHealthScreening.getMeasurementTest().getWaist())
            .bmi(findHealthScreening.getMeasurementTest().getBmi())
            .sightLeft(findHealthScreening.getMeasurementTest().getSightLeft())
            .sightRight(findHealthScreening.getMeasurementTest().getSightRight())
            .hearingLeft(findHealthScreening.getMeasurementTest().getHearingLeft())
            .hearingRight(findHealthScreening.getMeasurementTest().getHearingRight())
            .bloodPressureHigh(findHealthScreening.getMeasurementTest().getBloodPressureHigh())
            .bloodPressureLow(findHealthScreening.getMeasurementTest().getBloodPressureLow())
            .build();

        //* 혈액 검사 정보
        BloodTestInfo bloodTestInfo = BloodTestInfo.builder()
            .hemoglobin(findHealthScreening.getBloodTest().getHemoglobin())
            .fastingBloodSugar(findHealthScreening.getBloodTest().getFastingBloodSugar())
            .totalCholesterol(findHealthScreening.getBloodTest().getTotalCholesterol())
            .HDLCholesterol(findHealthScreening.getBloodTest().getHDLCholesterol())
            .triglyceride(findHealthScreening.getBloodTest().getTriglyceride())
            .LDLCholesterol(findHealthScreening.getBloodTest().getLDLCholesterol())
            .serumCreatinine(findHealthScreening.getBloodTest().getSerumCreatinine())
            .GFR(findHealthScreening.getBloodTest().getGFR())
            .AST(findHealthScreening.getBloodTest().getAST())
            .ALT(findHealthScreening.getBloodTest().getALT())
            .GPT(findHealthScreening.getBloodTest().getGPT())
            .build();

        OtherTestInfo otherTestInfo = OtherTestInfo.builder()
            .urinaryProtein(Optional.ofNullable(findHealthScreening.getOtherTest().getUrinaryProtein())
                .map(u -> u.getUrinaryProteinType())
                .orElse(null))
            .TBChestDisease(Optional.ofNullable(findHealthScreening.getOtherTest().getTBChestDisease())
                .map(t -> t.getTBChestDiseaseType())
                .orElse(null))
            .isHepB(findHealthScreening.getOtherTest().getIsHepB())
            .hepBSurfaceAntibody(Optional.ofNullable(findHealthScreening.getOtherTest().getHepBSurfaceAntibody())
                .map(h -> h.getHepBSurfaceAntibodyType())
                .orElse(null))
            .hepBSurfaceAntigen(Optional.ofNullable(findHealthScreening.getOtherTest().getHepBSurfaceAntigen())
                .map(h -> h.getHepBSurfaceAntigenType())
                .orElse(null))
            .hepB(findHealthScreening.getOtherTest().getHepB())
            .isDepression(findHealthScreening.getOtherTest().getIsDepression())
            .depression(Optional.ofNullable(findHealthScreening.getOtherTest().getDepression())
                .map(h -> h.getDepressionType())
                .orElse(null))
            .isCognitiveDysfunction(findHealthScreening.getOtherTest().getIsCognitiveDysfunction())
            .cognitiveDysfunction(Optional.ofNullable(findHealthScreening.getOtherTest().getCognitiveDysfunction())
                .map(h -> h.getCognitiveDysfunctionType())
                .orElse(null))
            .isBoneDensityTest(findHealthScreening.getOtherTest().getIsBoneDensityTest())
            .boneDensityTest(Optional.ofNullable(findHealthScreening.getOtherTest().getBoneDensityTest())
                .map(h -> h.getBoneDensityType())
                .orElse(null))
            .isElderlyPhysicalFunctionTest(findHealthScreening.getOtherTest().getIsElderlyPhysicalFunctionTest())
            .elderlyPhysicalFunctionTest(Optional.ofNullable(findHealthScreening.getOtherTest().getElderlyPhysicalFunctionTest())
                .map(h -> h.getElderlyPhysicalFunctionTestType())
                .orElse(null))
            .isElderlyFunctionalAssessment(findHealthScreening.getOtherTest().getIsElderlyFunctionalAssessment())
            .elderlyFunctionalAssessmentFalls(Optional.ofNullable(findHealthScreening.getOtherTest().getElderlyFunctionalAssessmentFalls())
                .map(h -> h.getElderlyFunctionalAssessmentFallsType())
                .orElse(null))
            .elderlyFunctionalAssessmentADL(Optional.ofNullable(findHealthScreening.getOtherTest().getElderlyFunctionalAssessmentADL())
                .map(h -> h.getElderlyFunctionalAssessmentADLType())
                .orElse(null))
            .elderlyFunctionalAssessmentVaccination(Optional.ofNullable(findHealthScreening.getOtherTest().getElderlyFunctionalAssessmentVaccination())
                .map(h -> h.getElderlyFunctionalAssessmentVaccinationType())
                .orElse(null))
            .elderlyFunctionalAssessmentUrinaryIncontinence(Optional.ofNullable(findHealthScreening.getOtherTest().getElderlyFunctionalAssessmentUrinaryIncontinence())
                .map(h -> h.getElderlyFunctionalAssessmentUrinaryIncontinenceType())
                .orElse(null))
            .smoke(findHealthScreening.getOtherTest().getSmoke())
            .drink(findHealthScreening.getOtherTest().getDrink())
            .physicalActivity(findHealthScreening.getOtherTest().getPhysicalActivity())
            .exercise(findHealthScreening.getOtherTest().getExercise())
            .build();

        return HealthScreeningDetailDto.builder()
            .doctorInfo(doctorInfo)
            .healthScreeningInfo(healthScreeningInfo)
            .measurementTestInfo(measurementTestInfo)
            .bloodTestInfo(bloodTestInfo)
            .otherTestInfo(otherTestInfo)
            .build();
    }

    private Long authenticateUser(UserAuthenticationDto userAuthenticationDto, User user) {
        // 유저 아이디가 존재할 경우 본인 인증에 성공한 것으로 간주한다.
        Long mydataUserId = mydataJdbcRepository.findByNameAndPin(userAuthenticationDto.getName(), userAuthenticationDto.getPin())
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.MYDATA_AUTHENTICATION_FAILED));

        if (user.getPin().equals(userAuthenticationDto.getPin()) && user.getName().equals(userAuthenticationDto.getName())) {
            return mydataUserId;
        } else {
            throw new UnauthorizedException(ErrorCode.MYDATA_AUTHENTICATION_FAILED);
        }
    }
}

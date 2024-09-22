package dac2dac.doctect.agency.service;

import static dac2dac.doctect.common.utils.DiagTimeUtils.findTodayCloseTime;
import static dac2dac.doctect.common.utils.DiagTimeUtils.findTodayOpenTime;
import static dac2dac.doctect.common.utils.DiagTimeUtils.isAgencyOpenNow;

import dac2dac.doctect.agency.dto.request.SearchCriteria;
import dac2dac.doctect.agency.dto.response.AgencySearchResultDto;
import dac2dac.doctect.agency.dto.response.AgencySearchResultListDto;
import dac2dac.doctect.agency.dto.response.HospitalDto;
import dac2dac.doctect.agency.dto.response.PharmacyDto;
import dac2dac.doctect.agency.entity.Agency;
import dac2dac.doctect.agency.entity.Hospital;
import dac2dac.doctect.agency.entity.Pharmacy;
import dac2dac.doctect.agency.repository.HospitalRepository;
import dac2dac.doctect.agency.repository.PharmacyRepository;
import dac2dac.doctect.common.constant.ErrorCode;
import dac2dac.doctect.common.entity.DiagTime;
import dac2dac.doctect.common.error.exception.NotFoundException;
import dac2dac.doctect.user.entity.User;
import dac2dac.doctect.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class AgencyService {

    private final UserRepository userRepository;
    private final PharmacyRepository pharmacyRepository;
    private final HospitalRepository hospitalRepository;

    @Transactional
    public AgencySearchResultListDto searchAgency(Long userId, SearchCriteria criteria) {
        final double latitude = criteria.getLatitude();
        final double longitude = criteria.getLongitude();
        final double radius = 2.0;

        //* 사용자 위치 정보 DB에 저장
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        user.setLocation(latitude, longitude);
        userRepository.save(user);

        //* 중복 제거를 위한 Set
        Set<Agency> searchResultSet = new HashSet<>();

        //* 검색 조건에 따른 데이터 조회
        loadDataBasedOnCriteria(criteria, latitude, longitude, radius, searchResultSet);

        //* 결과 필터링
        List<Agency> filteredResult = filterSearchResult(criteria, new ArrayList<>(searchResultSet));

        //* 가까운순 정렬
        List<AgencySearchResultDto> sortedResult = filteredResult.stream()
            .map(s -> createAgencySearchResultDto(s, latitude, longitude))
            .sorted(Comparator.comparing(AgencySearchResultDto::getDistance))
            .collect(Collectors.toList());

        return AgencySearchResultListDto.builder()
            .totalCnt(sortedResult.size())
            .agencySearchResultList(sortedResult)
            .build();
    }

    public PharmacyDto getDetailPharmacy(Long userId, Long pharmacyId) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.PHARMACY_NOT_FOUND));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return PharmacyDto.builder()
            .name(pharmacy.getName())
            .address(pharmacy.getAddress())
            .tel(pharmacy.getTel())
            .distance(calculateDistance(user.getLatitude(), user.getLongitude(), pharmacy.getLatitude(), pharmacy.getLongitude()))
            .isOpen(isAgencyOpenNow(pharmacy.getDiagTime()))
            .todayOpenTime(findTodayOpenTime(pharmacy.getDiagTime()))
            .todayCloseTime(findTodayCloseTime(pharmacy.getDiagTime()))
            .latitude(pharmacy.getLatitude())
            .longtitude(pharmacy.getLongitude())
            .build();
    }

    public HospitalDto getDetailHospital(Long userId, Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.PHARMACY_NOT_FOUND));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return HospitalDto.builder()
            .name(hospital.getName())
            .address(hospital.getAddress())
            .tel(hospital.getTel())
            .distance(calculateDistance(user.getLatitude(), user.getLongitude(), hospital.getLatitude(), hospital.getLongitude()))
            .isOpen(isAgencyOpenNow(hospital.getDiagTime()))
            .todayOpenTime(findTodayOpenTime(hospital.getDiagTime()))
            .todayCloseTime(findTodayCloseTime(hospital.getDiagTime()))
            .latitude(hospital.getLatitude())
            .longtitude(hospital.getLongitude())
            .build();
    }

    private void loadDataBasedOnCriteria(SearchCriteria criteria, double latitude, double longitude, double radius, Set<Agency> searchResultSet) {
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) { // 검색어 있는 경우
            if (criteria.isHospital()) {
                searchResultSet.addAll(hospitalRepository.findNearbyHospitalsWithKeyword(latitude, longitude, radius, criteria.getKeyword()));
            }
            if (criteria.isPharmacy()) {
                searchResultSet.addAll(pharmacyRepository.findNearbyPharmaciesWithKeyword(latitude, longitude, radius, criteria.getKeyword()));
            }
            if (criteria.isEr()) {
                searchResultSet.addAll(hospitalRepository.findNearbyERsWithKeyword(latitude, longitude, radius, criteria.getKeyword()));
            }
        } else { // 검색어 없는 경우
            if (criteria.isHospital()) {
                searchResultSet.addAll(hospitalRepository.findNearbyHospitals(latitude, longitude, radius));
            }
            if (criteria.isPharmacy()) {
                searchResultSet.addAll(pharmacyRepository.findNearbyPharmacies(latitude, longitude, radius));
            }
            if (criteria.isEr()) {
                searchResultSet.addAll(hospitalRepository.findNearbyERs(latitude, longitude, radius));
            }
        }
    }

    private List<Agency> filterSearchResult(SearchCriteria criteria, List<Agency> searchResult) {
        Stream<Agency> stream = searchResult.stream();

        // 영업 시간별 필터링
        if (criteria.isOpenNow()) {
            stream = stream.filter(s -> isAgencyOpenNow(s.getDiagTime()));
        }
        if (criteria.isOpenAllYear()) {
            stream = stream.filter(s -> isAgencyOpenAllYear(s.getDiagTime()));
        }
        if (criteria.isOpenAtMidnight()) {
            stream = stream.filter(s -> isAgencyOpenMidnight(s.getDiagTime()));
        }

        // 영업 요일별 필터링
        List<DayOfWeek> daysFilter = new ArrayList<>();
        if (criteria.isMon()) {
            daysFilter.add(DayOfWeek.MONDAY);
        }
        if (criteria.isTue()) {
            daysFilter.add(DayOfWeek.TUESDAY);
        }
        if (criteria.isWed()) {
            daysFilter.add(DayOfWeek.WEDNESDAY);
        }
        if (criteria.isThu()) {
            daysFilter.add(DayOfWeek.THURSDAY);
        }
        if (criteria.isFri()) {
            daysFilter.add(DayOfWeek.FRIDAY);
        }
        if (criteria.isSat()) {
            daysFilter.add(DayOfWeek.SATURDAY);
        }
        if (criteria.isSun()) {
            daysFilter.add(DayOfWeek.SUNDAY);
        }

        for (DayOfWeek day : daysFilter) {
            stream = stream.filter(s -> isAgencyOpenOnDay(s.getDiagTime(), day));
        }

        return stream.collect(Collectors.toList());
    }

    private boolean isAgencyOpenOnDay(DiagTime agency, DayOfWeek day) {
        // 요일별로 오픈 상태를 확인하는 로직
        switch (day) {
            case MONDAY:
                return agency.getDiagTimeMonOpen() != null && agency.getDiagTimeMonClose() != null;
            case TUESDAY:
                return agency.getDiagTimeTuesOpen() != null && agency.getDiagTimeTuesClose() != null;
            case WEDNESDAY:
                return agency.getDiagTimeWedsOpen() != null && agency.getDiagTimeWedsClose() != null;
            case THURSDAY:
                return agency.getDiagTimeThursOpen() != null && agency.getDiagTimeThursClose() != null;
            case FRIDAY:
                return agency.getDiagTimeFriOpen() != null && agency.getDiagTimeFriClose() != null;
            case SATURDAY:
                return agency.getDiagTimeSatOpen() != null && agency.getDiagTimeSatClose() != null;
            case SUNDAY:
                return agency.getDiagTimeSunOpen() != null && agency.getDiagTimeSunClose() != null;
            default:
                return false;
        }
    }

    private boolean isAgencyOpenAllYear(DiagTime agency) {
        return (agency.getDiagTimeHolidayOpen() != null && agency.getDiagTimeHolidayClose() != null)
            && (agency.getDiagTimeMonOpen() != null && agency.getDiagTimeMonClose() != null)
            && (agency.getDiagTimeTuesOpen() != null && agency.getDiagTimeTuesClose() != null)
            && (agency.getDiagTimeWedsOpen() != null && agency.getDiagTimeWedsClose() != null)
            && (agency.getDiagTimeThursOpen() != null && agency.getDiagTimeThursClose() != null)
            && (agency.getDiagTimeFriOpen() != null && agency.getDiagTimeFriClose() != null)
            && (agency.getDiagTimeSatOpen() != null && agency.getDiagTimeSatClose() != null)
            && (agency.getDiagTimeSunOpen() != null && agency.getDiagTimeSunClose() != null);
    }

    private boolean isAgencyOpenMidnight(DiagTime agency) {
        Integer todayOpenTime = findTodayOpenTime(agency);
        Integer todayCloseTime = findTodayCloseTime(agency);

        if (todayOpenTime != null && todayCloseTime != null) {
            return todayOpenTime == 0 && (todayCloseTime == 2400 || todayCloseTime == 2359);
        }
        return false;
    }

    private AgencySearchResultDto createAgencySearchResultDto(Agency a, double latitude, double longitude) {
        return AgencySearchResultDto.builder()
            .id(a.getId())
            .name(a.getName())
            .todayOpenTime(findTodayOpenTime(a.getDiagTime()))
            .todayCloseTime(findTodayCloseTime(a.getDiagTime()))
            .isOpen(isAgencyOpenNow(a.getDiagTime()))
            .address(a.getAddress())
            .tel(a.getTel())
            .distance(calculateDistance(a.getLatitude(), a.getLongitude(), latitude, longitude))
            .longtitude(a.getLongitude())
            .latitude(a.getLatitude())
            .agencyType(a.getAgencyType())
            .build();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; // Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }

    public List<HospitalDto> getAllHospitals() {
        Pageable pageable = PageRequest.of(0,100); // Create a Pageable instance with the provided page and size
        Page<Hospital> hospitalPage = hospitalRepository.findAll(pageable); // Fetch the hospitals with pagination

        return hospitalPage.getContent().stream() // Get the content (list of hospitals) from the page
                .map(hospital -> HospitalDto.builder()
                        .id(hospital.getId())
                        .name(hospital.getName())
                        .address(hospital.getAddress())
                        .build())
                .collect(Collectors.toList());
    }



}

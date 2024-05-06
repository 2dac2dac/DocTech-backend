package dac2dac.doctect.agency.service;

import dac2dac.doctect.agency.dto.request.SearchCriteria;
import dac2dac.doctect.agency.dto.response.AgencySearchResultDto;
import dac2dac.doctect.agency.dto.response.AgencySearchResultListDto;
import dac2dac.doctect.agency.entity.Agency;
import dac2dac.doctect.agency.repository.HospitalRepository;
import dac2dac.doctect.agency.repository.PharmacyRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgencyService {

    private final PharmacyRepository pharmacyRepository;
    private final HospitalRepository hospitalRepository;

    public AgencySearchResultListDto searchAgency(SearchCriteria criteria) {
        final double latitude = criteria.getLatitude();
        final double longitude = criteria.getLongitude();
        final double radius = 2.0;

        // 중복 제거를 위한 Set
        Set<Agency> searchResultSet = new HashSet<>();

        // 검색 조건에 따른 데이터 조회
        loadDataBasedOnCriteria(criteria, latitude, longitude, radius, searchResultSet);

        // 결과 필터링
        List<Agency> filteredResult = filterSearchResult(criteria, new ArrayList<>(searchResultSet));

        // 가까운순 정렬
        List<AgencySearchResultDto> sortedResult = filteredResult.stream()
            .map(s -> createAgencySearchResultDto(s, latitude, longitude))
            .sorted(Comparator.comparing(AgencySearchResultDto::getDistance))
            .collect(Collectors.toList());

        return AgencySearchResultListDto.builder()
            .totalCnt(sortedResult.size())
            .agencySearchResultList(sortedResult)
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
            stream = stream.filter(this::isAgencyOpenNow);
        }
        if (criteria.isOpenAllYear()) {
            stream = stream.filter(this::isAgencyOpenAllYear);
        }
        if (criteria.isOpenAtMidnight()) {
            stream = stream.filter(this::isAgencyOpenMidnight);
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
            stream = stream.filter(s -> isAgencyOpenOnDay(s, day));
        }

        return stream.collect(Collectors.toList());
    }

    private boolean isAgencyOpenOnDay(Agency agency, DayOfWeek day) {
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

    private boolean isAgencyOpenAllYear(Agency agency) {
        return (agency.getDiagTimeHolidayOpen() != null && agency.getDiagTimeHolidayClose() != null)
            && (agency.getDiagTimeMonOpen() != null && agency.getDiagTimeMonClose() != null)
            && (agency.getDiagTimeTuesOpen() != null && agency.getDiagTimeTuesClose() != null)
            && (agency.getDiagTimeWedsOpen() != null && agency.getDiagTimeWedsClose() != null)
            && (agency.getDiagTimeThursOpen() != null && agency.getDiagTimeThursClose() != null)
            && (agency.getDiagTimeFriOpen() != null && agency.getDiagTimeFriClose() != null)
            && (agency.getDiagTimeSatOpen() != null && agency.getDiagTimeSatClose() != null)
            && (agency.getDiagTimeSunOpen() != null && agency.getDiagTimeSunClose() != null);
    }

    private boolean isAgencyOpenMidnight(Agency agency) {
        Integer todayOpenTime = findTodayOpenTime(agency);
        Integer todayCloseTime = findTodayCloseTime(agency);

        if (todayOpenTime != null && todayCloseTime != null) {
            return todayOpenTime == 0 && (todayCloseTime == 2400 || todayCloseTime == 2359);
        }
        return false;
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

    private AgencySearchResultDto createAgencySearchResultDto(Agency a, double latitude, double longitude) {
        return AgencySearchResultDto.builder()
            .name(a.getName())
            .todayOpenTime(findTodayOpenTime(a))
            .todayCloseTime(findTodayCloseTime(a))
            .isOpen(isAgencyOpenNow(a))
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
}

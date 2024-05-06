package dac2dac.doctect.agency.repository;

import dac2dac.doctect.agency.entity.Hospital;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    // 가까운 병원 조회하기. JPQL 사용
    // 데이터베이스에서 주어진 반경 내에 있는 병원을 조회. Haversine 공식을 사용하여 두 지점 간의 거리를 계산하고, 6371을 곱해 거리를 킬로미터 단위로 변환.
    // 2km 이내에 있는 모든 병원 가져옴
    @Query(value =
        "SELECT *, (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(latitude)))) AS distance "
            + "FROM Hospital "
            + "HAVING distance < :radius "
            + "ORDER BY distance", nativeQuery = true)
    List<Hospital> findNearbyHospitals(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("radius") double radius);

    @Query(value =
        "SELECT *, (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(latitude)))) AS distance "
            + "FROM Hospital "
            + "WHERE is_er_operate = true "
            + "HAVING distance < :radius "
            + "ORDER BY distance", nativeQuery = true)
    List<Hospital> findNearbyERs(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("radius") double radius);

    @Query(value =
        "SELECT *, (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(latitude)))) AS distance "
            + "FROM Hospital "
            + "WHERE name like %:name% "
            + "HAVING distance < :radius "
            + "ORDER BY distance", nativeQuery = true)
    List<Hospital> findNearbyHospitalsWithKeyword(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("radius") double radius, @Param("name") String keyword);

    @Query(value =
        "SELECT *, (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(latitude)))) AS distance "
            + "FROM Hospital "
            + "WHERE is_er_operate = true and name like %:name% "
            + "HAVING distance < :radius "
            + "ORDER BY distance", nativeQuery = true)
    List<Hospital> findNearbyERsWithKeyword(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("radius") double radius, @Param("name") String keyword);
}

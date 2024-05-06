package dac2dac.doctect.agency.repository;

import dac2dac.doctect.agency.entity.Pharmacy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    // 가까운 약국 조회하기. JPQL 사용
    // 데이터베이스에서 주어진 반경 내에 있는 약국을 조회. Haversine 공식을 사용하여 두 지점 간의 거리를 계산하고, 6371을 곱해 거리를 킬로미터 단위로 변환.
    // 2km 이내에 있는 모든 약국 가져옴
    @Query(value =
        "SELECT *, (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(latitude)))) AS distance "
            + "FROM Pharmacy "
            + "HAVING distance < :radius "
            + "ORDER BY distance", nativeQuery = true)
    List<Pharmacy> findNearbyPharmacies(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("radius") double radius);

    @Query(value =
        "SELECT *, (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(latitude)))) AS distance "
            + "FROM Pharmacy "
            + "WHERE name like %:name% "
            + "HAVING distance < :radius "
            + "ORDER BY distance", nativeQuery = true)
    List<Pharmacy> findNearbyPharmaciesWithKeyword(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("radius") double radius, @Param("name") String keyword);
}
